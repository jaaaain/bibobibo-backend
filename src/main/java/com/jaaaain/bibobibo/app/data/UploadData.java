package com.jaaaain.bibobibo.app.data;

import com.jaaaain.bibobibo.common.enums.UploadEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Data
public class UploadData {
    // ===Query===

    // ===VO===
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InitUploadVO {
        private UploadEnums.FileUploadStateEnum state;
        private String uploadId;
        private long chunkSize;
        private int totalParts;
        private List<String> partUrls;
        private String path;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UploadResultVO {
        private String etag;
        private String path;
    }

    // ===DTO===
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InitUploadDto {
        private String fileName;
        private Long fileSize;
        private String fileMd5;
        private UploadEnums.FileUploadTypeEnum type;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UploadDto {
        @Schema(description ="上传文件")
        private MultipartFile file;
        @Schema(description ="上传文件类型,用于映射文件存储路径")
        private UploadEnums.FileUploadTypeEnum type;
    }

    // === POJO ===
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UploadSession {
        private String uploadId;
        private String path;
        private Long fileSize;
        private Long chunkSize;
        private Integer totalParts;
        private Boolean completed;
    }


}
