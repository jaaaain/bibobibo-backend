package com.jaaaain.bibobibo.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jaaaain.bibobibo.dal.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    Long getCount(Long vid);
}
