package com.reactive.programming;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

class OperatorsDemoTest {

    @Test
    void example_onComplete() {

        List<String> dataStream = new ArrayList<>();

        Flux.just( "y", "x", "z" ) //Publisher
            .log()
            .map( String::toUpperCase )
            .subscribe( dataStream::add ); // Subscriber
        // Subscription Implicitly created and managed by Reactor when subscribe is called

        Assertions.assertEquals( List.of( "Y", "X", "Z" ), dataStream );
    }

    /**
     * flatMap - Flattens a list of Publishers to the values that these publishers emit.
     */
    @Test
    void testFlatMap() {
        Flux<String> source = Flux.just( Mono.just( "A" ), Mono.just( "B" ) )
                                  .flatMap( letter -> letter );

        source.as( StepVerifier::create )
              .expectNext( "A", "B" )
              .verifyComplete();
    }

    /**
     * flatMapMany - Mono operator which is used to transform a Mono object into a Flux object.
     */
    @Test
    void testFlatMapMany() {
        Mono<String> userIdMono = Mono.just( "user123" );

        Flux<String> rolesFlux = userIdMono.flatMapMany( userId -> {
            // Simulate a database call that returns a Flux of roles for the given user ID
            if ( userId.equals( "user123" ) ) {
                return Flux.just( "ROLE_USER", "ROLE_ADMIN" );
            } else {
                return Flux.empty();
            }
        } );

        rolesFlux.as( StepVerifier::create )
                 .expectNext( "ROLE_USER", "ROLE_ADMIN" )
                 .verifyComplete();
    }

    /**
     * delayElements - delays the publishing of each element by a defined duration
     */
    @Test
    void testDelayElements() {
        Flux<Integer> source = Flux.just( 1, 2, 3 )
                                   .delayElements( Duration.ofMillis( 100 ) );

        StepVerifier.create( source )
                    .expectSubscription()
                    .expectNoEvent( Duration.ofMillis( 100 ) ) // No event should occur initially
                    .expectNext( 1 ) // 1 should appear after 100ms
                    .expectNoEvent( Duration.ofMillis( 100 ) ) // Delay between elements
                    .expectNext( 2 ) // 2 should appear after another 100ms
                    .expectNoEvent( Duration.ofMillis( 100 ) ) // Delay between elements
                    .expectNext( 3 ) // 3 should appear after another 100ms
                    .verifyComplete(); // Flux completes after emitting all elements
    }

    /**
     * concat - concatenates several Publisher instances in sequence, waiting for each one to complete
     * before subscribing to the next.
     */
    @Test
    void testConcat() {
        Flux<String> flux1 = Flux.just( "A", "B" );
        Flux<String> flux2 = Flux.just( "C", "D" );
        Flux<String> concatenated = Flux.concat( flux1, flux2 );

        concatenated.as( StepVerifier::create )
                    .expectNext( "A", "B", "C", "D" )
                    .verifyComplete();
    }

    /**
     * merge - It is used to combine the publishers without keeping its sequence
     */
    @Test
    void testMerge() {
        Flux<String> flux1 = Flux.just( "A", "B" ).delayElements( Duration.ofMillis( 100 ) );
        Flux<String> flux2 = Flux.just( "C", "D" ).delayElements( Duration.ofMillis( 50 ) );
        Flux<String> merged = Flux.merge( flux1, flux2 );

        merged.as( StepVerifier::create )
              .expectNext( "C", "A", "D", "B" )
              .verifyComplete();
    }

    /**
     * zip - Is used to combine two or more publishers by waiting on all the sources to emit one
     * element and combining these elements into an output value.
     */
    @Test
    void testZip() {
        Flux<String> firstNames = Flux.just( "John", "Jane", "Jack" );
        Flux<String> lastNames = Flux.just( "Doe", "Smith", "Johnson" );

        Flux<String> fullNames = Flux.zip( firstNames, lastNames,
                                           ( firstName, lastName ) -> firstName + " " + lastName );

        Flux<Object> fullNames2 = Flux.zip( firstNames, lastNames )
                               .map( tuple -> combineNames( tuple.getT1(), tuple.getT2() ) );

        fullNames2.as( StepVerifier::create )
                 .expectNext( "John Doe", "Jane Smith", "Jack Johnson" )
                 .verifyComplete();

        fullNames.as( StepVerifier::create )
                 .expectNext( "John Doe", "Jane Smith", "Jack Johnson" )
                 .verifyComplete();
    }

    private Object combineNames( String t1, String t2 ) {
        return t1 + " " + t2;
    }
}
