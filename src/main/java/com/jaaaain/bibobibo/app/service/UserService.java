package com.jaaaain.bibobibo.app.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.jaaaain.bibobibo.app.data.UserData;
import com.jaaaain.bibobibo.dal.entity.User;

import java.util.Map;
import java.util.Set;

/**
 * 用户表(Users)表服务接口
 */
public interface UserService extends IService<User> {
    User check(String username, String password);

    Boolean register(UserData.RegisterDto dto);

    String sendCode(String phone);

    User getByUsername(String username);

    String login(UserData.LoginDto dto);

    UserData.CardVO buildCard(Long uid);

    UserData.StatVO statistics(Long id);

    Map<Long, UserData.BriefVO> getBriefMapByIds(Set<Long> userIds);
}
