package ch.chattrix.gatewayservice.service;

import ch.chattrix.gatewayservice.aggregator.GetAllUsersAggregator;
import ch.chattrix.gatewayservice.rabbitmq.RabbitCommandPublisher;
import ch.chattrix.shared.command.BasicCommand;
import ch.chattrix.shared.response.ApiResponse;
import ch.chattrix.shared.types.UserData;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    private final RabbitCommandPublisher publisher;
    private final GetAllUsersAggregator getAllUsersAggregator;

    public UserService(RabbitCommandPublisher publisher, GetAllUsersAggregator getAllUsersAggregator) {
        this.publisher = publisher;
        this.getAllUsersAggregator = getAllUsersAggregator;
    }

    public ApiResponse<List<UserData>> getAllUsers() {

        String correlationId = UUID.randomUUID().toString();

        var future = getAllUsersAggregator.getAllUsers(correlationId);

        publisher.sendGetAllUsersRequest(
                new BasicCommand(),
                correlationId
        );
        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            ApiResponse<List<UserData>> response = new ApiResponse<>();
            response.setSuccess(false);
            response.setMessage("TIMEOUT_OR_ERROR");
            response.setData(null);
            return response;
        }
    }
}
