package com.mongodb.customer;

import com.mongodb.MongoDBApplicationTest;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.projection.TargetAware;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
class CustomerTest extends MongoDBApplicationTest {

    @Import(MetricsPostProcessor.class)
    @TestConfiguration
    static class MetricsConfig {
    }

    @Autowired
    CustomerRepository customerRepository;

    Customer dave, oliver, carter;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
        dave = new Customer("Dave", "Matthews", new Address(new Point(10, 10), "street1", "110"));
        oliver = new Customer("Oliver August", "Matthews", new Address(new Point(20, 20), "street2", "120"));
        carter = new Customer("Carter", "Beauford", new Address(new Point(30, 30), "street3", "119"));
        customerRepository.saveAll(Arrays.asList(dave, oliver, carter));
    }

    @Test
    void setIdOnSave() {
        Customer mary = customerRepository.save(new Customer("Dave", "Matthews"));
        log.info("result: {}", mary);
        Assertions.assertThat(mary.getId()).isNotNull();
    }

    @Test
    void sort() {
        List<Customer> result = customerRepository.findByLastname("Matthews", Sort.by(Sort.Order.asc("firstname")));
        log.info("result: {}", result);
        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result.get(0)).isEqualTo(dave);
        Assertions.assertThat(result.get(1)).isEqualTo(oliver);
    }

    @Test
    void geoSpatial() {
        GeospatialIndex indexDefinition = new GeospatialIndex("address.location");
        indexDefinition.getIndexOptions().put("min", -180);
        indexDefinition.getIndexOptions().put("max", 180);
        mongoTemplate.indexOps(Customer.class).ensureIndex(indexDefinition);

        Customer ollie = new Customer("Oliver", "Gierke");
        ollie.setAddress(new Address(new Point(52.52548, 13.41477)));
        ollie = customerRepository.save(ollie);
        log.info("ollie: {}", ollie);

        Point point = new Point(52.51790, 13.41239);
        Distance distance = new Distance(1, Metrics.KILOMETERS);

        GeoResults<Customer> result = customerRepository.findByAddressLocationNear(point, distance);
        log.info("result: {}", result);
        Assertions.assertThat(result.getContent()).hasSize(1);

        Distance distanceToFirstStore = result.getContent().get(0).getDistance();
        Assertions.assertThat(distanceToFirstStore.getMetric()).isEqualTo(Metrics.KILOMETERS);
        Assertions.assertThat(distanceToFirstStore.getValue()).isCloseTo(0.862, Offset.offset(0.001));
    }

    @Test
    void stream() {
        try (Stream<Customer> result = customerRepository.findAllByCustomQueryWithStream()) {
            result.forEach(c -> log.info("result: {}", c));
        }
    }

    @Test
    void projectToDto() {
        Collection<CustomerDto> result = customerRepository.findAllDtoedBy();
        log.info("result: {}", result);
        Assertions.assertThat(result).hasSize(3);
        Assertions.assertThat(result.iterator().next().getFirstname()).isEqualTo("Dave");
    }

    @Test
    void projectToInterface() {
        Collection<CustomerSummary> result = customerRepository.findAllProjectedBy();
        result.forEach(s -> log.info("result: {} {} {}", s.getFullName(), s.getAddress(), s));
        Assertions.assertThat(result).hasSize(3);
        Assertions.assertThat(result.iterator().next().getFullName()).isEqualTo("Dave Matthews");
    }

    @Test
    void projectDynamically() {
        CustomerSummary result = customerRepository.findProjectedById(dave.getId(), CustomerSummary.class);
        log.info("result: {}", result);
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getFullName()).isEqualTo("Dave Matthews");
        Assertions.assertThat(((TargetAware) result).getTarget()).isInstanceOf(Customer.class);
    }

    @Test
    void projectPage() {
        Page<CustomerSummary> page = customerRepository.findPagedProjectedBy(
                PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "lastname")));
        log.info("Total: {}, Pages: {}, List: {}", page.getTotalElements(), page.getTotalPages(), page.getContent());
        Assertions.assertThat(page.getContent().get(0).getFullName()).isEqualTo("Carter Beauford");
    }

}
