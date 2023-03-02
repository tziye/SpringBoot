package com.mybatis;

import com.common.util.MyUtil;
import com.dto.GenderEnum;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mybatis.model.MyUser;
import com.mybatis.model.MyUserExample;
import com.mybatis.model.MyUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@EnableAutoConfiguration(exclude = {
        KafkaAutoConfiguration.class, RabbitAutoConfiguration.class,
        RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class,
        MongoAutoConfiguration.class, MongoRepositoriesAutoConfiguration.class,
        ElasticsearchDataAutoConfiguration.class, ElasticsearchRepositoriesAutoConfiguration.class})
@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.allow-bean-definition-overriding=true")
class MyBatisTest {

    @MapperScan("com.mybatis.model")
    @TestConfiguration
    static class MyBatisConfig {
    }

    @Autowired
    MyUserMapper myUserMapper;

    @BeforeEach
    void setUp() {
        myUserMapper.deleteByExample(new MyUserExample());
        for (int i = 0; i < 100; i++) {
            MyUser user = new MyUser();
            user.setName(String.valueOf((char) (65 + MyUtil.random(5))));
            user.setAge(MyUtil.random(100));
            user.setGender(MyUtil.random() ? GenderEnum.BOY.getValue() : GenderEnum.GIRL.getValue());
            user.setEnabled(MyUtil.random());
            user.setTime(new Date());
            myUserMapper.insertSelective(user);
        }
    }

    @Test
    void selectOne() {
        MyUserExample example = new MyUserExample();
        example.createCriteria().andNameEqualTo("A");
        MyUser user = myUserMapper.selectOneByExample(example);
        log.info("Result: {}", user);
    }

    @Test
    void page() {
        MyUserExample example = new MyUserExample();
        example.createCriteria()
                .andNameEqualTo("A")
                .example()
                .page(1, 5);
        List<MyUser> users = myUserMapper.selectByExample(example);
        log.info("Result: {}", users);
    }

    @Test
    void enhanced() {
        MyUserExample example = MyUserExample.newAndCreateCriteria()
                .when(Math.random() > 0.5, criteria -> criteria.andNameEqualTo("A"))
                .when(Math.random() <= 0.5, criteria -> criteria.andNameEqualTo("E"))
                .andIdNotEqualToColumn(MyUser.Column.age)
                .example()
                .orderBy(MyUser.Column.age.asc(), MyUser.Column.gender.desc())
                .limit(2);
        List<MyUser> users = myUserMapper.selectByExample(example);
        log.info("Result: {}", users);
    }

    @Test
//    @Rollback
    void batchInsert() {
        MyUser user1 = new MyUser();
        user1.setName("X");
        user1.setAge(26);
        MyUser user2 = new MyUser();
        user2.setName("Y");
        user2.setAge(27);
        myUserMapper.batchInsertSelective(Arrays.asList(user1, user2), MyUser.Column.name, MyUser.Column.age);
        List<MyUser> users = myUserMapper
                .selectByExample(MyUserExample.newAndCreateCriteria().andNameIn(Arrays.asList("X", "Y")).example());
        log.info("Result: {}", users);
    }

    @Test
    void selective() {
        MyUserExample example = new MyUserExample();
        example.createCriteria().andNameEqualTo("A");
        List<MyUser> users = myUserMapper.selectByExampleSelective(example, MyUser.Column.name, MyUser.Column.age);
        log.info("Result: {}", users);
    }

    @Test
    @Rollback
    void version() {
        MyUser user = myUserMapper.selectOneByExample(new MyUserExample());
        user.setAge(200);
        myUserMapper.updateWithVersionByPrimaryKey(user.getVersion(), user);
        user = myUserMapper.selectByPrimaryKey(user.getId());
        log.info("Result: {}", user);
    }

    @Test
    @Rollback
    void upsert() {
        MyUser user = new MyUser();
        user.setId(1);
        user.setName("X");
        user.setAge(26);
        myUserMapper.upsertSelective(user);
        user = myUserMapper.selectByPrimaryKey(1);
        log.info("Result: {}", user);
    }

    @Test
    void pageHelper() {
        Page<MyUser> page = PageHelper.startPage(1, 5).doSelectPage(
                () -> myUserMapper.selectByExample(MyUserExample.newAndCreateCriteria().andNameEqualTo("A").example()));
        log.info("Result: {}", page.getResult());
    }

}
