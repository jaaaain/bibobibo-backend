package com.jaaaain.bibobibo.websocket;

import cn.hutool.json.JSONUtil;
import com.jaaaain.bibobibo.app.service.VideoService;
import com.jaaaain.bibobibo.dal.entity.Danmaku;
import com.jaaaain.bibobibo.dal.mapper.DanmakuMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class DanmakuHandler {

    // 存储每个视频的观看连接 <视频ID, Set<WsSession>>，用于广播消息
    private final ConcurrentHashMap<Long, Set<WsSession>> videoSessionMap = new ConcurrentHashMap<>();
    // 存储每个会话的视频ID <会话ID, 视频ID>，用于快速查找视频ID
    private final ConcurrentHashMap<String, Long> sessionVideoMap = new ConcurrentHashMap<>();

    private final DanmakuMapper danmakuMapper;

    /**
     * 用户加入视频观看
     */
    public void joinVideo(Long vid, WsSession session) {
        log.info("join video, vid={}, session={}", vid, session);
        videoSessionMap.computeIfAbsent(vid, k -> ConcurrentHashMap.newKeySet()).add(session);
        sessionVideoMap.put(session.getSessionId(), vid);
        broadcastViewerCount(vid);
    }

    /**
     * 用户离开视频
     */
    public void leaveVideo(WsSession session) {
        Long vid = sessionVideoMap.remove(session.getSessionId());
        if (vid == null) return;
        Set<WsSession> sessions = videoSessionMap.get(vid);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                videoSessionMap.remove(vid);
            }
        }
        broadcastViewerCount(vid);
    }

    /**
     * 发送弹幕
     */
    public void sendDanmaku(Long vid, Danmaku danmaku, WsSession session) {
        log.info("send danmaku, vid={}, danmaku={}, session={}", vid, danmaku, session);
        danmaku.setVid(vid);
        danmakuMapper.insert(danmaku);// todo 异步存储
        String msg = "{\"type\":\"danmaku\",\"data\":" + JSONUtil.toJsonStr(danmaku) + "}";
        broadcast(vid, msg, session);
    }

    /**
     * 向指定视频的所有观看者广播消息
     */
    public void broadcast(Long vid, String message, WsSession excludeSession) {
        Set<WsSession> sessions = videoSessionMap.get(vid);
        if (sessions == null) return;
        sessions.remove(excludeSession);
        List<WsSession> failedSessions = new ArrayList<>();
        for (WsSession session : sessions) {
            try {
                session.sendText(message);
            } catch (Exception e) { // 移除失败的会话
                log.warn("session error, will remove", e);
                failedSessions.add(session);
            }
        }
        failedSessions.forEach(this::leaveVideo);
    }

    /**
     * 当前观看人数
     */
    public int getViewerCount(Long vid) {
        Set<WsSession> sessions = videoSessionMap.get(vid);
        return sessions == null ? 0 : sessions.size();
    }

    /**
     * 广播观看人数
     */
    private void broadcastViewerCount(Long vid) {
        int count = getViewerCount(vid);
        log.info("broadcast viewer count, vid={}, count={}", vid, count);
        String msg = "{\"type\":\"viewerCount\",\"data\":" + count + "}";
        broadcast(vid, msg, null);
    }
}
