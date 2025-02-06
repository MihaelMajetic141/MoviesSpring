package hr.java.moviesspring.service;

import hr.java.moviesspring.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface MovieService {
    Page<Movie> getAllMovies(Pageable pageable);
    Optional<Movie> getMovieById(Long id);
    Optional<Page<Movie>> getMoviesByTitle(String title, Pageable pageable);
    Optional<Page<Movie>> getMoviesByGenresContaining(String genre, Pageable pageable);

}
