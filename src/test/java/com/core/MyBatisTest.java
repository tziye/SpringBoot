package com.core;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mybatis.MyUser;
import com.mybatis.MyUserExample;
import com.mybatis.MyUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.Arrays;
import java.util.List;

class MyBatisTest extends ApplicationTest {

    @Autowired
    private MyUserMapper myUserMapper;

    @Test
    void selectOneByExample() {
        MyUserExample example = new MyUserExample();
        example.createCriteria().andNameEqualTo("A");
        MyUser user = myUserMapper.selectOneByExample(example);
        log(user);
    }

    @Test
    void page() {
        MyUserExample example = new MyUserExample();
        example.createCriteria()
                .andNameEqualTo("A")
                .example()
                .page(1, 5);
        List<MyUser> users = myUserMapper.selectByExample(example);
        log(users);
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
        log(users);
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
        myUserMapper.batchInsert(Arrays.asList(user1, user2));
        List<MyUser> users = myUserMapper
                .selectByExample(MyUserExample.newAndCreateCriteria().andNameIn(Arrays.asList("X", "Y")).example());
        log(users);
    }

    @Test
    void selective() {
        MyUserExample example = new MyUserExample();
        example.createCriteria().andNameEqualTo("A");
        List<MyUser> users = myUserMapper.selectByExampleSelective(example, MyUser.Column.name, MyUser.Column.age);
        log(users);
    }

    @Test
    @Rollback
    void version() {
        MyUser user = myUserMapper.selectByPrimaryKey(1);
        user.setAge(200);
        myUserMapper.updateWithVersionByPrimaryKey(user.getVersion(), user);
        user = myUserMapper.selectByPrimaryKey(1);
        log(user);
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
        log(user);
    }

    @Test
    void pageHelper() {
        Page<MyUser> page = PageHelper.startPage(1, 5).doSelectPage(
                () -> myUserMapper.selectByExample(MyUserExample.newAndCreateCriteria().andNameEqualTo("A").example()));
        log(page.getTotal());
        log(page);
    }

}
