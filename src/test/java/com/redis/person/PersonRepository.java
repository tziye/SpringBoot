package com.redis.person;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Circle;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.List;

public interface PersonRepository extends CrudRepository<Person, String>, QueryByExampleExecutor<Person> {

    List<Person> findByFirstnameAndLastname(String firstname, String lastname);

    List<Person> findByFirstnameOrLastname(String firstname, String lastname);

    Page<Person> findPersonByLastname(String lastname, Pageable page);

    List<Person> findByAddress_City(String city);

    List<Person> findByAddress_LocationWithin(Circle circle);
}
