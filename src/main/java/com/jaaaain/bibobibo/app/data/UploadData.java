package com.jaaaain.bibobibo.app.data;

import com.jaaaain.bibobibo.common.enums.UploadEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

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
    public static class FinishVO {
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
