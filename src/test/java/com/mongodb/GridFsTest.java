package com.mongodb;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.person.Person;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.util.StreamUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
class GridFsTest extends MongoDBApplicationTest {

    @Autowired
    GridFsOperations gridFsOperations;

    String fileName = "blog.json";
    String source = "mongodb/" + fileName;

    @BeforeEach
    void setUp() {
        gridFsOperations.delete(new Query());
    }

    @Test
    void simpleFile() throws IOException {
        try (InputStream is = new BufferedInputStream(new ClassPathResource(source).getInputStream())) {
            gridFsOperations.store(is, fileName);
        }
        GridFSFile file = gridFsOperations.findOne(Query.query(GridFsCriteria.whereFilename().is(fileName)));
        log.info("result: {}", file);
        Assertions.assertThat(file.getFilename()).isEqualTo(fileName);
    }

    @Test
    void withMetadata() throws IOException {
        try (InputStream is = new BufferedInputStream(new ClassPathResource(source).getInputStream())) {
            Person metadata = new Person("Hardy", "Lang");
            gridFsOperations.store(is, fileName, metadata);
        }
        GridFSFile file = gridFsOperations.findOne(Query.query(GridFsCriteria.whereMetaData("firstname").is("Hardy")));
        log.info("result: {}", file);
        Assertions.assertThat(file.getFilename()).isEqualTo(fileName);
    }

    @Test
    void resource() throws IOException {
        byte[] bytes;
        try (InputStream is = new BufferedInputStream(new ClassPathResource(source).getInputStream())) {
            bytes = StreamUtils.copyToByteArray(is);
        }
        gridFsOperations.store(new ByteArrayInputStream(bytes), fileName);
        GridFsResource resource = gridFsOperations.getResource(fileName);
        byte[] loaded;
        try (InputStream is = resource.getInputStream()) {
            loaded = StreamUtils.copyToByteArray(is);
        }
        Assertions.assertThat(bytes).isEqualTo(loaded);
    }
}
