package ch.chattrix.chatservice.repository;

import ch.chattrix.chatservice.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByChatUuid(UUID chatUuid);
}