package com.jaaaain.bibobibo.app.controller;

import com.jaaaain.bibobibo.app.data.UploadData;
import com.jaaaain.bibobibo.app.data.UserData;
import com.jaaaain.bibobibo.app.service.UploadService;
import com.jaaaain.bibobibo.common.Result;
import com.jaaaain.bibobibo.common.enums.UploadEnums;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/file/upload")
@RequiredArgsConstructor
@Slf4j
class FileUploadController {
    private final UploadService uploadService;

    /**
     * 初始化上传任务并生成预签名 URL
     */
    @PostMapping("/init")
    public Result<UploadData.InitUploadVO> init(@RequestBody UploadData.InitUploadDto req) {
        return Result.success(uploadService.init(req));
    }

    /**
     * 上传任务完成，校验已上传分片，合并分片
     */
    @PostMapping("/finish")
    public Result<UploadData.FinishVO> finish(@AuthenticationPrincipal UserData.AuthDto authDto,@RequestParam String md5) {
        return Result.success(uploadService.finish(authDto, md5));
    }

    /**
     * 上传文件，返回文件路径
     */
    @PostMapping("/upload")
    public Result<String> upload(@AuthenticationPrincipal UserData.AuthDto authDto,@RequestBody File file, @RequestBody UploadEnums.FileUploadTypeEnum type, @RequestParam(required = false) String fileKey) {
        return Result.success(uploadService.upload(authDto, file, type, fileKey));
    }
}
