package by.innowise.inventoryservice.converters;

import by.innowise.inventoryservice.domain.Position;
import by.innowise.inventoryservice.dto.PositionDto;
import by.innowise.inventoryservice.dto.converters.PositionConverter;
import by.innowise.inventoryservice.dto.converters.PositionConverterImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class PositionConverterTest {

    private final PositionConverter positionConverter = new PositionConverterImpl();
    private static Position position;
    private static PositionDto positionDto;

    @BeforeAll
    public static void setUp() {
        UUID productId = UUID.randomUUID();
        position = Position.builder().
                productId(productId).
                amount(4).
                build();
        positionDto = PositionDto.builder().
                productId(productId).
                amount(4).
                build();
    }

    @Test
    public void toDto() {
        Assertions.assertEquals(positionDto, positionConverter.toDto(position));
    }

    @Test
    public void fromDto() {
        Assertions.assertEquals(position, positionConverter.fromDto(positionDto));
    }
}
