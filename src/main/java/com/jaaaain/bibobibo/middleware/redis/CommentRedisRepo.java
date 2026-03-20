package com.jaaaain.bibobibo.middleware.redis;

import com.jaaaain.bibobibo.common.constants.RedisConstants;
import com.jaaaain.bibobibo.dal.entity.Comment;
import com.jaaaain.bibobibo.infrastructure.RedisClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommentRedisRepo {
    private final RedisClient redisClient;

    private static final String COMMENT_TIME_KEY = RedisConstants.COMMENT_TIME_KEY;
    private static final String COMMENT_HOT_KEY = RedisConstants.COMMENT_HOT_KEY;
    private static final String COMMENT_LIKE_COUNT_KEY = RedisConstants.COMMENT_LIKE_COUNT_KEY;

    public void addCommentLikeCount(Long commentId, Integer likeCount){
        redisClient.set(COMMENT_LIKE_COUNT_KEY + commentId, likeCount);
    }

    public Integer getCommentLikeCount(Long commentId){
        return redisClient.get(COMMENT_LIKE_COUNT_KEY + commentId, Integer.class);
    }
    public void updateCommentLikeCount(Long commentId, Integer increment){
        redisClient.incr(COMMENT_LIKE_COUNT_KEY + commentId, increment);
    }
    public List<Long> getAllKeys(String keyPrefix){
        return redisClient.getAllKeys(keyPrefix + "*");
    }


    public void addComment(Comment comment){
        // 时间排序
        redisClient.zAdd(COMMENT_TIME_KEY + comment.getVid(), comment.getId(), comment.getCreateTime().toEpochSecond(ZoneOffset.UTC));
        // 热度排序（初始值为0）
        redisClient.zAdd(COMMENT_HOT_KEY + comment.getVid(), comment.getId(), comment.getLikeCount()*3 + comment.getReplyCount()*2 + comment.getCreateTime().toEpochSecond(ZoneOffset.UTC)*0.1);
    }
    // 更新评论热度
    public void updateCommentHotScore(Comment comment){
        redisClient.zAdd(COMMENT_HOT_KEY + comment.getVid(), comment.getId(), comment.getLikeCount()*3 + comment.getReplyCount()*2 + comment.getCreateTime().toEpochSecond(ZoneOffset.UTC)*0.1);
    }

    public Set<Long> getCommentFeed(Long vid, String sortType, Integer start, Integer end){
        if("hot".equals(sortType)){
            return redisClient.zRevRange(COMMENT_HOT_KEY + vid, start, end);
        } else if ("time".equals(sortType)) {
            return redisClient.zRevRange(COMMENT_TIME_KEY + vid, start, end);
        } else {
            throw new IllegalArgumentException("Invalid sortType: " + sortType);
        }
    }
    public LinkedHashSet<Long> getCommentByCursor(Long vid, String sortType, Double cursor, int size){
        String key = "hot".equals(sortType) ? COMMENT_HOT_KEY + vid : COMMENT_TIME_KEY + vid;
        if(cursor == null){
            return redisClient.zRevRange(key, 0, size - 1);
        }
        return redisClient.zRevRangeByScore(key,Double.MIN_VALUE, cursor-1, size); // 热度从大到小，时间从新到旧也是从大到小
    }

    public void updateCommentHotScore(Long vid, Long commentId, Double increment){
        redisClient.zIncrBy(COMMENT_HOT_KEY + vid, increment, commentId);
    }

    public void deleteComment(Long vid, Long commentId){
        redisClient.zRemove(COMMENT_TIME_KEY + vid, commentId);
        redisClient.zRemove(COMMENT_HOT_KEY + vid, commentId);
        redisClient.delete(COMMENT_LIKE_COUNT_KEY + commentId);
    }

    public Double getScore(Long vid, String sortType, Long id) {
        if(sortType.equals("time")){
            return redisClient.zScore(COMMENT_TIME_KEY + vid, id);
        }else if(sortType.equals("hot")){
            return redisClient.zScore(COMMENT_HOT_KEY + vid, id);
        }else{
            throw new IllegalArgumentException("Invalid sortType: " + sortType);
        }
    }
}
