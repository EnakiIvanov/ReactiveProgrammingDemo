package com.traditional.programming;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TraditionalWayDemoApplicationTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void givenBookId_whenFindById_thenReturnBook() {
        Book book = new Book( "Book of Secrets" );

        given( bookRepository.findById( book.getId() ) ).willReturn( Optional.of( book ) );

        Optional<Book> result = bookService.findById( book.getId() );

        assertTrue( result.isPresent() );
        assertEquals( book, result.get() );
    }

    @Test
    void givenNegativeBookId_whenFindById_thenReturnError() {
        given( bookRepository.findById( -1 ) ).willThrow( new IllegalArgumentException() );

        assertThrows( IllegalArgumentException.class, () -> bookService.findById( -1 ) );
    }
}