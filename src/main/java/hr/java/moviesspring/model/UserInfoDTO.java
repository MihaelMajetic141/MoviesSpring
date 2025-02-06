package hr.java.moviesspring.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class UserInfoDTO {
    private String username;
    private String email;
    private String profilePicture;
    private List<Movie> watchLaterMovies;
    private List<Movie> likedMovies;
}
