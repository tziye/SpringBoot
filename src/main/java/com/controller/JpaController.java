package com.controller;

import com.common.Result;
import com.google.common.collect.Lists;
import com.pojo.entity.*;
import com.pojo.vo.UserVo;
import com.repository.SingerRepository;
import com.repository.SongRepository;
import com.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.sql.Types;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/jpa")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JpaController {

    @PersistenceContext
    private EntityManager entityManager;

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final SingerRepository singerRepository;
    private final SongRepository songRepository;
    private final TransactionTemplate transactionTemplate;

    @GetMapping("/save")
    public Result<String> save() {
        List<User> userList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            User user = User.builder()
                    .name(String.valueOf((char) (65 + random.nextInt(5))))
                    .age(random.nextInt(100))
                    .gender(random.nextBoolean() ? GenderEnum.BOY : GenderEnum.GIRL)
                    .enabled(random.nextBoolean())
                    .time(new Date()).build();
            userList.add(user);
        }
        userRepository.saveAll(userList);
        return Result.success();
    }

    @GetMapping("/findByNameAndAge")
    public Result<List<User>> findByNameAndAge(@RequestParam("name") String name, @RequestParam("age") int age) {
        List<User> users = userRepository.findByNameAndAgeGreaterThan(name, age);
        return Result.success(users);
    }

    @GetMapping("/findByObject")
    public Result<List<User>> findByObject(@RequestParam("name") String name, @RequestParam("age") int age) {
        User user = User.builder().name(name).age(age).build();
        List<User> users = userRepository.jpqlFindByObject(user);
        return Result.success(users);
    }

    @GetMapping("/findVo")
    public Result<List<UserVo>> findVo(@RequestParam("name") String name) {
        List<UserVo> users = userRepository.jpqlFindVoByName(name);
        return Result.success(users);
    }

    @GetMapping("/sort")
    public Result<List<User>> sort() {
        Sort sort = Sort.by("name").ascending().and(Sort.by("age").descending());
        List<User> users = userRepository.findTop10By(sort);
        return Result.success(users);
    }

    @GetMapping("/page")
    public Result<Page<User>> page(@RequestParam("name") String name, @RequestParam("page") int page, @RequestParam("size") int size) {
        Page<User> users = userRepository.findByNamePage(name, PageRequest.of(page - 1, size));
        return Result.success(users);
    }

    @GetMapping("/example")
    public Result<List<User>> example(@RequestParam("name") String name, @RequestParam("age") int age) {
        User user = new User();
        user.setName(name);
        user.setAge(age);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", matcher1 -> matcher1.startsWith().ignoreCase())
                .withMatcher("age", ExampleMatcher.GenericPropertyMatcher::exact);
        Example<User> example = Example.of(user, matcher);
        List<User> users = Lists.newArrayList(userRepository.findAll(example));
        return Result.success(users);
    }

    @GetMapping("/specification")
    public Result<Page<User>> specification(@RequestParam("name") String name, @RequestParam("age") int age,
                                            @RequestParam("page") int page, @RequestParam("size") int size) {
        Page<User> getPage = userRepository.findAll((Specification<User>) (root, query, criteria) -> query
                .where(criteria.and(criteria.equal(root.get("name").as(String.class), name),
                        criteria.greaterThan(root.get("age").as(Integer.class), age)))
                .orderBy(criteria.desc(root.get("createTime").as(Date.class)))
                .getRestriction(), PageRequest.of(page - 1, size));
        return Result.success(getPage);
    }

    @GetMapping("/cache")
    public Result<List<User>> cache(@RequestParam("name") String name) {
        List<User> users = userRepository.cache(name);
        return Result.success(users);
    }

    @GetMapping("/lock")
    public Result<List<User>> lock(@RequestParam("name") String name) {
        List<User> users = userRepository.hqlLock(name);
        return Result.success(users);
    }

    @GetMapping("/entityManager")
    public Result<List<UserVo>> entityManager(@RequestParam("name") String name) {
        List<UserVo> users = entityManager.createQuery(
                "select new com.pojo.vo.UserVo(u.id, u.name, u.age, u.gender, u.enabled, u.time) from User u where u.name = ?1",
                UserVo.class).setParameter(1, name).getResultList();
        return Result.success(users);
    }

    @GetMapping("/jdbcTemplate")
    public Result<List<UserVo>> jdbcTemplate(@RequestParam("name") String name) {
        List<UserVo> users = jdbcTemplate.query("select id, name, age, gender, enabled, time from user where name = ?",
                new Object[]{name}, new int[]{Types.VARCHAR}, new BeanPropertyRowMapper<>(UserVo.class));
        return Result.success(users);
    }

    @GetMapping("/update")
    public Result<Integer> update(@RequestParam("name") String name, @RequestParam("age") int age) {
        int count = userRepository.updateAgeByName(name, age);
        return Result.success(count);
    }

    @GetMapping("/delete")
    public Result<Integer> delete(@RequestParam("name") String name) {
        int count = userRepository.deleteByName(name);
        return Result.success(count);
    }

    @GetMapping("/mappingSave")
    public Result<Singer> mappingSave() {
        Style love = Style.builder().style(Style.StyleEnum.情歌).build();
        Style rap = Style.builder().style(Style.StyleEnum.饶舌).build();

        Song s1 = Song.builder().name("以父之名").score(10).time(new Date()).build();
        Song s2 = Song.builder().name("夜曲").score(9).time(new Date()).build();

        Nickname nickname = Nickname.builder().name("Jay").build();
        Location location = Location.builder().country("中国").city("台北").build();

        Singer singer = Singer.builder().name("周杰伦").gender(GenderEnum.BOY).age(42).location(location)
                .song(new ArrayList<>(Arrays.asList(s1, s2))).style(new ArrayList<>(Arrays.asList(rap, love)))
                .nickname(nickname).build();

        s1.setBelongSinger(singer);
        s2.setBelongSinger(singer);
        nickname.setBelongSinger(singer);

        singerRepository.save(singer);
        return Result.success(singer);
    }

    @GetMapping("/mappingFindSinger")
    public Result<Singer> mappingFindSinger() {
        Singer result = singerRepository.findByName("周杰伦").get(0);
        return Result.success(result);
    }

    @GetMapping("/mappingFindSong")
    public Result<List<Song>> mappingFindSong() {
        List<Song> result = songRepository.findByNameIn(Arrays.asList("以父之名", "夜曲"));
        result.forEach(s -> log.info("{}", s));
        return Result.success(result);
    }

    @GetMapping("/mappingDelete")
    public Result<String> mappingDelete() {
        Singer singer = singerRepository.findByName("周杰伦").get(0);
        singerRepository.delete(singer);
        return Result.success();
    }

    @GetMapping("/transaction")
    public Result<List<User>> transaction() {
        User user = User.builder().name("tziye").age(25).build();
        try {
            transactionTemplate.execute((TransactionCallback) status -> {
                userRepository.save(user);
                return 1 / 0;
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Result.success(userRepository.findByNameAndAgeGreaterThan("tziye", 20));
    }

    @GetMapping("/transaction2")
    @Transactional(rollbackOn = Exception.class)
    public Result<List<User>> transaction2() {
        User user = User.builder().name("tziye").age(25).build();
        userRepository.save(user);
//        int a = 1 / 0;
        return Result.success(userRepository.findByNameAndAgeGreaterThan("tziye", 20));
    }

}
