package dev.barahow.movies.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class MovieRating {


    private double totalReviews;
    private double totalRating;
    private double averageRating;

    public double getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(double totalReviews) {
        this.totalReviews = totalReviews;
    }

    public double getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(double totalRating) {
        this.totalRating = totalRating;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}