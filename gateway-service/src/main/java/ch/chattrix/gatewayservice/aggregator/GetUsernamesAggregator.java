package ch.chattrix.gatewayservice.aggregator;

import ch.chattrix.shared.rabbitmq.event.GetUsernamesResultEvent;
import ch.chattrix.shared.response.ApiResponse;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

@Component
public class GetUsernamesAggregator {

    private final Map<String, CompletableFuture<ApiResponse<Map<UUID, String>>>> futures =
            new ConcurrentHashMap<>();

    private final Map<String, UsernameState> store =
            new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    private static final long TIMEOUT_SECONDS = 5;

    public CompletableFuture<ApiResponse<Map<UUID, String>>> getUsernames(String correlationId) {
        CompletableFuture<ApiResponse<Map<UUID, String>>> future = new CompletableFuture<>();

        futures.put(correlationId, future);
        store.put(correlationId, new UsernameState());

        scheduler.schedule(
                () -> fail(correlationId, "TIMEOUT"),
                TIMEOUT_SECONDS,
                TimeUnit.SECONDS
        );

        return future;
    }

    public void handleUsernames(String correlationId, GetUsernamesResultEvent event) {

        UsernameState state = store.get(correlationId);

        if (state == null || !event.isSuccess()) {
            return;
        }

        if (event.getUsernames() != null) {
            state.usernames.putAll(event.getUsernames());
        }

        state.received = true;

        tryComplete(correlationId, state);
    }

    private void tryComplete(String correlationId, UsernameState state) {

        if (state.received) {

            ApiResponse<Map<UUID, String>> response = new ApiResponse<>();
            response.setSuccess(true);
            response.setMessage("GET_USERNAMES_SUCCESS");
            response.setData(state.usernames);

            complete(correlationId, response);
        }
    }

    private void fail(String correlationId, String message) {

        ApiResponse<Map<UUID, String>> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setData(Collections.emptyMap());

        complete(correlationId, response);
    }

    private void complete(String correlationId,
                          ApiResponse<Map<UUID, String>> response) {

        CompletableFuture<ApiResponse<Map<UUID, String>>> future =
                futures.remove(correlationId);

        store.remove(correlationId);

        if (future != null) {
            future.complete(response);
        }
    }

    private static class UsernameState {
        Map<UUID, String> usernames = new HashMap<>();
        boolean received = false;
    }
}