package com.elasticsearch.conference;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.util.List;

@Data
@Builder
@Document(indexName = "conference-index")
public class Conference {

    @Id
    String id;

    String name;

    @Field(type = FieldType.Date)
    String date;

    GeoPoint location;

    List<String> keywords;

    int attendees;

}
