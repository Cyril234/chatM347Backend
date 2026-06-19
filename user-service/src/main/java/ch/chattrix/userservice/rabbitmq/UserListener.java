package ch.chattrix.userservice.rabbitmq;

import ch.chattrix.shared.command.BasicCommand;
import ch.chattrix.shared.command.UserRegisterCommand;
import ch.chattrix.shared.event.BasicRabbitMqResultEvent;
import ch.chattrix.shared.event.GetAllUsersResultEvent;
import ch.chattrix.shared.rabbitmq.Exchanges;
import ch.chattrix.shared.rabbitmq.Queues;
import ch.chattrix.shared.rabbitmq.RoutingKeys;
import ch.chattrix.shared.response.ApiResponse;
import ch.chattrix.shared.types.UserData;
import ch.chattrix.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserListener {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final UserService userService;

    public UserListener(ObjectMapper objectMapper,
                        RabbitTemplate rabbitTemplate,
                        UserService userService) {
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.userService = userService;
    }

    @RabbitListener(queues = Queues.USER_REGISTER_QUEUE)
    public void handleUserCreate(Message message) {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        try {
            UserRegisterCommand command =
                    objectMapper.readValue(message.getBody(), UserRegisterCommand.class);

            ApiResponse<Void> serviceResponse =
                    userService.create(command.getUsername(), command.getUserUuid());

            BasicRabbitMqResultEvent event = new BasicRabbitMqResultEvent();
            event.setSuccess(serviceResponse.isSuccess());
            event.setErrorMessage(
                    serviceResponse.isSuccess()
                            ? null
                            : serviceResponse.getMessage()
            );

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.USER_RESULT_REGISTER,
                    event,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );

        } catch (Exception e) {

            BasicRabbitMqResultEvent event = new BasicRabbitMqResultEvent();
            event.setSuccess(false);
            event.setErrorMessage(
                    e.getMessage() != null ? e.getMessage() : "UNKNOWN_ERROR"
            );

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.USER_RESULT_REGISTER,
                    event,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );
        }
    }

    @RabbitListener(queues = Queues.USER_GET_ALL_QUEUE)
    public void handleGetAllUsers(Message message) {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        try {
            ApiResponse<List<UserData>> serviceResponse =
                    userService.getAll();

            GetAllUsersResultEvent event = new GetAllUsersResultEvent();
            event.setSuccess(serviceResponse.isSuccess());
            event.setErrorMessage(
                    serviceResponse.isSuccess()
                            ? null
                            : serviceResponse.getMessage()
            );
            event.setUsers(serviceResponse.getData());

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.USER_RESULT_GET_ALL,
                    event,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );

        } catch (Exception e) {

            GetAllUsersResultEvent event = new GetAllUsersResultEvent();
            event.setSuccess(false);
            event.setErrorMessage(
                    e.getMessage() != null ? e.getMessage() : "UNKNOWN_ERROR"
            );
            event.setUsers(null);

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.USER_RESULT_GET_ALL,
                    event,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );
        }
    }
}