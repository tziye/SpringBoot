package com.mongodb.reference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DbManager {

    @Id
    String id;
    String name;

    @DBRef
    List<DbEmployee> employees;

    public DbManager(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
