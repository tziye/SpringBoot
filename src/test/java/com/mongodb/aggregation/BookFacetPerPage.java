package com.mongodb.aggregation;

import lombok.Data;

import java.util.List;

@Data
public class BookFacetPerPage {

    BookFacetPerPageId id;
    int count;
    List<String> titles;
}
