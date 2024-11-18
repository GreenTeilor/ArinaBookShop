package by.innowise.productservice.services.impl;

import by.innowise.productservice.dto.ActionDto;
import by.innowise.productservice.dto.BookDto;
import by.innowise.productservice.dto.InventoryChangeDto;
import by.innowise.productservice.dto.converters.BookConverter;
import by.innowise.productservice.exceptions.ProductExistsException;
import by.innowise.productservice.exceptions.Messages;
import by.innowise.productservice.exceptions.NoProductExistsException;
import by.innowise.productservice.filters.ContextHolder;
import by.innowise.productservice.repositories.BookRepository;
import by.innowise.productservice.services.BookService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Validated
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookConverter bookConverter;
    private final KafkaTemplate<String, InventoryChangeDto> kafkaTemplate;

    private BookServiceImpl bookService;

    @Autowired
    public void setInventoryService(@Lazy BookServiceImpl bookService) {
        this.bookService = bookService;
    }

    @Value("${topic.inventory-change}")
    private String inventoryChangeTopic;

    @Transactional(propagation = Propagation.MANDATORY)
    protected void sendInventoryChange(InventoryChangeDto inventoryChangeDto) {
        ProducerRecord<String, InventoryChangeDto> record = new ProducerRecord<>(inventoryChangeTopic, null, System.currentTimeMillis(),
                String.valueOf(inventoryChangeDto.getActionDto()), inventoryChangeDto,
                ContextHolder.getContext().getKafkaHeaders());
        kafkaTemplate.send(record);
    }

    @Override
    @Transactional(rollbackFor = {ProductExistsException.class})
    public BookDto create(BookDto bookDto) throws ProductExistsException {
        if (bookRepository.findByTitle(bookDto.getTitle()).isPresent()) {
            throw new ProductExistsException(Messages.PRODUCT_EXISTS);
        }
        bookDto.setId(null);
        BookDto book = bookConverter.toDto(bookRepository.
                save(bookConverter.fromDto(bookDto)));
        bookService.sendInventoryChange(InventoryChangeDto.builder().
                actionDto(ActionDto.ADD_POSITION).
                data(book.getId()).
                build());
        return book;
    }

    @Override
    public Optional<BookDto> read(UUID id) {
        return Optional.ofNullable(bookConverter.toDto(bookRepository.
                findById(id).orElse(null)));
    }

    @Override
    public List<BookDto> readAll() {
        return bookRepository.findAll().stream().
                map(bookConverter::toDto).toList();
    }

    @Override
    @Transactional(rollbackFor = NoProductExistsException.class)
    public BookDto update(BookDto bookDto) throws NoProductExistsException {
        bookRepository.findById(bookDto.getId()).orElseThrow(() ->
                new NoProductExistsException(Messages.NO_PRODUCT_EXISTS));
        return bookConverter.toDto(bookRepository.
                save(bookConverter.fromDto(bookDto)));
    }

    @Override
    @Transactional(rollbackFor = NoProductExistsException.class)
    public UUID delete(UUID id) throws NoProductExistsException {
        bookRepository.findById(id).orElseThrow(() ->
                new NoProductExistsException(Messages.NO_PRODUCT_EXISTS));
        bookRepository.deleteById(id);
        sendInventoryChange(InventoryChangeDto.builder().
                actionDto(ActionDto.DELETE_POSITION).
                data(id).
                build());
        return id;
    }
}
