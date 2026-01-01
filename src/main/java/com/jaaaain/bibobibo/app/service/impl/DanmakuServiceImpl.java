package com.jaaaain.bibobibo.app.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jaaaain.bibobibo.app.service.DanmakuService;
import com.jaaaain.bibobibo.dal.entity.Danmaku;
import com.jaaaain.bibobibo.dal.mapper.DanmakuMapper;
import org.springframework.stereotype.Service;

@Service
public class DanmakuServiceImpl extends ServiceImpl<DanmakuMapper, Danmaku> implements DanmakuService {
}