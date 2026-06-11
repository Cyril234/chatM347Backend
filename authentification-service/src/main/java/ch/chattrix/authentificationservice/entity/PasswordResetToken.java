package ch.chattrix.authentificationservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID passwordResetTokenUuid;

    private UUID userUuid;

    private String token;

    private Date expiresAt;

    private Boolean used;
}
