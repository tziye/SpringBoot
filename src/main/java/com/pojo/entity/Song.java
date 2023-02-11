package com.pojo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "song", uniqueConstraints = {@UniqueConstraint(name = "name_time", columnNames = {"name", "time"})})
@EqualsAndHashCode(callSuper = true)
public class Song extends BaseModel {

    private String name;
    private Date time;
    private Integer score;

    /**
     * 下面两个属性只是禁止循环输出，从数据库查出来还是双向的
     */
    @ToString.Exclude
    @JsonBackReference
    @ManyToOne(optional = false)
    @JoinColumn(name = "singer_id")
    private Singer belongSinger;

}
