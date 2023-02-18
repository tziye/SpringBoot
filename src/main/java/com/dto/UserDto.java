package com.dto;

import com.common.validator.CustomConstraint;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {

    @NotNull(message = "id不能为空")
    @Range(min = 0, max = 10000, message = "id必须介于0-10000")
    int id;

    @NotEmpty(message = "name不能为空")
    String name;

    @Min(value = 1, message = "年龄必须在1-100之间")
    @Max(value = 100, message = "年龄必须在1-100之间")
    int age;

    @NotNull(message = "gender不能为空")
    String gender;

    @NotNull
    @JsonProperty("isEnabled")
    boolean enabled;

    @CustomConstraint
    @NotNull
    Date time;

    public UserDto(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserDto(int id, String name, int age, GenderEnum gender, boolean enabled, Date time) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender.value;
        this.enabled = enabled;
        this.time = time;
    }

}
