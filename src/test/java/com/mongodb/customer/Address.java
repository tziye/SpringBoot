package com.mongodb.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    Point location;
    String street;
    String zipCode;

    public Address(Point location) {
        this.location = location;
    }
}
