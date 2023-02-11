package com.elasticsearch;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class HighLevelClientTest extends ElasticsearchApplicationTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void textSearch() throws Exception {
        String expectedWord = "java";
        String expectedDate = "2014-10-29";

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("keywords", expectedWord));
        queryBuilder.must(QueryBuilders.rangeQuery("date").gte(expectedDate));
        sourceBuilder.query(queryBuilder);
        SearchRequest searchRequest = new SearchRequest("conference-index");
        searchRequest.source(sourceBuilder);

        SearchResponse searchResp = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        log.info("result: {}", searchResp);
        SearchHit[] searchHitArr = searchResp.getHits().getHits();
        Assertions.assertThat(searchHitArr).hasSize(3);

        for (SearchHit searchHit : searchHitArr) {
            log.info("conference: {}", searchHit.getSourceAsString());
        }
    }

    @Test
    public void geoSpatialSearch() throws Exception {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.filter(QueryBuilders.geoDistanceQuery("location").distance("530km").point(50.0646501D, 19.9449799D));
        sourceBuilder.query(queryBuilder);
        SearchRequest searchRequest = new SearchRequest("conference-index");
        searchRequest.source(sourceBuilder);

        SearchResponse searchResp = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        log.info("result: {}", searchResp);
        SearchHit[] searchHitArr = searchResp.getHits().getHits();
        Assertions.assertThat(searchHitArr).hasSize(2);

        for (SearchHit searchHit : searchHitArr) {
            log.info("conference: {}", searchHit.getSourceAsString());
        }
    }
}
