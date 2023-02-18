package com.core.jpa.entity;

import com.dto.GenderEnum;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Entity
@Table(name = "user")
@EqualsAndHashCode(callSuper = true)
public class User extends BaseModel implements Serializable {

    String name;
    Integer age;
    Boolean enabled;

    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    Date time;

    @Enumerated(EnumType.STRING)
    GenderEnum gender;

    /**
     * 这个字段不会被持久化
     */
    @Transient
    String ignore;

    /**
     * 一系列触发器
     */
    @PostLoad
    public void readTrigger() {
        log.debug("【触发器】PostLoad：{}", this);
    }

    @PrePersist
    public void beforeInsertTrigger() {
        log.debug("【触发器】PrePersist：{}", this);
    }

    @PostPersist
    public void afterInsertTrigger() {
        log.debug("【触发器】PostPersist：{}", this);
    }

    @PreUpdate
    public void beforeUpdateTrigger() {
        log.debug("【触发器】PreUpdate：{}", this);
    }

    @PostUpdate
    public void afterUpdateTrigger() {
        log.debug("【触发器】PostUpdate：{}", this);
    }

    @PreRemove
    public void beforeDeleteTrigger() {
        log.debug("【触发器】PreRemove：{}", this);
    }

    @PostRemove
    public void afterDeleteTrigger() {
        log.debug("【触发器】PostRemove：{}", this);
    }
}
