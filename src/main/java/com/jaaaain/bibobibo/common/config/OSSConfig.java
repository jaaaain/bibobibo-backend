package com.jaaaain.bibobibo.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Data
@Configuration
public class OSSConfig implements Serializable {

    @Value("${oss.bucket}")
    private String bucket;

    @Value("${oss.endpoint}")
    private String endpoint;

    @Value("${oss.url}")
    private String url;

    @Value("${oss.accessKeyId}")
    private String accessKeyId;

    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;

    @Value("${oss.idleTimeout}")
    private long idleTimeout;

    @Value("${oss.maxSize}")
    private long maxSize;
}