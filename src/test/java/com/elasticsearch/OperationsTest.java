package com.elasticsearch;

import com.common.util.MyUtil;
import com.elasticsearch.conference.Conference;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

@Slf4j
class OperationsTest extends ElasticsearchApplicationTest {

    @Test
    void textSearch() {
        String expectedWord = "java";
        String expectedDate = "2014-10-29";

        CriteriaQuery query = new CriteriaQuery(new Criteria("keywords").contains(expectedWord)
                .and(new Criteria("date").greaterThanEqual(expectedDate)));

        SearchHits<Conference> result = operations.search(query, Conference.class, IndexCoordinates.of("conference-index"));
        log.info("result: {}", result);
        Assertions.assertThat(result).hasSize(3);

        for (SearchHit<Conference> conference : result) {
            log.info("conference: {}", conference);
            Assertions.assertThat(conference.getContent().getKeywords()).contains(expectedWord);
            Assertions.assertThat(MyUtil.toDate(conference.getContent().getDate())).isAfter(MyUtil.toDate(expectedDate));
        }
    }

    @Test
    void geoSpatialSearch() {
        GeoPoint startLocation = new GeoPoint(50.0646501D, 19.9449799D);
        String range = "530km";
        CriteriaQuery query = new CriteriaQuery(new Criteria("location").within(startLocation, range));

        SearchHits<Conference> result = operations.search(query, Conference.class, IndexCoordinates.of("conference-index"));
        log.info("result: {}", result);
        Assertions.assertThat(result).hasSize(2);

        for (SearchHit<Conference> conference : result) {
            log.info("conference: {}", conference);
        }
    }

}