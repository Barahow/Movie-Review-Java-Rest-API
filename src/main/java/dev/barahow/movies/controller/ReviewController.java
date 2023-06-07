package dev.barahow.movies.controller;

import dev.barahow.movies.model.Review;
import dev.barahow.movies.model.ReviewSummary;
import dev.barahow.movies.service.ReviewService;
import dev.barahow.movies.service.UserAuthenticationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {


    @Autowired
    private final ReviewService reviewService;
    private final UserAuthenticationService userAuthenticationService;


    @PostMapping
    public ResponseEntity<ReviewSummary> createReview(@RequestBody Map<String, Object> request) {

        String email = (String) request.get("email");
        String imdbId = (String) request.get("imdbId");
        String body = (String) request.get("body");
        double rating = (double) request.get("ratings");


        ReviewSummary review = reviewService.createReview(email, imdbId, body, rating);

        return new ResponseEntity<ReviewSummary>(review, HttpStatus.CREATED);

    }

    @DeleteMapping("/comment/{id}")
    public ResponseEntity<Review> deleteComment(@PathVariable("id") ObjectId commentId, @RequestHeader("Authorization") String authorizationHeader) {

        String loggedInUserEmail = userAuthenticationService.getLoggedInUserEmail(authorizationHeader);


        // retrieve the comment from the database

        Optional<Review> commentOptional = reviewService.getCommentById(commentId);

        Optional<ReviewSummary> commentOptional2 = reviewService.getCommentByIdFromReviewSummary(commentId);

        if (commentOptional.isEmpty() && commentOptional2.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (commentOptional.isPresent() && commentOptional2.isPresent()) {
            Review comment = commentOptional.get();
            ReviewSummary comment1 = commentOptional2.get();
            // check authenticated user is the owner of the comment

            if (!comment.getUserEmail().equals(loggedInUserEmail)) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }


            // delete comment all together
            reviewService.deleteComment(comment);

            reviewService.deleteCommentFromReviewSummary(comment1);



            return ResponseEntity.ok(comment);

        }else {
            return ResponseEntity.internalServerError().build();
        }
    }


    @PutMapping("/comment/{id}")
    public ResponseEntity<Review> editComment(@PathVariable("id") ObjectId commentId, @RequestBody Review updateComment, @RequestHeader("Authorization") String authorization) {
        // retrieve the loggged-in user email

        String loggedInUserEmail = userAuthenticationService.getLoggedInUserEmail(authorization);


        // retrieve the existing comment from database

        Optional<Review> commentOptional = reviewService.getCommentById(commentId);
        Optional<ReviewSummary> commentOptional2= reviewService.getCommentByIdFromReviewSummary(commentId);

        if (commentOptional.isEmpty()|| commentOptional2.isEmpty()) {
            return ResponseEntity.notFound().build();
        }


        Review existingComment = commentOptional.get();
        ReviewSummary existingComment2= commentOptional2.get();



        if (!existingComment.getUserEmail().equals(loggedInUserEmail)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // update the comment from content
        existingComment.setBody(updateComment.getBody());
        existingComment2.setBody(updateComment.getBody());


        Review updatedComment = reviewService.saveComment( existingComment);
        reviewService.saveCommentFromReviewSummary(existingComment2);


        return ResponseEntity.ok(updatedComment);


    }





    @GetMapping("/{imdbId}")
    public ResponseEntity<Optional<Review>> getSingleMovie(@PathVariable String imdbId) {
        return new ResponseEntity<Optional<Review>>(reviewService.singleMovie(imdbId),HttpStatus.OK);
    }

}

