package com.jaaaain.bibobibo.dal.mapper;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jaaaain.bibobibo.dal.entity.User;
import org.apache.ibatis.annotations.Mapper;


@Mapper
@TableName("users")
public interface UserMapper extends BaseMapper<User> {
}

