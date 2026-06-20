package ch.chattrix.websocketservice.redis;

import ch.chattrix.shared.redis.event.ChatCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@RequiredArgsConstructor
public class ChatCreatedListener implements MessageListener {

    private final ObjectMapper objectMapper;

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    public void register(WebSocketSession session) {
        sessions.add(session);
    }

    public void remove(WebSocketSession session) {
        sessions.remove(session);
    }

    @Override
    public void onMessage(org.springframework.data.redis.connection.Message message, byte[] pattern) {
        try {
            String body = new String(message.getBody());
            ChatCreatedEvent event = objectMapper.readValue(body, ChatCreatedEvent.class);

            String payload = objectMapper.writeValueAsString(event);

            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(payload));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}