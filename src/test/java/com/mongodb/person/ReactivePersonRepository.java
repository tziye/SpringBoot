package com.mongodb.person;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactivePersonRepository extends ReactiveCrudRepository<Person, String> {

    Flux<Person> findByLastname(String lastname);

    Flux<Person> findByLastname(Mono<String> lastname);

    @Query("{ 'firstname': ?0, 'lastname': ?1}")
    Mono<Person> findByFirstnameAndLastname(String firstname, String lastname);

    Mono<Person> findByFirstnameAndLastname(Mono<String> firstname, String lastname);

    @Tailable
    Flux<Person> findWithTailableCursorBy();
}
