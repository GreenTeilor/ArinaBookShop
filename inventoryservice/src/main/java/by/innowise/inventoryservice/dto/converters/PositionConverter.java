package by.innowise.inventoryservice.dto.converters;

import by.innowise.inventoryservice.domain.Position;
import by.innowise.inventoryservice.dto.PositionDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PositionConverter {
    Position fromDto(PositionDto positionDto);
    PositionDto toDto(Position position);
}
