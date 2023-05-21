package dev.barahow.movies.repository;

import dev.barahow.movies.model.Movie;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends MongoRepository<Movie, ObjectId> {


    Optional<Movie> findByImdbId(String imdbId);

    List<Movie> findByImdbIdIn(List<String> movies);




    Optional<Movie> deleteByImdbId(String imdbId);

    Optional<Movie> findByTitleIgnoreCase(String title);

    List<Movie> findByGenresInIgnoreCase(List<String> genre);

}
