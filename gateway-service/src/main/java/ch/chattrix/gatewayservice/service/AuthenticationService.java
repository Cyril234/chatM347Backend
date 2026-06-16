package ch.chattrix.gatewayservice.service;

import ch.chattrix.gatewayservice.aggregator.LoginAggregator;
import ch.chattrix.gatewayservice.aggregator.RegistrationAggregator;
import ch.chattrix.gatewayservice.rabbitmq.RabbitCommandPublisher;
import ch.chattrix.shared.command.user.AuthenticationRegisterCommand;
import ch.chattrix.shared.command.user.UserLoginCommand;
import ch.chattrix.shared.command.user.UserProfileCommand;
import ch.chattrix.shared.dto.user.LoginUserRequest;
import ch.chattrix.shared.dto.user.RegisterUserRequest;
import ch.chattrix.shared.response.ApiResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class AuthenticationService {

    private final RabbitCommandPublisher publisher;
    private final RegistrationAggregator registrationAggregator;
    private final LoginAggregator loginAggregator;

    public AuthenticationService(
            RabbitCommandPublisher publisher,
            RegistrationAggregator registrationAggregator,
            LoginAggregator loginAggregator
    ) {
        this.publisher = publisher;
        this.registrationAggregator = registrationAggregator;
        this.loginAggregator = loginAggregator;
    }

    public ApiResponse<Void> register(RegisterUserRequest request) {

        String correlationId = UUID.randomUUID().toString();
        UUID userUuid = UUID.randomUUID();

        var future = registrationAggregator.createRegistration(correlationId);

        publisher.sendRegisterRequest(
                new AuthenticationRegisterCommand(
                        request.getEmail(),
                        request.getPassword(),
                        userUuid
                ),
                correlationId
        );

        publisher.sendCreateUserRequest(
                new UserProfileCommand(
                        request.getUsername(),
                        userUuid
                ),
                correlationId
        );

        return getVoidApiResponse(future);
    }

    private ApiResponse<Void> getVoidApiResponse(CompletableFuture<ApiResponse<Void>> future) {
        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            ApiResponse<Void> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("TIMEOUT_OR_ERROR");
            response.setData(null);
            return response;
        }
    }

    public ApiResponse<Void> login(LoginUserRequest request) {

        String correlationId = UUID.randomUUID().toString();

        var future = loginAggregator.createLogin(correlationId);

        publisher.sendLoginRequest(
                new UserLoginCommand(
                        request.getEmail(),
                        request.getPassword()
                ),
                correlationId
        );

        return getVoidApiResponse(future);
    }
}