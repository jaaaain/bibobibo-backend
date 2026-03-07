package com.jaaaain.bibobibo.app.job;

import com.jaaaain.bibobibo.app.service.CommentService;
import com.jaaaain.bibobibo.app.service.VideoService;
import com.jaaaain.bibobibo.dal.entity.Comment;
import com.jaaaain.bibobibo.middleware.redis.CommentRedisRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static com.jaaaain.bibobibo.common.constants.RedisConstants.COMMENT_HOT_KEY;
import static com.jaaaain.bibobibo.common.constants.RedisConstants.COMMENT_LIKE_COUNT_KEY;

/**
 * 更新计数定时任务
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class updateCountJob {
    private final CommentService commentService;
    private final VideoService videoService;
    private final CommentRedisRepo commentRedisRepo;

    // 每30秒执行一次
    @Scheduled(cron = "0/30 * * * * ?")
    public void updateCommentCount() {
        log.info("[updateCommentCount] 开始更新评论点赞数");
        // 更新数据库评论点赞数
        List<Long> likeCountKeys = commentRedisRepo.getAllKeys(COMMENT_LIKE_COUNT_KEY + "*");
        for (Long likeCountKey : likeCountKeys) {
            Integer likeCount = commentRedisRepo.getCommentLikeCount(likeCountKey);
            Comment comment = commentService.getById(likeCountKey);
            if(comment == null){
                continue;
            }
            comment.setLikeCount(likeCount);
            commentService.updateById(comment);
        }
        log.info("[updateCommentCount] 更新评论点赞数完成");
        log.info("[updateCommentCount] 开始更新评论热度");
        // 更新ZSet评论热度
        List<Long> hotScoreKeys = commentRedisRepo.getAllKeys(COMMENT_HOT_KEY + "*");
        for (Long hotScoreKey : hotScoreKeys) {
            Set<Long> commentIds = commentRedisRepo.getCommentFeed(hotScoreKey, "time", 0, Integer.MAX_VALUE);
            for (Long commentId : commentIds) {
                Comment comment = commentService.getById(commentId);
                if(comment == null){
                    throw new IllegalArgumentException("评论不存在:" + commentId);
                }
                commentRedisRepo.updateCommentHotScore(comment);
            }
        }
        log.info("[updateCommentCount] 更新评论热度完成");

    }

}
