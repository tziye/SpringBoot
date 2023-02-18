package com.mongodb.reference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Manager {

    @Id
    private String id;
    private String name;

    @DocumentReference
    private List<Employee> employees;

    public Manager(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
