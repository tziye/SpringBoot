package com.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MyUserMapper {
    long countByExample(MyUserExample example);

    int deleteWithVersionByExample(@Param("version") Integer version, @Param("example") MyUserExample example);

    int deleteByExample(MyUserExample example);

    int deleteWithVersionByPrimaryKey(@Param("version") Integer version, @Param("key") Integer id);

    int deleteByPrimaryKey(Integer id);

    int insert(MyUser record);

    int insertSelective(@Param("record") MyUser record, @Param("selective") MyUser.Column... selective);

    MyUser selectOneByExample(MyUserExample example);

    MyUser selectOneByExampleSelective(@Param("example") MyUserExample example, @Param("selective") MyUser.Column... selective);

    List<MyUser> selectByExampleSelective(@Param("example") MyUserExample example, @Param("selective") MyUser.Column... selective);

    List<MyUser> selectByExample(MyUserExample example);

    MyUser selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") MyUser.Column... selective);

    MyUser selectByPrimaryKey(Integer id);

    int updateWithVersionByExample(@Param("version") Integer version, @Param("record") MyUser record, @Param("example") MyUserExample example);

    int updateWithVersionByExampleSelective(@Param("version") Integer version, @Param("record") MyUser record, @Param("example") MyUserExample example, @Param("selective") MyUser.Column... selective);

    int updateByExampleSelective(@Param("record") MyUser record, @Param("example") MyUserExample example, @Param("selective") MyUser.Column... selective);

    int updateByExample(@Param("record") MyUser record, @Param("example") MyUserExample example);

    int updateWithVersionByPrimaryKey(@Param("version") Integer version, @Param("record") MyUser record);

    int updateWithVersionByPrimaryKeySelective(@Param("version") Integer version, @Param("record") MyUser record, @Param("selective") MyUser.Column... selective);

    int updateByPrimaryKeySelective(@Param("record") MyUser record, @Param("selective") MyUser.Column... selective);

    int updateByPrimaryKey(MyUser record);

    int batchInsert(@Param("list") List<MyUser> list);

    int batchInsertSelective(@Param("list") List<MyUser> list, @Param("selective") MyUser.Column... selective);

    int upsert(MyUser record);

    int upsertSelective(@Param("record") MyUser record, @Param("selective") MyUser.Column... selective);
}