package ch.chattrix.authenticationservice.rabbitmq;

import ch.chattrix.authenticationservice.service.AuthenticationService;
import ch.chattrix.shared.command.user.AuthenticationRegisterCommand;
import ch.chattrix.shared.command.user.UserLoginCommand;
import ch.chattrix.shared.event.RabbitMqResultEvent;
import ch.chattrix.shared.rabbitmq.Exchanges;
import ch.chattrix.shared.rabbitmq.Queues;
import ch.chattrix.shared.rabbitmq.RoutingKeys;
import ch.chattrix.shared.response.ApiResponse;
import ch.chattrix.shared.types.LoginData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationListener {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;
    private final AuthenticationService authService;

    public AuthenticationListener(ObjectMapper objectMapper,
                                  RabbitTemplate rabbitTemplate,
                                  AuthenticationService authService) {
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.authService = authService;
    }

    @RabbitListener(queues = Queues.AUTH_REGISTER_QUEUE)
    public void handleRegister(Message message) {

        String correlationId = message.getMessageProperties().getCorrelationId();
        if (correlationId == null) return;

        try {
            AuthenticationRegisterCommand command =
                    objectMapper.readValue(message.getBody(), AuthenticationRegisterCommand.class);

            ApiResponse<Void> serviceResponse =
                    authService.register(
                            command.getEmail(),
                            command.getPassword(),
                            command.getUserUuid()
                    );

            RabbitMqResultEvent result = new RabbitMqResultEvent();
            result.setSuccess(serviceResponse.isSuccess());
            result.setErrorMessage(serviceResponse.isSuccess() ? null : serviceResponse.getMessage());

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.AUTH_RESULT_REGISTER,
                    result,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );

        } catch (Exception e) {

            RabbitMqResultEvent result = new RabbitMqResultEvent();
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage() != null ? e.getMessage() : "UNKNOWN_ERROR");

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.AUTH_RESULT_REGISTER,
                    result,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );
        }
    }

    @RabbitListener(queues = Queues.AUTH_LOGIN_QUEUE)
    public void handleLogin(Message message) {

        String correlationId = message.getMessageProperties().getCorrelationId();

        try {
            UserLoginCommand command =
                    objectMapper.readValue(message.getBody(), UserLoginCommand.class);

            ApiResponse<LoginData> serviceResponse =
                    authService.login(command.getEmail(), command.getPassword());

            RabbitMqResultEvent result = new RabbitMqResultEvent();
            result.setSuccess(serviceResponse.isSuccess());
            result.setErrorMessage(
                    serviceResponse.isSuccess()
                            ? null
                            : serviceResponse.getMessage()
            );

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.AUTH_RESULT_LOGIN,
                    result,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );

        } catch (Exception e) {

            RabbitMqResultEvent result = new RabbitMqResultEvent();
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());

            rabbitTemplate.convertAndSend(
                    Exchanges.USER_RESPONSE,
                    RoutingKeys.AUTH_RESULT_LOGIN,
                    result,
                    msg -> {
                        msg.getMessageProperties().setCorrelationId(correlationId);
                        return msg;
                    }
            );
        }
    }
}