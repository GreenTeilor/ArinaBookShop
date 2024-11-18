package by.innowise.userservice.controllers;

import by.innowise.userservice.dto.AccessTokenDto;
import by.innowise.userservice.dto.RefreshAndAccessTokenDto;
import by.innowise.userservice.exceptions.InvalidCredentialsException;
import by.innowise.userservice.exceptions.InvalidRefreshTokenException;
import by.innowise.userservice.exceptions.Messages;
import by.innowise.userservice.filters.ContextHolder;
import by.innowise.userservice.services.KeycloakService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KeycloakService keycloakService;

    @PostMapping("/tokens/byCredentials")
    public AccessTokenDto getTokens(@RequestParam String username, @RequestParam String password,
                                                        HttpServletResponse response)
            throws InvalidCredentialsException {
        Optional<RefreshAndAccessTokenDto> tokens = keycloakService.getUserTokensByCredentials(
                username, password);
        Cookie cookie = new Cookie("refreshToken", tokens.orElseThrow(() ->
                new InvalidCredentialsException(Messages.INVALID_CREDENTIALS)).getRefreshToken());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return AccessTokenDto.builder().
                accessToken(tokens.get().getAccessToken()).
                build();
    }

    @PostMapping("/tokens/byRefreshToken")
    public AccessTokenDto getTokens(@CookieValue(value = "refreshToken") String refreshToken,
                                                        HttpServletResponse response)
            throws InvalidRefreshTokenException {
        Optional<RefreshAndAccessTokenDto> tokens = keycloakService.getUserTokensByRefreshToken(refreshToken);
        Cookie cookie = new Cookie("refreshToken", tokens.orElseThrow(() ->
                new InvalidRefreshTokenException(Messages.INVALID_REFRESH_TOKEN)).getRefreshToken());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return AccessTokenDto.builder().
                accessToken(tokens.get().getAccessToken()).
                build();
    }

    @PostMapping("/self/logout")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public void logout() {
        keycloakService.logout(ContextHolder.getContext().getUserId());
    }
}
