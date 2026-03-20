package com.jaaaain.bibobibo.middleware.mq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableRabbit
public class MqConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    // ==== 视频处理队列 ====
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(VideoMQ.EXCHANGE);
    }
    @Bean
    public Queue generateCoverQueue() {
        return new Queue(VideoMQ.Queue.videoProgress);
    }
    @Bean
    public Binding generateCoverBinding() {
        return BindingBuilder
                .bind(generateCoverQueue())
                .to(exchange())
                .with(VideoMQ.RoutingKey.videoProgress);
    }
    // ==== 通知队列 ====
    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(NotificationMQ.EXCHANGE);
    }
    @Bean
    public Queue notificationQueue() { // 队列
        return new Queue(NotificationMQ.Queue.notification);
    }
    @Bean
    public Binding notificationBinding() { // 将队列绑定到交换机上
        return BindingBuilder
                .bind(notificationQueue()) // 队列
                .to(notificationExchange()) // 交换机
                .with(NotificationMQ.RoutingKey.notification); // 路由键, 用于将消息路由到特定的队列
    }
}

