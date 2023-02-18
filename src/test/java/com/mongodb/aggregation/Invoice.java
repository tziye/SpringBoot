package com.mongodb.aggregation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    String orderId;
    double taxAmount;
    double netAmount;
    double totalAmount;
    List<LineItem> items;
}
