package com.core.jpa.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "style")
@EqualsAndHashCode(callSuper = true)
public class Style extends BaseModel {

    @Enumerated(EnumType.STRING)
    StyleEnum style;

    @ToString.Exclude
    @JsonBackReference
    @ManyToMany
    @JoinTable(name = "singer_style", joinColumns = @JoinColumn(name = "style_id"),
            inverseJoinColumns = @JoinColumn(name = "singer_id"))
    List<Singer> singer;

    public enum StyleEnum {
        情歌,
        民谣,
        摇滚,
        饶舌
    }

}
