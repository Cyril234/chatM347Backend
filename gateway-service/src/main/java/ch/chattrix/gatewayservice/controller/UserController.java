package ch.chattrix.gatewayservice.controller;

import ch.chattrix.gatewayservice.service.AuthenticationService;
import ch.chattrix.gatewayservice.service.UserService;
import ch.chattrix.shared.response.ApiResponse;
import ch.chattrix.shared.types.UserData;
import ch.chattrix.shared.utils.JwtValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final JwtValidator jwtValidator;

    public UserController(UserService userService, JwtValidator jwtValidator) {
        this.userService = userService;
        this.jwtValidator = jwtValidator;
    }

    @GetMapping("/all")
    public ApiResponse<List<UserData>> getAllUsers(HttpServletRequest request) {
        String token = null;
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null | !jwtValidator.isTokenValid(token)) {
            return new ApiResponse<>(false, "INVALID_ACCESS_TOKEN", null);
        }

        return userService.getAllUsers();
    }
}
