package com.mongodb;

import com.common.util.MyUtil;
import com.mongodb.person.Person;
import com.mongodb.person.ReactivePersonRepository;
import com.mongodb.reactivestreams.client.MongoCollection;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
class ReactiveTest extends MongoDBApplicationTest {

    @Autowired
    ReactiveMongoOperations reactiveMongoOperations;
    @Autowired
    ReactivePersonRepository reactivePersonRepository;

    @BeforeEach
    void setUp() {
        Mono<MongoCollection<Document>> createCollection = reactiveMongoOperations.createCollection(Person.class,
                CollectionOptions.empty().size(1024 * 1024).maxDocuments(100).capped());
        Mono<MongoCollection<Document>> recreateCollection = reactiveMongoOperations.collectionExists(Person.class)
                .flatMap(exists -> exists ? reactiveMongoOperations.dropCollection(Person.class) : Mono.just(exists))
                .then(createCollection);
        recreateCollection.as(StepVerifier::create).expectNextCount(1).verifyComplete();

        Flux<Person> insertAll = reactiveMongoOperations.insertAll(
                Flux.just(new Person("Walter", "White", 50),
                        new Person("Skyler", "White", 45),
                        new Person("Saul", "Goodman", 42),
                        new Person("Jesse", "Pinkman", 27)).collectList());
        insertAll.as(StepVerifier::create).expectNextCount(4).verifyComplete();
    }

    @Test
    void insertAndCount() {
        Mono<Long> saveAndCount = reactivePersonRepository.count()
                .doOnNext(count -> log.info("count1: {}", count))
                .thenMany(reactivePersonRepository.saveAll(
                        Flux.just(new Person("Hank", "Schrader", 43),
                                new Person("Mike", "Ehrmantraut", 62))))
                .last()
                .flatMap(v -> reactivePersonRepository.count())
                .doOnNext(count -> log.info("count2: {}", count));

        saveAndCount.as(StepVerifier::create).expectNext(6L).verifyComplete();
    }

    @Test
    void queryDerivation() {
        reactivePersonRepository.findByLastname("White").as(StepVerifier::create).expectNextCount(2).verifyComplete();
        reactivePersonRepository.findByLastname(Mono.just("White")).as(StepVerifier::create).expectNextCount(2).verifyComplete();
        reactivePersonRepository.findByFirstnameAndLastname("Walter", "White").as(StepVerifier::create).expectNextCount(1).verifyComplete();
        reactivePersonRepository.findByFirstnameAndLastname(Mono.just("Walter"), "White").as(StepVerifier::create).expectNextCount(1).verifyComplete();
    }

    @Test
    void streamTailableCursor() {
        Queue<Person> people = new ConcurrentLinkedQueue<>();
        Disposable disposable = reactivePersonRepository.findWithTailableCursorBy()
                .doOnNext(p -> log.info("{}", p))
                .doOnNext(people::add)
                .doOnComplete(() -> log.info("Complete"))
                .doOnTerminate(() -> log.info("Terminated"))
                .subscribe();
        MyUtil.sleep(1);

        reactivePersonRepository.save(new Person("Tuco", "Salamanca", 33))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
        MyUtil.sleep(1);

        reactivePersonRepository.save(new Person("Mike", "Ehrmantraut", 62))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
        MyUtil.sleep(1);

        disposable.dispose();

        reactivePersonRepository.save(new Person("Gus", "Fring", 53))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
        MyUtil.sleep(1);

        Assertions.assertThat(people).hasSize(6);
    }

}
