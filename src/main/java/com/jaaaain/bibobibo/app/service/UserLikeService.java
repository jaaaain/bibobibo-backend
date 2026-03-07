package com.jaaaain.bibobibo.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jaaaain.bibobibo.dal.entity.UserLike;

import java.util.List;
import java.util.Set;

public interface UserLikeService extends IService<UserLike> {
    Set<Long> getLikedCommentIds(Long currentUid, List<Long> commentIds);

    Set<Long> getDislikedCommentIds(Long currentUid, List<Long> commentIds);

    void cancelLikeComment(Long id);

    void likeComment(Long id);

    void dislikeComment(Long id);
}
