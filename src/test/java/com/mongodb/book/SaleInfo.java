package com.mongodb.book;

import lombok.Data;

import java.util.List;

@Data
public class SaleInfo {

    String country;
    String saleability;
    Boolean isEbook;
    SalePrice listPrice;
    SalePrice retailPrice;
    List<Offer> offers;
    String buyLink;
}
