package com.reactive.programming;

import lombok.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class ReactiveWayDemoApplication {

    public static void main( String[] args ) {
        SpringApplication.run( ReactiveWayDemoApplication.class, args );
    }

}

// R2DBC(Reactive Relational Database Connectivity)
// doesn't require the entity annotations because it doesn't use an ORM like Hibernate
@Table(name = "books")
@NoArgsConstructor
@Getter
@Setter
@ToString
class Book {

    @Id
    private int id;

    @Column("name")
    private String name;

    public Book( String name ) {
        this.name = name;
    }
}

@Repository
interface BookRepository extends ReactiveCrudRepository<Book, Integer> {

    // @Query("SELECT * FROM books WHERE name = :name") - queries are written in SQL
    Flux<Book> findAllByNameContaining( String name );
}

@Service
@RequiredArgsConstructor
class BookService {
    private final BookRepository bookRepository;

    public Flux<Book> findAll() {
        return bookRepository.findAll();
    }

    public Mono<Book> findById( int id ) {
        //Instead of writing step-by-step instructions, define data flows and transformations.
        //Also instead of try/catch the reactor library provides operators for handling errors.
        return bookRepository.findById( id );
    }

    public Flux<Book> findAllByNameContaining( String name ) {
        return bookRepository.findAllByNameContaining( name );
    }
}

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
class BookController {
    private final BookService bookService;

    @GetMapping
    public Flux<Book> findAll() {
        return bookService.findAll();
    }

    @GetMapping("/search")
    public Flux<Book> findAllByNameContaining( @RequestParam String bookName ) {
        return bookService.findAllByNameContaining( bookName );
    }

    @GetMapping("/{id}")
    public Mono<Book> findById( @PathVariable int id ) {
        return bookService.findById( id );
    }
}
