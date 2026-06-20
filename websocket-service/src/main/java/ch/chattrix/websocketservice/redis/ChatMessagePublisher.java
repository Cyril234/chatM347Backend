package ch.chattrix.websocketservice.redis;

import ch.chattrix.shared.redis.channel.RedisChannels;
import ch.chattrix.shared.redis.event.ChatCreateEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessagePublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void createChat(ChatCreateEvent event) {
        try {
            redisTemplate.convertAndSend(
                    RedisChannels.CHAT_CREATE,
                    objectMapper.writeValueAsString(event)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}