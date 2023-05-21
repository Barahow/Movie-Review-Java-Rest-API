package dev.barahow.movies.repository;

import dev.barahow.movies.model.Review;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<Review, ObjectId> {

    Optional<Review> findByMovieImdbId(String imdbId);

    Optional<Review> findById(ObjectId id);

    Review findByBody(String body);

    Review findByUserEmail(String email);




}
