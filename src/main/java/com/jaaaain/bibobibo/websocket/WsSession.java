package com.jaaaain.bibobibo.websocket;

public interface WsSession {

    String getSessionId();

    void sendText(String text);

    void close();

}
