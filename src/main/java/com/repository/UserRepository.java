package com.repository;

import com.pojo.entity.User;
import com.pojo.vo.UserVo;
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
    List<User> jpqlFindByObject(User us);

    @Query("select new com.pojo.vo.UserVo(u.id, u.name, u.age, u.gender, u.enabled, u.time) from User u where u.name = :name")
    List<UserVo> jpqlFindVoByName(@Param("name") String name);

    List<User> findTop10By(Sort sort);

    @Query(value = "select * from user where name = :name",
            countQuery = "select count(*) from user where name = :name", nativeQuery = true)
    Page<User> findByNamePage(String name, Pageable pageable);

    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    @Query(value = "select * from user where name = ?1", nativeQuery = true)
    List<User> cache(String name);

    @Lock(LockModeType.READ)
    @Transactional(rollbackOn = Exception.class)
    @Query(value = "select u from User u where u.name = ?1")
    List<User> hqlLock(String name);

    @Modifying
    @Transactional(rollbackOn = Exception.class)
    @Query(value = "update user set age =:age where name = :name", nativeQuery = true)
    int updateAgeByName(String name, int age);

    @Modifying
    @Transactional(rollbackOn = Exception.class)
    @Query(value = "delete from user where name = :name", nativeQuery = true)
    int deleteByName(String name);
}