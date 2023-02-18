package com.mongodb.reference;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    String id;
    String name;

    @ToString.Exclude
    @JsonBackReference
    @DocumentReference(lazy = true)
    Manager manager;

    public Employee(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
