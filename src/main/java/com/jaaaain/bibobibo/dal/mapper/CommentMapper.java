package com.jaaaain.bibobibo.dal.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jaaaain.bibobibo.dal.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.LinkedHashSet;
import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    Long getCount(Long vid);

    List<Comment> listOrderByCommentId(LinkedHashSet<Long> commentIds, Long vid);
}
