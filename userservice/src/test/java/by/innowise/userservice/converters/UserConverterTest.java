package by.innowise.userservice.converters;

import by.innowise.userservice.domain.User;
import by.innowise.userservice.dto.UserRequestDto;
import by.innowise.userservice.dto.UserResponseDto;
import by.innowise.userservice.dto.converters.UserConverter;
import by.innowise.userservice.dto.converters.UserConverterImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

public class UserConverterTest {

    private final UserConverter userConverter = new UserConverterImpl();
    private static UserRequestDto userRequestDto;
    private static UserResponseDto userResponseDto;
    private static User userEntity;

    @BeforeAll
    public static void initRequestAndResponseDtoAndEntity() {
        userRequestDto = UserRequestDto.builder().
                email("testemail@gmail.com").
                password("a2345678").
                build();
        userResponseDto = UserResponseDto.builder().
                id(UUID.fromString("439cba0c-09f6-43cf-bdf0-873eab388b6c")).
                email("testemail@gmail.com").
                balance(BigDecimal.valueOf(3.44)).
                build();
        userEntity = User.builder().
                id(UUID.fromString("439cba0c-09f6-43cf-bdf0-873eab388b6c")).
                email("testemail@gmail.com").
                balance(BigDecimal.valueOf(3.44)).
                build();
    }

    @Test
    public void fromRequestDto() {
        User expectedUserEntity = User.builder().
                email("testemail@gmail.com").
                build();
        //Compare just emails, as id is not passed in request and entity equals() returns false
        Assertions.assertEquals(expectedUserEntity.getEmail(),
                userConverter.fromRequestDto(userRequestDto).getEmail());
    }

    @Test
    public void toRequestDto() {
        UserRequestDto expectedUserRequestDto = UserRequestDto.builder().
                email("testemail@gmail.com").
                build();
        Assertions.assertEquals(expectedUserRequestDto, userConverter.toRequestDto(userEntity));
    }

    @Test
    public void fromResponseDto() {
        User expectedUserEntity = User.builder().
                id(UUID.fromString("439cba0c-09f6-43cf-bdf0-873eab388b6c")).
                email("testemail@gmail.com").
                balance(BigDecimal.valueOf(3.44)).
                build();
        Assertions.assertEquals(expectedUserEntity, userConverter.fromResponseDto(userResponseDto));
    }

    @Test
    public void toResponseDto() {
        UserResponseDto expectedUserResponseDto = UserResponseDto.builder().
                id(UUID.fromString("439cba0c-09f6-43cf-bdf0-873eab388b6c")).
                email("testemail@gmail.com").
                balance(BigDecimal.valueOf(3.44)).
                build();
        Assertions.assertEquals(expectedUserResponseDto, userConverter.toResponseDto(userEntity));
    }
}
