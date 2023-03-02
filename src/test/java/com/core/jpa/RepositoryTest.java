package com.core.jpa;

import com.common.util.MyUtil;
import com.core.ApplicationTest;
import com.core.jpa.entity.User;
import com.core.jpa.repository.UserRepository;
import com.dto.GenderEnum;
import com.dto.UserDto;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
class RepositoryTest extends ApplicationTest {

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            User user = User.builder()
                    .name(String.valueOf((char) (65 + MyUtil.random(5))))
                    .age(MyUtil.random(100))
                    .gender(MyUtil.random() ? GenderEnum.BOY : GenderEnum.GIRL)
                    .enabled(MyUtil.random())
                    .time(new Date()).build();
            userList.add(user);
        }
        userRepository.saveAll(userList);
    }

    @Test
    void findByNameAndAge() {
        List<User> users = userRepository.findByNameAndAgeGreaterThan("A", 28);
        log.info("result: {}", users);
    }

    @Test
    void findByDto() {
        User user = User.builder().name("A").age(28).build();
        List<User> users = userRepository.findByDto(user);
        log.info("result: {}", users);
    }

    @Test
    void findDto() {
        List<UserDto> users = userRepository.findDtoByName("A");
        log.info("result: {}", users);
    }

    @Test
    void sort() {
        Sort sort = Sort.by("name").ascending().and(Sort.by("age").descending());
        List<User> users = userRepository.findTop10By(sort);
        log.info("result: {}", users);
    }

    @Test
    void page() {
        Page<User> users = userRepository.findByNamePage("A", PageRequest.of(0, 10));
        log.info("Total: {}, Pages: {}, Content: {}", users.getTotalElements(), users.getTotalPages(), users.getContent());
    }

    @Test
    void example() {
        User user = new User();
        user.setName("A");
        user.setAge(28);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", m -> m.startsWith().ignoreCase())
                .withMatcher("age", ExampleMatcher.GenericPropertyMatcher::exact);
        Example<User> example = Example.of(user, matcher);
        List<User> users = Lists.newArrayList(userRepository.findAll(example));
        log.info("result: {}", users);
    }

    @Test
    void update() {
        int count = userRepository.updateAgeByName("A", 18);
        log.info("result: {}", count);
    }

    @Test
    void delete() {
        int count = userRepository.deleteByName("A");
        log.info("result: {}", count);
    }

    @Test
    void cache() {
        List<User> users = userRepository.cache("A");
        log.info("result: {}", users);
    }

    @Test
    void lock() {
        List<User> users = userRepository.lock("A");
        log.info("result: {}", users);
    }

}
