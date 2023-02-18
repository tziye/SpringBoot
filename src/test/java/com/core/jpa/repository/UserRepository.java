package com.core.jpa.repository;

import com.core.jpa.entity.User;
import com.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import javax.transaction.Transactional;
import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer>, JpaSpecificationExecutor<User>, QueryByExampleExecutor<User> {

    List<User> findByNameAndAgeGreaterThan(String name, int age);

    @Query("select u from User u where u.name = :#{#us.name} and u.age > :#{#us.age}")
    List<User> findByDto(User us);

    @Query("select new com.dto.UserDto(u.id, u.name, u.age, u.gender, u.enabled, u.time) from User u where u.name = :name")
    List<UserDto> findDtoByName(@Param("name") String name);

    List<User> findTop10By(Sort sort);

    @Query(value = "select u from User u where u.name = :name", countQuery = "select count(u) from User u where u.name = :name")
    Page<User> findByNamePage(String name, Pageable pageable);

    @Modifying
    @Transactional(rollbackOn = Exception.class)
    @Query(value = "update User u set u.age =:age where u.name = :name")
    int updateAgeByName(String name, int age);

    @Modifying
    @Transactional(rollbackOn = Exception.class)
    @Query(value = "delete from user where name = :name", nativeQuery = true)
    int deleteByName(String name);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    @Query(value = "select u from User u where u.name = ?1")
    List<User> cache(String name);

    @Lock(LockModeType.READ)
    @Transactional(rollbackOn = Exception.class)
    @Query(value = "select u from User u where u.name = ?1")
    List<User> lock(String name);
}