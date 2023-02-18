package com.mongodb.aggregation;

import com.common.util.MyUtil;
import com.mongodb.MongoDBApplicationTest;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Files;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
class AggregationTest extends MongoDBApplicationTest {

    @Autowired
    OrderRepository orderRepository;

    LineItem product1 = new LineItem("p1", 1.23);
    LineItem product2 = new LineItem("p2", 0.87, 2);
    LineItem product3 = new LineItem("p3", 5.33);
    Order order1 = new Order("c1", new Date()).addItem(product1).addItem(product2).addItem(product3);
    Order order2 = new Order("c1", new Date()).addItem(product1);
    Order order3 = new Order("c1", new Date()).addItem(product2);
    Order order4 = new Order("c2", new Date()).addItem(product1).addItem(product2);
    Order order5 = new Order("c2", new Date()).addItem(product2).addItem(product3);

    @BeforeEach
    void setup() throws IOException {
        orderRepository.deleteAll();
        orderRepository.saveAll(Arrays.asList(order1, order2, order3, order4, order5));
        mongoTemplate.dropCollection("book");
        String content = Files.contentOf(new ClassPathResource("mongodb/book.json").getFile(), StandardCharsets.UTF_8);
        Document wrapper = Document.parse("{wrapper: " + content + "}");
        List<Object> books = wrapper.getList("wrapper", Object.class);
        mongoTemplate.insert(books, "book");
    }

    @Test
    void totalOrdersForCustomer() {
        Long result = orderRepository.totalOrdersForCustomer("c1");
        log.info("result: {}", result);
        Assertions.assertThat(result).isEqualTo(3);
    }

    @Test
    void totalOrdersPerCustomer() {
        List<OrdersPerCustomer> result = orderRepository.totalOrdersPerCustomer(Sort.by(Sort.Order.desc("total")));
        log.info("result: {}", result);
        Assertions.assertThat(result).containsExactly(new OrdersPerCustomer("c1", 3L), new OrdersPerCustomer("c2", 2L));
    }

    @Test
    void aggregateInvoiceForOrder() {
        Invoice invoice = orderRepository.aggregateInvoiceForOrder(order1.getId(), 0.19, 1.19);
        log.info("result: {}", invoice);
    }

    @Test
    void getInvoiceFor() {
        double taxRate = 0.19;
        var results = mongoTemplate.aggregate(Aggregation.newAggregation(Order.class,
                Aggregation.match(Criteria.where("id").is(order1.getId())),
                Aggregation.unwind("items"),
                Aggregation.project("id", "customerId", "items")
                        .andExpression("'$items.price' * '$items.quantity'").as("lineTotal"),
                Aggregation.group("id")
                        .sum("lineTotal").as("netAmount")
                        .addToSet("items").as("items"),
                Aggregation.project("id", "netAmount", "items")
                        .and("orderId").previousOperation()
                        .andExpression("netAmount * [0]", taxRate).as("taxAmount")
                        .andExpression("netAmount * (1 + [0])", taxRate).as("totalAmount")
        ), Invoice.class);
        Invoice invoice = results.getUniqueMappedResult();
        log.info("result: {}", invoice);
    }

    @Test
    void pagesPerAuthor() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("volumeInfo.authors").exists(true)),
                Aggregation.replaceRoot("volumeInfo"),
                Aggregation.project("authors", "pageCount")
                        .and(ArithmeticOperators.valueOf("pageCount")
                                .divideBy(ArrayOperators.arrayOf("authors").length()))
                        .as("pagesPerAuthor"),
                Aggregation.unwind("authors"),
                Aggregation.group("authors")
                        .sum("pageCount").as("totalPageCount")
                        .sum("pagesPerAuthor").as("approxWritten"),
                Aggregation.sort(Sort.Direction.DESC, "totalPageCount"));

        AggregationResults<BookPagesPerAuthor> result = mongoTemplate.aggregate(aggregation, "book", BookPagesPerAuthor.class);
        log.info("result: {}", MyUtil.toString(result.getMappedResults()));
    }

    @Test
    void bookFacetPerPage() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.replaceRoot("volumeInfo"),
                Aggregation.match(Criteria.where("pageCount").exists(true)),
                Aggregation.bucketAuto("pageCount", 10)
                        .withGranularity(BucketAutoOperation.Granularities.SERIES_1_2_5)
                        .andOutput("title").push().as("titles")
                        .andOutput("titles").count().as("count"));
        AggregationResults<BookFacetPerPage> result = mongoTemplate.aggregate(aggregation, "book", BookFacetPerPage.class);
        log.info("result: {}", MyUtil.toString(result.getMappedResults()));
    }

    @Test
    void multipleFacets() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("volumeInfo.authors").exists(true).and("volumeInfo.publisher").exists(true)),
                Aggregation.facet().and(Aggregation.match(Criteria.where("saleInfo.listPrice").exists(true)),
                        Aggregation.replaceRoot("saleInfo"),
                        Aggregation.bucket("listPrice.amount").withBoundaries(1, 10, 50, 100)).as("prices")
                        .and(Aggregation.unwind("volumeInfo.authors"),
                                Aggregation.replaceRoot("volumeInfo"),
                                Aggregation.match(Criteria.where("authors").not().size(0)),
                                Aggregation.project().andExpression("substrCP(authors, 0, 1)").as("startsWith")
                                        .and("authors").as("author"),
                                Aggregation.bucketAuto("startsWith", 10)
                                        .andOutput("author").push().as("authors")
                        ).as("authors"));
        AggregationResults<Document> result = mongoTemplate.aggregate(aggregation, "book", Document.class);
        log.info("result: {}", MyUtil.toString(result.getMappedResults()));
    }

}