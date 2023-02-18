package com.mongodb.textsearch;

import com.mongodb.MongoDBApplicationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@Slf4j
class TextSearchTest extends MongoDBApplicationTest {

    @TestConfiguration
    static class TextSearchConfig {

        @Autowired
        MongoOperations mongoOperations;

        @Bean
        Jackson2RepositoryPopulatorFactoryBean repositoryPopulator() {

            Jackson2RepositoryPopulatorFactoryBean factoryBean = new Jackson2RepositoryPopulatorFactoryBean();
            factoryBean.setResources(new Resource[]{new ClassPathResource("mongodb/blog.json")});
            return factoryBean;
        }

        @PostConstruct
        void postConstruct() {
            IndexResolver resolver = IndexResolver.create(mongoOperations.getConverter().getMappingContext());
            resolver.resolveIndexFor(BlogPost.class).forEach(mongoOperations.indexOps(BlogPost.class)::ensureIndex);
        }

        @PreDestroy
        void dropTestDB() {
            mongoOperations.dropCollection(BlogPost.class);
        }
    }

    @Autowired
    BlogPostRepository blogPostRepository;

    @Test
    void matchingAny() {
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny("release");
        List<BlogPost> blogPosts1 = blogPostRepository.findAllBy(criteria);
        log.info("1-Count: {}, List: {}", blogPosts1.size(), blogPosts1);

        List<BlogPost> blogPosts2 = mongoTemplate.find(Query.query(criteria), BlogPost.class);
        log.info("2-Count: {}, List: {}", blogPosts2.size(), blogPosts2);
    }

    @Test
    void notMatching() {
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny("release").notMatching("engineering");
        List<BlogPost> blogPosts = blogPostRepository.findAllBy(criteria);
        log.info("Count: {}, List: {}", blogPosts.size(), blogPosts);
    }

    @Test
    void matchingPhrase() {
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase("release candidate");
        List<BlogPost> blogPosts = blogPostRepository.findAllBy(criteria);
        log.info("Count: {}, List: {}", blogPosts.size(), blogPosts);
    }

    @Test
    void scoreOrder() {
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase("release candidate");
        List<BlogPost> blogPosts1 = blogPostRepository.findAllByOrderByScoreDesc(criteria);
        log.info("1-Count: {}, List: {}", blogPosts1.size(), blogPosts1);

        TextQuery query = new TextQuery(criteria);
        query.setScoreFieldName("score");
        query.sortByScore();
        List<BlogPost> blogPosts2 = mongoTemplate.find(query, BlogPost.class);
        log.info("2-Count: {}, List: {}", blogPosts2.size(), blogPosts2);
    }

}
