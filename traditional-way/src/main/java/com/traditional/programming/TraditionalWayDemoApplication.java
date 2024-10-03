package com.traditional.programming;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class TraditionalWayDemoApplication {

    public static void main( String[] args ) {
        SpringApplication.run( TraditionalWayDemoApplication.class, args );
    }
}

@Entity
@Table(name = "books")
@NoArgsConstructor
@Getter
@Setter
@ToString
class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    public Book( String name ) {
        this.name = name;
    }
}

@Repository
interface BookRepository extends ListCrudRepository<Book, Integer> {
    //Queries can be written in HQL/JPQL
    List<Book> findAllByNameContaining( String name );
}

@Service
@RequiredArgsConstructor
class BookService {
    private final BookRepository bookRepository;

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Optional<Book> findById( int id ) {
        //Code written sequentially
        //Using of common patterns like loops, ifs, etc.
        //Error handling is straightforward with try-catch
        return bookRepository.findById( id );
    }

    public List<Book> findAllByNameContaining( String name ) {
        return bookRepository.findAllByNameContaining( name );
    }
}

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
class BookController {
    private final BookService bookService;

    @GetMapping
    public List<Book> findAll() {
        return bookService.findAll();
    }

    @GetMapping("/search")
    public List<Book> findAllByNameContaining( @RequestParam String bookName ) {
        return bookService.findAllByNameContaining( bookName );
    }

    @GetMapping("/{id}")
    public Optional<Book> findById( @PathVariable int id ) {
        return bookService.findById( id );
    }
}
