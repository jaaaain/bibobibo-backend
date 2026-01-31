package com.jaaaain.bibobibo.middleware.mq.producer;

import com.jaaaain.bibobibo.middleware.mq.VideoMQ;
import com.jaaaain.bibobibo.middleware.mq.message.VideoProgressMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/send")
public class VideoProgressProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping
    public String send(VideoProgressMessage msg) {
        rabbitTemplate.convertAndSend(VideoMQ.EXCHANGE,VideoMQ.RoutingKey.videoProgress,msg);
        return "发送成功：" + msg;
    }
}
