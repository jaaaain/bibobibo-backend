package com.jaaaain.bibobibo.app.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jaaaain.bibobibo.app.data.UserData;
import com.jaaaain.bibobibo.app.service.UserService;
import com.jaaaain.bibobibo.dal.entity.User;
import com.jaaaain.bibobibo.dal.mapper.UserMapper;
import com.jaaaain.bibobibo.middleware.redis.AuthRedisRepo;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AuthRedisRepo authRedisRepo;

    @Override
    public User check(String username, String password) {
        return userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getUsername, username)
                .eq(User::getPassword, password));
    }

    @Override
    public Boolean register(UserData.RegisterDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setPhone(dto.getPhone());
        return save(user);
    }

    @Override
    public String sendCode(String phone) {
        String redisCode = authRedisRepo.getLoginCode(phone);
        if (!StringUtils.isBlank(redisCode) && System.currentTimeMillis() - Long.parseLong(redisCode.split("-")[1]) < 60 * 1000) {
            return null;
        }
        // 生成验证码
        String code = RandomUtil.randomNumbers(6);
        String fullCode = code + "-" + System.currentTimeMillis();
        // 将生成的验证码保存到redis
        authRedisRepo.setLoginCode(phone, fullCode);
        // todo 发送验证码

        log.info("发送登录验证码：{}", code);

        return code;
    }

    @Override
    public User getByUsername(String username) {
        return userMapper.selectOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getUsername, username));
    }
    /** 构建卡片 */
    @Override
    public UserData.CardVO buildCard(Long uid) {
        User user = getById(uid);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        UserData.CardVO card = new UserData.CardVO();
        BeanUtil.copyProperties(user, card);
        card.setStat(statistics(uid));
        return card;
    }

    /** uid -> briefVO 映射 */
    @Override
    public Map<Long, UserData.BriefVO> getBriefMapByIds(Set<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<User> users = listByIds(userIds);

        return users.stream().collect(Collectors.toMap(
                User::getId,
                user -> {
                    UserData.BriefVO vo = new UserData.BriefVO();
                    BeanUtil.copyProperties(user,vo,true);
                    return vo;
                }
        ));
    }

    @Override
    public UserData.StatVO statistics(Long uid) {
        // todo Redis + 定时落库
        UserData.StatVO statVO = new UserData.StatVO();
        statVO.setFans(0L);
        statVO.setFollows(0L);
        statVO.setLikes(0L);
        statVO.setPlays(0L);
        statVO.setVideos(0L);
        return statVO;
    }

    @Override
    public String login(UserData.LoginDto dto) {
        User user = check(dto.getUsername(), dto.getPassword());
        if (user == null) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        UserData.AuthDto authDto = new UserData.AuthDto();
        BeanUtil.copyProperties(user, authDto);

        // 生成 token
        String token = UUID.randomUUID().toString();
        authDto.setToken(token);

        // redis 保存
        authRedisRepo.setAuth(token, authDto);
        return token;
    }

}
