package com.jaaaain.bibobibo.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jaaaain.bibobibo.app.service.UserWatchHistoryService;
import com.jaaaain.bibobibo.dal.entity.UserWatchHistory;
import com.jaaaain.bibobibo.dal.mapper.UserWatchHistoryMapper;
import org.springframework.stereotype.Service;

@Service("userWatchHistoryService")
public class UserWatchHistoryServiceImpl extends ServiceImpl<UserWatchHistoryMapper, UserWatchHistory> implements UserWatchHistoryService {
}
