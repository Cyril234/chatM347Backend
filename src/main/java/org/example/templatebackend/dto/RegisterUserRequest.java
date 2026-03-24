package org.example.templatebackend.dto;

public record RegisterUserRequest(String displayName, String eMail, String password) {
}
