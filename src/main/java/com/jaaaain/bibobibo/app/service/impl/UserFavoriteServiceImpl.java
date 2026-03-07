package com.jaaaain.bibobibo.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jaaaain.bibobibo.app.service.UserFavoriteService;
import com.jaaaain.bibobibo.dal.entity.UserFavorite;
import com.jaaaain.bibobibo.dal.mapper.UserFavoriteMapper;
import org.springframework.stereotype.Service;

@Service
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite> implements UserFavoriteService {
}
