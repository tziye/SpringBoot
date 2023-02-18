package com.mongodb.aggregation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    String id;
    String customerId;
    Date orderDate;
    List<LineItem> items;

    public Order(String customerId, Date orderDate) {
        this(null, customerId, orderDate, new ArrayList<>());
    }

    public Order addItem(LineItem item) {
        this.items.add(item);
        return this;
    }
}
