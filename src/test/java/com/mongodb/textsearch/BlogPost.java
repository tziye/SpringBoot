package com.mongodb.textsearch;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TextScore;

import java.util.List;

@Document
@Data
public class BlogPost {
    @Id
    String id;

    @TextIndexed(weight = 3)
    String title;

    @TextIndexed(weight = 2)
    String content;

    @TextIndexed
    List<String> categories;

    @TextScore
    Float score;
}
