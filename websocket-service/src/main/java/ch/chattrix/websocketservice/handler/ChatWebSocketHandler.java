package ch.chattrix.websocketservice.handler;

import ch.chattrix.shared.enums.ChatType;
import ch.chattrix.shared.redis.event.ChatCreateEvent;
import ch.chattrix.websocketservice.redis.ChatCreatedListener;
import ch.chattrix.websocketservice.redis.ChatMessagePublisher;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ChatMessagePublisher publisher;
    private final ChatCreatedListener chatCreatedListener;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        chatCreatedListener.register(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        chatCreatedListener.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        JsonNode node = objectMapper.readTree(message.getPayload());

        String eventType = node.get("type").asText();

        if ("CREATE_CHAT".equals(eventType)) {

            UUID creatorUuid = (UUID) session.getAttributes().get("userUuid");

            if (creatorUuid == null) {
                throw new IllegalStateException("User not authenticated in WebSocket session");
            }

            List<UUID> memberUuids = objectMapper.convertValue(
                    node.get("memberUuids"),
                    objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, UUID.class)
            );

            if (!memberUuids.contains(creatorUuid)) {
                memberUuids.add(creatorUuid);
            }

            ChatCreateEvent event = ChatCreateEvent.builder()
                    .name(node.get("name").asText())
                    .chatType(ChatType.valueOf(node.get("chatType").asText()))
                    .creatorUuid(creatorUuid)
                    .memberUuids(memberUuids)
                    .timestamp(System.currentTimeMillis())
                    .build();

            publisher.createChat(event);
        }
    }
}