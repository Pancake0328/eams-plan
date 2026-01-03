package com.eams.notify.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通知WebSocket处理器
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Slf4j
@Component
public class NotifyWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 存储所有连接的会话，key为用户名
    private static final Map<String, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = getUsernameFromSession(session);
        if (username != null) {
            SESSIONS.put(username, session);
            log.info("WebSocket连接建立: {}", username);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 客户端发送的心跳或其他消息
        log.debug("收到消息: {}", message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = getUsernameFromSession(session);
        if (username != null) {
            SESSIONS.remove(username);
            log.info("WebSocket连接关闭: {}", username);
        }
    }

    /**
     * 发送通知给指定用户
     */
    public void sendNotifyToUser(String username, Object notify) {
        WebSocketSession session = SESSIONS.get(username);
        if (session != null && session.isOpen()) {
            try {
                String message = objectMapper.writeValueAsString(notify);
                session.sendMessage(new TextMessage(message));
                log.info("发送通知给用户 {}: {}", username, message);
            } catch (IOException e) {
                log.error("发送通知失败", e);
            }
        }
    }

    /**
     * 广播通知给所有用户
     */
    public void broadcast(Object notify) {
        try {
            String message = objectMapper.writeValueAsString(notify);
            SESSIONS.values().forEach(session -> {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(message));
                    } catch (IOException e) {
                        log.error("广播通知失败", e);
                    }
                }
            });
            log.info("广播通知给 {} 个用户", SESSIONS.size());
        } catch (Exception e) {
            log.error("广播通知失败", e);
        }
    }

    /**
     * 从session中获取用户名
     */
    private String getUsernameFromSession(WebSocketSession session) {
        // 从URL参数中获取用户名
        String query = session.getUri().getQuery();
        if (query != null && query.contains("username=")) {
            return query.split("username=")[1].split("&")[0];
        }
        return null;
    }

    /**
     * 获取在线用户数
     */
    public int getOnlineCount() {
        return SESSIONS.size();
    }
}
