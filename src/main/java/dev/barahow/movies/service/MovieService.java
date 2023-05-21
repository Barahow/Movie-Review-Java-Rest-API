package dev.barahow.movies.service;

import dev.barahow.movies.model.Movie;
import dev.barahow.movies.model.MovieRating;
import dev.barahow.movies.model.ReviewSummary;
import dev.barahow.movies.repository.MovieRepository;
import dev.barahow.movies.repository.ReviewRepository;
import dev.barahow.movies.repository.ReviewSummaryRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MovieService {
    @Autowired
    private final MovieRepository movieRepository;

    @Autowired
    private final ReviewRepository reviewRepository;

    @Autowired
    private final ReviewSummaryRepository reviewSummaryRepository;

    public Page<Movie> allMovies(Pageable pageable) throws Exception {
        Page<Movie> movies = movieRepository.findAll(pageable);
        List<ReviewSummary> reviewSummaries = reviewSummaryRepository.findAll();
        Map<String, MovieRating> movieRatings = new HashMap<>();

        for (ReviewSummary reviewSummary : reviewSummaries) {
            String imdbId = reviewSummary.getMovieImdbId();
            double rating = reviewSummary.getRating();

            if (!movieRatings.containsKey(imdbId)) {
                MovieRating movieRating = new MovieRating();
                movieRating.setTotalReviews(1);
                movieRating.setTotalRating(rating);
                movieRating.setAverageRating(rating);
                movieRatings.put(imdbId, movieRating);
            } else {
                MovieRating movieRating = movieRatings.get(imdbId);
                movieRating.setTotalReviews(movieRating.getTotalReviews() + 1);
                movieRating.setTotalRating(movieRating.getTotalRating() + rating);
                double avgRating = movieRating.getTotalRating() / movieRating.getTotalReviews();
                movieRating.setAverageRating(avgRating);
            }
        }

        for (Movie movie : movies) {
            String imdbId = movie.getImdbId();
            if (movieRatings.containsKey(imdbId)) {
                MovieRating movieRating = movieRatings.get(imdbId);
                movie.setAvgRating(movieRating.getAverageRating());
            } else {
                movie.setAvgRating(0.0);
            }
        }

        return movies;
    }






    public Optional<Movie> singleMovie(String imdbId) {
        return movieRepository.findByImdbId(imdbId);
    }



    public Optional<Movie> deleteMovie(String imdbId) {
        return movieRepository.deleteByImdbId(imdbId);
    }

    public Optional<Movie> updateMovie(String imdbId, Movie updatedMovie) {
        Optional<Movie> optionalMovie = movieRepository.findByImdbId(imdbId);
        if (optionalMovie.isPresent()) {
            Movie existingMovie = optionalMovie.get();
            existingMovie.setTitle(updatedMovie.getTitle());
            existingMovie.setReleaseDate(updatedMovie.getReleaseDate());
            return Optional.of(movieRepository.save(existingMovie));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Movie> getMovieByTitle(String title) {

        return movieRepository.findByTitleIgnoreCase(title);

    }

    public List<Movie> getMovieByGenre(List<String> genre) {

        return movieRepository.findByGenresInIgnoreCase(genre);
    }
}