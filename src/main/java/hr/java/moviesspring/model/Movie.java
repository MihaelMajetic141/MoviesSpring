package hr.java.moviesspring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "movie_url")
    private String movieUrl;

    @Column(name = "title")
    private String title;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "release_year")
    private Integer releaseYear;

    @Column(name = "length_min")
    private Float lengthMin;

    @Column(name = "imdb_rating")
    private Float imdbRating;

    @Column(name = "rating_count")
    private Float ratingCount;

    @Column(name = "plot", columnDefinition = "TEXT")
    private String plot;

    @Column(name = "directors")
    private String directors;

    @Column(name = "writers")
    private String writers;

    @Column(name = "stars")
    private String stars;

    @Column(name = "genres")
    private String genres;
}
