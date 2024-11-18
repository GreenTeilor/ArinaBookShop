package by.innowise.userservice.dto.converters;

import by.innowise.userservice.domain.User;
import by.innowise.userservice.dto.UserRequestDto;
import by.innowise.userservice.dto.UserResponseDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserConverter {
    User fromRequestDto(UserRequestDto userRequestDto);
    UserRequestDto toRequestDto(User user);

    User fromResponseDto(UserResponseDto userResponseDto);
    UserResponseDto toResponseDto(User user);
}
