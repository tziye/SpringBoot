package com.mongodb.textsearch;

import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BlogPostRepository extends CrudRepository<BlogPost, String> {

    List<BlogPost> findAllBy(TextCriteria criteria);

    List<BlogPost> findAllByOrderByScoreDesc(TextCriteria criteria);
}
