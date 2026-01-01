package com.jaaaain.bibobibo.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jaaaain.bibobibo.app.data.VideoData;
import com.jaaaain.bibobibo.app.service.VideoService;
import com.jaaaain.bibobibo.common.enums.VideoEnums;
import com.jaaaain.bibobibo.common.utils.OSSUtil;
import com.jaaaain.bibobibo.common.utils.RedisUtil;
import com.jaaaain.bibobibo.dal.entity.Video;
import com.jaaaain.bibobibo.dal.mapper.VideoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    private final VideoMapper videoMapper;
    private final RedisUtil redisUtil;
    private final OSSUtil ossUtil;

    @Override
    public Video createDraft(String url) {
        Video video = new Video();
        video.setVideoUrl(url);
        video.setState(VideoEnums.State.DRAFT);
        // todo 访问阿里云，ffmpeg计算duration
        try (InputStream inputStream = ossUtil.getInputStream(url)) {
            // 使用FFmpeg计算视频时长
            // ...
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        save(video);
        return video;
    }

    @Override
    public Page<Video> getPageByQuery(Page<Video> page, VideoData.Query query) {
        LambdaQueryWrapper<Video> wrapper = Wrappers.lambdaQuery(Video.class);

        // 基本等值查询条件
        wrapper.eq(query.getUid() != null, Video::getUid, query.getUid());
        wrapper.eq(query.getType() != null, Video::getType, query.getType());
        wrapper.eq(query.getVisible() != null, Video::getVisible, query.getVisible());
        wrapper.eq(query.getState() != null, Video::getState, query.getState());

        // 字符串模糊查询
        if (StringUtils.hasText(query.getTitleLike())) {
            wrapper.like(Video::getTitle, query.getTitleLike());
        }
        if (StringUtils.hasText(query.getTitle())) {
            wrapper.eq(Video::getTitle, query.getTitle());
        }
        if (StringUtils.hasText(query.getIntroduction())) {
            wrapper.eq(Video::getIntroduction, query.getIntroduction());
        }
        if (StringUtils.hasText(query.getTags())) {
            wrapper.eq(Video::getTags, query.getTags());
        }

        // 数值范围查询
        if (query.getDuration() != null) {
            wrapper.eq(Video::getDuration, query.getDuration());
        } else {
            wrapper.ge(query.getDurationMin() != null, Video::getDuration, query.getDurationMin());
            wrapper.le(query.getDurationMax() != null, Video::getDuration, query.getDurationMax());
        }

        // 时间范围查询
        if (query.getReleaseTime() != null) {
            wrapper.eq(Video::getReleaseTime, query.getReleaseTime());
        } else {
            wrapper.ge(query.getReleaseTimeMin() != null, Video::getReleaseTime, query.getReleaseTimeMin());
            wrapper.le(query.getReleaseTimeMax() != null, Video::getReleaseTime, query.getReleaseTimeMax());
        }

        if (query.getUpdateTime() != null) {
            wrapper.eq(Video::getUpdateTime, query.getUpdateTime());
        } else {
            wrapper.ge(query.getUpdateTimeMin() != null, Video::getUpdateTime, query.getUpdateTimeMin());
            wrapper.le(query.getUpdateTimeMax() != null, Video::getUpdateTime, query.getUpdateTimeMax());
        }

        // 排序处理
        if (StringUtils.hasText(query.getSort())) {
            if ("asc".equalsIgnoreCase(query.getOrder())) {
                wrapper.orderByAsc(Video::getUpdateTime); // 默认按更新时间升序
            } else {
                wrapper.orderByDesc(Video::getUpdateTime); // 默认按更新时间降序
            }
        } else {
            // 默认排序
            wrapper.orderByDesc(Video::getUpdateTime);
        }

        return videoMapper.selectPage(page, wrapper);
    }

}