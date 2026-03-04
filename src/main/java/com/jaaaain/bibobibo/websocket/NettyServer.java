package com.jaaaain.bibobibo.websocket;

import com.jaaaain.bibobibo.security.filter.TokenAuthFilter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NettyServer {

    private EventLoopGroup boss;
    private EventLoopGroup worker;
    private Channel serverChannel;

    @Autowired
    private DanmakuHandler danmakuHandler;

    @PostConstruct
    public void start() throws InterruptedException {
        // 主从结构
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        // 绑定监听端口
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();// 处理流
                        pipeline.addLast(new HttpServerCodec()) // 添加 Http 编码解码器
                            .addLast(new HttpObjectAggregator(1024 * 64)) // 对 Http 消息做聚合操作方便处理，产生 FullHttpRequest 和 FullHttpResponse
                            // todo 身份校验
                            .addLast(new WebSocketServerProtocolHandler("/ws/danmaku")) // 添加 WebSocket 支持
                            .addLast(new NettyWebSocketHandler(danmakuHandler)); // 处理 WebSocket 消息
                    }
                });

        serverChannel = bootstrap.bind(7071).sync().channel();
    }

    @PreDestroy
    public void stop() {
        if (serverChannel != null) {
            serverChannel.close();
        }
        boss.shutdownGracefully();
        worker.shutdownGracefully();
    }
}

