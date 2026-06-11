package ch.chattrix.authentificationservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
public class UserCredential {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userCredentialUuid;

    private UUID userUuid;

    private String email;

    private String passwordHash;

    private Date createdAt;

    private Date updatedAt;
}
