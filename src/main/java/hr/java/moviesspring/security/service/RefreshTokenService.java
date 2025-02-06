package hr.java.moviesspring.security.service;

import hr.java.moviesspring.model.RefreshToken;
import hr.java.moviesspring.model.UserInfo;
import hr.java.moviesspring.security.repository.RefreshTokenRepository;
import hr.java.moviesspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    UserRepository userRepository;

    public RefreshToken createRefreshToken(String email) {
        Optional<UserInfo> userInfo = userRepository.findByEmail(email);
        if (userInfo.isPresent()) {
            RefreshToken refreshToken = RefreshToken.builder()
                    .user(userInfo.get())
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusMillis(600000))
                    .build();
            Optional<RefreshToken> existingToken = findByUser(userInfo.get());
            if (existingToken.isPresent()) {
                existingToken.get().setExpiryDate(refreshToken.getExpiryDate());
                existingToken.get().setUser(refreshToken.getUser());

                Optional<RefreshToken> deletedToken = deleteRefreshToken(refreshToken.getToken());
                if (deletedToken.isPresent()) {
                    return refreshTokenRepository.save(existingToken.get());
                }
            } else {
                return refreshTokenRepository.save(refreshToken);
            }
        }
        return null;
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }
    public Optional<RefreshToken> findByUser(UserInfo user){ return refreshTokenRepository.findByUserId(user.getId());}
    public Optional<RefreshToken> deleteRefreshToken(String token) {
        return refreshTokenRepository.deleteRefreshTokenByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh refreshToken is expired. Please make a new login..!");
        }
        return token;
    }

}
