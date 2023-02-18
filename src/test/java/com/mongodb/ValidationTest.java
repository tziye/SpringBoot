package com.mongodb;

import com.mongodb.person.Person;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.schema.JsonSchemaProperty;
import org.springframework.data.mongodb.core.schema.MongoJsonSchema;
import org.springframework.data.mongodb.core.validation.Validator;

import java.util.List;

@Slf4j
class ValidationTest extends MongoDBApplicationTest {

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection("person");
    }

    /**
     * <pre>
     * {
     *   "type": "object",
     *   "required": [ "lastname", "age" ],
     *   "properties": {
     *     "lastname": {
     *       "type": "string",
     *       "minLength": 1
     *     },
     *     "age": {
     *       "type": "int",
     *       "minimum" : 0,
     *       "exclusiveMinimum" : false,
     *       "maximum" : 125,
     *       "exclusiveMaximum" : false
     *     }
     *   }
     * }
     * </pre>
     */
    @Test
    void schema() {
        Person luke = new Person("luke", "skywalker", 25);
        Person yoda = new Person("yoda", null, 900);
        mongoTemplate.save(luke);
        mongoTemplate.save(yoda);

        MongoJsonSchema schema = MongoJsonSchema.builder()
                .required("lastname", "age")
                .properties(
                        JsonSchemaProperty.string("lastname").minLength(1),
                        JsonSchemaProperty.int32("age").gte(0).lte(125)
                ).build();

        List<Person> list = mongoTemplate.find(Query.query(Criteria.matchingDocumentStructure(schema)), Person.class);
        log.info("result: {}", list);
    }

    /**
     * <pre>
     * {
     *     lastname : {
     *         $exists : true,
     *         $ne : null,
     *         $type : 2
     *     },
     *     age : {
     *         $exists : true,
     *         $ne : null,
     *         $type : 16,
     *         $gte : 0,
     *         $lte : 125
     *     }
     * }
     * </pre>
     */
    @Test
    void validator() {
        Validator validator = Validator.criteria(
                Criteria.where("lastname").exists(true).ne(null).type(2)
                        .and("age").exists(true).ne(null).type(16).gte(0).lte(125));
        mongoTemplate.createCollection(Person.class, CollectionOptions.empty().validator(validator));

        Assertions.assertThat(mongoTemplate.save(new Person("luke", "skywalker", 25))).isNotNull();
        Assertions.assertThatExceptionOfType(DataIntegrityViolationException.class)
                .isThrownBy(() -> mongoTemplate.save(new Person("yoda", null, 900)));
    }

    /**
     * <pre>
     * {
     *   "type": "object",
     *   "required": [ "lastname", "age" ],
     *   "properties": {
     *     "lastname": {
     *       "type": "string",
     *       "minLength": 1
     *     },
     *     "age": {
     *       "type": "int",
     *       "minimum" : 0,
     *       "exclusiveMinimum" : false,
     *       "maximum" : 125,
     *       "exclusiveMaximum" : false
     *     }
     *   }
     * }
     * </pre>
     */
    @Test
    void schemaValidator() {
        Validator validator = Validator.schema(MongoJsonSchema.builder()
                .required("lastname", "age")
                .properties(
                        JsonSchemaProperty.string("lastname").minLength(1),
                        JsonSchemaProperty.int32("age").gte(0).lte(125)
                ).build());
        mongoTemplate.createCollection(Person.class, CollectionOptions.empty().validator(validator));

        Assertions.assertThat(mongoTemplate.save(new Person("luke", "skywalker", 25))).isNotNull();
        Assertions.assertThatExceptionOfType(DataIntegrityViolationException.class)
                .isThrownBy(() -> mongoTemplate.save(new Person("yoda", null, 900)));
    }
}
