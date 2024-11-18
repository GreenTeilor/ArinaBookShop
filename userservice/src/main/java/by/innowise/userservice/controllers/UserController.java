package by.innowise.userservice.controllers;

import by.innowise.userservice.dto.UpdateBalanceResponseDto;
import by.innowise.userservice.dto.UserRequestDto;
import by.innowise.userservice.dto.UserResponseDto;
import by.innowise.userservice.exceptions.InsufficientFundsException;
import by.innowise.userservice.exceptions.NoRoleExistsException;
import by.innowise.userservice.exceptions.NoUserExistsException;
import by.innowise.userservice.exceptions.UserEmailExistsException;
import by.innowise.userservice.exceptions.UserNotCreatedException;
import by.innowise.userservice.filters.ContextHolder;
import by.innowise.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserResponseDto create(@RequestBody UserRequestDto userRequestDto) throws NoRoleExistsException, UserNotCreatedException, UserEmailExistsException {
        return userService.create(userRequestDto);
    }

    @GetMapping("/self")
    @PreAuthorize("isAuthenticated()")
    public Optional<UserResponseDto> read() {
        return userService.read(ContextHolder.getContext().getUserId());
    }

    @PostMapping("/self/writeOffFunds")
    @PreAuthorize("isAuthenticated()")
    public UpdateBalanceResponseDto writeOffFunds(@RequestParam BigDecimal amount) throws NoUserExistsException, InsufficientFundsException {
        return userService.writeOffFunds(ContextHolder.getContext().getUserId(), amount);
    }

    @PostMapping("/self/topUp")
    @PreAuthorize("isAuthenticated()")
    public UpdateBalanceResponseDto topUp(@RequestParam BigDecimal amount) throws NoUserExistsException {
        return userService.topUp(ContextHolder.getContext().getUserId(), amount);
    }
}
