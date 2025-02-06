package hr.java.moviesspring.service;

import hr.java.moviesspring.model.Movie;
import hr.java.moviesspring.repository.MovieRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MovieServiceImplementation implements MovieService {

    private MovieRepository movieRepository;

    @Override
    public Page<Movie> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable);
    }

    @Override
    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.getMovieById(id);
    }

    @Override
    public Optional<Page<Movie>> getMoviesByTitle(String title, Pageable pageable) {
        return movieRepository.getMoviesByTitleContainingIgnoreCase(title, pageable);
    }

    @Override
    public Optional<Page<Movie>> getMoviesByGenresContaining(String genre, Pageable pageable) {
        return movieRepository.getMoviesByGenresContainingIgnoreCase(genre, pageable);
    }

}
