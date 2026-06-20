package ch.chattrix.shared.redis.event;

import ch.chattrix.shared.enums.ChatType;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatCreatedEvent {
    private UUID chatUuid;
    private String name;
    private ChatType chatType;
    private UUID creatorUuid;
    private List<UUID> memberUuids;
    private long createdAt;
}