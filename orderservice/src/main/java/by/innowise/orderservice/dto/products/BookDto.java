package by.innowise.orderservice.dto.products;

import by.innowise.orderservice.dto.validationgroups.CreateProduct;
import by.innowise.orderservice.dto.validationgroups.UpdateProduct;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@SuperBuilder
@NoArgsConstructor
@Data
public class BookDto {

    @NotNull(message = "Null not allowed", groups = {UpdateProduct.class})
    private UUID id;

    @NotBlank(message = "Blank is not allowed",
            groups = {CreateProduct.class, UpdateProduct.class})
    @Size(min = 1, max = 50, message = "Title size must be between 1 and 50",
            groups = {CreateProduct.class, UpdateProduct.class})
    @NotNull(message = "Null not allowed",
            groups = {CreateProduct.class, UpdateProduct.class})
    private String title;

    @NotBlank(message = "Blank is not allowed",
            groups = {CreateProduct.class, UpdateProduct.class})
    @Size(min = 1, max = 50, message = "Genre size must be between 1 and 50",
            groups = {CreateProduct.class, UpdateProduct.class})
    @NotNull(message = "Null not allowed",
            groups = {CreateProduct.class, UpdateProduct.class})
    private String genre;

    @NotBlank(message = "Blank is not allowed",
            groups = {CreateProduct.class, UpdateProduct.class})
    @Size(min = 1, max = 50, message = "Author size must be between 1 and 50",
            groups = {CreateProduct.class, UpdateProduct.class})
    @NotNull(message = "Null not allowed",
            groups = {CreateProduct.class, UpdateProduct.class})
    private String author;

    @Positive(message = "Publication year must be positive",
            groups = {CreateProduct.class, UpdateProduct.class})
    @NotNull(message = "Null not allowed",
            groups = {CreateProduct.class, UpdateProduct.class})
    private Integer publicationYear;

    @Digits(integer = 6, fraction = 2, message = "Wrong weight format",
            groups = {CreateProduct.class, UpdateProduct.class})
    @NotNull(message = "Null not allowed",
            groups = {CreateProduct.class, UpdateProduct.class})
    private BigDecimal weight;

    @Digits(integer = 9, fraction = 2, message = "Wrong price format",
            groups = {CreateProduct.class, UpdateProduct.class})
    @NotNull(message = "Null not allowed",
            groups = {CreateProduct.class, UpdateProduct.class})
    private BigDecimal price;
}
