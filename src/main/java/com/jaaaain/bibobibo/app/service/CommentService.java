package com.jaaaain.bibobibo.app.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jaaaain.bibobibo.app.data.CommentData;
import com.jaaaain.bibobibo.dal.entity.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {
    // 基于 cursor 的 feed 查询：cursor 为时间戳（毫秒）或 null（从最新开始），返回指定数量的根评论
    List<CommentData.CommentVO> getRootCommentsByFeed(Long vid, String sortType, String cursor, Integer size);


    Long getCount(Long vid);

    Page<Comment> getReplies(Long rootId, Integer page, Integer size);

    List<CommentData.CommentVO> convertToCommentVO(List<Comment> allComments);

    CommentData.CommentVO createComment(CommentData.CreateDto createDto);

    void deleteComment(Comment comment);

    List<Comment> getTopComment(Long vid);

    Double getScore(Long vid, String sortType, Long id);
}
