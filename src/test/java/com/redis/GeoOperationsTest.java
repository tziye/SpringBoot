package com.redis;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisGeoCommands.DistanceUnit;
import org.springframework.data.redis.core.GeoOperations;

import java.util.List;

@Slf4j
public class GeoOperationsTest extends RedisApplicationTest {

    GeoOperations<String, Object> geoOpts;

    @BeforeEach
    void setup() {
        geoOpts = redisTemplate.opsForGeo();
        geoOpts.add(K1, new Point(13.361389, 38.115556), "Arigento");
        geoOpts.add(K1, new Point(15.087269, 37.502669), "Catania");
        geoOpts.add(K1, new Point(13.583333, 37.316667), "Palermo");
    }

    @Test
    void radiusByMember() {
        GeoResults<RedisGeoCommands.GeoLocation<Object>> byDistance =
                geoOpts.radius(K1, "Palermo", new Distance(100, DistanceUnit.KILOMETERS));
        log.info("byDistance: {}", byDistance);
        Assertions.assertThat(byDistance).hasSize(2).extracting("content.name").contains("Arigento", "Palermo");

        GeoResults<RedisGeoCommands.GeoLocation<Object>> greaterDistance =
                geoOpts.radius(K1, "Palermo", new Distance(200, DistanceUnit.KILOMETERS));
        log.info("greaterDistance: {}", greaterDistance);
        Assertions.assertThat(greaterDistance).hasSize(3).extracting("content.name").contains("Arigento", "Catania", "Palermo");
    }

    @Test
    void radiusByCircle() {
        Circle circle = new Circle(new Point(13.583333, 37.316667), new Distance(100, DistanceUnit.KILOMETERS));
        GeoResults<RedisGeoCommands.GeoLocation<Object>> result = geoOpts.radius(K1, circle);
        log.info("byCircle: {}", result);
        Assertions.assertThat(result).hasSize(2).extracting("content.name").contains("Arigento", "Palermo");
    }

    @Test
    void distance() {
        Distance distance = geoOpts.distance(K1, "Catania", "Palermo", DistanceUnit.KILOMETERS);
        log.info("distance: {}", distance);
        Assertions.assertThat(distance.getValue()).isBetween(130d, 140d);
    }

    @Test
    void hash() {
        List<String> hashes = geoOpts.hash(K1, "Catania", "Palermo");
        log.info("hash: {}", hashes);
        Assertions.assertThat(hashes).hasSize(2).contains("sqdtr74hyu0", "sq9sm1716e0");
    }
}
