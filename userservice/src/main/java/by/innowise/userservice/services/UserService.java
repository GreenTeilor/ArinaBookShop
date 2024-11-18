package by.innowise.userservice.services;

import by.innowise.userservice.dto.UpdateBalanceResponseDto;
import by.innowise.userservice.dto.UserRequestDto;
import by.innowise.userservice.dto.UserResponseDto;
import by.innowise.userservice.exceptions.InsufficientFundsException;
import by.innowise.userservice.exceptions.NoRoleExistsException;
import by.innowise.userservice.exceptions.NoUserExistsException;
import by.innowise.userservice.exceptions.UserEmailExistsException;
import by.innowise.userservice.exceptions.UserNotCreatedException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserResponseDto create(@Valid UserRequestDto userRequestDto) throws NoRoleExistsException,
            UserNotCreatedException, UserEmailExistsException;

    Optional<UserResponseDto> read(UUID id);

    UpdateBalanceResponseDto writeOffFunds(UUID id, @Digits(integer = 12, fraction = 2,
                                     message = "Invalid amount") BigDecimal amount)
            throws NoUserExistsException, InsufficientFundsException;

    UpdateBalanceResponseDto topUp(UUID id, @Digits(integer = 12, fraction = 2,
            message = "Invalid amount") BigDecimal amount)
            throws NoUserExistsException;
}