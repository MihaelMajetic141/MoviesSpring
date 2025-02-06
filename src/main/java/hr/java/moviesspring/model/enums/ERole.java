package hr.java.moviesspring.model.enums;

public enum ERole {
    ROLE_USER(1L, "ROLE_USER"),
    ROLE_ADMIN(2L, "ROLE_ADMIN"),;

    ERole(Long id, String name) {}
}
