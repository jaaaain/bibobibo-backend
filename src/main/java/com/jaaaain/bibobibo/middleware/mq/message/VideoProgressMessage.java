package com.jaaaain.bibobibo.middleware.mq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoProgressMessage  implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long videoId;
    private Boolean needAnalyze;
    private Boolean needCover;
    private Boolean needReview;

    public VideoProgressMessage(Long videoId) {
        this.videoId = videoId;
        this.needAnalyze = true;
        this.needCover = true;
        this.needReview = true;
    }
}
