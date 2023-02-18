package com.mongodb.reference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JpaManager {

    @Id
    String id;
    String name;

    @ReadOnlyProperty
    @DocumentReference(lookup = "{'managerId':?#{#self._id} }")
    List<JpaEmployee> employees;

    public JpaManager(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
