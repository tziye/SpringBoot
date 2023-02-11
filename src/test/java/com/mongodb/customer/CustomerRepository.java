package com.mongodb.customer;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public interface CustomerRepository extends CrudRepository<Customer, ObjectId> {

    List<Customer> findByLastname(String lastname, Sort sort);

    GeoResults<Customer> findByAddressLocationNear(Point point, Distance distance);

    @Query("{}")
    Stream<Customer> findAllByCustomQueryWithStream();

    Collection<CustomerDto> findAllDtoedBy();

    Collection<CustomerSummary> findAllProjectedBy();

    <T> T findProjectedById(ObjectId id, Class<T> projection);

    Page<CustomerSummary> findPagedProjectedBy(Pageable pageable);
}