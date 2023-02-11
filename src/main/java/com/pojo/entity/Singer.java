package com.pojo.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "singer")
@EqualsAndHashCode(callSuper = true)
public class Singer extends BaseModel {

    @Column(unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    private Integer age;

    /**
     * 内嵌在一张表
     */
    @Embedded
    private Location location;

    /**
     * 一对一，两张表
     */
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "belongSinger", fetch = FetchType.LAZY)
    private Nickname nickname;

    /**
     * 一对多，两张表
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "belongSinger", fetch = FetchType.LAZY)
    private List<Song> song;

    /**
     * 多对多，三张表
     */
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "singer_style", joinColumns = @JoinColumn(name = "singer_id"),
            inverseJoinColumns = @JoinColumn(name = "style_id"))
    private List<Style> style;

}
