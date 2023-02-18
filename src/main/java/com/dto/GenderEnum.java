package com.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
public enum GenderEnum {

    BOY("男"), GIRL("女");

    @Getter
    String value;

    public GenderEnum of(String tag) {
        return Stream.of(GenderEnum.values()).filter(g -> g.value.equals(tag)).findFirst().orElse(null);
    }
}
