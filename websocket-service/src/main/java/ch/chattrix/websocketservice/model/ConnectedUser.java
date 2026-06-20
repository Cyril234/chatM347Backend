package ch.chattrix.websocketservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectedUser {
    private UUID connectedUserUuid;
    private UUID userUuid;
    private Date connectedAt;
    private Date lastHeartBeat;
}