package com.mongodb;

import com.mongodb.person.Person;
import com.mongodb.person.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Optional;

@Slf4j
class ExampleTest extends MongoDBApplicationTest {

    @Autowired
    PersonRepository personRepository;

    Person skyler, walter, flynn, marie, hank;

    @BeforeEach
    void setUp() {
        personRepository.deleteAll();
        skyler = personRepository.save(new Person("Skyler", "White", 45));
        walter = personRepository.save(new Person("Walter", "White", 50));
        flynn = personRepository.save(new Person("Walter Jr. (Flynn)", "White", 17));
        marie = personRepository.save(new Person("Marie", "Schrader", 38));
        hank = personRepository.save(new Person("Hank", "Schrader", 43));
    }

    @Test
    void single() {
        Example<Person> example = Example.of(new Person("(Skyl|Walt)er", null, null),
                ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.REGEX));
        Iterable<Person> result1 = personRepository.findAll(example);
        log.info("result1: {}", result1);
        Iterable<Person> result2 = mongoTemplate.find(Query.query(Criteria.byExample(example)), Person.class);
        log.info("result2: {}", result2);
    }

    @Test
    void multi() {
        Example<Person> example = Example.of(new Person("Walter", "WHITE", 18),
                ExampleMatcher.matching().withIgnorePaths("age").
                        withMatcher("firstname", ExampleMatcher.GenericPropertyMatcher::startsWith).
                        withMatcher("lastname", ExampleMatcher.GenericPropertyMatcher::ignoreCase));
        Iterable<Person> result = personRepository.findAll(example);
        log.info("result: {}", result);
    }

    @Test
    void transformer() {
        Example<Person> example = Example.of(new Person(null, "White", 99),
                ExampleMatcher.matching().withMatcher("age", matcher -> matcher.transform(value -> Optional.of(45))));
        Iterable<Person> result = personRepository.findAll(example);
        log.info("result: {}", result);
    }

    @Test
    void count() {
        Example<Person> example = Example.of(new Person(null, null, null));
        long result = personRepository.count(example);
        log.info("result: {}", result);
    }

}
