package com.core.jpa;

import com.core.jpa.entity.User;
import com.dto.GenderEnum;
import com.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.Date;
import java.util.List;

@Slf4j
class CriteriaTest extends RepositoryTest {

    @PersistenceContext
    EntityManager entityManager;

    @Test
    void specification() {
        Page<User> page = userRepository.findAll((Specification<User>) (root, query, criteria) -> query
                .where(criteria.and(
                                criteria.or(criteria.le(root.get("id"), 40), criteria.gt(root.get("id"), 60)),
                                criteria.in(root.get("name")).value("A").value("B"),
                                criteria.between(root.get("age"), 18, 28)),
                        criteria.lessThanOrEqualTo(root.get("time"), new Date()),
                        criteria.isTrue(root.get("enabled")),
                        criteria.equal(root.get("gender"), GenderEnum.BOY))
                .orderBy(criteria.desc(root.get("createTime")))
                .getRestriction(), PageRequest.of(0, 10));
        log.info("Total: {}, Pages: {}, Content: {}", page.getTotalElements(), page.getTotalPages(), page.getContent());
    }

    @Test
    void criteria() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        Root<User> root = criteria.from(User.class);
        criteria.select(root);
        criteria.where(builder.equal(root.get("name"), "A"));
        List<User> users = entityManager.createQuery(criteria).getResultList();
        log.info("result: {}", users);
    }

    @Test
    void select() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserDto> criteria = builder.createQuery(UserDto.class);
        Root<User> root = criteria.from(User.class);

        Path<Long> idPath = root.get("id");
        Path<String> namePath = root.get("name");
        criteria.multiselect(idPath, namePath);

        ParameterExpression<String> parameter = builder.parameter(String.class);
        criteria.where(builder.equal(root.get("name"), parameter));
        TypedQuery<UserDto> query = entityManager.createQuery(criteria);
        query.setParameter(parameter, "A");

        List<UserDto> list = query.getResultList();
        log.info("result: {}", list);
    }

    @Test
    void group() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteria = builder.createQuery(Tuple.class);
        Root<User> root = criteria.from(User.class);
        criteria.multiselect(root.get("name"), builder.count(root))
                .groupBy(root.get("name"));
        List<Tuple> list = entityManager.createQuery(criteria).getResultList();
        for (Tuple tuple : list) {
            String name = (String) tuple.get(0);
            Long count = (Long) tuple.get(1);
            log.info("name: {}, count: {}", name, count);
        }
    }

}
