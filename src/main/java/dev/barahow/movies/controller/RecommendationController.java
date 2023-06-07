package dev.barahow.movies.controller;

import dev.barahow.movies.model.Movie;
import dev.barahow.movies.service.RecommendationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/recommendation")
public class RecommendationController {

    @Autowired
    private final RecommendationService recommendationService;



    @GetMapping("/{email}")
    public ResponseEntity<List<Movie>> getRecommendations(@PathVariable("email") String email) {

        List<Movie> recommendation=recommendationService.getMovieRecommendation(email,0.3);
        log.info("Should not be null"+ recommendation.toString());
        return ResponseEntity.ok(recommendation);
    }

}
