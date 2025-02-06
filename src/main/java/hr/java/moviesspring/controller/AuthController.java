package hr.java.moviesspring.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import hr.java.moviesspring.model.RefreshToken;
import hr.java.moviesspring.model.Role;
import hr.java.moviesspring.model.UserInfo;
import hr.java.moviesspring.model.UserInfoDTO;
import hr.java.moviesspring.model.enums.ERole;
import hr.java.moviesspring.model.payload.request.LoginRequest;
import hr.java.moviesspring.model.payload.request.RefreshTokenRequest;
import hr.java.moviesspring.model.payload.request.RegistrationRequest;
import hr.java.moviesspring.model.payload.response.AuthResponse;
import hr.java.moviesspring.model.payload.response.JwtResponse;
import hr.java.moviesspring.repository.RoleRepository;
import hr.java.moviesspring.repository.UserRepository;
import hr.java.moviesspring.security.jwt.JwtService;
import hr.java.moviesspring.security.service.RefreshTokenService;
import hr.java.moviesspring.security.service.google.GoogleAuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private GoogleAuthService googleAuthService;
    @Autowired
    RefreshTokenService refreshTokenService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtService jwtService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegistrationRequest registrationRequest
    ) {
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
        Optional<Role> basicRole = roleRepository.findByName(ERole.valueOf("ROLE_USER"));
        Set<Role> basicRoleSet = new HashSet<>();
        basicRoleSet.add(basicRole.get());

        UserInfo user = UserInfo.builder()
                .email(registrationRequest.getEmail())
                .username(registrationRequest.getUsername())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .roles(basicRoleSet)
                .likedMovies(new HashSet<>())
                .watchLaterMovies(new HashSet<>())
                .build();
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        Optional<UserInfo> user = userRepository.findByUsername(loginRequest.getUsername());
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Invalid user request!");
        }

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword())
                );

        if (authentication.isAuthenticated()) {
            UserInfoDTO userDTO = UserInfoDTO.builder()
                    .username(user.get().getUsername())
                    .email(user.get().getEmail())
                    .profilePicture(user.get().getProfilePicture())
                    .build();
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.get().getEmail());
            JwtResponse jwtResponse = JwtResponse.builder()
                    .accessToken(jwtService.generateToken(userDTO.getEmail()))
                    .refreshToken(refreshToken.getToken())
                    .build();
            return ResponseEntity.ok().body(
                    AuthResponse.builder()
                            .jwtResponse(jwtResponse)
                            .userInfo(userDTO)
                            .build());
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(
            @RequestBody Map<String, String> request
    ) {
        String idToken = request.get("idToken");
        GoogleIdToken.Payload payload = googleAuthService.verifyToken(idToken);
        if (payload == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID refreshToken");
        }

        String userEmail = payload.getEmail();
        String userName = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");

        Optional<Role> basicRole = roleRepository.findByName(ERole.valueOf("ROLE_USER"));
        Set<Role> basicRoleSet = new HashSet<>();

        Optional<UserInfo> existingUser = userRepository.findByEmail(userEmail);

        UserInfo user;
        if (existingUser.isPresent()) {
            UserInfo oldUser = UserInfo.builder()
                    .id(existingUser.get().getId())
                    .username(existingUser.get().getUsername())
                    .email(existingUser.get().getEmail())
                    .password(existingUser.get().getPassword())
                    .roles(existingUser.get().getRoles())
                    .profilePicture(pictureUrl)
                    .likedMovies(existingUser.get().getLikedMovies())
                    .watchLaterMovies(existingUser.get().getWatchLaterMovies())
                    .build();
            user = userRepository.save(oldUser);
        } else {
            basicRoleSet.add(basicRole.get());
            UserInfo newUser = UserInfo.builder()
                    .username(userName)
                    .email(userEmail)
                    .password("")
                    .roles(basicRoleSet)
                    .profilePicture(pictureUrl)
                    .likedMovies(new HashSet<>())
                    .watchLaterMovies(new HashSet<>())
                    .build();
            user = userRepository.save(newUser);
        }

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userEmail);
        JwtResponse jwtResponse = JwtResponse.builder()
            .accessToken(jwtService.generateToken(userEmail))
            .refreshToken(refreshToken.getToken())
            .build();
        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .build();

        return ResponseEntity.ok().body(
                AuthResponse.builder()
                        .jwtResponse(jwtResponse)
                        .userInfo(userInfoDTO)
                        .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(
            @RequestBody String refreshToken
    ) {
        Optional<RefreshToken> deletedRefreshToken = refreshTokenService.deleteRefreshToken(refreshToken);
        if(deletedRefreshToken.isPresent()) {
            return ResponseEntity.ok().body("You've been signed out!");
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refreshToken");
    }

    @PostMapping("/refreshToken")
    public JwtResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        return refreshTokenService.findByToken(refreshTokenRequest.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(userInfo -> {
                    String accessToken = jwtService.generateToken(userInfo.getEmail());
                    return JwtResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshTokenRequest.getToken()).build();
                }).orElseThrow(() -> new RuntimeException("Refresh Token is not in database!"));
    }

}
