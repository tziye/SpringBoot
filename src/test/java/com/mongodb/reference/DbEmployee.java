package com.mongodb.reference;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DbEmployee {

    @Id
    String id;
    String name;

    @ToString.Exclude
    @JsonBackReference
    @DBRef(lazy = true)
    DbManager manager;

    public DbEmployee(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
