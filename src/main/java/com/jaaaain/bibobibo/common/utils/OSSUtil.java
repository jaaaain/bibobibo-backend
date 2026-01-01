package com.jaaaain.bibobibo.common.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.jaaaain.bibobibo.common.config.OSSConfig;
import com.jaaaain.bibobibo.common.enums.UploadEnums;
import com.jaaaain.bibobibo.common.exception.ApiException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OSSUtil {
    private final OSSConfig ossConfig;
    private static OSS client;

    @PostConstruct
    public void init() {
        client = new OSSClientBuilder().build(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
    }

    /**
     * 初始化分片上传
     */
    public InitiateMultipartUploadResult initMultipart(String path) {
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(ossConfig.getBucket(), path);
        return client.initiateMultipartUpload(request);
    }

    /**
     * 生成预签名
     */
    public String presignPutUrl(String path, String uploadId, int partNumber) {
        // 生成过期时间
        long expireEndTime = System.currentTimeMillis() + ossConfig.getIdleTimeout() * 1000;
        Date expiration = new Date(expireEndTime);
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(ossConfig.getBucket(), path);
        generatePresignedUrlRequest.setExpiration(expiration);
        generatePresignedUrlRequest.addQueryParameter("partNumber", String.valueOf(partNumber));
        generatePresignedUrlRequest.addQueryParameter("uploadId", uploadId);

        URL url = client.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }

    /**
     * 完成分片上传，文件合并
     */
    public CompleteMultipartUploadResult completeMultipart(String path, String uploadId) {
        try {
            log.info("开始合并分片，path: {}, uploadId: {}", path, uploadId);
            ListPartsRequest listPartsRequest = new ListPartsRequest(ossConfig.getBucket(), path, uploadId);
            PartListing partListing = client.listParts(listPartsRequest);
            // 收集分片的 ETag 信息用于后续合并
            List<PartETag> partETags = partListing.getParts().stream()
                    .map(part -> new PartETag(part.getPartNumber(), part.getETag()))
                    .sorted(Comparator.comparingInt(PartETag::getPartNumber))
                    .toList();
            CompleteMultipartUploadRequest completeReq = new CompleteMultipartUploadRequest(ossConfig.getBucket(), path, uploadId, partETags);
            CompleteMultipartUploadResult result = client.completeMultipartUpload(completeReq);
            log.info("合并分片成功！");
            log.info("ETag: {}", result.getETag());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException("分片合并失败：" + e.getMessage());
        }
    }

    /**
     * 取消分片上传
     */
    public void abortMultipart(File file, UploadEnums.FileUploadTypeEnum type, String uploadId) {
        String path = getPath(type, file.getName());
        client.abortMultipartUpload(new AbortMultipartUploadRequest(ossConfig.getBucket(), path, uploadId));
    }

    /**
     * 根据path生成文件的访问地址
     */
    public String getUrl(String path) {
        // 拼接文件访问路径。由于拼接的字符串大多为String对象，而不是""的形式，所以直接用+拼接的方式没有优势
        StringBuffer url = new StringBuffer();
        url.append("http://")
                .append(ossConfig.getEndpoint())
                .append(".")
                .append(ossConfig.getBucket())
                .append("/")
                .append(path);
        return url.toString();
    }

    /**
     * 获取上传文件的 path（格式：/path/yyyyMMdd-UUID.ext）
     * @param type   文件类型（用于获取文件存储路径）
     * @param fileName 文件名称（用于提取后缀）
     */
    public String getPath(UploadEnums.FileUploadTypeEnum type, String fileName) {
        // 路径处理
        String path = StrUtil.blankToDefault(type.path, UploadEnums.FileUploadTypeEnum.FILE.path);
        // 文件名处理
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // 后缀处理（文件名后缀）
        String suffix = FileUtil.getSuffix(fileName);
        if (StringUtils.isBlank(suffix)) {
            throw new NullPointerException("文件后缀不能为空");
        }
        String ext = suffix.startsWith(".")? suffix : "." + suffix;

        return String.format(
                "%s/%s-%s%s", path, datePath, uuid, ext
        );
    }

    /**
     * 上传文件基础方法
     * @param path         文件path
     * @param inputStream 输入流
     */
    public String putFile(String path, InputStream inputStream) {
        if (inputStream == null) {
            throw new RuntimeException("文件不能为空");
        }

        // 上传文件最大值 MB->bytes
        long maxSize = ossConfig.getMaxSize() * 1024 * 1024;

        // 计算inputStream大小
        long size;
        try {
            size = inputStream.available();
        } catch (Exception e) {
            throw new RuntimeException("无法获取文件大小");
        }

        if (size <= 0 || size > maxSize) {
            throw new RuntimeException("请检查文件大小");
        }

        try {
            PutObjectRequest request = new PutObjectRequest(ossConfig.getBucket(), path, inputStream);
            client.putObject(request);
            return getUrl(path);
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new ApiException("文件上传失败：" + e.getMessage());
        }
    }


    /**
     * 通过文件名获取文件流
     * @param path 要下载的文件名（OSS服务器上的）
     */
    public InputStream getInputStream(String path) {
        // 下载OSS文件到本地文件。如果指定的本地文件存在会覆盖，不存在则新建。
        return client.getObject(new GetObjectRequest(ossConfig.getBucket(), path)).getObjectContent();
    }

    /**
     * 根据path下载文件1
     */
    public void download(String path) {
        GetObjectRequest request = new GetObjectRequest(ossConfig.getBucket(), path);
        client.getObject(request);
    }

    /**
     * 根据path下载文件
     */
    public void download(String path, String fileName) {
        GetObjectRequest request = new GetObjectRequest(ossConfig.getBucket(), path);
        client.getObject(request, new File(fileName));
    }


    /**
     * 检查文件是否存在
     */
    public Boolean checkExist(String path) {
        return client.doesObjectExist(ossConfig.getBucket(), path);
    }
}