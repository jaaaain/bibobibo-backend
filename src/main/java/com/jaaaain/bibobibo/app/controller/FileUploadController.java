package com.jaaaain.bibobibo.app.controller;

import com.jaaaain.bibobibo.app.data.UploadData;
import com.jaaaain.bibobibo.app.service.UploadService;
import com.jaaaain.bibobibo.common.Result;
import com.jaaaain.bibobibo.common.enums.UploadEnums;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/file/upload")
@RequiredArgsConstructor
@Tag(name = "文件上传", description = "文件上传操作接口")
class FileUploadController {
    private final UploadService uploadService;


    /**
     * 初始化上传任务并生成预签名 URL
     */
    @PostMapping("/init")
    @Operation(summary = "初始化上传任务", description = "初始化上传任务并生成预签名 URL")
    public Result<UploadData.InitUploadVO> init(@RequestBody UploadData.InitUploadDto req) {
        return Result.success(uploadService.init(req));
    }

    /**
     * 上传任务完成，校验已上传分片，合并分片
     */
    @PostMapping("/finish")
    @Operation(summary = "完成上传任务", description = "上传任务完成，校验已上传分片，合并分片")
    public Result<UploadData.UploadResultVO> finish(@RequestParam String md5) {
        return Result.success(uploadService.finish(md5));
    }

    /**
     * 上传文件，返回文件路径
     */
    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "上传文件，返回文件路径")
    public Result<UploadData.UploadResultVO> upload(@RequestBody UploadData.UploadDto uploadDto) {
        return Result.success(uploadService.upload(uploadDto.getFile(), uploadDto.getType()));
    }
}
