package com.jaaaain.bibobibo.app.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jaaaain.bibobibo.app.data.CommentData;
import com.jaaaain.bibobibo.app.data.UserData;
import com.jaaaain.bibobibo.app.service.CommentService;
import com.jaaaain.bibobibo.app.service.UserLikeService;
import com.jaaaain.bibobibo.app.service.UserService;
import com.jaaaain.bibobibo.app.service.VideoService;
import com.jaaaain.bibobibo.common.Result;
import com.jaaaain.bibobibo.dal.entity.Comment;
import com.jaaaain.bibobibo.dal.entity.Video;
import com.jaaaain.bibobibo.dal.mapper.CommentMapper;
import com.jaaaain.bibobibo.middleware.redis.CommentRedisRepo;
import com.jaaaain.bibobibo.security.auth.AuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    private final CommentMapper commentMapper;
    private final UserService userService;
    private final CommentRedisRepo commentRedisRepo;
    private final UserLikeService userLikeService;
    private final VideoService videoService;
    @Override
    public List<CommentData.CommentVO> getRootCommentsByFeed(Long vid, String sortType, Integer page, Integer size) {
        // 1. 查询置顶评论 (isTop=1, rootId=0)
        List<Comment> topComments = new ArrayList<>();
        if(page == null || page <= 0){
            topComments = this.getTopComment(vid);
        }

        // 2. 查询普通根评论 (isTop=0, rootId=0) 并排序
        Set<Long> commentIds = commentRedisRepo.getCommentFeed(vid, sortType, page * size, (page + 1) * size);
        LambdaQueryWrapper<Comment> normalWrapper = Wrappers.lambdaQuery();
        normalWrapper.in(Comment::getId, commentIds)
                .eq(Comment::getIsTop, 0);
        List<Comment> normalComments = this.list(normalWrapper);

        // 合并列表：置顶在前，普通在后
        List<Comment> allRootComments = new java.util.ArrayList<>();
        allRootComments.addAll(topComments);
        allRootComments.addAll(normalComments);

        if (allRootComments.isEmpty()) {
            return List.of();
        }
        return this.convertToCommentVO(allRootComments);
    }

    @Override
    public List<CommentData.CommentVO> convertToCommentVO(List<Comment> allComments) {
        // 批量查询用户信息
        Set<Long> userIds = allComments.stream().map(Comment::getUid).collect(Collectors.toSet());
        Map<Long, UserData.BriefVO> userMap = userService.getBriefMapByIds(userIds);

        // 用户行为状态映射 (like/dislike)
        Long currentUid = AuthHelper.getCurrent().getId();
        List<Long> commentIds = allComments.stream().map(Comment::getId).toList();
        Set<Long> likedCommentIds = userLikeService.getLikedCommentIds(currentUid, commentIds);
        Set<Long> dislikedCommentIds = userLikeService.getDislikedCommentIds(currentUid, commentIds);

        return allComments.stream().map(comment -> {
            CommentData.CommentVO vo = new CommentData.CommentVO();
            BeanUtil.copyProperties(comment, vo);
            vo.setUser(userMap.get(comment.getUid()));
            vo.setReplyToUser(userMap.get(comment.getToUid()));
            vo.setIsLiked(likedCommentIds.contains(comment.getId()));
            vo.setIsBad(dislikedCommentIds.contains(comment.getId()));
            vo.setIsUpOwner(comment.getUid().equals(comment.getVid()));
            return vo;
        }).toList();
    }

    @Override
    public Long getCount(Long vid) {
        return commentMapper.getCount(vid);
    }

    @Override
    public Page<Comment> getReplies(Long rootId, Integer page, Integer size) {
        LambdaQueryWrapper<Comment> replyWrapper = Wrappers.lambdaQuery();
        replyWrapper.eq(Comment::getRootId, rootId);
        replyWrapper.orderByDesc(Comment::getCreateTime);
        return this.page(new Page<>(page, size), replyWrapper);
    }

    @Override
    public CommentData.CommentVO createComment(CommentData.CreateDto createDto) {
        Video video = videoService.getById(createDto.getVid());
        if(video == null){
            throw new IllegalArgumentException("视频不存在");
        }
        Comment comment = new Comment();
        BeanUtil.copyProperties(createDto, comment);
        comment.setRootId(createDto.getRootId() != null ? createDto.getRootId() : 0L);
        comment.setParentId(createDto.getParentId() != null ? createDto.getParentId() : 0L);
        comment.setUid(AuthHelper.getCurrent().getId());
        commentMapper.insert(comment);
        if(comment.getRootId() == 0){ // 根评论
            commentRedisRepo.addComment(comment);
            // todo MQ通知
        }else if(comment.getParentId() == null) {
            throw new IllegalArgumentException("子评论不能没有父评论");
        }else{
            Comment parentComment = this.getById(comment.getParentId());
            if(parentComment == null){
                throw new IllegalArgumentException("回复的评论不存在");
            }
            parentComment.setReplyCount(parentComment.getReplyCount() + 1);
            commentMapper.updateById(parentComment);
            // todo MQ通知
        }
        return null;
    }

    @Override
    public void deleteComment(Comment comment) {
        commentMapper.deleteById(comment.getId());
        if(comment.getParentId() != null){
            Comment parentComment = this.getById(comment.getParentId());
            parentComment.setReplyCount(parentComment.getReplyCount() - 1);
            commentMapper.updateById(parentComment);
        }
        commentRedisRepo.deleteComment(comment.getVid(), comment.getId());
    }

    @Override
    public List<Comment> getTopComment(Long vid) {
        LambdaQueryWrapper<Comment> topWrapper = Wrappers.lambdaQuery();
        topWrapper.eq(Comment::getVid, vid)
                .eq(Comment::getRootId, 0)
                .eq(Comment::getIsTop, 1);
        return this.list(topWrapper);
    }
}
