package com.mongodb.geojson;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Store {

    @Id
    String id;
    String name;
    String street;
    String city;
    GeoJsonPoint location;
}
