package com.jaaaain.bibobibo.app.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jaaaain.bibobibo.app.service.DanmakuService;
import com.jaaaain.bibobibo.common.PageResult;
import com.jaaaain.bibobibo.common.Result;
import com.jaaaain.bibobibo.dal.entity.Danmaku;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/dm")
@RequiredArgsConstructor
public class DanmakuController {
    private final DanmakuService danmakuService;

    // 列表
    @GetMapping("/list/{vid}")
    public Result<List<Danmaku>> list(@PathVariable Long vid) {
        log.info("list vid={}", vid);
        Wrapper<Danmaku> queryWrapper = Wrappers.lambdaQuery(Danmaku.class).eq(Danmaku::getVid, vid);
        List<Danmaku> list = danmakuService.list(queryWrapper);
        return Result.success(list);
    }

    // 删除（逻辑删除）
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(danmakuService.removeById(id));
    }
}