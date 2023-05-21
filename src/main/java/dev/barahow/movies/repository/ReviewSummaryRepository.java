package dev.barahow.movies.repository;

import dev.barahow.movies.model.ReviewSummary;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface ReviewSummaryRepository extends MongoRepository<ReviewSummary,ObjectId> {

    List<ReviewSummary> findByMovieImdbId(String imdbId);

    Optional<ReviewSummary> findById(ObjectId id);
    List<ReviewSummary> findByUserEmail(String email);



}
