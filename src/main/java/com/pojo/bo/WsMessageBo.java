package com.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WsMessageBo {

    /**
     * 1代表上线，2代表下线，3代表在线名单，4代表普通消息
     */
    private int messageType;

    private String message;

    private String fromUser;

    private String toUser;

    private Set<String> onlineUsers;
}
