package com.jaaaain.bibobibo.websocket;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;

public class NettyWsSessionAdapter implements WsSession {
    // 用于绑定会话对象session和 channel，避免重复创建
    // 确保了同一个 TCP 连接（Channel）在整个生命周期内，只对应唯一的一个 NettyWsSessionAdapter 对象
    private static final AttributeKey<NettyWsSessionAdapter> SESSION_KEY = AttributeKey.valueOf("WS_SESSION");

    private final Channel channel;

    private NettyWsSessionAdapter(Channel channel) {
        this.channel = channel;
    }

    public static NettyWsSessionAdapter get(Channel channel) {
        NettyWsSessionAdapter session = channel.attr(SESSION_KEY).get();
        if (session == null) {
            session = new NettyWsSessionAdapter(channel);
            channel.attr(SESSION_KEY).set(session);
        }
        return session;
    }

    @Override
    public String getSessionId() {
        return channel.id().asLongText();
    }

    @Override
    public void sendText(String text) {
        if (channel.isActive()) {
            channel.writeAndFlush(new TextWebSocketFrame(text));
        }
    }

    @Override
    public void close() {
        channel.close();
    }

    /**
     * 后续插入Set<WsSession>进行去重判断
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof NettyWsSessionAdapter)) return false;
        return this.channel.equals(((NettyWsSessionAdapter) obj).channel);
    }
    @Override
    public int hashCode() {
        return channel.hashCode();
    }

}

