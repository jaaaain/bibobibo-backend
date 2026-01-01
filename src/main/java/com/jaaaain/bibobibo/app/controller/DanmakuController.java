package com.jaaaain.bibobibo.app.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jaaaain.bibobibo.app.service.DanmakuService;
import com.jaaaain.bibobibo.common.PageResult;
import com.jaaaain.bibobibo.common.Result;
import com.jaaaain.bibobibo.dal.entity.Danmaku;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/danmaku")
@RequiredArgsConstructor
public class DanmakuController {
    private final DanmakuService danmakuService;

    // 分页列表
    @GetMapping("/list")
    public Result<PageResult<Danmaku>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Danmaku> p = danmakuService.page(new Page<>(page, size));
        return Result.success(PageResult.of(p.getTotal(), p.getRecords()));
    }

    // 详情
    @GetMapping("/{id}")
    public Result<Danmaku> detail(@PathVariable Long id) {
        return Result.success(danmakuService.getById(id));
    }

    // 新增
    @PostMapping
    public Result<Boolean> add(@RequestBody Danmaku danmaku) {
        return Result.success(danmakuService.save(danmaku));
    }

    // 修改
    @PutMapping
    public Result<Boolean> update(@RequestBody Danmaku danmaku) {
        return Result.success(danmakuService.updateById(danmaku));
    }

    // 删除（逻辑删除）
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(danmakuService.removeById(id));
    }
}