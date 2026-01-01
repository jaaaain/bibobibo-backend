package com.jaaaain.bibobibo.app.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jaaaain.bibobibo.app.data.UserData;
import com.jaaaain.bibobibo.app.data.VideoData;
import com.jaaaain.bibobibo.app.service.VideoService;
import com.jaaaain.bibobibo.common.PageResult;
import com.jaaaain.bibobibo.common.Result;
import com.jaaaain.bibobibo.dal.entity.Video;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/platform")
@RequiredArgsConstructor
public class PlatformController {
    private final VideoService videoService;
    @GetMapping("/my/draft")
    public Result<List<Video>> getMyDraftVideoList(@AuthenticationPrincipal UserData.AuthDto authDto) {
        List<Video> videos = videoService.list(Wrappers.<Video>lambdaQuery()
                .eq(Video::getState, -1) // 只获取草稿的视频
                .eq(Video::getUid, authDto.getId())
        );
        return Result.success(videos);
    }
}
