package hr.java.moviesspring.model.payload.response;

import hr.java.moviesspring.model.UserInfoDTO;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class AuthResponse {
    private JwtResponse jwtResponse;
    private UserInfoDTO userInfo;
}
