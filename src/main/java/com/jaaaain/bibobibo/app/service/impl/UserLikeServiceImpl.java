package com.jaaaain.bibobibo.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jaaaain.bibobibo.app.service.UserLikeService;
import com.jaaaain.bibobibo.dal.entity.UserLike;
import com.jaaaain.bibobibo.dal.mapper.UserLikeMapper;
import com.jaaaain.bibobibo.middleware.redis.CommentRedisRepo;
import com.jaaaain.bibobibo.security.auth.AuthHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserLikeServiceImpl extends ServiceImpl<UserLikeMapper, UserLike> implements UserLikeService {
    private final CommentRedisRepo commentRedisRepo;

    @Override
    public Set<Long> getLikedCommentIds(Long currentUid, List<Long> commentIds) {
        LambdaQueryWrapper<UserLike> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(UserLike::getUid, currentUid)
                .eq(UserLike::getTargetType, 2)
                .in(UserLike::getTargetId, commentIds)
                .eq(UserLike::getStatus, 1);
        List<UserLike> likedComments = this.list(queryWrapper);
        return likedComments.stream().map(UserLike::getTargetId).collect(Collectors.toSet());
    }

    @Override
    public Set<Long> getDislikedCommentIds(Long currentUid, List<Long> commentIds) {
        LambdaQueryWrapper<UserLike> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(UserLike::getUid, currentUid)
                .eq(UserLike::getTargetType, 2)
                .in(UserLike::getTargetId, commentIds)
                .eq(UserLike::getStatus, -1);
        List<UserLike> dislikedComments = this.list(queryWrapper);
        return dislikedComments.stream().map(UserLike::getTargetId).collect(Collectors.toSet());
    }

    private UserLike getUserLike(Long currentUid,Integer targetType, Long targetId) {
        LambdaQueryWrapper<UserLike> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(UserLike::getUid, currentUid)
                .eq(UserLike::getTargetType, targetType)
                .eq(UserLike::getTargetId, targetId);
        return this.getOne(queryWrapper);
    }

    @Override
    public void cancelLikeComment(Long id) {
        Long currentUid = AuthHelper.getCurrent().getId();
        UserLike userLike = getUserLike(currentUid, 2, id);
        if(userLike == null || userLike.getStatus() == 0){
            return;
        }
        userLike.setStatus(0);
        this.updateById(userLike);
        if(userLike.getStatus() == 1){ // 已点赞
            commentRedisRepo.updateCommentLikeCount(id, -1);
        }
        // todo MQ通知
    }

    @Override
    public void likeComment(Long id) {
        Long currentUid = AuthHelper.getCurrent().getId();
        UserLike userLike = getUserLike(currentUid, 2, id);
        if(userLike != null){
            if(userLike.getStatus() == 1){ // 已点赞
                return;
            }
            if(userLike.getStatus() == -1){ // 已点踩
                throw new IllegalArgumentException("不能同时点踩和点赞");
            }
            if(userLike.getStatus() == 0){ // 未点赞
                userLike.setStatus(1);
                this.updateById(userLike);
            }
        }else{
            UserLike like = new UserLike();
            like.setUid(currentUid);
            like.setTargetType(2);
            like.setTargetId(id);
            like.setStatus(1);
            this.save(like);
        }

        commentRedisRepo.updateCommentLikeCount(id, 1);
        // todo MQ通知
    }

    @Override
    public void dislikeComment(Long id) {
        Long currentUid = AuthHelper.getCurrent().getId();
        LambdaQueryWrapper<UserLike> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(UserLike::getUid, currentUid)
                .eq(UserLike::getTargetType, 2)
                .eq(UserLike::getTargetId, id);
        UserLike userLike = this.getOne(queryWrapper);
        if(userLike != null){
            if(userLike.getStatus() == -1){ // 已点踩
                return;
            }
            if(userLike.getStatus() == 1){ // 已点赞
                throw new IllegalArgumentException("不能同时点踩和点赞");
            }
            if(userLike.getStatus() == 0){ // 未点踩
                userLike.setStatus(-1);
                this.updateById(userLike);
            }
        }else{
            UserLike like = new UserLike();
            like.setUid(currentUid);
            like.setTargetType(2);
            like.setTargetId(id);
            like.setStatus(-1);
            this.save(like);
        }
        // todo MQ通知
    }
}
