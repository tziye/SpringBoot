package com.mongodb.reference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JpaEmployee {

    @Id
    String id;
    String name;
    String managerId;

    public JpaEmployee(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
