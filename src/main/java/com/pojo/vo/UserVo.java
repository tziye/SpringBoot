package com.pojo.vo;

import com.common.validator.CustomConstraint;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pojo.entity.GenderEnum;
import com.pojo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 用于测试validation
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // json转pojo时忽略未知属性
public class UserVo {

    @NotNull(message = "id不能为空")
    @Range(min = 0, max = 10000, message = "id必须介于0-10000")
    private int id;

    @NotEmpty(message = "name不能为空")
    private String name;

    @Min(value = 1, message = "年龄必须在1-100之间")
    @Max(value = 100, message = "年龄必须在1-100之间")
    private int age;

    @NotNull(message = "gender不能为空")
    private GenderEnum gender;

    @NotNull
    @JsonProperty("isEnabled")
    private boolean enabled;

    @CustomConstraint
    @NotNull
    // @JsonIgnore
    private Date time;

    public User transfer() {
        User user = new User();
        BeanUtils.copyProperties(this, user);
        return user;
    }

    public UserVo(int id, String name, int age, String gender, boolean enabled, Date time) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = GenderEnum.valueOf(gender);
        this.enabled = enabled;
        this.time = time;
    }

}
