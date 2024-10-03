package com.reactive.programming;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@WebFluxTest(BookController.class)
class BookControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BookService bookService;

    @Test
    void webClientTest() {
        Book book = new Book( "Book of Secrets" );

        given( bookService.findById( book.getId() ) ).willReturn( Mono.just( book ) );

        Book receivedBook = webTestClient
                .get()
                .uri( "/books/{id}", book.getId() )
                .exchange()
                .expectStatus().isOk()
                .expectBody( Book.class )
                .returnResult()
                .getResponseBody();

        assertEquals( book.getName(), receivedBook.getName() );
    }
}
