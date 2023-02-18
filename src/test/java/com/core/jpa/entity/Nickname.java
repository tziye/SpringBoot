package com.core.jpa.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nickname")
@EqualsAndHashCode(callSuper = true)
public class Nickname extends BaseModel {

    @ToString.Exclude
    @JsonBackReference
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "singer_id")
    Singer belongSinger;

    String name;
}
