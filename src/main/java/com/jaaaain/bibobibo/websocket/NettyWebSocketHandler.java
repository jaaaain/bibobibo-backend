package com.jaaaain.bibobibo.websocket;

import cn.hutool.json.JSONUtil;
import com.jaaaain.bibobibo.dal.entity.Danmaku;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final DanmakuHandler danmakuHandler;

    public NettyWebSocketHandler(DanmakuHandler danmakuHandler) {
        this.danmakuHandler = danmakuHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                TextWebSocketFrame frame) {

        NettyWsSessionAdapter session =
                NettyWsSessionAdapter.get(ctx.channel());

        WsRequest req;
        try {
            req = JSONUtil.toBean(frame.text(), WsRequest.class);
        } catch (Exception e) {
            log.warn("JSON parse error: {}", frame.text());
            return;
        }

        if (req == null || req.getType() == null) {
            return;
        }

        switch (req.getType()) {

            case "join":
                danmakuHandler.joinVideo(req.getVideoId(), session);
                break;

            case "leave":
                danmakuHandler.leaveVideo(session);
                break;

            case "danmaku":
                Danmaku danmaku = JSONUtil.toBean(JSONUtil.toJsonStr(req.getData()), Danmaku.class);
                danmakuHandler.sendDanmaku(req.getVideoId(), danmaku);
                break;

            case "heartbeat":
                break;

            default:
                log.warn("unknown type: {}", req.getType());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        danmakuHandler.leaveVideo(NettyWsSessionAdapter.get(ctx.channel()));
    }
}
