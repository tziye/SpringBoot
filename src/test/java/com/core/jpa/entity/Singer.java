package com.core.jpa.entity;

import com.dto.GenderEnum;
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
    String name;

    @Enumerated(EnumType.STRING)
    GenderEnum gender;

    Integer age;

    /**
     * 内嵌在一张表
     */
    @Embedded
    Location location;

    /**
     * 一对一，两张表
     */
    @OrderColumn
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "belongSinger", fetch = FetchType.EAGER)
    Nickname nickname;

    /**
     * 一对多，两张表
     */
    @OrderColumn
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "belongSinger", fetch = FetchType.EAGER)
    List<Song> song;

    /**
     * 多对多，三张表
     */
    @OrderColumn
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "singer_style", joinColumns = @JoinColumn(name = "singer_id"),
            inverseJoinColumns = @JoinColumn(name = "style_id"))
    List<Style> style;

}
