package ch.chattrix.chatservice.redis;

import ch.chattrix.chatservice.model.Message;
import ch.chattrix.chatservice.repository.MessageRepository;
import ch.chattrix.shared.redis.channel.RedisChannels;
import ch.chattrix.shared.redis.event.ChatMessageEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class ChatMessageListener implements MessageListener {

    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void onMessage(org.springframework.data.redis.connection.Message redisMessage, byte[] pattern) {
        try {
            String body = new String(redisMessage.getBody());
            ChatMessageEvent event = objectMapper.readValue(body, ChatMessageEvent.class);

            Message msg = new Message();
            msg.setMessageUuid(event.getMessageUuid());
            msg.setChatUuid(event.getChatUuid());
            msg.setSenderUuid(event.getSenderUuid());
            msg.setContent(event.getContent());
            msg.setCreatedAt(new Date(event.getTimestamp()));

            messageRepository.save(msg);

            redisTemplate.convertAndSend(
                    RedisChannels.MESSAGE_SAVED,
                    objectMapper.writeValueAsString(event)
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}