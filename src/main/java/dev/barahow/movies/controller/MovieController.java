package dev.barahow.movies.controller;

import dev.barahow.movies.model.Movie;
import dev.barahow.movies.service.MovieService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/movies")
public class MovieController {
    @Autowired
    private final MovieService movieService;



    @GetMapping
    @Cacheable("getAllMovies")
    public ResponseEntity<Page<Movie>> getAllMovies(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) throws Exception {


        Page<Movie> movies =movieService.allMovies(PageRequest.of(page,size));

        return ResponseEntity.ok().body(movies);
    }


    @GetMapping("/{imdbId}")
    public ResponseEntity<Optional<Movie>> getSingleMovie(@PathVariable String imdbId) {



        return new ResponseEntity<Optional<Movie>>(movieService.singleMovie(imdbId), HttpStatus.OK);
    }

    @GetMapping("/title/{title}")
    @Cacheable("getMovieByTitle")
    public ResponseEntity<Optional<Movie>> getMovieByTitle(@PathVariable String title) {
        try{
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8)
                    // handle the spaces in movie titles
                    .replace("+","%20");
            log.info("encoded title {}",encodedTitle);

            // turn the encoding normal here
            String decodedTitle = URLDecoder.decode(encodedTitle,StandardCharsets.UTF_8);
            log.info("decoded title {}",decodedTitle);

            Optional<Movie> movie = movieService.getMovieByTitle(decodedTitle);
            if (movie.isPresent()) {

                return new ResponseEntity<Optional<Movie>>(movie, HttpStatus.OK);
            }else {
                return ResponseEntity.notFound().build();

            }


        }catch (Exception ex) {
            log.error("couldnt encode the title for the movie  "+ex.getMessage());

            return ResponseEntity.internalServerError().build();

        }


    }


    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Movie>> getMovieByGenre(@PathVariable List<String> genre) {

      List<Movie> movie=  movieService.getMovieByGenre(genre);

      if (movie.isEmpty()) {
          log.info("The genre doesnt match any movies");
          return ResponseEntity.notFound().build();

      }

        return new ResponseEntity<List<Movie>>(movie,HttpStatus.OK);

    }

    @DeleteMapping("/{imdbId}")
    public ResponseEntity<Optional<Movie>> deleteSingleMovie(@PathVariable String imdbId) {
        return new ResponseEntity<Optional<Movie>>(movieService.deleteMovie(imdbId), HttpStatus.OK);
    }

    @PutMapping("/{imdbId}")
    public ResponseEntity<Movie> updateMovie(@PathVariable String imdbId, @RequestBody Movie updatedMovie) {
        Optional<Movie> optionalMovie = movieService.updateMovie(imdbId, updatedMovie);
        if (optionalMovie.isPresent()) {
            return ResponseEntity.ok(optionalMovie.get());
        } else {
            return ResponseEntity.notFound().build();
        }

    }
}

