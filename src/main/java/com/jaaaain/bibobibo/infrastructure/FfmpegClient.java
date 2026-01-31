package com.jaaaain.bibobibo.infrastructure;

import cn.hutool.core.lang.UUID;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jaaaain.bibobibo.app.data.VideoData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class FfmpegClient {

    @Value("${ffmpeg.ffmpeg-path}")
    private String ffmpegPath;

    @Value("${ffmpeg.ffprobe-path}")
    private String ffprobePath;

    @Value("${ffmpeg.local-cover-path}")
    private String localCoverPath;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // =====================================视频元信息========================================

    /**
     * 通过视频 url 解析视频元信息
     */
    public VideoData.Meta analyzeVideo(String videoUrl) {
        try {
            String json = probe(videoUrl);
            return parseForVideoMeta(json);
        } catch (Exception e) {
            log.error("ffprobe 解析视频元信息失败, url={}", videoUrl, e);
            throw new RuntimeException("解析视频信息失败", e);
        }
    }

    /**
     * 执行 ffprobe，返回 JSON 原文
     */
    private String probe(String videoUrl) throws Exception {
        List<String> command = List.of(
                ffprobePath,
                "-v", "error",
                "-print_format", "json",
                "-show_format",
                "-show_streams",
                "-analyzeduration", "1000000",
                "-probesize", "1000000",
                videoUrl
        );
        return exec(command);
    }


    /**
     * 解析 JSON 为 VideoMeta
     */
    private VideoData.Meta parseForVideoMeta(String json) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(json);

        JsonNode formatNode = root.path("format");
        JsonNode streamsNode = root.path("streams");

        Double duration = formatNode.path("duration").asDouble(0);
        Long bitrate = formatNode.path("bit_rate").asLong(0);

        Integer width = null;
        Integer height = null;
        String videoCodec = null;
        String audioCodec = null;

        for (JsonNode stream : streamsNode) {
            switch (stream.path("codec_type").asText()) {
                case "video" -> {
                    if (width == null) {
                        width = stream.path("width").asInt();
                        height = stream.path("height").asInt();
                        videoCodec = stream.path("codec_name").asText();
                    }
                }
                case "audio" -> {
                    if (audioCodec == null) {
                        audioCodec = stream.path("codec_name").asText();
                    }
                }
            }
        }

        return VideoData.Meta.builder()
                .duration(duration)
                .width(width)
                .height(height)
                .videoCodec(videoCodec)
                .audioCodec(audioCodec)
                .bitrate(bitrate)
                .build();
    }

    // ====================================视频封面========================================

    /**
     * 生成视频封面（提取内嵌封面或截取视频帧）
     */
    public String generateCover(String videoUrl) {
        String uuid = UUID.randomUUID().toString();
        String outputImage = localCoverPath + uuid + ".jpg";
        try {
            boolean hasAttachedPic = hasAttachedPic(videoUrl);
            if (hasAttachedPic) {
                log.info("提取内嵌封面，videoUrl: {}, outputImage: {}", videoUrl, outputImage);
                extractAttachedPic(videoUrl, outputImage);
            } else {
                log.info("截取视频帧，videoUrl: {}, outputImage: {}", videoUrl, outputImage);
                extractFrame(videoUrl, outputImage);
            }

            return outputImage;
        } catch (Exception e) {
            log.error("生成视频封面失败，videoUrl: {}, outputImage: {}", videoUrl, outputImage, e);
            throw new RuntimeException("生成视频封面失败", e);
        }
    }

    /**
     * ffprobe 判断是否有内嵌封面
     */
    private boolean hasAttachedPic(String videoUrl) throws Exception {
        List<String> command = List.of(
                ffprobePath,
                "-v", "error",
                "-print_format", "json",
                "-show_streams",
                videoUrl
        );

        String json = exec(command);

        JsonNode root = objectMapper.readTree(json);
        for (JsonNode stream : root.path("streams")) {
            if ("video".equals(stream.path("codec_type").asText())
                    && stream.path("disposition").path("attached_pic").asInt() == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 提取内嵌封面
     */
    private void extractAttachedPic(String videoUrl, String output) throws Exception {
        List<String> command = List.of(
                ffmpegPath,
                "-y",
                "-i", videoUrl,
                "-map", "0:v",
                "-c", "copy",
                output
        );
        exec(command);
    }

    /**
     * 截取第 1 秒的视频帧
     */
    private void extractFrame(String videoUrl, String output) throws Exception {
        List<String> command = List.of(
                ffmpegPath,
                "-y",
                "-loglevel", "error",
                "-ss", "1",
                "-i", videoUrl,
                "-frames:v", "1",
                output
        );
        exec(command);
    }

    /**
     * 通用命令执行
     */
    private String exec(List<String> command) throws Exception {
        log.info("FFmpeg 执行命令：{}", command);

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        StringBuilder output = new StringBuilder();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                output.append(line).append('\n');
            }
        }

        boolean finished = process.waitFor(15, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new TimeoutException("ffmpeg 执行超时");
        }

        if (process.exitValue() != 0) {
            throw new IllegalStateException("ffmpeg 执行失败: " + output);
        }

        return output.toString();
    }

}
