package com.jaaaain.bibobibo.app.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jaaaain.bibobibo.app.data.UserData;
import com.jaaaain.bibobibo.app.data.VideoData;
import com.jaaaain.bibobibo.app.service.VideoService;
import com.jaaaain.bibobibo.common.PageResult;
import com.jaaaain.bibobibo.common.Result;
import com.jaaaain.bibobibo.common.enums.VideoEnums;
import com.jaaaain.bibobibo.dal.entity.Video;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/video")
@RequiredArgsConstructor
@Tag(name = "视频管理", description = "视频相关操作接口")
public class VideoController {

    private final VideoService videoService;

    @PostMapping("/create-draft")
    @Operation(summary = "创建视频草稿", description = "创建视频草稿记录")
    public Result<Video> createDraft(@RequestParam String url, @RequestParam String title, @RequestParam String fileKey){
        return Result.success(videoService.createDraft(url, title, fileKey));
    }

    @PostMapping("/update")
    @Operation(summary = "更新视频信息", description = "更新视频的基本信息")
    public Result<Void> update(@RequestBody Video video){
        videoService.updateById(video);
        return Result.success();
    }

    @PostMapping("/publish/{id}")
    @Operation(summary = "发布视频", description = "提交视频审核")
    public Result<Void> publish(@PathVariable Long id){
        Video video = videoService.getById(id);
        video.setState(VideoEnums.State.REVIEWING);
        // todo 送给阿里云审核
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取视频详情", description = "根据ID获取视频详细信息")
    public Result<Video> getById(@PathVariable Long id) {
        return Result.success(videoService.getById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除视频", description = "根据ID删除视频")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(videoService.removeById(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获取公开视频列表", description = "分页获取公开视频列表")
    public Result<PageResult<Video>> getPublicVideoPage(VideoData.Query query) {
        query.setVisible(1); // 只获取公开的视频
        query.setState(1); // 只获取通过的视频
        if (query.getPage() == null || query.getPage() < 1) {
            query.setPage(1);
        }
        if (query.getSize() == null || query.getSize() < 1) {
            query.setSize(10);
        }
        Page<Video> page = videoService.getPageByQuery(new Page<>(query.getPage(), query.getSize()), query);
        return Result.success(PageResult.of(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/page/my")
    @Operation(summary = "获取我的视频列表", description = "分页获取当前用户上传的视频列表")
    public Result<PageResult<Video>> getMyVideoPage(@AuthenticationPrincipal UserData.AuthDto authDto, VideoData.Query query) {
        query.setUid(authDto.getId());
        Page<Video> page = videoService.getPageByQuery(new Page<>(query.getPage(), query.getSize()), query);
        return Result.success(PageResult.of(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/page/my/favorite")
    @Operation(summary = "获取我的收藏视频列表", description = "分页获取当前用户收藏的视频列表")
    public Result<PageResult<Video>> getMyFavoriteVideoPage(@AuthenticationPrincipal UserData.AuthDto authDto, VideoData.Query query) {
        query.setUid(authDto.getId());
//        query.setFavorite(1);
        Page<Video> page = videoService.getPageByQuery(new Page<>(query.getPage(), query.getSize()), query);
        return Result.success(PageResult.of(page.getTotal(), page.getRecords()));
    }

}
