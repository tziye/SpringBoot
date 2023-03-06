package com.mongodb;

import com.common.util.MyUtil;
import com.mongodb.book.Book;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSDownloadOptions;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.*;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Files;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@ActiveProfiles("unit")
@DataMongoTest
class MongoClientTest {

    @Autowired(required = false)
    public MongoClient mongoClient;

    MongoDatabase database;
    MongoCollection<Book> bookCollection;
    MongoCollection<Document> docCollection;

    @BeforeEach
    void setup() throws IOException {
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        database = mongoClient.getDatabase("spring").withCodecRegistry(pojoCodecRegistry)
                .withWriteConcern(WriteConcern.MAJORITY).withReadConcern(ReadConcern.MAJORITY);

        bookCollection = database.getCollection(Book.class.getSimpleName(), Book.class);
        docCollection = database.getCollection(Book.class.getSimpleName(), Document.class);
        bookCollection.drop();
        String content = Files.contentOf(new ClassPathResource("book.json").getFile(), StandardCharsets.UTF_8);
        List<Book> books = MyUtil.parseList(content, Book.class);
        bookCollection.insertMany(books);
    }

    @Test
    void query() {
        bookCollection.createIndex(Indexes.text("volumeInfo.title"),
                new IndexOptions().languageOverride("en").expireAfter(30L, TimeUnit.SECONDS));
        bookCollection.createIndex(Indexes.ascending("volumeInfo.industryIdentifiers.type", "volumeInfo.industryIdentifiers.identifier"));

        List<Book> list = new ArrayList<>();
        bookCollection.find(
                        Updates.combine(
                                Filters.text("Spring Framework"),
                                Filters.size("volumeInfo.authors", 1),
                                Filters.eq("volumeInfo.printType", "BOOK"),
                                Filters.in("volumeInfo.language", "en", "de"),
                                Filters.eq("volumeInfo.categories", "Computers"),
                                Filters.and(Filters.gt("volumeInfo.publishedDate", MyUtil.toDate("2005-01-01")),
                                        Filters.lt("volumeInfo.publishedDate", MyUtil.toDate("2015-01-01"))),
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
        list.forEach(book -> log.info("Book: {}", book));
    }

    @Test
    void update() {
        List<Book> list = new ArrayList<>();
        BasicDBObject projection = new BasicDBObject()
                .append("volumeInfo.title", 1)
                .append("volumeInfo.authors", 1)
                .append("saleInfo.listPrice.amount", 1)
                .append("updateTime", 1);
        bookCollection.find().projection(projection).limit(3).into(list);
        log.info("Before: {}", list);

        List<String> ids = list.stream().map(Book::getId).collect(Collectors.toList());
        List<WriteModel<Book>> models = list.stream()
                .map(book -> new UpdateOneModel<Book>(
                        Updates.combine(Filters.in("_id", book.getId())),
                        Updates.combine(Updates.set("volumeInfo.authors", Collections.singleton("tziye")),
                                Updates.inc("saleInfo.listPrice.amount", 100),
                                Updates.currentDate("updateTime")),
                        new UpdateOptions().upsert(true)))
                .collect(Collectors.toList());
        bookCollection.bulkWrite(models);
        list = new ArrayList<>();
        bookCollection.find(Filters.in("_id", ids)).projection(projection).into(list);
        log.info("After: {}", list);
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

        AggregateIterable<Document> it = docCollection.aggregate(Arrays.asList(
                Aggregates.match(Filters.and(
                        Filters.and(Filters.eq("volumeInfo.printType", "BOOK"),
                                Filters.in("volumeInfo.language", "en", "de"),
                                Filters.gte("volumeInfo.publishedDate", MyUtil.toDate("2000-01-01")),
                                Filters.size("volumeInfo.categories", 1)),
                        Filters.and(Filters.eq("saleInfo.isEbook", true),
                                Filters.ne("saleInfo.saleability", "NOT_FOR_SALE")))),
                Aggregates.unwind("$volumeInfo.authors"),
                Aggregates.group(new Document("_id", new Document("author", "$volumeInfo.authors")
                        .append("language", new Document("$cond",
                                new Document("if", new Document("$eq", Arrays.asList("$volumeInfo.language", "en")))
                                        .append("then", "英语").append("else", "德语"))))
                        .append("count", new Document("$sum", 1))
                        .append("totalPrice", new Document("$sum", "$saleInfo.listPrice.amount"))
                        .append("avgPrice", new Document("$avg", "$saleInfo.listPrice.amount"))),
                Aggregates.sort(Sorts.orderBy(Sorts.descending("totalPrice"), Sorts.ascending("avgPrice"))),
                Aggregates.skip(0),
                Aggregates.limit(5)));
        it.iterator().forEachRemaining(doc -> log.info("Result: {}", doc));
    }

    @Test
    void gridFs() throws IOException {
        String source = "book.json";
        GridFSBucket gridFSBucket = GridFSBuckets.create(database, "bookFs");
        ObjectId fileId;
        try (InputStream is = new BufferedInputStream(new ClassPathResource(source).getInputStream())) {
            GridFSUploadOptions options = new GridFSUploadOptions().chunkSizeBytes(1048576).metadata(new Document("source", source));
            fileId = gridFSBucket.uploadFromStream(source, is, options);
            log.info("Upload: {}", fileId);
        }

        gridFSBucket.find(Filters.eq("_id", fileId)).forEach(file -> log.info("Find: {}", file));

        try (GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(fileId)) {
            int fileLength = (int) downloadStream.getGridFSFile().getLength();
            byte[] bytesToWriteTo = new byte[fileLength];
            int bytes = downloadStream.read(bytesToWriteTo);
            log.info("Download: {}", bytes);
        }

        GridFSDownloadOptions downloadOptions = new GridFSDownloadOptions().revision(0);
        try (FileOutputStream streamToDownloadTo = new FileOutputStream("target/" + fileId + source)) {
            gridFSBucket.downloadToStream(source, streamToDownloadTo, downloadOptions);
            streamToDownloadTo.flush();
        }

        gridFSBucket.delete(fileId);
        gridFSBucket.drop();
    }

}