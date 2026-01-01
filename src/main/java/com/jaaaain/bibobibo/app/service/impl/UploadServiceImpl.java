package com.jaaaain.bibobibo.app.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.model.*;
import com.jaaaain.bibobibo.app.data.UploadData;
import com.jaaaain.bibobibo.app.data.UserData;
import com.jaaaain.bibobibo.app.service.UploadService;
import com.jaaaain.bibobibo.app.service.VideoService;
import com.jaaaain.bibobibo.common.enums.UploadEnums;
import com.jaaaain.bibobibo.common.enums.VideoEnums;
import com.jaaaain.bibobibo.infrastructure.OSSClient;
import com.jaaaain.bibobibo.dal.entity.Video;
import com.jaaaain.bibobibo.middleware.redis.UploadRedisRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadServiceImpl implements UploadService {
    private final OSSClient ossClient;
    private final UploadRedisRepo redisRepo;

    // 分片大小
    private final long CHUNK_SIZE = 5 * 1024L * 1024L; // 5MB
    // MD5 正则
    private static final Pattern MD5_PATTERN = Pattern.compile("^[a-fA-F0-9]{32}$");

    /**
     * 初始化上传任务并生成预签名 URL
     */
    public UploadData.InitUploadVO init(UploadData.InitUploadDto req) {
        // 1. 校验 MD5
        checkMd5(req.getFileMd5());
        UploadData.UploadSession session = redisRepo.getUploadSession(req.getFileMd5());
        // 2. Redis 已存在
        if (session != null) {
            // 2.1 已完成 → 秒传
            if (session.getCompleted()) {
                // todo 第一个参数改为枚举
                return new UploadData.InitUploadVO(UploadEnums.FileUploadStateEnum.COMPLETED, null, 0, 0, null, session.getPath());
            }
            // 2.2 未完成 → 断点续传（前端存已上传的分片信息）
            return buildResumeVO(session.getPath(), session.getUploadId(), session.getTotalParts());
        }
        // 3. Redis 不存在 → 新建上传会话
        return createNewUpload(req);
    }

    /**
     * 断点续传获取预签名
     */
    private UploadData.InitUploadVO buildResumeVO(String path, String uploadId, int totalParts) {
        List<String> partUrls = new ArrayList<>();
        for (int i = 1; i <= totalParts; i++) {
            // 为各分片生成预签名 URL
            partUrls.add(ossClient.presignPutUrl(path, uploadId, i));
            log.info("part {} presign put url", i);
        }
        return new UploadData.InitUploadVO(UploadEnums.FileUploadStateEnum.RESUME, uploadId, CHUNK_SIZE, totalParts, partUrls, path);
    }

    /**
     * 新建上传会话
     */
    private UploadData.InitUploadVO createNewUpload(UploadData.InitUploadDto req) {
        // 1. 获取文件路径
        String path = ossClient.getPath(req.getType(), req.getFileName(), null);
        // 2. 初始化分片上传
        InitiateMultipartUploadResult initResult = ossClient.initMultipart(path);
        String uploadId = initResult.getUploadId();
        // 3. 计算分片数量
        int totalParts = (int) ((req.getFileSize() + CHUNK_SIZE - 1) / CHUNK_SIZE);
        // 4. Redis 保存会话
        UploadData.UploadSession session = new UploadData.UploadSession();
        session.setUploadId(uploadId);
        session.setPath(path);
        session.setFileSize(req.getFileSize());
        session.setChunkSize(CHUNK_SIZE);
        session.setTotalParts(totalParts);
        session.setCompleted(false);
        redisRepo.setUploadSession(req.getFileMd5(), session);
        // 5. 生成预签名 URL
        List<String> partUrls = new ArrayList<>();
        for (int i = 1; i <= totalParts; i++) {
            // 为各分片生成预签名 URL
            partUrls.add(ossClient.presignPutUrl(path, uploadId, i));
            log.info("part {} presign put url", i);
        }
        // 6. 返回分片预签名信息
        return new UploadData.InitUploadVO(UploadEnums.FileUploadStateEnum.INIT, uploadId, CHUNK_SIZE, totalParts, partUrls, path);
    }

    /**
     * 上传任务完成，校验已上传分片，合并分片
     */
    public UploadData.FinishVO finish(UserData.AuthDto authDto,String md5) {
        // 从Redis获取完整文件存储文件路径
        UploadData.UploadSession session = redisRepo.getUploadSession(md5);
        if (session == null) {
            throw new IllegalArgumentException("上传会话不存在");
        }
        String path = session.getPath();
        String uploadId = session.getUploadId();
        // 合并分片
        CompleteMultipartUploadResult completeRes = ossClient.completeMultipart(path, uploadId);
        session.setCompleted(true);
        redisRepo.setUploadSession(md5, session);

        // Redis 缓存文件md5
        return new UploadData.FinishVO(completeRes.getETag(), path);// todo 返回video
    }

    /**
     * 上传文件
     */
    public String upload(UserData.AuthDto authDto, File file, UploadEnums.FileUploadTypeEnum type, String fileKey) {
        String path = ossClient.getPath(type, file.getName(), fileKey);
        return ossClient.putFile(path, FileUtil.getInputStream(file));
    }

    /**
     * 校验 MD5
     */
    private void checkMd5(String md5) {
        if (StrUtil.isBlank(md5) || !MD5_PATTERN.matcher(md5).matches()) {
            throw new IllegalArgumentException("非法 MD5");
        }
    }
}