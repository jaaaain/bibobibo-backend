package com.jaaaain.bibobibo.middleware.mq.consumer;

import com.jaaaain.bibobibo.app.data.UploadData;
import com.jaaaain.bibobibo.app.data.VideoData;
import com.jaaaain.bibobibo.app.service.UploadService;
import com.jaaaain.bibobibo.app.service.VideoService;
import com.jaaaain.bibobibo.common.enums.UploadEnums;
import com.jaaaain.bibobibo.common.enums.VideoEnums;
import com.jaaaain.bibobibo.dal.entity.Video;
import com.jaaaain.bibobibo.infrastructure.FfmpegClient;
import com.jaaaain.bibobibo.middleware.mq.VideoMQ;
import com.jaaaain.bibobibo.middleware.mq.message.VideoProgressMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class VideoProgressConsumer {

    private final VideoService videoService;
    private final FfmpegClient ffmpegClient;
    private final UploadService uploadService;

    @RabbitListener(queues = VideoMQ.Queue.videoProgress)
    public void handle(VideoProgressMessage msg) {
        log.debug("[消息队列 videoProgress] 开始视频处理 videoId={}", msg.getVideoId());
        Video video = videoService.getById(msg.getVideoId());
        if (video == null) {
            log.error("[消息队列 videoProgress] 视频不存在 videoId={}", msg.getVideoId());
            return;
        }

        try {
            // 1. 分析视频
            if (msg.getNeedAnalyze()) {
                VideoData.Meta meta = ffmpegClient.analyzeVideo(video.getVideoUrl());
                video.setDuration(meta.getDuration());
                // todo 其他视频元数据
            }

            // 2️. 生成封面
            if (msg.getNeedCover()) {
                String localCoverPath = ffmpegClient.generateCover(video.getVideoUrl());

                File localCoverFile = new File(localCoverPath);
                UploadData.UploadResultVO result = uploadService.upload(localCoverFile, UploadEnums.FileUploadTypeEnum.COVER);

                video.setCoverUrl(result.getPath());
                localCoverFile.delete();
            }

            // 3. 视频审核
            if(msg.getNeedReview() && video.getState() == VideoEnums.State.REVIEWING){
                // todo 送给阿里云审核
                video.setState(VideoEnums.State.APPROVED);
            }

            video.setReleaseTime(LocalDateTime.now());
            videoService.updateById(video);
            log.debug("[消息队列 videoProgress] 视频处理完成 videoId={}", video.getId());

        } catch (Exception e) {
            log.error("[消息队列 videoProgress] 视频处理失败 videoId={}", video.getId(), e);
            // todo 重试等
            throw e;
        }
    }
}
