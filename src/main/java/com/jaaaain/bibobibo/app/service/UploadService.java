package com.jaaaain.bibobibo.app.service;

import com.jaaaain.bibobibo.app.data.UploadData;
import com.jaaaain.bibobibo.common.enums.UploadEnums;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface UploadService {
    UploadData.InitUploadVO init(UploadData.InitUploadDto req);
    UploadData.UploadResultVO finish(String md5);
    UploadData.UploadResultVO upload(MultipartFile file, UploadEnums.FileUploadTypeEnum type);
    UploadData.UploadResultVO upload(File file, UploadEnums.FileUploadTypeEnum type);
}
