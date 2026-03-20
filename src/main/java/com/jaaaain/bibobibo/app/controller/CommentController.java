package com.jaaaain.bibobibo.app.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jaaaain.bibobibo.app.data.CommentData;
import com.jaaaain.bibobibo.app.service.CommentService;
import com.jaaaain.bibobibo.app.service.UserLikeService;
import com.jaaaain.bibobibo.app.service.VideoService;
import com.jaaaain.bibobibo.common.PageResult;
import com.jaaaain.bibobibo.common.Result;
import com.jaaaain.bibobibo.dal.entity.Comment;
import com.jaaaain.bibobibo.dal.entity.Video;
import com.jaaaain.bibobibo.security.auth.AuthHelper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.LocalDateTime;

import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;
    private final VideoService videoService;
    private final UserLikeService userLikeService;

    @GetMapping("/list")
    @Operation(summary = "获取根评论列表", description = "feed流获取视频的根评论列表")
    public Result<PageResult<CommentData.CommentVO>> getRootComments(
            @RequestParam Long vid,
            @RequestParam(defaultValue = "hot") String sortType,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") Integer size
    ){
        Video video = videoService.getById(vid);
        if (video == null) {
            return Result.failed("视频不存在");
        }
        List<CommentData.CommentVO> items = commentService.getRootCommentsByFeed(vid, sortType, cursor, size);
        boolean hasMore = items.size() == size;
        String nextCursor = null;
        if(hasMore){
            CommentData.CommentVO last = items.get(items.size() - 1);
            Double score = commentService.getScore(vid, sortType, last.getId());
            nextCursor = String.valueOf(score);
        }
        return Result.success(PageResult.of(0L, items, nextCursor, hasMore));
    }

    @GetMapping("/count")
    @Operation(summary = "获取评论总数", description = "获取视频的评论总数")
    public Result<Long> getCommentCount(@RequestParam Long vid) {
        Video video = videoService.getById(vid);
        if(video == null){
            return Result.failed("视频不存在");
        }
        Long count = commentService.getCount(vid);
        return Result.success(count);
    }

    @GetMapping("/replies")
    @Operation(summary = "获取子评论列表", description = "分页获取某条评论的回复列表")
    public Result<PageResult<CommentData.CommentVO>> getReplies(
            @RequestParam Long rootId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "5") Integer size
    ) {
        Page<Comment> result = commentService.getReplies(rootId, page, size);
        return Result.success(PageResult.of(result.getTotal(), commentService.convertToCommentVO(result.getRecords())));
    }

    @PostMapping("/create")
    @Operation(summary = "发表评论", description = "发表根评论或回复评论")
    public Result<CommentData.CommentVO> createComment(@RequestBody @Valid CommentData.CreateDto createDto) {
        CommentData.CommentVO commentVO = commentService.createComment(createDto);
        return Result.success(commentVO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除评论", description = "删除自己的评论或管理员删除评论")
    public Result<Void> deleteComment(@PathVariable Long id) {
        Comment comment = commentService.getById(id);
        if(comment == null){
            return Result.failed("评论不存在");
        }
        if(!AuthHelper.isSelfOrAdmin(comment.getUid()) && !AuthHelper.isSelf(comment.getVid())){ // 非评论作者或视频作者
            return Result.failed("无权限");
        }
        commentService.deleteComment(comment);
        return Result.success();
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "点赞评论", description = "点赞或取消点赞评论")
    public Result<Void> likeComment(@PathVariable Long id) {
        userLikeService.likeComment(id);
        return Result.success();
    }
    @PostMapping("/{id}/cancel-like")
    @Operation(summary = "取消点赞评论", description = "取消点赞评论")
    public Result<Void> cancelLikeComment(@PathVariable Long id) {
        userLikeService.cancelLikeComment(id);
        return Result.success();
    }

    @PostMapping("/{id}/bad")
    @Operation(summary = "点踩评论", description = "点踩或取消点踩评论")
    public Result<Void> dislikeComment(@PathVariable Long id) {
        userLikeService.dislikeComment(id);
        return Result.success();
    }

    @PostMapping("/{id}/cancel-bad")
    @Operation(summary = "取消点踩评论", description = "取消点踩评论")
    public Result<Void> cancelDislikeComment(@PathVariable Long id) {
        userLikeService.cancelDislikeComment(id);
        return Result.success();
    }

    @PostMapping("/{id}/top")
    @Operation(summary = "置顶评论", description = "视频作者置顶评论")
    public Result<Void> topComment(@PathVariable Long id) {
        Comment comment = commentService.getById(id);
        if(comment == null){
            return Result.failed("评论不存在");
        }
        if(comment.getRootId() != 0){
            return Result.failed("不能置顶二级评论");
        }
        Video video = videoService.getById(comment.getVid());
        if(!AuthHelper.isSelfOrAdmin(video.getUid())){
            return Result.failed("无权限");
        }
        List<Comment> topComments = commentService.getTopComment(comment.getVid());
        if(topComments.size() >= 3){
            return Result.failed("最多只能置顶3条评论");
        }
        comment.setIsTop(1);
        commentService.updateById(comment);
        return Result.success();
    }

    @PostMapping("/{id}/untop")
    @Operation(summary = "取消置顶评论", description = "视频作者取消置顶评论")
    public Result<Void> untopComment(@PathVariable Long id) {
        Comment comment = commentService.getById(id);
        if(comment == null){
            return Result.failed("评论不存在");
        }
        Video video = videoService.getById(comment.getVid());
        if(!AuthHelper.isSelfOrAdmin(video.getUid())){
            return Result.failed("无权限");
        }
        comment.setIsTop(0);
        commentService.updateById(comment);
        return Result.success();
    }
}
