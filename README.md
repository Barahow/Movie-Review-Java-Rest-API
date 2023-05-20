# Movie-Review-Java-Rest-API
The Movie Review API is a Java Spring Boot RESTful web service for managing movie reviews. It supports CRUD operations on movies, users, and reviews, with authentication, authorization, and security. Includes a movie recommendation system based on user preferences.



The Movie Review API is a Java Spring Boot RESTful web service designed to manage movie reviews and provide various functionalities related to movies, users, and recommendations. It allows users to perform CRUD operations on movies, users, and reviews, while also providing authentication, authorization, and security features. Additionally, the API includes a movie recommendation system based on user preferences.

**Functionality:**

Movies: CRUD operations for movie records (title, genre, release year, director, cast).
Users: User registration, login, and profile management (username, email).
Reviews: Creation, retrieval, update, and deletion of movie reviews (movie ID, user ID, rating, comments).
Authentication and Authorization: Token-based authentication and role-based access control.
Security: Ensures secure access to sensitive endpoints based on user roles.
Movie Recommendations: Generates personalized movie recommendations based on user preferences.
Additional Features:

**Input Validation:**
Validates user input for security and data integrity.
Pagination: Optimizes performance for data-heavy endpoints.
Error Handling: Provides meaningful feedback for errors or invalid requests.
Search: Allows users to search for movies based on various criteria.
Rating and Average Rating Calculation: Users can rate movies, and the API calculates the average rating.
Sorting and Filtering: Enables sorting and filtering of movie and review lists.
Commenting on Reviews: Users can leave comments on movie reviews.
User Profile Management: Users can view and update their profile information.
Social Sharing and Integration: Allows sharing movie details and ratings on social media platforms.
Notifications: Sends notifications to users about new reviews, ratings, or comments.

**Technologies used:**

MongoDB: Used as the database to store movie, user, and review data.
Redis Server: Utilized for caching purposes to enhance performance.
