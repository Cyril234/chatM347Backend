package ch.chattrix.shared.redis.event;

import ch.chattrix.shared.dto.ChatDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatEditedEvent {
    private ChatDto chat;
}
