package org.example.templatebackend.config;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() {
        super("E-Mail existiert bereits");
    }
}