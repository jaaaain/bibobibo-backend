package com.jaaaain.bibobibo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private long total;
    private List<T> list;
    private String nextCursor;       // 下一页游标（可为空）
    private Boolean hasMore;         // 是否还有更多

    public static <T> PageResult<T> of(long total, List<T> list) {
        return new PageResult<>(total, list, null, null);
    }
    public static <T> PageResult<T> of(long total, List<T> list, String nextCursor, Boolean hasMore) {
        return new PageResult<>(total, list, nextCursor, hasMore);
    }
}