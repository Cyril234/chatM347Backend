package ch.chattrix.chatservice.repository;

import ch.chattrix.chatservice.model.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRepository extends MongoRepository<Chat, String> {
}