package com.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WsMessageDto {

    /**
     * 1代表上线，2代表下线，3代表在线名单，4代表普通消息
     */
    int messageType;

    String message;

    String fromUser;

    String toUser;

    Set<String> onlineUsers;
}
