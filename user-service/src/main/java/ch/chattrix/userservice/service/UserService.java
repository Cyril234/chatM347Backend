package ch.chattrix.userservice.service;

import ch.chattrix.shared.response.ApiResponse;
import ch.chattrix.shared.types.UserAnonymData;
import ch.chattrix.shared.types.UserBaseData;
import ch.chattrix.userservice.entity.User;
import ch.chattrix.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ApiResponse<Void> create(String username, UUID userUuid) {

        if (userRepository.existsById(userUuid)) {
            return new ApiResponse<>(false, "USER_ALREADY_EXISTS", null);
        }

        if (userRepository.existsByUsername(username)) {
            return new ApiResponse<>(false, "USERNAME_ALREADY_EXISTS", null);
        }

        try {
            User user = new User();
            user.setUserUuid(userUuid);
            user.setUsername(username);
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());

            userRepository.save(user);

            return new ApiResponse<>(true, "USER_CREATED_SUCCESSFULLY", null);

        } catch (Exception e) {
            return new ApiResponse<>(false, "USER_CREATION_FAILED", null);
        }
    }

    public ApiResponse<List<UserAnonymData>> getAll() {
        try {
            List<User> users = userRepository.findAll();
            List<UserAnonymData> userAnonymData = new java.util.ArrayList<>(List.of());
            for (User user : users) {
                userAnonymData.add(new UserAnonymData(user.getUsername(), user.getUserUuid()));
            }
            return new ApiResponse<>(true, "USER_GET_ALL_SUCCESSFULLY", userAnonymData);

        } catch (Exception e) {
            return new ApiResponse<>(false, "USER_GET_ALL_FAILED", null);
        }
    }

    public ApiResponse<UserBaseData> getOne(UUID userUuid) {

        try {
            Optional<User> userOpt = userRepository.findByUserUuid(userUuid);

            if (userOpt.isEmpty()) {
                return new ApiResponse<>(false, "USER_NOT_FOUND", null);
            }

            User user = userOpt.get();

            UserBaseData data = new UserBaseData();
            data.setUserUuid(user.getUserUuid());
            data.setUsername(user.getUsername());
            data.setCreatedAt(user.getCreatedAt());

            return new ApiResponse<>(true, "USER_GET_ONE_SUCCESS", data);

        } catch (Exception e) {
            return new ApiResponse<>(false, "USER_GET_ONE_FAILED", null);
        }
    }
}