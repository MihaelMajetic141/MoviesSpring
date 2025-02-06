package hr.java.moviesspring.repository;

import hr.java.moviesspring.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> getMovieById(Long id);
    Optional<Page<Movie>> getMoviesByTitleContainingIgnoreCase(String title, Pageable pageable);
    Optional<Page<Movie>> getMoviesByGenresContainingIgnoreCase(String genre, Pageable pageable);
}