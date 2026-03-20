package com.jaaaain.bibobibo.middleware.mq;

public interface NotificationMQ {
    String EXCHANGE = "notification.exchange";

    interface Queue {
        String notification = "notification.notification";
    }

    interface RoutingKey {
        String notification = "notification.notification";
    }
}
