package com.mongodb.geojson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoDBApplicationTest;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.geo.Point;
import org.springframework.data.geo.Polygon;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.geo.GeoJson;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.repository.init.AbstractRepositoryPopulatorFactoryBean;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Slf4j
class GeoJsonTest extends MongoDBApplicationTest {

    @TestConfiguration
    static class GeoJsonConfig {

        @Bean
        AbstractRepositoryPopulatorFactoryBean repositoryPopulator() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.addMixIn(GeoJsonPoint.class, GeoJsonPointMixin.class);
            mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

            Jackson2RepositoryPopulatorFactoryBean factoryBean = new Jackson2RepositoryPopulatorFactoryBean();
            factoryBean.setResources(new Resource[]{new ClassPathResource("mongodb/store.json")});
            factoryBean.setMapper(mapper);
            return factoryBean;
        }
    }

    static abstract class GeoJsonPointMixin {
        GeoJsonPointMixin(@JsonProperty("longitude") double x, @JsonProperty("latitude") double y) {
        }
    }

    @Autowired
    StoreRepository storeRepository;

    GeoJsonPolygon geoJsonPolygon = new GeoJsonPolygon(new Point(-73.992514, 40.758934),
            new Point(-73.961138, 40.760348), new Point(-73.991658, 40.730006), new Point(-73.992514, 40.758934));

    /**
     * Get all the Starbucks stores within the triangle defined by
     * <pre>
     * {
     *   "location": {
     *     "$geoWithin": {
     *       "geometry": {
     *         "type": "Polygon",
     *         "coordinates": [
     *           [
     *             [-73.992514,40.758934],
     *             [-73.961138,40.760348],
     *             [-73.991658,40.730006],
     *             [-73.992514,40.758934]
     *           ]
     *         ]
     *       }
     *     }
     *   }
     * }
     * <pre>
     */
    @Test
    void geoJsonPolygon() {
        storeRepository.findByLocationWithin(geoJsonPolygon).forEach(store -> log.info("result: {}", store));
    }

    /**
     * <pre>
     * {
     *   "location" : {
     *     "$geoWithin" : {
     *        "$polygon" : [ [ -73.992514, 40.758934 ] , [ -73.961138, 40.760348 ] , [ -73.991658, 40.730006 ] ]
     *     }
     *   }
     * }
     * <pre>
     */
    @Test
    void legacyPolygon() {
        storeRepository.findByLocationWithin(
                new Polygon(new Point(-73.992514, 40.758934),
                        new Point(-73.961138, 40.760348),
                        new Point(-73.991658, 40.730006)))
                .forEach(store -> log.info("result: {}", store));
    }

    /**
     * The {@code $geoIntersects} keyword is not yet available via {@link Criteria} we need to fall back to manual
     * creation of the query using the registered {@link MongoConverter} for {@link GeoJson} conversion.
     */
    @Test
    void geoIntersects() {
        Document geoJsonDbo = new Document();
        mongoTemplate.getConverter().write(geoJsonPolygon, geoJsonDbo);
        BasicQuery bq = new BasicQuery(
                new Document("location", new Document("$geoIntersects", new Document("$geometry", geoJsonDbo))));
        mongoTemplate.find(bq, Store.class).forEach(store -> log.info("result: {}", store));
    }

}
