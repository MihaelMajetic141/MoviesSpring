package hr.java.moviesspring.controller;

import hr.java.moviesspring.model.Movie;
import hr.java.moviesspring.service.MovieService;
import hr.java.moviesspring.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;


@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/movies")
public class MovieController {
    @Autowired
    private MovieService movieService;
    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/browse")
    public ResponseEntity<?> getAllMovies(
            @RequestParam(defaultValue = "0") int page
    ) {
        Pageable pageable = PageRequest.of(page, 20,
                Sort.by("releaseYear")
                        .and(Sort.by("id").descending()));
        Page<Movie> moviePage = movieService.getAllMovies(pageable);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(moviePage);
    }

    @GetMapping("")
    public ResponseEntity<?> getMovieById(
            @RequestParam Long id
    ) {
        Optional<Movie> movie = movieService.getMovieById(id);
        if (movie.isPresent()) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(movie);
        } else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Optional.empty());
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<?> getMoviesContainingTitle(
            @PathVariable String title,
            @RequestParam(defaultValue = "0") int page
    ) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("title")
                .and(Sort.by("id").ascending()));
        Optional<Page<Movie>> movies = movieService.getMoviesByTitle(title, pageable);
        if (movies.isPresent()) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(movies);
        } else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Optional.empty());
    }

   @GetMapping("/genre/{genre}")
    public ResponseEntity<?> getMoviesByGenre(
            @PathVariable String genre,
            @RequestParam(defaultValue = "0") int page
   ) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("imdbRating")
                .and(Sort.by("id").descending()));
        Optional<Page<Movie>> moviesInGenre = movieService.getMoviesByGenresContaining(genre, pageable);
        if (moviesInGenre.isPresent()) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(moviesInGenre);
        } else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Optional.empty());
   }

    @GetMapping("/topRated")
    public ResponseEntity<?> getTopRatedMovies(
            @RequestParam(defaultValue = "0") int page
    ) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("imdbRating")
                .and(Sort.by("id").descending()));
        Page<Movie> movies = movieService.getAllMovies(pageable);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(movies);
    }


   @GetMapping("/user/getWatchLaterMovies/{username}")
   public ResponseEntity<?> getWatchLaterMovies(
           @PathVariable String username
   ) {
        try {
            List<Movie> movieList = userInfoService.getWatchLaterMovies(username);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(movieList);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Optional.empty());
        }
   }

    @GetMapping("/user/getLikedMovies/{username}")
    public ResponseEntity<?> getLikedMovies(
            @PathVariable String username
    ) {
        try {
            List<Movie> movieList = userInfoService.getLikedMovies(username);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(movieList);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

   @PostMapping("/user/addMovieToLikedList")
   public ResponseEntity<?> addMovieToLikedList(
           @RequestParam String username,
           @RequestParam Long movieId
   ) {
        try {
            Optional<Movie> movie = userInfoService.addMovieToLikedList(username, movieId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(movie);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
   }

    @PostMapping("/user/addMovieToWatchLaterList")
    public ResponseEntity<?> addMovieToWatchLaterList(
            @RequestParam String username,
            @RequestParam Long movieId
    ) {
        try {
            Optional<Movie> movie = userInfoService.addMovieToWatchLater(username, movieId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(movie);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/user/removeMovieFromLikedList")
    public ResponseEntity<?> removeMovieFromLikedList(
            @RequestParam String username,
            @RequestParam Long movieId
    ) {
        try {
            Optional<Movie> movie = userInfoService.removeMovieFromLikedList(username, movieId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(movie);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/user/removeMovieFromWatchLaterList")
    public ResponseEntity<?> removeMovieFromWatchLaterList(
            @RequestParam String username,
            @RequestParam Long movieId
    ) {
        try {
            Optional<Movie> movie = userInfoService.removeMovieFromWatchLater(username, movieId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(movie);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
