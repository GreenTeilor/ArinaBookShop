package by.innowise.userservice.services;

import by.innowise.userservice.domain.User;
import by.innowise.userservice.dto.UpdateBalanceResponseDto;
import by.innowise.userservice.dto.UserRequestDto;
import by.innowise.userservice.dto.UserResponseDto;
import by.innowise.userservice.dto.converters.UserConverter;
import by.innowise.userservice.exceptions.InsufficientFundsException;
import by.innowise.userservice.exceptions.NoRoleExistsException;
import by.innowise.userservice.exceptions.NoUserExistsException;
import by.innowise.userservice.exceptions.UserEmailExistsException;
import by.innowise.userservice.exceptions.UserNotCreatedException;
import by.innowise.userservice.repositories.UserRepository;
import by.innowise.userservice.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private KeycloakService keycloakService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserConverter userConverter;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void createUserWithEmailThatDoesntExist() throws UserNotCreatedException, UserEmailExistsException, NoRoleExistsException {
        String email = "testemail@gmail.com";
        String password = "a2345678";
        UUID id = UUID.fromString("439cba0c-09f6-43cf-bdf0-873eab388b6c");
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        UserRepresentation userRepresentation = new UserRepresentation();
        Mockito.when(keycloakService.createUser(any(UserRepresentation.class))).
                thenReturn(id);
        UserRequestDto userRequestDto = UserRequestDto.builder().
                email(email).
                password(password).
                build();
        UserResponseDto userResponseDto = UserResponseDto.builder().
                id(keycloakService.createUser(userRepresentation)).
                email(userRequestDto.getEmail()).
                balance(BigDecimal.ZERO).
                build();
        Mockito.when(userConverter.fromResponseDto(userResponseDto)).thenReturn(User.builder().
                id(id).
                email(email).
                balance(BigDecimal.ZERO).
                build());
        Mockito.when(keycloakService.getRealmRoleRepresentation("hello-user")).
                thenReturn(Optional.of(new RoleRepresentation("user", "user", true)));
        Assertions.assertEquals(userService.create(userRequestDto), userResponseDto);
    }

    @Test
    public void createUserWithEmailThatExists() {
        String email = "testemail@gmail.com";
        String password = "a2345678";
        UserRequestDto userRequestDto = UserRequestDto.builder().
                email(email).
                password(password).
                build();
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(User.builder().
                build()));
        Assertions.assertThrows(UserEmailExistsException.class, () -> userService.create(userRequestDto));
    }

    @Test
    public void readExistingUser() {
        UUID id = UUID.fromString("439cba0c-09f6-43cf-bdf0-873eab388b6c");
        String email = "testemail@gmail.com";
        UserResponseDto userResponseDto = UserResponseDto.builder().
                id(id).
                email(email).
                balance(BigDecimal.ONE).
                build();
        User user = User.builder().
                id(id).
                email(email).
                balance(BigDecimal.ONE).
                build();
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(userConverter.toResponseDto(user)).thenReturn(userResponseDto);
        Assertions.assertEquals(userService.read(id), Optional.of(userResponseDto));
    }

    @Test
    public void readNotExistingUser() {
        UUID id = UUID.fromString("439cba0c-09f6-43cf-bdf0-873eab388b6c");
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());
        Mockito.when(userConverter.toResponseDto(null)).thenReturn(null);
        Assertions.assertEquals(userService.read(id), Optional.empty());
    }

    @Test
    public void writeOffFundsIfUserHasSufficientFunds() throws NoUserExistsException, InsufficientFundsException {
        UUID id = UUID.fromString("439cba0c-09f6-43cf-bdf0-873eab388b6c");
        String email = "testemail@gmail.com";
        User user = User.builder().
                id(id).
                email(email).
                balance(BigDecimal.TEN).
                build();
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Assertions.assertEquals(userService.writeOffFunds(id, BigDecimal.ONE),
                UpdateBalanceResponseDto.builder().updatedBalance(BigDecimal.valueOf(9)).build());
    }

    @Test
    public void writeOffFundsIfUserHasInsufficientFunds() {
        UUID id = UUID.fromString("439cba0c-09f6-43cf-bdf0-873eab388b6c");
        String email = "testemail@gmail.com";
        User user = User.builder().
                id(id).
                email(email).
                balance(BigDecimal.ZERO).
                build();
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Assertions.assertThrows(InsufficientFundsException.class,
                () -> userService.writeOffFunds(id, BigDecimal.TEN));
    }

    @Test
    public void topUp() throws NoUserExistsException {
        UUID id = UUID.fromString("439cba0c-09f6-43cf-bdf0-873eab388b6c");
        String email = "testemail@gmail.com";
        User user = User.builder().
                id(id).
                email(email).
                balance(BigDecimal.ZERO).
                build();
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Assertions.assertEquals(userService.topUp(id, BigDecimal.TEN),
                UpdateBalanceResponseDto.builder().updatedBalance(BigDecimal.TEN).build());
    }
}
