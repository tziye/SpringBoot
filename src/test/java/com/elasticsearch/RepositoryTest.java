package com.elasticsearch;

import com.elasticsearch.conference.Conference;
import com.elasticsearch.conference.ConferenceRepository;
import com.util.MyUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class RepositoryTest extends ElasticsearchApplicationTest {

    @Autowired
    ConferenceRepository repository;

    @Test
    void textSearch() {
        String expectedWord = "java";
        String expectedDate = "2014-10-29";

        List<Conference> result = repository.findByKeywordsContainsAndDateGreaterThanEqual(expectedWord, expectedDate);
        log.info("result: {}", result);
        Assertions.assertThat(result).hasSize(3);

        for (Conference conference : result) {
            log.info("conference: {}", conference);
            Assertions.assertThat(conference.getKeywords()).contains(expectedWord);
            Assertions.assertThat(MyUtil.toDate(conference.getDate())).isAfter(MyUtil.toDate(expectedDate));
        }
    }

    @Test
    void geoSpatialSearch() {
        List<Conference> result = repository.findByLocationWithin("530km", 50.0646501D, 19.9449799D);
        log.info("result: {}", result);
        Assertions.assertThat(result).hasSize(2);

        for (Conference conference : result) {
            log.info("conference: {}", conference);
        }
    }

}
