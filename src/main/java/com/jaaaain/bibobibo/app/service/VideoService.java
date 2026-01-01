package com.jaaaain.bibobibo.app.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jaaaain.bibobibo.app.data.VideoData;
import com.jaaaain.bibobibo.dal.entity.Video;

public interface VideoService extends IService<Video> {
    Page<Video> getPageByQuery(Page<Video> page, VideoData.Query query);
    Video createDraft(String url);
}