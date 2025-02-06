package hr.java.moviesspring.service;

import hr.java.moviesspring.model.Movie;
import hr.java.moviesspring.model.UserInfo;
import hr.java.moviesspring.repository.MovieRepository;
import hr.java.moviesspring.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserInfoServiceImplementation implements UserInfoService {
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    @Override
    public Optional<Movie> addMovieToWatchLater(String username, Long movieId) {
        UserInfo user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        user.getWatchLaterMovies().add(movie);
        userRepository.save(user);

        return Optional.of(movie);
    }

    @Override
    public Optional<Movie> addMovieToLikedList(String username, Long movieId) {
        UserInfo user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        user.getLikedMovies().add(movie);
        userRepository.save(user);

        return Optional.of(movie);
    }

    @Override
    @Transactional
    public Optional<Movie> removeMovieFromWatchLater(String username, Long movieId) {
        UserInfo user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Optional<Movie> movie = movieRepository.getMovieById(movieId);
        Set<Movie> watchLaterMovies = user.getWatchLaterMovies();
        if (movie.isPresent()) {
            watchLaterMovies.removeIf(m -> m.getId().equals(movieId));
            user.setWatchLaterMovies(watchLaterMovies);
            userRepository.save(user);
            return movie;
        } else return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Movie> removeMovieFromLikedList(String username, Long movieId) {
        UserInfo user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Optional<Movie> movie = movieRepository.getMovieById(movieId);
        Set<Movie> likedMovies = user.getLikedMovies();
        if (movie.isPresent()) {
            likedMovies.removeIf(m -> m.getId().equals(movieId));
            user.setLikedMovies(likedMovies);
            userRepository.save(user);
            return movie;
        } else return Optional.empty();
    }

    @Override
    public List<Movie> getWatchLaterMovies(String username) {
        UserInfo user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Set<Movie> watchLaterMovies = user.getWatchLaterMovies();
        return watchLaterMovies.stream().toList();
    }

    @Override
    public List<Movie> getLikedMovies(String username) {
        UserInfo user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Set<Movie> likedMovies = user.getLikedMovies();
        return likedMovies.stream().toList();
    }
}
