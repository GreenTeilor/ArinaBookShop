package by.innowise.productservice.dto.converters;

import by.innowise.productservice.domain.Book;
import by.innowise.productservice.dto.BookDto;
import org.mapstruct.Builder;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassExhaustiveStrategy;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION,
        builder = @Builder(disableBuilder = true), injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookConverter {

    Book fromDto(BookDto bookDto);

    BookDto toDto(Book book);
}
