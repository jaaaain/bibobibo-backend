package com.jaaaain.bibobibo.websocket;

import lombok.Data;

@Data
public class WsRequest {

    private String type;

    private Long videoId; // todo 放到data里？

    private Object data;

}
