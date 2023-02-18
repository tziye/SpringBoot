package com.core.jpa;

import com.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Types;
import java.util.List;

@Slf4j
class SqlQueryTest extends RepositoryTest {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void entityManager() {
        List<UserDto> users = entityManager.createQuery(
                "select new com.dto.UserDto(u.id, u.name, u.age, u.gender, u.enabled, u.time) from User u where u.name = ?1",
                UserDto.class).setParameter(1, "A").getResultList();
        log.info("result: {}", users);
    }

    @Test
    void jdbcTemplate() {
        List<UserDto> users = jdbcTemplate.query("select id, name, age, gender, enabled, time from user where name = ?",
                new Object[]{"A"}, new int[]{Types.VARCHAR}, new BeanPropertyRowMapper<>(UserDto.class));
        log.info("result: {}", users);
    }

}
