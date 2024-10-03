package com.reactive.programming;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReactiveWayDemoApplicationTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;


    @Test
    void givenBookId_whenFindById_thenReturnBook() {
        Book book = new Book( "Book of Secrets" );

        given( bookRepository.findById( book.getId() ) ).willReturn( Mono.just( book ) );

        bookService.findById( book.getId() )
                   .as( StepVerifier::create )
                   .expectNext( book )
                   .verifyComplete();
    }

    @Test
    void givenBookName_whenFindByName_thenReturnMatchingBooks() {
        Book book1 = new Book( "Book of Secrets" );
        Book book2 = new Book( "Book of Java" );

        given( bookRepository.findAllByNameContaining( "book" ) ).willReturn( Flux.just( book1, book2 ) );

        bookService.findAllByNameContaining( "book" )
                   .as( StepVerifier::create )
                   .expectNext( book1, book2 )
                   .verifyComplete();
    }

    @Test
    void givenNonExistentBookId_whenFindById_thenReturnEmptyBook() {

        given( bookRepository.findById( 0 ) ).willReturn( Mono.empty() );

        bookService.findById( 0 )
                   .as( StepVerifier::create )
                   .verifyComplete();
    }

    @Test
    void givenNegativeBookId_whenFindById_thenReturnError() {
        given( bookRepository.findById( -1 ) ).willReturn( Mono.error( new IllegalArgumentException() ) );

        bookService.findById( -1 )
                   .as( StepVerifier::create )
                   .expectError( IllegalArgumentException.class )
                   .verify();
    }

}
