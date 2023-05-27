package dev.barahow.movies.service;

import dev.barahow.movies.model.*;
import dev.barahow.movies.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ReviewService {
    @Autowired
    private final ReviewRepository reviewRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final MovieRepository movieRepository;

    @Autowired
    private final RatingRepository ratingRepository;

    @Autowired
    private final ReviewSummaryRepository reviewSummaryRepository;
    @Autowired
    private MongoTemplate mongoTemplate;


    public ReviewSummary createReview(String email, String imdbId, String reviewBody, double rating) {



        AppUser user = userRepository.findByEmailIgnoreCase(email);
        Optional<Movie> movies = movieRepository.findByImdbId(imdbId);

        log.info("the movie object should return something {}",movies);


        if (user== null) {
            throw  new UsernameNotFoundException("user not found with that email??");

        }

        //validate rating
        if (rating<0 || rating>5) {
            throw new IllegalStateException("Rating must be between 1-5");

        }
        if (movies.isEmpty()) {
            throw new IllegalStateException("Movie does not exist"
            );
        }

        Rating newRatings = new Rating(new ObjectId(),user.getEmail(),movies.get().getImdbId(),rating);

        ratingRepository.save(newRatings);



        Review review= reviewRepository.insert(new Review(new ObjectId(),user.getEmail(),movies.get().getImdbId(),newRatings,reviewBody));


        mongoTemplate.update(Movie.class).matching(Criteria.where("imdbId").is(imdbId))
                .apply(new Update().push("reviewIds").value(review)
                        .push("ratings").value(newRatings))
                .first();
        ReviewSummary  newReviewSummary = new ReviewSummary(review.getId(),review.getUserEmail(),review.getMovieImdbId(),review.getRating().getValue(),review.getBody());



        return reviewSummaryRepository.insert(newReviewSummary);
    }


    public Optional<Review> singleMovie(String imdbId) {
        return reviewRepository.findByMovieImdbId(imdbId);
        }



    public Optional<Review> getCommentById(ObjectId commentId) {

        return reviewRepository.findById(commentId);
    }
    public Optional<ReviewSummary> getCommentByIdFromReviewSummary(ObjectId commentId) {

        return reviewSummaryRepository.findById(commentId);
    }



    public void deleteComment(Review comment) {

            reviewRepository.delete(comment);
    }

    public void deleteCommentFromReviewSummary(ReviewSummary comment) {

        reviewSummaryRepository.delete(comment);
    }

    public Review saveComment(Review existingComment) {

        return reviewRepository.save(existingComment);

    }

    public ReviewSummary saveCommentFromReviewSummary(ReviewSummary existingComment2) {

        return reviewSummaryRepository.save(existingComment2);

    }
}

