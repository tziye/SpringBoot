package com.mongodb;

import com.common.util.MyUtil;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.person.Person;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.messaging.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.Duration;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
class ChangeStreamTest extends MongoDBApplicationTest {

    @Autowired
    MessageListenerContainer container;

    @TestConfiguration
    static class ChangeStreamConfig {
        @Bean
        MessageListenerContainer messageListenerContainer(MongoTemplate template) {
            return new DefaultMessageListenerContainer(template) {
                @Override
                public boolean isAutoStartup() {
                    return true;
                }
            };
        }
    }

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(Person.class);
    }

    // @Test
    void changeEvent() throws InterruptedException {
        CollectingMessageListener<ChangeStreamDocument<Document>, Person> messageListener = new CollectingMessageListener<>();
        ChangeStreamRequest<Person> request = ChangeStreamRequest.builder(messageListener).collection("person")
                .filter(Aggregation.newAggregation(Aggregation.match(Criteria.where("operationType").is("insert")))).build();

        Subscription subscription = container.register(request, Person.class);
        subscription.await(Duration.ofMillis(200));

        Person gabriel = new Person("Gabriel", "Lorca", 30);
        Person ash = new Person("Ash", "Tyler", 35);
        Person michael = new Person("Michael", "Burnham", 30);
        mongoTemplate.save(gabriel);
        mongoTemplate.save(ash);

        messageListener.awaitNextMessages(2);
        Assertions.assertThat(messageListener.messageCount()).isEqualTo(2);

        mongoTemplate.update(Person.class)
                .matching(Query.query(Criteria.where("id").is(ash.getId())))
                .apply(Update.update("age", 40))
                .first();

        MyUtil.sleep(1);
        Assertions.assertThat(messageListener.messageCount()).isEqualTo(2);

        mongoTemplate.save(michael);
        messageListener.awaitNextMessages(1);
        Assertions.assertThat(messageListener.messageCount()).isEqualTo(3);
    }

    static class CollectingMessageListener<S, T> implements MessageListener<S, T> {
        BlockingDeque<Message<S, T>> messages = new LinkedBlockingDeque<>();
        AtomicInteger count = new AtomicInteger();

        @Override
        public void onMessage(Message<S, T> message) {
            log.info("Received Message in collection {}.\n\traw source: {}\n\tconverted: {}",
                    message.getProperties().getCollectionName(), message.getRaw(), message.getBody());
            count.incrementAndGet();
            messages.add(message);
        }

        int messageCount() {
            return count.get();
        }

        void awaitNextMessages(int count) throws InterruptedException {
            for (var i = 0; i < count; i++) {
                messages.take();
            }
        }
    }

}
