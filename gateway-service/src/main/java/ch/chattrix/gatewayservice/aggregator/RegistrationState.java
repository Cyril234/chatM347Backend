package ch.chattrix.gatewayservice.aggregator;

import ch.chattrix.shared.event.user.AuthenticationRegisterResultEvent;
import ch.chattrix.shared.event.user.UserProfileResultEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationState {
    private AuthenticationRegisterResultEvent auth;
    private UserProfileResultEvent user;
}