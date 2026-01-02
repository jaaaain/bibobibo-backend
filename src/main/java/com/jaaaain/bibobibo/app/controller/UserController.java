package com.jaaaain.bibobibo.app.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jaaaain.bibobibo.app.data.UserData;
import com.jaaaain.bibobibo.app.service.UserService;
import com.jaaaain.bibobibo.common.PageResult;
import com.jaaaain.bibobibo.common.Result;
import com.jaaaain.bibobibo.common.constants.RedisConstants;
import com.jaaaain.bibobibo.infrastructure.RedisClient;
import com.jaaaain.bibobibo.dal.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final RedisClient redisClient;

    @GetMapping("/info")
    public Result<UserData.SelfCardVO> simpleInfo(@AuthenticationPrincipal UserData.AuthDto authDto) {
        User user = userService.getById(authDto.getId());
        if (user == null) {
            return Result.failed("用户不存在");
        }
        UserData.SelfCardVO selfCardVO = new UserData.SelfCardVO();
        selfCardVO.setAvatar(user.getAvatar());
        selfCardVO.setNickname(user.getNickname());
        // 根据经验值估算等级（假设每级经验差固定为2000）
        int estimatedLevel = Math.min(6, user.getExp() / 2000 + 1);
        // todo 等级所需经验上下限；暂只显示等级
        selfCardVO.setExp(user.getExp());
        selfCardVO.setLevel(estimatedLevel);
        selfCardVO.setVip(user.getVip()); // todo VIP到期时间
        selfCardVO.setCoin(user.getCoin());
        selfCardVO.setState(user.getState());

        return Result.success(selfCardVO);
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody UserData.LoginDto dto) {
        log.info("登录请求：{}", dto);
        return Result.success(userService.login(dto));
    }

    @PostMapping("/logout")
    public Result<Boolean> logout(@AuthenticationPrincipal UserData.AuthDto authDto) {
        log.info("登出请求：{}", authDto);
        String token = authDto.getToken();
        redisClient.delete(RedisConstants.LOGIN_TOKEN_KEY + token);
        return Result.success(true);
    }


    @GetMapping("/code")
    public Result<String> sendCode(@RequestParam @NotNull(message = "手机号不能为空") @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone) {
        return Result.success(userService.sendCode(phone));
    }

    @PostMapping("/register")
    public Result<Boolean> register(@RequestBody UserData.RegisterDto dto) {
        return Result.success(userService.register(dto));
    }

    // 分页列表
    @GetMapping("/list")
    public Result<PageResult<User>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<User> p = userService.page(new Page<>(page, size));
        return Result.success(PageResult.of(p.getTotal(), p.getRecords()));
    }

    // 详情
    @GetMapping("/detail")
    public Result<UserData.SelfProfileVO> detail(@AuthenticationPrincipal UserData.AuthDto authDto) {
        User user = userService.getById(authDto.getId());
        if (user == null) {
            return Result.failed("用户不存在");
        }
        UserData.SelfProfileVO selfProfileVO = new UserData.SelfProfileVO();
        BeanUtil.copyProperties(user, selfProfileVO);
        return Result.success(selfProfileVO);
    }

    // 修改
    @PutMapping
    public Result<Boolean> update(@RequestBody User user) {
        return Result.success(userService.updateById(user));
    }

    // 删除（逻辑删除）
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(userService.removeById(id));
    }
}

