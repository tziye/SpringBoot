package com.elasticsearch.conference;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

@ConditionalOnClass(ElasticsearchRepositoriesAutoConfiguration.class)
public interface ConferenceRepository extends ElasticsearchRepository<Conference, String> {

    List<Conference> findByKeywordsContainsAndDateGreaterThanEqual(String word, String date);

    @Query("{\"bool\": {" +
            "   \"must\": {" +
            "       \"match_all\": {}" +
            "   }, " +
            "   \"filter\": {" +
            "       \"geo_distance\": {" +
            "           \"distance\": \"?0\"," +
            "           \"location\": {" +
            "               \"lat\": \"?1\"," +
            "               \"lon\": \"?2\"" +
            "           }" +
            "       }" +
            "   }" +
            "}}")
    List<Conference> findByLocationWithin(String distance, double lat, double lon);
}