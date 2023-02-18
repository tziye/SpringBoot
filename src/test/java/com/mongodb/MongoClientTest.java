package com.mongodb;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Files;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
class MongoClientTest extends MongoDBApplicationTest {

    MongoCollection<Document> bookCollection;

    @BeforeEach
    void setup() throws IOException {
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoDatabase database = mongoClient.getDatabase("spring").withCodecRegistry(pojoCodecRegistry)
                .withWriteConcern(WriteConcern.MAJORITY).withReadConcern(ReadConcern.MAJORITY);

        bookCollection = database.getCollection("book");
        bookCollection.drop();
        String content = Files.contentOf(new ClassPathResource("mongodb/book.json").getFile(), StandardCharsets.UTF_8);
        Document wrapper = Document.parse("{wrapper: " + content + "}");
        List<Object> books = wrapper.getList("wrapper", Object.class);
        mongoTemplate.insert(books, "book");
    }

    @Test
    void query() {
        bookCollection.createIndex(Indexes.text("volumeInfo.title"),
                new IndexOptions().languageOverride("en").expireAfter(30L, TimeUnit.SECONDS));

        List<Document> list = new ArrayList<>();
        bookCollection.find(
                Updates.combine(
                        Filters.text("Spring Framework"),
                        Filters.size("volumeInfo.authors", 1),
                        Filters.eq("volumeInfo.printType", "BOOK"),
                        Filters.in("volumeInfo.language", "en", "de"),
                        Filters.eq("volumeInfo.categories", "Computers"),
                        Filters.and(Filters.gt("volumeInfo.publishedDate", "2005-01-01"),
                                Filters.lt("volumeInfo.publishedDate", "2015-01-01")),
                        Filters.elemMatch("volumeInfo.industryIdentifiers",
                                Filters.eq("type", "ISBN_10")),
                        Filters.exists("saleInfo.listPrice.amount")))
                .projection(new BasicDBObject().append("volumeInfo.title", 1)
                        .append("volumeInfo.authors", 1)
                        .append("volumeInfo.printType", 1)
                        .append("volumeInfo.language", 1)
                        .append("volumeInfo.categories", 1)
                        .append("volumeInfo.publishedDate", 1)
                        .append("volumeInfo.industryIdentifiers", 1)
                        .append("saleInfo.listPrice", 1)
                        .append("updateTime", 1))
                .sort(Updates.combine(Sorts.descending("volumeInfo.pageCount"),
                        Sorts.ascending("saleInfo.listPrice.amount")))
                .skip(0)
                .limit(10)
                .into(list);
        list.forEach(p -> log.info("result: {}", p));
    }

    @Test
    void update() {
        List<Document> list = new ArrayList<>();
        BasicDBObject projection = new BasicDBObject()
                .append("volumeInfo.title", 1)
                .append("volumeInfo.authors", 1)
                .append("saleInfo.listPrice.amount", 1)
                .append("updateTime", 1);
        bookCollection.find().projection(projection).limit(3).into(list);
        log.info("before: {}", list);

        List<Object> ids = list.stream().map(doc -> doc.get("_id")).collect(Collectors.toList());
        List<WriteModel<Document>> models = list.stream()
                .map(doc -> new UpdateOneModel<Document>(
                        Updates.combine(Filters.in("_id", doc.get("_id"))),
                        Updates.combine(Updates.set("volumeInfo.authors", Collections.singleton("tziye")),
                                Updates.inc("saleInfo.listPrice.amount", 100),
                                Updates.currentDate("updateTime")),
                        new UpdateOptions().upsert(true)))
                .collect(Collectors.toList());
        bookCollection.bulkWrite(models);
        list = new ArrayList<>();
        bookCollection.find(Filters.in("_id", ids)).projection(projection).into(list);
        log.info("after: {}", list);
    }

    @Test
    void aggregation() {
        bookCollection.createIndex(Indexes.compoundIndex(
                Indexes.ascending("volumeInfo.printType"), Indexes.ascending("volumeInfo.language"),
                Indexes.descending("volumeInfo.publishedDate"), Indexes.ascending("volumeInfo.categories")
        ), new IndexOptions());
        bookCollection.createIndex(Indexes.compoundIndex(
                Indexes.ascending("saleInfo.isEbook"), Indexes.ascending("saleInfo.saleability")
        ), new IndexOptions());

        AggregateIterable<Document> it = bookCollection.aggregate(Arrays.asList(
                new Document("$match",
                        new Document("$and", Arrays.asList(
                                new Document("$and", Arrays.asList(
                                        new Document("volumeInfo.printType", new Document("$eq", "BOOK")),
                                        new Document("volumeInfo.language", new Document("$in", Arrays.asList("en", "de"))),
                                        new Document("volumeInfo.publishedDate", new Document("$gte", "2000-01-01")),
                                        new Document("volumeInfo.categories", new Document("$size", 1))
                                )),
                                new Document("$and", Arrays.asList(
                                        new Document("saleInfo.isEbook", new Document("$eq", true)),
                                        new Document("saleInfo.saleability", new Document("$ne", "NOT_FOR_SALE"))
                                ))
                        ))),
                new Document("$unwind", "$volumeInfo.authors"),
                new Document("$group",
                        new Document("_id",
                                new Document("author", "$volumeInfo.authors")
                                        .append("language",
                                                new Document("$cond",
                                                        new Document("if",
                                                                new Document("$eq", Arrays.asList("$volumeInfo.language", "en")))
                                                                .append("then", "英语")
                                                                .append("else", "德语")))
                        ).append("count", new Document("$sum", 1))
                                .append("totalPrice", new Document("$sum", "$saleInfo.listPrice.amount"))
                                .append("avgPrice", new Document("$avg", "$saleInfo.listPrice.amount"))),
                new Document("$sort", new Document("count", -1).append("totalPrice", -1).append("avgPrice", 1)),
                new Document("$skip", 0),
                new Document("$limit", 5))
        );
        it.iterator().forEachRemaining(document -> log.info("result: {}", document));
    }

}