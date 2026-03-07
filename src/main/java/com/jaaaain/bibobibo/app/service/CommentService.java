package com.jaaaain.bibobibo.app.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jaaaain.bibobibo.app.data.CommentData;
import com.jaaaain.bibobibo.dal.entity.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {
    List<CommentData.CommentVO> getRootCommentsByFeed(Long vid, String sortType, Integer page, Integer size);

    Long getCount(Long vid);

    Page<Comment> getReplies(Long rootId, Integer page, Integer size);

    List<CommentData.CommentVO> convertToCommentVO(List<Comment> allComments);

    CommentData.CommentVO createComment(CommentData.CreateDto createDto);

    void deleteComment(Comment comment);

    List<Comment> getTopComment(Long vid);
}
