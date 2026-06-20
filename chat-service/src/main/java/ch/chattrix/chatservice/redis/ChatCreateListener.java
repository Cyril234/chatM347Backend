package ch.chattrix.chatservice.redis;

import ch.chattrix.chatservice.model.Chat;
import ch.chattrix.chatservice.repository.ChatRepository;
import ch.chattrix.shared.redis.channel.RedisChannels;
import ch.chattrix.shared.redis.event.ChatCreateEvent;
import ch.chattrix.shared.redis.event.ChatCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChatCreateListener implements MessageListener {

    private final ChatRepository chatRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(org.springframework.data.redis.connection.Message redisMessage, byte[] pattern) {
        try {
            String body = new String(redisMessage.getBody());
            ChatCreateEvent event = objectMapper.readValue(body, ChatCreateEvent.class);

            Chat chat = new Chat();
            chat.setChatUuid(UUID.randomUUID());
            chat.setName(event.getName());
            chat.setCreatorUuid(event.getCreatorUuid());
            chat.setMemberUuids(event.getMemberUuids());
            chat.setCreatedAt(new Date(event.getTimestamp()));

            Chat saved = chatRepository.save(chat);

            ChatCreatedEvent createdEvent = ChatCreatedEvent.builder()
                    .chatUuid(saved.getChatUuid())
                    .name(saved.getName())
                    .creatorUuid(saved.getCreatorUuid())
                    .memberUuids(saved.getMemberUuids())
                    .createdAt(saved.getCreatedAt().getTime())
                    .build();

            redisTemplate.convertAndSend(
                    RedisChannels.CHAT_CREATED,
                    objectMapper.writeValueAsString(createdEvent)
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}