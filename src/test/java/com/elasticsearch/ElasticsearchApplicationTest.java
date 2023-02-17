package com.elasticsearch;

import com.elasticsearch.conference.Conference;
import com.elasticsearch.conference.ConferenceRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

@EnableAutoConfiguration(exclude = {
        KafkaAutoConfiguration.class, RabbitAutoConfiguration.class,
        RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class,
        MongoAutoConfiguration.class, MongoRepositoriesAutoConfiguration.class})
@Slf4j
@ActiveProfiles("unit")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.allow-bean-definition-overriding=true")
class ElasticsearchApplicationTest {

    @Autowired
    ElasticsearchOperations operations;
    @Autowired
    ConferenceRepository repository;

    @BeforeEach
    void insertData() {
        operations.indexOps(Conference.class).refresh();
        Conference c1 = Conference.builder().date("2014-11-06").name("Spring eXchange 2014 - London")
                .keywords(Arrays.asList("java", "spring")).location(new GeoPoint(51.500152D, -0.126236D))
                .attendees(10).build();
        Conference c2 = Conference.builder().date("2014-12-07").name("Scala eXchange 2014 - London")
                .keywords(Arrays.asList("scala", "play", "java")).location(new GeoPoint(51.500152D, -0.126236D))
                .attendees(50).build();
        Conference c3 = Conference.builder().date("2014-11-20").name("Elasticsearch 2014 - Berlin")
                .keywords(Arrays.asList("java", "elasticsearch", "kibana"))
                .location(new GeoPoint(52.5234051D, 13.4113999)).attendees(80).build();
        Conference c4 = Conference.builder().date("2014-11-12").name("AWS London 2014").keywords(Arrays.asList("cloud", "aws"))
                .location(new GeoPoint(51.500152D, -0.126236D)).attendees(100).build();
        Conference c5 = Conference.builder().date("2014-10-04").name("JDD14 - Cracow")
                .keywords(Arrays.asList("java", "spring")).location(new GeoPoint(50.0646501D, 19.9449799))
                .attendees(150).build();
        List<Conference> documents = Arrays.asList(c1, c2, c3, c4, c5);
        repository.saveAll(documents);
    }

    @AfterEach
    void deleteIndex() {
        operations.indexOps(Conference.class).delete();
    }
}
