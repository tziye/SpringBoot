package com.mongodb.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    ObjectId id = new ObjectId();
    String firstname;
    String lastname;
    Address address;

    public Customer(String firstname, String lastname) {
        this(firstname, lastname, null);
    }

    public Customer(String firstname, String lastname, Address address) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.address = address;
    }
}
