package com.elasticsearch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class HighLevelClientTest extends ElasticsearchApplicationTest {

//    @Autowired
//    RestHighLevelClient restHighLevelClient;
//
//    @Test
//    void textSearch() throws Exception {
//        String expectedWord = "java";
//        String expectedDate = "2014-10-29";
//
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
//        queryBuilder.must(QueryBuilders.termQuery("keywords", expectedWord));
//        queryBuilder.must(QueryBuilders.rangeQuery("date").gte(expectedDate));
//        sourceBuilder.query(queryBuilder);
//        SearchRequest searchRequest = new SearchRequest("conference-index");
//        searchRequest.source(sourceBuilder);
//
//        SearchResponse searchResp = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//        log.info("result: {}", searchResp);
//        SearchHit[] searchHitArr = searchResp.getHits().getHits();
//        Assertions.assertThat(searchHitArr).hasSize(3);
//
//        for (SearchHit searchHit : searchHitArr) {
//            log.info("conference: {}", searchHit.getSourceAsString());
//        }
//    }
//
//    @Test
//    void geoSpatialSearch() throws Exception {
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
//        queryBuilder.filter(QueryBuilders.geoDistanceQuery("location").distance("530km").point(50.0646501D, 19.9449799D));
//        sourceBuilder.query(queryBuilder);
//        SearchRequest searchRequest = new SearchRequest("conference-index");
//        searchRequest.source(sourceBuilder);
//
//        SearchResponse searchResp = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//        log.info("result: {}", searchResp);
//        SearchHit[] searchHitArr = searchResp.getHits().getHits();
//        Assertions.assertThat(searchHitArr).hasSize(2);
//
//        for (SearchHit searchHit : searchHitArr) {
//            log.info("conference: {}", searchHit.getSourceAsString());
//        }
//    }
}
