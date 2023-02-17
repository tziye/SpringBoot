package com.redis;

import com.redis.person.Address;
import com.redis.person.Gender;
import com.redis.person.Person;
import com.redis.person.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisConnection;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
class PersonRepositoryTest extends RedisApplicationTest {

    @Autowired
    PersonRepository repository;

    Person eddard = new Person("eddard", "stark", Gender.MALE);
    Person robb = new Person("robb", "stark", Gender.MALE);
    Person sansa = new Person("sansa", "stark", Gender.FEMALE);
    Person arya = new Person("arya", "stark", Gender.FEMALE);
    Person bran = new Person("bran", "stark", Gender.MALE);
    Person rickon = new Person("rickon", "stark", Gender.MALE);
    Person jon = new Person("jon", "snow", Gender.MALE);

    @BeforeEach
    void setUp() {
        redisTemplate.execute((RedisConnection connection) -> {
            connection.flushDb();
            repository.saveAll(Arrays.asList(eddard, robb, sansa, arya, bran, rickon, jon));
            return "OK";
        });
    }

    @Test
    void find() {
        List<Person> result = repository.findByFirstnameAndLastname("arya", "stark");
        log.info("result: {}", result);
        Assertions.assertThat(result).containsOnly(arya);
    }

    @Test
    void findOr() {
        List<Person> result = repository.findByFirstnameOrLastname("arya", "snow");
        log.info("result: {}", result);
        Assertions.assertThat(result).containsOnly(arya, jon);
    }

    @Test
    void findByExample() {
        Example<Person> example = Example.of(new Person(null, "stark", null));
        Iterable<Person> result = repository.findAll(example);
        log.info("result: {}", result);
        Assertions.assertThat(result).contains(arya, eddard).doesNotContain(jon);
    }

    @Test
    void findPage() {
        Page<Person> page1 = repository.findPersonByLastname(eddard.getLastname(), PageRequest.of(0, 5));
        log.info("result1: {}", page1.getContent());
        Assertions.assertThat(page1.getNumberOfElements()).isEqualTo(5);
        Assertions.assertThat(page1.getTotalElements()).isEqualTo(6);

        var page2 = repository.findPersonByLastname(eddard.getLastname(), PageRequest.of(1, 5));
        log.info("result2: {}", page2.getContent());
        Assertions.assertThat(page2.getNumberOfElements()).isEqualTo(1);
        Assertions.assertThat(page2.getTotalElements()).isEqualTo(6);
    }

    @Test
    void findByEmbedded() {
        Address winterfell = new Address();
        winterfell.setCountry("the north");
        winterfell.setCity("winterfell");
        eddard.setAddress(winterfell);
        repository.save(eddard);

        List<Person> result = repository.findByAddress_City(winterfell.getCity());
        log.info("result: {}", result);
        Assertions.assertThat(result).containsOnly(eddard);
    }

    @Test
    void geoLocation() {
        Address winterfell = new Address();
        winterfell.setCountry("the north");
        winterfell.setCity("winterfell");
        winterfell.setLocation(new Point(52.9541053, -1.2401016));
        eddard.setAddress(winterfell);
        repository.save(eddard);

        Address casterlystein = new Address();
        casterlystein.setCountry("Westerland");
        casterlystein.setCity("Casterlystein");
        casterlystein.setLocation(new Point(51.5287352, -0.3817819));
        robb.setAddress(casterlystein);
        repository.save(robb);

        Circle innerCircle = new Circle(new Point(51.8911912, -0.4979756), new Distance(50, Metrics.KILOMETERS));
        List<Person> result1 = repository.findByAddress_LocationWithin(innerCircle);
        log.info("result1: {}", result1);
        Assertions.assertThat(result1).containsOnly(robb);

        Circle biggerCircle = new Circle(new Point(51.8911912, -0.4979756), new Distance(200, Metrics.KILOMETERS));
        List<Person> result2 = repository.findByAddress_LocationWithin(biggerCircle);
        log.info("result2: {}", result2);
        Assertions.assertThat(result2).hasSize(2).contains(robb, eddard);
    }

    @Test
    void reference() {
        eddard.setChildren(Arrays.asList(jon, robb, sansa, arya, bran, rickon));
        repository.save(eddard);
        Optional<Person> optional = repository.findById(eddard.getId());
        log.info("result1: {}", optional);
        Assertions.assertThat(optional).hasValueSatisfying(it ->
                Assertions.assertThat(it.getChildren()).contains(jon, robb, sansa, arya, bran, rickon)
        );

        repository.deleteAll(Arrays.asList(robb, jon));
        optional = repository.findById(eddard.getId());
        log.info("result2: {}", optional);
        Assertions.assertThat(optional).hasValueSatisfying(it -> {
            Assertions.assertThat(it.getChildren()).contains(sansa, arya, bran, rickon);
            Assertions.assertThat(it.getChildren()).doesNotContain(robb, jon);
        });
    }

}
