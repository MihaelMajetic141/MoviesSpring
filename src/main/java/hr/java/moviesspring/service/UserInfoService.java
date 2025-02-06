package hr.java.moviesspring.service;

import hr.java.moviesspring.model.Movie;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public interface UserInfoService {
    Optional<Movie> addMovieToWatchLater(String username, Long movieId);
    Optional<Movie> addMovieToLikedList(String username, Long movieId);
    Optional<Movie> removeMovieFromWatchLater(String username, Long movieId);
    Optional<Movie> removeMovieFromLikedList(String username, Long movieId);
    List<Movie> getWatchLaterMovies(String username);
    List<Movie> getLikedMovies(String username);
}
