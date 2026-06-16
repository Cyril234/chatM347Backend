package ch.chattrix.gatewayservice.rabbitmq;

import ch.chattrix.gatewayservice.aggregator.LoginAggregator;
import ch.chattrix.gatewayservice.aggregator.RegistrationAggregator;
import ch.chattrix.shared.event.RabbitMqResultEvent;
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

    public RabbitResultListener(
            RegistrationAggregator registrationAggregator,
            LoginAggregator loginAggregator,
            ObjectMapper objectMapper
    ) {
        this.registrationAggregator = registrationAggregator;
        this.loginAggregator = loginAggregator;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = Queues.AUTH_REGISTER_RESULT_QUEUE)
    public void handleAuthRegister(Message message) throws Exception {

        String correlationId =
                message.getMessageProperties().getCorrelationId();

        if (correlationId == null) return;

        RabbitMqResultEvent event =
                objectMapper.readValue(
                        message.getBody(),
                        RabbitMqResultEvent.class
                );

        registrationAggregator.handleAuth(correlationId, event);
    }

    @RabbitListener(queues = Queues.USER_CREATE_RESULT_QUEUE)
    public void handleUserCreate(Message message) throws Exception {

        String correlationId =
                message.getMessageProperties().getCorrelationId();

        if (correlationId == null) return;

        RabbitMqResultEvent event =
                objectMapper.readValue(
                        message.getBody(),
                        RabbitMqResultEvent.class
                );

        registrationAggregator.handleUser(correlationId, event);
    }

    @RabbitListener(queues = Queues.AUTH_LOGIN_RESULT_QUEUE)
    public void handleAuthLogin(Message message) throws Exception {

        String correlationId =
                message.getMessageProperties().getCorrelationId();

        if (correlationId == null) return;

        RabbitMqResultEvent event =
                objectMapper.readValue(message.getBody(), RabbitMqResultEvent.class);

        loginAggregator.completeLogin(correlationId, event);
    }
}