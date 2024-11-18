package by.innowise.userservice.services.impl;

import by.innowise.userservice.domain.User;
import by.innowise.userservice.dto.UpdateBalanceResponseDto;
import by.innowise.userservice.dto.UserRequestDto;
import by.innowise.userservice.dto.UserResponseDto;
import by.innowise.userservice.dto.converters.UserConverter;
import by.innowise.userservice.exceptions.InsufficientFundsException;
import by.innowise.userservice.exceptions.Messages;
import by.innowise.userservice.exceptions.NoRoleExistsException;
import by.innowise.userservice.exceptions.NoUserExistsException;
import by.innowise.userservice.exceptions.UserEmailExistsException;
import by.innowise.userservice.exceptions.UserNotCreatedException;
import by.innowise.userservice.repositories.UserRepository;
import by.innowise.userservice.services.KeycloakService;
import by.innowise.userservice.services.UserService;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Validated
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final KeycloakService keycloakService;
    private final UserRepository userRepository;
    private final UserConverter userConverter;

    @Override
    @Transactional(rollbackFor = {NoRoleExistsException.class,
            UserNotCreatedException.class, UserEmailExistsException.class})
    public UserResponseDto create(UserRequestDto userRequestDto) throws NoRoleExistsException,
            UserNotCreatedException, UserEmailExistsException {
        if (userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new UserEmailExistsException(Messages.USER_EMAIL_EXISTS);
        }
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(userRequestDto.getEmail());
        userRepresentation.setEnabled(true);
        userRepresentation.setEmail(userRequestDto.getEmail());
        userRepresentation.setEmailVerified(true);
        CredentialRepresentation password = new CredentialRepresentation();
        password.setType("password");
        password.setTemporary(false);
        password.setValue(userRequestDto.getPassword());
        userRepresentation.setCredentials(List.of(
                password
        ));
        UserResponseDto userResponseDto = UserResponseDto.builder().
                id(keycloakService.createUser(userRepresentation)).
                email(userRequestDto.getEmail()).
                balance(BigDecimal.ZERO).
                build();
        keycloakService.assignRealmRolesToUser(userResponseDto.getId(),
                List.of(keycloakService.getRealmRoleRepresentation("hello-user").
                        orElseThrow(() -> new NoRoleExistsException(Messages.NO_ROLE_EXISTS))));
        User user = userConverter.fromResponseDto(userResponseDto);
        userRepository.save(user);
        return userResponseDto;
    }

    @Override
    public Optional<UserResponseDto> read(UUID id) {
        return Optional.ofNullable(userConverter.toResponseDto(userRepository.findById(id).orElse(null)));
    }

    @Override
    @Transactional(rollbackFor = {NoUserExistsException.class, InsufficientFundsException.class})
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "2000")})
    public UpdateBalanceResponseDto writeOffFunds(UUID id, BigDecimal amount)
            throws NoUserExistsException, InsufficientFundsException {
        User user = userRepository.findById(id).orElseThrow(() -> new NoUserExistsException(Messages.NO_USER_EXISTS));
        if (user.getBalance().compareTo(amount) >= 0) {
            user.setBalance(user.getBalance().subtract(amount));
        } else {
            throw new InsufficientFundsException(Messages.INSUFFICIENT_FUNDS);
        }
        return UpdateBalanceResponseDto.builder().updatedBalance(user.getBalance()).build();
    }

    @Override
    @Transactional(rollbackFor = NoUserExistsException.class)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "2000")})
    public UpdateBalanceResponseDto topUp(UUID id, BigDecimal amount) throws NoUserExistsException {
        User user = userRepository.findById(id).orElseThrow(() -> new NoUserExistsException(Messages.NO_USER_EXISTS));
        user.setBalance(user.getBalance().add(amount));
        return UpdateBalanceResponseDto.builder().updatedBalance(user.getBalance()).build();
    }
}
