package com.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum GenderEnum {

    BOY("男"), GIRL("女");

    @Getter
    private String value;

    public GenderEnum of(String tag) {
        for (GenderEnum g : GenderEnum.values()) {
            if (g.value.equals(tag)) {
                return g;
            }
        }
        return null;
    }
}
