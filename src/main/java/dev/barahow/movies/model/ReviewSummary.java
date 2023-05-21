package dev.barahow.movies.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "review_summary")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewSummary {

    @Id
    private ObjectId id;
    private String userEmail;
    private String movieImdbId;
    private double rating;
    private String body;




}
