package com.mongodb.geojson;

import org.springframework.data.geo.Polygon;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StoreRepository extends CrudRepository<Store, String> {

    List<Store> findByLocationWithin(Polygon polygon);
}