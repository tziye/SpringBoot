package com.mongodb.transition;

import com.mongodb.MongoDBApplicationTest;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.function.Consumer;

@Slf4j
class TransactionTest extends MongoDBApplicationTest {

    static String DB_NAME = "spring";
    static String COLLECTION = "process";

    @Autowired
    TransitionService transitionService;

    @Import(TransitionService.class)
    @TestConfiguration
    @EnableTransactionManagement
    static class TransactionConfig {

        @Bean
        PlatformTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
            return new MongoTransactionManager(dbFactory);
        }
    }

    // @Test
    void txCommitRollback() {
        for (var i = 0; i < 10; i++) {
            Process process = transitionService.newProcess();
            try {
                transitionService.run(process.getId());
                Assertions.assertThat(stateInDb(process)).isEqualTo(State.DONE);
            } catch (IllegalStateException e) {
                Assertions.assertThat(stateInDb(process)).isEqualTo(State.CREATED);
            }
        }

        mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION).find(new Document())
                .forEach((Consumer<? super Document>) System.out::println);
    }

    State stateInDb(Process process) {
        return State.valueOf(mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION).find(Filters.eq("_id", process.getId()))
                .projection(Projections.include("state")).first().get("state", String.class));
    }
}
