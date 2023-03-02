package com.elasticsearch;

import com.common.util.MyUtil;
import com.mongodb.book.Book;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Files;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@EnableAutoConfiguration(exclude = {
        KafkaAutoConfiguration.class, RabbitAutoConfiguration.class,
        RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class,
        MongoAutoConfiguration.class, MongoRepositoriesAutoConfiguration.class})
@Slf4j
@ActiveProfiles("unit")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EsHighLevelClientTest {

    static String KEYWORD = ".keyword";
    static String BOOK = "book";

    @Autowired
    RestHighLevelClient highClient;

    @BeforeEach
    void setUp() throws IOException {
        IndicesClient indicesClient = highClient.indices();
        if (indicesClient.exists(new GetIndexRequest(BOOK), RequestOptions.DEFAULT)) {
            indicesClient.delete(new DeleteIndexRequest(BOOK), RequestOptions.DEFAULT);
        }
        indicesClient.create(new CreateIndexRequest(BOOK), RequestOptions.DEFAULT);

        String content = Files.contentOf(new ClassPathResource("book.json").getFile(), StandardCharsets.UTF_8);
        List<Book> books = MyUtil.parseList(content, Book.class);

        BulkRequest request = new BulkRequest();
        books.forEach(book -> request.add(new IndexRequest(BOOK).id(book.getId()).source(MyUtil.toMap(book))));
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        BulkResponse response = highClient.bulk(request, RequestOptions.DEFAULT);
        log.info("InitData: {} {}", response.getItems().length, response.status());
        GetMappingsResponse getMappingsResponse = indicesClient.getMapping(new GetMappingsRequest().indices(BOOK), RequestOptions.DEFAULT);
        log.info("Mapping: {}", getMappingsResponse.mappings().get(BOOK).sourceAsMap());
    }

    Book getById(String id) throws IOException {
        GetRequest getRequest = new GetRequest(BOOK, id);
        String[] includes = Strings.EMPTY_ARRAY;
        String[] excludes = new String[]{"kind", "searchInfo"};
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, excludes);
        getRequest.fetchSourceContext(fetchSourceContext);
        GetResponse getResponse = highClient.get(getRequest, RequestOptions.DEFAULT);
        log.info("GetById: {}", getResponse);
        return MyUtil.parse(getResponse.getSource(), Book.class);
    }

    @Test
    void update() throws Exception {
        String id = "QjmkjzN6foQC";
        Book book = getById(id);
        book.setKind("update");
        book.setSaleInfo(null);
        UpdateRequest updateRequest = new UpdateRequest(BOOK, id).doc(MyUtil.toMap(book));
        UpdateResponse updateResponse = highClient.update(updateRequest, RequestOptions.DEFAULT);
        log.info("Update: {}", updateResponse);
        getById(id);
    }

    @Test
    void delete() throws Exception {
        String id = "QjmkjzN6foQC";
        getById(id);
        DeleteResponse deleteResponse = highClient.delete(new DeleteRequest(BOOK, id), RequestOptions.DEFAULT);
        log.info("Delete: {}", deleteResponse);

        boolean exists = highClient.exists(new GetRequest(BOOK, id), RequestOptions.DEFAULT);
        log.info("Exists: {}", exists);
    }

    @Test
    void search() throws Exception {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.matchQuery("volumeInfo.title", "Spring Framework")
                .fuzziness(Fuzziness.AUTO).prefixLength(3).maxExpansions(10));
        queryBuilder.must(QueryBuilders.wildcardQuery("volumeInfo.title" + KEYWORD, "*Spring*"));
        queryBuilder.must(QueryBuilders.termQuery("volumeInfo.printType" + KEYWORD, "BOOK"));
        queryBuilder.must(QueryBuilders.termsQuery("volumeInfo.authors" + KEYWORD, Arrays.asList("Ashish Sarin", "Josh Long")));
        queryBuilder.must(QueryBuilders.rangeQuery("volumeInfo.publishedDate").gte(MyUtil.toDate("2005-01-01").getTime()));
        queryBuilder.must(QueryBuilders.existsQuery("saleInfo"));
        queryBuilder.mustNot(QueryBuilders.termQuery("saleInfo.isEbook", false));
        BoolQueryBuilder subBuilder = QueryBuilders.boolQuery();
        subBuilder.should(QueryBuilders.rangeQuery("volumeInfo.pageCount").gte(300));
        subBuilder.should(QueryBuilders.rangeQuery("saleInfo.listPrice.amount").gte(30));
        queryBuilder.must(subBuilder);
        sourceBuilder.query(queryBuilder);
        sourceBuilder.sort(new FieldSortBuilder("saleInfo.listPrice.amount").order(SortOrder.ASC));
        sourceBuilder.from(0);
        sourceBuilder.size(5);
        String[] includeFields = {"id", "volumeInfo.title", "volumeInfo.authors", "volumeInfo.publishedDate",
                "volumeInfo.pageCount", "volumeInfo.printType", "saleInfo.isEbook", "saleInfo.listPrice"};
        sourceBuilder.fetchSource(includeFields, Strings.EMPTY_ARRAY);
        sourceBuilder.timeout(new TimeValue(1, TimeUnit.SECONDS));

        SearchRequest searchRequest = new SearchRequest(BOOK);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResp = highClient.search(searchRequest, RequestOptions.DEFAULT);
        log.info("Result: {}", searchResp);
        SearchHit[] searchHitArr = searchResp.getHits().getHits();
        for (SearchHit searchHit : searchHitArr) {
            log.info("Book: {}", searchHit.getSourceAsString());
        }
    }

    @Test
    void count() throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.wildcardQuery("volumeInfo.title" + KEYWORD, "*Java*"));
        CountRequest countRequest = new CountRequest();
        countRequest.indices(BOOK);
        countRequest.source(sourceBuilder);
        CountResponse countResponse = highClient.count(countRequest, RequestOptions.DEFAULT);
        log.info("Count: {}", countResponse.getCount());
    }

    @Test
    void aggregation() throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("byAuthor").field("volumeInfo.authors" + KEYWORD);
        aggregation.subAggregation(AggregationBuilders.sum("totalPrice").field("saleInfo.listPrice.amount"));
        aggregation.subAggregation(AggregationBuilders.avg("avgPrice").field("saleInfo.listPrice.amount"));
        aggregation.order(BucketOrder.aggregation("totalPrice", false)).size(5);
        sourceBuilder.aggregation(aggregation);

        SearchRequest searchRequest = new SearchRequest(BOOK);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = highClient.search(searchRequest, RequestOptions.DEFAULT);
        log.info("Result: {}", searchResponse);

        Aggregations aggregations = searchResponse.getAggregations();
        Terms terms = aggregations.get("byAuthor");
        terms.getBuckets().forEach(bucket -> {
            Sum sum = bucket.getAggregations().get("totalPrice");
            Avg avg = bucket.getAggregations().get("avgPrice");
            log.info("Author: {}, bookCount: {}, totalPrice: {}, avgPrice: {}", bucket.getKey(), bucket.getDocCount(), sum.getValue(), avg.getValue());
        });
    }

    @Test
    void scroll() throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("volumeInfo.title", "Spring Framework"));
        sourceBuilder.size(2);
        sourceBuilder.fetchSource(new String[]{"volumeInfo.title"}, Strings.EMPTY_ARRAY);

        SearchRequest searchRequest = new SearchRequest(BOOK);
        Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
        searchRequest.scroll(scroll);
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = highClient.search(searchRequest, RequestOptions.DEFAULT);
        String scrollId = searchResponse.getScrollId();
        SearchHit[] searchHits = searchResponse.getHits().getHits();

        while (searchHits != null && searchHits.length > 0) {
            log.info("ScrollId: {}", scrollId);
            for (SearchHit searchHit : searchHits) {
                log.info("Score: {}, book: {}", searchHit.getScore(), searchHit.getSourceAsMap());
            }
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            scrollRequest.scroll(scroll);
            searchResponse = highClient.scroll(scrollRequest, RequestOptions.DEFAULT);
            scrollId = searchResponse.getScrollId();
            searchHits = searchResponse.getHits().getHits();
        }

        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        ClearScrollResponse clearScrollResponse = highClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        log.info("Clear scroll: {}", clearScrollResponse.isSucceeded());
    }

    @Test
    void script() throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("category", "Computers");
        params.put("author", "Ramon Wartala");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        String queryStr = "doc.containsKey('volumeInfo.categories') && doc['volumeInfo.categories.keyword'].size() > 0 && doc['volumeInfo.categories.keyword'].value.contains(params.category)";
        Script queryScript = new Script(ScriptType.INLINE, "painless", queryStr, params);
        queryBuilder.must(QueryBuilders.scriptQuery(queryScript));
        sourceBuilder.query(queryBuilder);

        String sortStr = "doc.containsKey('volumeInfo.authors') && doc['volumeInfo.authors.keyword'].size() > 0 && doc['volumeInfo.authors.keyword'].contains(params.author) ? 1 : 0";
        Script sortScript = new Script(ScriptType.INLINE, "painless", sortStr, params);
        sourceBuilder.sort(new ScriptSortBuilder(sortScript, ScriptSortBuilder.ScriptSortType.NUMBER).order(SortOrder.DESC));
        sourceBuilder.fetchSource(new String[]{"id", "volumeInfo.categories", "volumeInfo.authors"}, Strings.EMPTY_ARRAY);

        SearchRequest searchRequest = new SearchRequest(BOOK);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResp = highClient.search(searchRequest, RequestOptions.DEFAULT);
        log.info("Result: {}", searchResp);
        SearchHit[] searchHitArr = searchResp.getHits().getHits();
        for (SearchHit searchHit : searchHitArr) {
            log.info("Book: {}", searchHit.getSourceAsString());
        }
    }
}
