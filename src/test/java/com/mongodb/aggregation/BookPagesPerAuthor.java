package com.mongodb.aggregation;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class BookPagesPerAuthor {

    @Id
    String author;
    int totalPageCount;
    int approxWritten;
}
