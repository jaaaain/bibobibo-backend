package com.jaaaain.bibobibo.app.service;

import com.jaaaain.bibobibo.app.data.UploadData;
import com.jaaaain.bibobibo.app.data.UserData;
import com.jaaaain.bibobibo.common.enums.UploadEnums;

import java.io.File;

public interface UploadService {
    UploadData.InitUploadVO init(UploadData.InitUploadDto req);
    UploadData.FinishVO finish(UserData.AuthDto authDto,String md5);
    String upload(UserData.AuthDto authDto, File file, UploadEnums.FileUploadTypeEnum type);
}
