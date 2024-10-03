package com.reactive.programming;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Log4j2
@SpringBootApplication
public class Main {

    private static final String URL_REACTIVE = "http://localhost:8081";

    private static final String URL_TRADITIONAL = "http://localhost:8082";

    public static void main( String[] args ) {
        SpringApplication.run( Main.class, args );
    }

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            retrieveAllBooks( URL_TRADITIONAL, log::info );
            retrieveAllBooks( URL_REACTIVE, log::warn );

            exchangeSearch( URL_TRADITIONAL, "java", log::info );
            exchangeSearch( URL_REACTIVE, "pascal", log::warn );

            exchangeMono( URL_TRADITIONAL, 1, log::info );
            exchangeMono( URL_REACTIVE, 6, log::warn );
        };
    }

    private void retrieveAllBooks( String url, Consumer<String> logger ) {
        WebClient.create( url )
                 .get()
                 .uri( "/books" )
                 .retrieve()
                 .bodyToFlux( String.class )
                 .subscribe( logger );
    }

    private void exchangeSearch( String url, String bookName, Consumer<String> logger ) {
        WebClient.create( url )
                 .get()
                 .uri( uriBuilder -> uriBuilder
                         .path( "/books/search" )
                         .queryParam( "bookName", bookName )
                         .build() )
                 .exchangeToFlux( response -> {
                     if ( response.statusCode().equals( HttpStatus.OK ) ) {
                         return response.bodyToFlux( String.class );
                     } else if ( response.statusCode().is4xxClientError() ) {
                         return Flux.just( "Error response: " + response.statusCode() );
                     } else {
                         return response.createException()
                                        .flux()
                                        .map( WebClientResponseException::getMessage );
                     }
                 } )
                 .subscribe( logger );
    }

    private void exchangeMono( String url, int bookId, Consumer<String> logger ) {
        WebClient.create( url )
                 .get()
                 .uri( "/books/" + bookId )
                 .exchangeToMono( response -> {
                     if ( response.statusCode().equals( HttpStatus.OK ) ) {
                         return response.bodyToMono( String.class );
                     } else if ( response.statusCode().is4xxClientError() ) {
                         return Mono.just( "Error response: " + response.statusCode() );
                     } else {
                         return response.createException()
                                        .map( WebClientResponseException::getMessage );
                     }
                 } )
                 .subscribe( logger );
    }
}
