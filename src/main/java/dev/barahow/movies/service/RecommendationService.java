package dev.barahow.movies.service;

import dev.barahow.movies.model.Movie;
import dev.barahow.movies.model.ReviewSummary;
import dev.barahow.movies.repository.MovieRepository;
import dev.barahow.movies.repository.ReviewSummaryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Slf4j
@Service
@AllArgsConstructor
public class RecommendationService {

    @Autowired
    private final MovieRepository movieRepository;

    @Autowired
    final ReviewSummaryRepository reviewSummaryRepository;


    // the algorthm growth of this algorithm should fall between O(n) and O(n*logn)
    // there probably are ways to make it more efficient.

    public List<Movie> getMovieRecommendation(String email,double similarityTreshold) {

        List<ReviewSummary> reviewSummaries = reviewSummaryRepository.findByUserEmail(email);

        // extract the nessesary fields such as movieId and ratings from the table

        List<String> movieIds = new ArrayList<>();
        List<Double> ratings = new ArrayList<>();

        for (ReviewSummary reviewSummary : reviewSummaries) {
            movieIds.add(reviewSummary.getMovieImdbId());
            ratings.add(reviewSummary.getRating());
        }


        // item based recommendation filtering
        List<Movie> allMovies = movieRepository.findAll();

        List<Movie> unratedMovies = allMovies.stream()
                .filter(movie -> movieIds.contains(movie.getImdbId())).toList();


        // calculate the similarity between the unrated Movies and the rated movies

        List<Movie> recommendedMovies = new ArrayList<>();

        for (Movie unratedMovie : unratedMovies) {
            double similaritySum = 0.0;
            double weightedRatingSum = 0.0;


            List<Movie> similarMovies = findSimilarMovies(unratedMovie, movieIds);


            for (int i = 0; i < movieIds.size(); i++) {
                String ratedMovieId = movieIds.get(i);
                double rating = ratings.get(i);

                for (int j = 0; j < similarMovies.size(); j++) {
                    if (similarMovies.get(j).getImdbId().equals(ratedMovieId)) {
                        // calcuated similarity between rated and unrated movies
                        double simiarity = calculateSimilarity(ratedMovieId, movieIds, rating);


                        similaritySum += simiarity;
                        weightedRatingSum += simiarity * rating;
                        break;
                    }

                }

            }

            // calculate the wieghted average rating for the unrated movie

            if (similaritySum!=0.0) {
                double weightedAverageRating = weightedRatingSum / similaritySum;
                unratedMovie.setPredictedRating(weightedAverageRating);


                // make sure that the similarity score is not below a certain treshold.
                // i will stick with 0.3 as the minimum to show up in recommendation

                if (similaritySum>= similarityTreshold) {

                    recommendedMovies.add(unratedMovie);

                }


            }

        }

        // sort the recommended movies based on the predicted Rating

        recommendedMovies.sort(Comparator.comparingDouble(Movie::getPredictedRating).reversed());


        // return the recommended movies
        return recommendedMovies;

    }

    private List<Movie> findSimilarMovies(Movie ratedMovie, List<String> movieImdbIds) {
        List<Movie> similarMovies = new ArrayList<>();

        for (String imdbId : movieImdbIds) {
            if (!imdbId.equals(ratedMovie.getImdbId())) {

                double similarity = calculateSimilarity(imdbId, movieImdbIds, ratedMovie.getAvgRating());

                // creating a movie object that represnt the similar movie

                Movie similarMovie = new Movie();

                similarMovie.setImdbId(imdbId);
                similarMovie.setSimilarity(similarity);
                similarMovies.add(similarMovie);

            }
        }

log.info("We should get similar movie here" +  similarMovies.size());
        return similarMovies;
    }

    private double calculateSimilarity(String ratedMovie, List<String> movies, double rating) {
        //using Jacccard similarity coefficient to calculate similarity
        // retriev the rated movie from the databased using the provided imdbid

        Optional<Movie> ratedMovieObj = movieRepository.findByImdbId(ratedMovie);

        if (ratedMovieObj.isEmpty()) {
            throw new IllegalArgumentException("rated movie not found");
        }


        // retrieve the movies from the database usiign the provided list

        List<Movie> movieObjects = movieRepository.findByImdbIdIn(movies);

        // calculate the similarity based on the rated movie adn other

        double commonGenreCount=0.0;
        double totalGenreCount=0.0;
        double weightedCommonGenreCount =0.0;

        // iterate Through each movie and compare its genres and ratings
        for (Movie movie : movieObjects) {
            if (movie.getImdbId().equals(ratedMovie)) {
                continue; // SKip the rated movie itself
            }

            Set<String> commonGenres = new HashSet<>(ratedMovieObj.get().getGenres());
            Set<String> movieGenres = new HashSet<>(movie.getGenres());


            // we need to calculate the intersection of genres(common genres)

            commonGenres.retainAll(movieGenres);

            commonGenreCount+= commonGenres.size();


            // calculate the union of genres(total genres

            Set<String> totalGenres = new HashSet<>(ratedMovieObj.get().getGenres());

            totalGenres.addAll(movieGenres);
            totalGenreCount+= totalGenres.size();
            weightedCommonGenreCount+= commonGenres.size()*rating;









        }

        double similarityValue=0.0;

        if (totalGenreCount != 0) {
            similarityValue = weightedCommonGenreCount/totalGenreCount;

        }


        log.info("similarity score should not be 0 " + similarityValue);
        return similarityValue;


    }
}
