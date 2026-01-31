package com.jaaaain.bibobibo.app.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jaaaain.bibobibo.app.data.UserData;
import com.jaaaain.bibobibo.app.data.VideoData;
import com.jaaaain.bibobibo.app.service.UserService;
import com.jaaaain.bibobibo.app.service.VideoService;
import com.jaaaain.bibobibo.common.enums.VideoEnums;
import com.jaaaain.bibobibo.dal.entity.User;
import com.jaaaain.bibobibo.dal.entity.Video;
import com.jaaaain.bibobibo.dal.mapper.VideoMapper;
import com.jaaaain.bibobibo.middleware.mq.message.VideoProgressMessage;
import com.jaaaain.bibobibo.middleware.mq.producer.VideoProgressProducer;
import com.jaaaain.bibobibo.security.auth.AuthHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    private final VideoMapper videoMapper;
    private final UserService userService;
    private final VideoProgressProducer videoProgressProducer;

    @Override
    public VideoData.DraftVO createDraft(String url, String title) {
        Video video = new Video();
        UserData.AuthDto authDto = AuthHelper.getCurrent();
        video.setUid(authDto.getId());
        video.setTitle(title);
        video.setVideoUrl(url);
        video.setVisible(VideoEnums.Visible.PUBLIC);
        video.setState(VideoEnums.State.DRAFT);

        save(video);
        VideoData.DraftVO vo = new VideoData.DraftVO();
        BeanUtil.copyProperties(video, vo, true);
        return vo;
    }
    public void publish(Video video) {
        // 判断状态
        switch (video.getState()){
            case DRAFT:
                break;
            case REVIEWING:
                throw new RuntimeException("视频正在审核中");
            case APPROVED:
                System.out.println("视频已发布1");
                log.debug("视频已发布2");
                throw new RuntimeException("视频已发布3");
            case VIOLATION_DELETE:
                throw new RuntimeException("视频违规被删除");
        }

        video.setState(VideoEnums.State.REVIEWING); // 在消息队列中进行后续操作并送给阿里云审核
        video.setReleaseTime(LocalDateTime.now());
        updateById(video);

        // 发送消息,异步处理
        VideoProgressMessage msg = new VideoProgressMessage(video.getId());
        if(video.getCoverUrl()!=null){
            msg.setNeedCover(false);
        }
        videoProgressProducer.send(msg);
    }

    @Override
    public Page<VideoData.CardVO> QueryCardByPage(VideoData.Query query) {
        // 1. 固定条件
        query.setVisible(VideoEnums.Visible.PUBLIC); // 只获取公开的视频
        query.setState(VideoEnums.State.APPROVED); // 只获取通过的视频
        // 2. 分页查 video
        Page<Video> page = getPageByQuery(new Page<>(query.getPage(), query.getSize()), query);
        if (page.getRecords().isEmpty()) {
            return new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        }
        // 3. 批量收集 uid / videoId
        List<Video> videos = page.getRecords();
        Set<Long> userIds = videos.stream()
                .map(Video::getUid)
                .collect(Collectors.toSet());
        Set<Long> videoIds = videos.stream()
                .map(Video::getId)
                .collect(Collectors.toSet());
        // 4. 批量查询用户简略信息
        Map<Long, UserData.BriefVO> userMap = userService.getBriefMapByIds(userIds);
        // 5. 批量查询视频统计（当前可空实现 / mock）
        Map<Long, VideoData.StatVO> statMap = getVideoStatMap(videoIds);
        // 6. 组装 CardVO
        List<VideoData.CardVO> records = videos.stream().map(video -> {
            VideoData.CardVO vo = new VideoData.CardVO();
            BeanUtil.copyProperties(video,vo,true);
            vo.setOwner(userMap.get(video.getUid()));
            vo.setStat(statMap.get(video.getId()));
            return vo;
        }).toList();

        // 7. 返回 Page<VO>
        Page<VideoData.CardVO> result = new Page<>();
        result.setCurrent(page.getCurrent());
        result.setSize(page.getSize());
        result.setTotal(page.getTotal());
        result.setRecords(records);

        return result;
    }

    @Override
    public VideoData.DraftVO getDraftById(Long id) {
        Video video = getById(id);
        if(video == null){
            throw new RuntimeException("视频不存在");
        }
        // 校验该视频是否属于当前用户
        UserData.AuthDto authDto = AuthHelper.getCurrent();
        if(!video.getUid().equals(authDto.getId())){
            throw new RuntimeException("无权限");
        }
        VideoData.DraftVO vo = new VideoData.DraftVO();
        BeanUtil.copyProperties(video, vo, true);
        return vo;
    }

    private Map<Long, VideoData.StatVO> getVideoStatMap(Set<Long> videoIds) {
        if(videoIds.isEmpty()){
            return Collections.emptyMap();
        }
        // todo 查库获取视频统计数据
        return videoIds.stream().collect(Collectors.toMap(
                vid -> vid, this::statistics
                ));
    }

    @Override
    public VideoData.DetailVO getDetailById(Long id) {
        Video video = getById(id);
        if (video == null) {
            throw new RuntimeException("视频不存在");
        }
        log.info("video state:{}", video.getState());
        checkPermission(video);

        VideoData.DetailVO detailVO = new VideoData.DetailVO();
        BeanUtil.copyProperties(video, detailVO, true);
        User owner = userService.getById(video.getUid());
        // 作者信息卡片
        UserData.CardVO ownerCardVO = userService.buildCard(owner.getId());
        detailVO.setOwner(ownerCardVO);
        // 视频统计信息
        VideoData.StatVO statVO = statistics(video.getId());
        detailVO.setStatVO(statVO);
        return detailVO;
    }

    private VideoData.StatVO statistics(Long id) {
        // todo Redis + 定时落库
        VideoData.StatVO statVO = new VideoData.StatVO();
        statVO.setCoin(0L);
        statVO.setDanmaku(0L);
        statVO.setFavorite(0L);
        statVO.setLike(0L);
        statVO.setPlay(0L);
        statVO.setShare(0L);
        return statVO;
    }
    /** 校验权限 */
    private void checkPermission(Video video) {
        boolean isOwnerOrAdmin = AuthHelper.isSelfOrAdmin(video.getUid());
        switch (video.getState()) {
            case APPROVED -> {
                // 对于已审核通过的视频，如果是私有且当前用户不是作者或管理员，则无访问权限
                if (video.getVisible() == VideoEnums.Visible.PRIVATE && !isOwnerOrAdmin) {
                    throw new RuntimeException("无访问权限");
                }
            }
            case DRAFT, REVIEWING -> {
                // 对于草稿和审核中视频，只有作者或管理员才能访问
                if (!isOwnerOrAdmin) {
                    throw new RuntimeException("无访问权限");
                }
            }
            case VIOLATION_DELETE -> {
                // 对于违规删除的视频，只有作者或管理员才能访问
                if (!isOwnerOrAdmin) {
                    throw new RuntimeException("视频已被删除");
                }
            }
            default -> {
                // 其他未知状态，为了安全起见，只允许作者和管理员访问
                if (!isOwnerOrAdmin) {
                    throw new RuntimeException("视频状态异常");
                }
            }
        }
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