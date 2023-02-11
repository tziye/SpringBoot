package com.redis.person;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;

@RedisHash("persons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"children"})
public class Person {

    @Id
    String id;

    @Indexed
    String firstname;

    @Indexed
    String lastname;

    Gender gender;

    Address address;

    @Reference
    List<Person> children;

    public Person(String firstname, String lastname, Gender gender) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.gender = gender;
    }
}
