package com.function.websocket;

import com.common.util.MyUtil;
import com.dto.WsMessageDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Data
@Component
@ServerEndpoint("/websocket/{username}")
public class WebSocketServer {

    int onlineNumber = 0;
    Map<String, WebSocketServer> clients = new ConcurrentHashMap<>();
    Session session;
    String username;

    @OnOpen
    public void onOpen(@PathParam("username") String username, Session session) {
        onlineNumber++;
        log.info("【OnOpen】新连接加入：{}，当前在线人数：{}", username, onlineNumber);
        this.username = username;
        this.session = session;
        WsMessageDto sendBo = WsMessageDto.builder().messageType(1).fromUser(username).build();
        sendMessageAll(sendBo);
        clients.put(username, this);
        sendBo = WsMessageDto.builder().messageType(3).toUser(username).onlineUsers(clients.keySet()).build();
        sendMessageTo(sendBo);
    }

    @OnError
    public void onError(Throwable e) {
        log.error("【OnError】{}", e.getMessage(), e);
    }

    @OnClose
    public void onClose() {
        onlineNumber--;
        clients.remove(username);
        WsMessageDto sendBo = WsMessageDto.builder().messageType(2).fromUser(username).onlineUsers(clients.keySet()).build();
        sendMessageAll(sendBo);
        log.info("【OnClose】连接关闭：{}，当前在线人数：{}", username, onlineNumber);
    }

    /**
     * 收到客户端的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("【OnMessage】收到客户端消息：{}，客户端id：{}", message, session.getId());
        WsMessageDto receiveBo = MyUtil.parse(message, WsMessageDto.class);
        WsMessageDto sendBo = WsMessageDto.builder().messageType(4).message(receiveBo.getMessage())
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

    void sendMessageTo(WsMessageDto message) {
        for (WebSocketServer item : clients.values()) {
            if (item.username.equals(message.getToUser())) {
                item.session.getAsyncRemote().sendText(MyUtil.toString(message));
                break;
            }
        }
    }

    void sendMessageAll(WsMessageDto message) {
        for (WebSocketServer item : clients.values()) {
            if (!Objects.equals(item.username, message.getFromUser())) {
                item.session.getAsyncRemote().sendText(MyUtil.toString(message));
            }
        }
    }

}
