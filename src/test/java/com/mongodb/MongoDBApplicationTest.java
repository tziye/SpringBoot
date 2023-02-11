package com.mongodb;

import com.mongodb.customer.*;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.projection.TargetAware;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@ActiveProfiles("unit")
@DataMongoTest
class MongoDBApplicationTest {

    @Autowired
    private MongoTemplate template;
    @Autowired
    private CustomerRepository repository;

    private Customer dave, oliver, carter;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
        dave = new Customer("Dave", "Matthews", new Address(new Point(10, 10), "street1", "110"));
        oliver = new Customer("Oliver August", "Matthews", new Address(new Point(20, 20), "street2", "120"));
        carter = new Customer("Carter", "Beauford", new Address(new Point(30, 30), "street3", "119"));
        repository.saveAll(Arrays.asList(dave, oliver, carter));
    }

    @Test
    void setsIdOnSave() {
        Customer mary = repository.save(new Customer("Dave", "Matthews"));
        log.info("result: {}", mary);
        Assertions.assertThat(mary.getId()).isNotNull();
    }

    @Test
    void findCustomersUsingSort() {
        List<Customer> result = repository.findByLastname("Matthews", Sort.by(Sort.Order.asc("firstname")));
        log.info("result: {}", result);
        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result.get(0)).isEqualTo(dave);
        Assertions.assertThat(result.get(1)).isEqualTo(oliver);
    }

    @Test
    void exposesGeoSpatialFunctionality() {
        GeospatialIndex indexDefinition = new GeospatialIndex("address.location");
        indexDefinition.getIndexOptions().put("min", -180);
        indexDefinition.getIndexOptions().put("max", 180);
        template.indexOps(Customer.class).ensureIndex(indexDefinition);

        Customer ollie = new Customer("Oliver", "Gierke");
        ollie.setAddress(new Address(new Point(52.52548, 13.41477)));
        ollie = repository.save(ollie);
        log.info("ollie: {}", ollie);

        Point referenceLocation = new Point(52.51790, 13.41239);
        Distance oneKilometer = new Distance(1, Metrics.KILOMETERS);

        GeoResults<Customer> result = repository.findByAddressLocationNear(referenceLocation, oneKilometer);
        log.info("result: {}", result);
        Assertions.assertThat(result.getContent()).hasSize(1);

        Distance distanceToFirstStore = result.getContent().get(0).getDistance();
        Assertions.assertThat(distanceToFirstStore.getMetric()).isEqualTo(Metrics.KILOMETERS);
        Assertions.assertThat(distanceToFirstStore.getValue()).isCloseTo(0.862, Offset.offset(0.001));
    }

    @Test
    public void findCustomersAsStream() {
        try (Stream<Customer> result = repository.findAllByCustomQueryWithStream()) {
            result.forEach(c -> log.info("result: {}", c));
        }
    }

    @Test
    public void projectsToDto() {
        Collection<CustomerDto> result = repository.findAllDtoedBy();
        log.info("result: {}", result);
        Assertions.assertThat(result).hasSize(3);
        Assertions.assertThat(result.iterator().next().getFirstname()).isEqualTo("Dave");
    }

    @Test
    public void projectsEntityIntoInterface() {
        Collection<CustomerSummary> result = repository.findAllProjectedBy();
        log.info("result: {}", result);
        Assertions.assertThat(result).hasSize(3);
        Assertions.assertThat(result.iterator().next().getFullName()).isEqualTo("Dave Matthews");
    }

    @Test
    public void projectsIndividualDynamically() {
        CustomerSummary result = repository.findProjectedById(dave.getId(), CustomerSummary.class);
        log.info("result: {}", result);
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getFullName()).isEqualTo("Dave Matthews");
        Assertions.assertThat(((TargetAware) result).getTarget()).isInstanceOf(Customer.class);
    }

    @Test
    public void supportsProjectionInCombinationWithPagination() {
        Page<CustomerSummary> page = repository
                .findPagedProjectedBy(PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "lastname")));
        log.info("result: {}", page);
        Assertions.assertThat(page.getContent().get(0).getFullName()).isEqualTo("Carter Beauford");
    }

}
