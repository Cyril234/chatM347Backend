package ch.chattrix.gatewayservice.rabbitmq;

import ch.chattrix.gatewayservice.aggregator.*;
import ch.chattrix.shared.event.BasicRabbitMqResultEvent;
import ch.chattrix.shared.event.GetAllUsersResultEvent;
import ch.chattrix.shared.event.LoginResultEvent;
import ch.chattrix.shared.event.RefreshTokenResultEvent;
import ch.chattrix.shared.rabbitmq.Queues;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitResultListener {

    private final RegistrationAggregator registrationAggregator;
    private final LoginAggregator loginAggregator;
    private final ObjectMapper objectMapper;
    private final LogoutAggregator logoutAggregator;
    private final RefreshTokenAggregator refreshTokenAggregator;
    private final GetAllUsersAggregator getAllUsersAggregator;

    public RabbitResultListener(
            RegistrationAggregator registrationAggregator,
            LoginAggregator loginAggregator,
            ObjectMapper objectMapper,
            RefreshTokenAggregator refreshTokenAggregator,
            LogoutAggregator logoutAggregator,
            GetAllUsersAggregator getAllUsersAggregator) {
        this.registrationAggregator = registrationAggregator;
        this.loginAggregator = loginAggregator;
        this.objectMapper = objectMapper;
        this.logoutAggregator = logoutAggregator;
        this.refreshTokenAggregator = refreshTokenAggregator;
        this.getAllUsersAggregator = getAllUsersAggregator;
    }

    @RabbitListener(queues = Queues.AUTH_REGISTER_RESULT_QUEUE)
    public void handleAuthRegister(Message message) throws Exception {
        String correlationId =
                message.getMessageProperties().getCorrelationId();

        if (correlationId == null) return;

        BasicRabbitMqResultEvent event =
                objectMapper.readValue(
                        message.getBody(),
                        BasicRabbitMqResultEvent.class
                );

        registrationAggregator.handleAuth(correlationId, event);
    }

    @RabbitListener(queues = Queues.USER_REGISTER_RESULT_QUEUE)
    public void handleUserCreate(Message message) throws Exception {

        String correlationId =
                message.getMessageProperties().getCorrelationId();

        if (correlationId == null) return;

        BasicRabbitMqResultEvent event =
                objectMapper.readValue(
                        message.getBody(),
                        BasicRabbitMqResultEvent.class
                );

        registrationAggregator.handleUser(correlationId, event);
    }

    @RabbitListener(queues = Queues.AUTH_LOGIN_RESULT_QUEUE)
    public void handleAuthLogin(Message message) throws Exception {

        String correlationId =
                message.getMessageProperties().getCorrelationId();

        if (correlationId == null) return;

        LoginResultEvent event =
                objectMapper.readValue(message.getBody(), LoginResultEvent.class);

        loginAggregator.completeLogin(correlationId, event);
    }

    @RabbitListener(queues = Queues.AUTH_REFRESH_RESULT_QUEUE)
    public void handleRefreshToken(Message message) throws Exception {

        String correlationId =
                message.getMessageProperties().getCorrelationId();

        if (correlationId == null) return;

        RefreshTokenResultEvent event =
                objectMapper.readValue(message.getBody(), RefreshTokenResultEvent.class);

        refreshTokenAggregator.completeRefreshToken(correlationId, event);
    }

    @RabbitListener(queues = Queues.AUTH_LOGOUT_RESULT_QUEUE)
    public void handleAuthLogout(Message message) throws Exception {

        String correlationId =
                message.getMessageProperties().getCorrelationId();

        if (correlationId == null) return;

        BasicRabbitMqResultEvent event =
                objectMapper.readValue(message.getBody(), BasicRabbitMqResultEvent.class);

        logoutAggregator.completeLogout(correlationId, event);
    }

    @RabbitListener(queues = Queues.USER_GET_ALL_RESULT_QUEUE)
    public void handleGetAllUsers(Message message) throws Exception {

        String correlationId =
                message.getMessageProperties().getCorrelationId();

        if (correlationId == null) return;

        GetAllUsersResultEvent event =
                objectMapper.readValue(message.getBody(), GetAllUsersResultEvent.class);

        getAllUsersAggregator.completeGetAllUsers(correlationId, event);
    }
}