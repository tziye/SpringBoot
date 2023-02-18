package com.mongodb.aggregation;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, String> {

    @Aggregation("{ $group : { _id : $customerId, total : { $sum : 1 } } }")
    List<OrdersPerCustomer> totalOrdersPerCustomer(Sort sort);

    @Aggregation(pipeline = {"{ $match : { customerId : ?0 } }", "{ $count : total }"})
    Long totalOrdersForCustomer(String customerId);

    @Aggregation(pipeline = {"{ $match : { id : ?0 } }", "{ $unwind : { path : '$items' } }",
            "{ $project : { id : 1 , customerId : 1 , items : 1 , lineTotal : { $multiply: [ '$items.price' , '$items.quantity' ] } } }",
            "{ $group : { '_id' : '$_id' , 'netAmount' : { $sum : '$lineTotal' } , 'items' : { $addToSet : '$items' } } }",
            "{ $project : { 'orderId' : '$_id' , 'items' : 1 , 'netAmount' : 1 , 'taxAmount' : { $multiply: [ '$netAmount' , ?1 ] } , 'totalAmount' : { $multiply: [ '$netAmount' , ?2 ] } } }"})
    Invoice aggregateInvoiceForOrder(String orderId, double taxRate, double total);

}
