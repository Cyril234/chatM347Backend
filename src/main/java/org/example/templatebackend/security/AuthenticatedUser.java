package org.example.templatebackend.security;

public record AuthenticatedUser(Integer id, String username, String email) {
}
