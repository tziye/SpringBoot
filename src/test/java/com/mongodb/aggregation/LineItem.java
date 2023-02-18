package com.mongodb.aggregation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineItem {

    String caption;
    double price;
    int quantity;

    public LineItem(String caption, double price) {
        this.caption = caption;
        this.price = price;
    }
}
