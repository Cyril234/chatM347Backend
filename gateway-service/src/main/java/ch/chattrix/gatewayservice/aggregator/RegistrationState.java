package ch.chattrix.gatewayservice.aggregator;

import ch.chattrix.shared.event.RabbitMqResultEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationState {
    private RabbitMqResultEvent auth;
    private RabbitMqResultEvent user;
}