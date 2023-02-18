package com.core.jpa;

import com.core.ApplicationTest;
import com.core.jpa.entity.User;
import com.core.jpa.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
class TransactionTest extends ApplicationTest {

    @Autowired
    TransactionTemplate transactionTemplate;
    @Autowired
    UserRepository userRepository;

    @Test
    void transaction() {
        User user = User.builder().name("tziye").age(25).build();
        Optional<User> optional = transactionTemplate.execute(status -> {
            userRepository.save(user);
            return userRepository.findById(user.getId());
        });
        log.info("result: {}", optional);
    }

    @Transactional(rollbackOn = Exception.class)
    @Test
    void transaction2() {
        User user = User.builder().name("tziye").age(25).build();
        userRepository.save(user);
        Optional<User> optional = userRepository.findById(user.getId());
        log.info("result: {}", optional);
    }

}
