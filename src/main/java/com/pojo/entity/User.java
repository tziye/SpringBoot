package com.pojo.entity;

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

    private String name;
    private Integer age;
    private Boolean enabled;

    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date time;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    /**
     * 这个字段不会被持久化
     */
    @Transient
    private String ignore;

    /**
     * 一系列触发器
     */
    @PostLoad
    public void readTrigger() {
        log.info("【触发器】PostLoad：{}", this);
    }

    @PrePersist
    public void beforeInsertTrigger() {
        log.info("【触发器】PrePersist：{}", this);
    }

    @PostPersist
    public void afterInsertTrigger() {
        log.info("【触发器】PostPersist：{}", this);
    }

    @PreUpdate
    public void beforeUpdateTrigger() {
        log.info("【触发器】PreUpdate：{}", this);
    }

    @PostUpdate
    public void afterUpdateTrigger() {
        log.info("【触发器】PostUpdate：{}", this);
    }

    @PreRemove
    public void beforeDeleteTrigger() {
        log.info("【触发器】PreRemove：{}", this);
    }

    @PostRemove
    public void afterDeleteTrigger() {
        log.info("【触发器】PostRemove：{}", this);
    }
}
