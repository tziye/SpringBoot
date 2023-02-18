package com.mongodb.reference;

import com.mongodb.DBRef;
import com.mongodb.MongoDBApplicationTest;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

@Slf4j
class ReferenceTest extends MongoDBApplicationTest {

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(Employee.class);
        mongoTemplate.dropCollection(Employee.class);
        mongoTemplate.dropCollection(JpaManager.class);
        mongoTemplate.dropCollection(JpaEmployee.class);
        mongoTemplate.dropCollection(DbManager.class);
        mongoTemplate.dropCollection(DbEmployee.class);
    }

    @Test
    void simple() {
        Manager manager = new Manager("jabba-the-hutt", "jabba");
        mongoTemplate.save(manager);

        Employee employee1 = new Employee("greedo-tetsu-jr", "greedo");
        employee1.setManager(manager);
        mongoTemplate.save(employee1);
        mongoTemplate.update(Manager.class)
                .matching(Criteria.where("id").is(manager.getId()))
                .apply(new Update().push("employees").value(employee1))
                .first();

        Employee employee2 = new Employee("boba-fett", "boba");
        employee2.setManager(manager);
        mongoTemplate.save(employee2);
        mongoTemplate.update(Manager.class)
                .matching(Criteria.where("id").is(manager.getId()))
                .apply(new Update().push("employees").value(employee2))
                .first();

        mongoTemplate.execute(Manager.class, collection -> {
            Document rawManager = collection.find(new Document("_id", manager.getId())).first();
            log.info("document: {}", rawManager);
            Assertions.assertThat(rawManager.get("employees", List.class))
                    .containsExactly("greedo-tetsu-jr", "boba-fett");
            return "OK";
        });

        Manager loaded = mongoTemplate.query(Manager.class)
                .matching(Criteria.where("id").is(manager.getId()))
                .firstValue();
        log.info("manager: {}", loaded);

        Assertions.assertThat(loaded.getEmployees())
                .allMatch(it -> it instanceof Employee)
                .extracting("name").containsExactly("greedo", "boba");
    }

    @Test
    void jpaStyle() {
        JpaManager manager = new JpaManager("jabba-the-hutt", "jabba");
        mongoTemplate.save(manager);

        JpaEmployee employee1 = new JpaEmployee("greedo-tetsu-jr", "greedo");
        employee1.setManagerId(manager.getId());
        mongoTemplate.save(employee1);

        JpaEmployee employee2 = new JpaEmployee("boba-fett", "boba");
        employee2.setManagerId(manager.getId());
        mongoTemplate.save(employee2);

        mongoTemplate.execute(JpaManager.class, collection -> {
            Document rawManager = collection.find(new Document("_id", manager.getId())).first();
            log.info("document: {}", rawManager);
            Assertions.assertThat(rawManager).doesNotContainKey("employees");
            return "OK";
        });

        JpaManager loaded = mongoTemplate.query(JpaManager.class)
                .matching(Criteria.where("id").is(manager.getId()))
                .firstValue();
        log.info("manager: {}", loaded);

        Assertions.assertThat(loaded.getEmployees()) //
                .allMatch(it -> it instanceof JpaEmployee) //
                .extracting("name").containsExactly("greedo", "boba");
    }

    @Test
    void dbRef() {
        DbManager manager = new DbManager("jabba-the-hutt", "jabba");
        mongoTemplate.save(manager);

        DbEmployee employee1 = new DbEmployee("greedo-tetsu-jr", "greedo");
        employee1.setManager(manager);
        mongoTemplate.save(employee1);
        mongoTemplate.update(DbManager.class)
                .matching(Criteria.where("id").is(manager.getId()))
                .apply(new Update().push("employees").value(employee1))
                .first();

        DbEmployee employee2 = new DbEmployee("boba-fett", "boba");
        employee2.setManager(manager);
        mongoTemplate.save(employee2);
        mongoTemplate.update(DbManager.class)
                .matching(Criteria.where("id").is(manager.getId()))
                .apply(new Update().push("employees").value(employee2))
                .first();

        mongoTemplate.execute(DbManager.class, collection -> {
            Document rawManager = collection.find(new Document("_id", manager.getId())).first();
            log.info("document: {}", rawManager);
            Assertions.assertThat(rawManager.get("employees", List.class))
                    .containsExactly(
                            new DBRef("dbEmployee", "greedo-tetsu-jr"),
                            new DBRef("dbEmployee", "boba-fett")
                    );
            return "OK";
        });

        DbManager loaded = mongoTemplate.query(DbManager.class)
                .matching(Criteria.where("id").is(manager.getId()))
                .firstValue();
        log.info("manager: {}", loaded);

        Assertions.assertThat(loaded.getEmployees())
                .allMatch(it -> it instanceof DbEmployee)
                .extracting("name").containsExactly("greedo", "boba");
    }
}
