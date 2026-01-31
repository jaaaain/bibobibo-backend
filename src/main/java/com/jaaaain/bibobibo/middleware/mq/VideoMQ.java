package com.jaaaain.bibobibo.middleware.mq;

public interface VideoMQ {
    String EXCHANGE = "video.exchange";

    interface Queue {
        String videoProgress = "video.videoProgress";
    }

    interface RoutingKey {
        String videoProgress = "video.videoProgress";
    }
}
