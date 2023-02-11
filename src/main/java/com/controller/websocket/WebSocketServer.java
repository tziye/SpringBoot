package com.controller.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pojo.bo.WsMessageBo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Data
@Component
@ServerEndpoint("/websocket/{username}")
public class WebSocketServer {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static int onlineNumber = 0;
    private static Map<String, WebSocketServer> clients = new ConcurrentHashMap<>();
    private Session session;
    private String username;

    @OnOpen
    public void onOpen(@PathParam("username") String username, Session session) throws IOException {
        onlineNumber++;
        log.info("【OnOpen】新连接加入：{}，当前在线人数：{}", username, onlineNumber);
        this.username = username;
        this.session = session;
        WsMessageBo sendBo = WsMessageBo.builder().messageType(1).fromUser(username).build();
        sendMessageAll(sendBo);
        clients.put(username, this);
        sendBo = WsMessageBo.builder().messageType(3).toUser(username).onlineUsers(clients.keySet()).build();
        sendMessageTo(sendBo);
    }

    @OnError
    public void onError(Throwable e) {
        log.error("【OnError】{}", e.getMessage(), e);
    }

    @OnClose
    public void onClose() throws IOException {
        onlineNumber--;
        clients.remove(username);
        WsMessageBo sendBo = WsMessageBo.builder().messageType(2).fromUser(username).onlineUsers(clients.keySet()).build();
        sendMessageAll(sendBo);
        log.info("【OnClose】连接关闭：{}，当前在线人数：{}", username, onlineNumber);
    }

    /**
     * 收到客户端的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) throws Exception {
        log.info("【OnMessage】收到客户端消息：{}，客户端id：{}", message, session.getId());
        WsMessageBo receiveBo = MAPPER.readValue(message, WsMessageBo.class);
        WsMessageBo sendBo = WsMessageBo.builder().messageType(4).message(receiveBo.getMessage())
                .fromUser(receiveBo.getFromUser()).build();
        String toUser = receiveBo.getToUser();
        if ("All".equals(toUser)) {
            sendBo.setToUser("所有人");
            sendMessageAll(sendBo);
        } else {
            sendBo.setToUser(toUser);
            sendMessageTo(sendBo);
        }
    }

    private void sendMessageTo(WsMessageBo message) throws IOException {
        for (WebSocketServer item : clients.values()) {
            if (item.username.equals(message.getToUser())) {
                item.session.getAsyncRemote().sendText(MAPPER.writeValueAsString(message));
                break;
            }
        }
    }

    private void sendMessageAll(WsMessageBo message) throws IOException {
        for (WebSocketServer item : clients.values()) {
            if (!Objects.equals(item.username, message.getFromUser())) {
                item.session.getAsyncRemote().sendText(MAPPER.writeValueAsString(message));
            }
        }
    }

}
