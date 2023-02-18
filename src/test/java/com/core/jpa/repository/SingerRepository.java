package com.core.jpa.repository;

import com.core.jpa.entity.Singer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SingerRepository extends CrudRepository<Singer, Integer> {

    List<Singer> findByName(String name);
}
