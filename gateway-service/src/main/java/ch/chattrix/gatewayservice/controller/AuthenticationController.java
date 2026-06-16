package ch.chattrix.gatewayservice.controller;

import ch.chattrix.gatewayservice.service.AuthenticationService;
import ch.chattrix.shared.dto.user.LoginUserRequest;
import ch.chattrix.shared.dto.user.RegisterUserRequest;
import ch.chattrix.shared.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody RegisterUserRequest request) {
        return authenticationService.register(request);
    }

    @PostMapping("/login")
    public ApiResponse<Void> login(
            @RequestBody LoginUserRequest request,
            HttpServletResponse response
    ) {

        ApiResponse<Void> serviceResponse =
                authenticationService.login(request);

        if (!serviceResponse.isSuccess()) {
            return serviceResponse;
        }

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", "TEMP")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofMinutes(60))
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "TEMP")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofDays(14))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return serviceResponse;
    }
}