package com.function.async;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.ApplicationEvent;

/**
 * 自定义事件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MyEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;
    String message;

    public MyEvent(Object source, String message) {
        super(source);
        this.message = message;
    }
}