package com.mongodb.book;

import lombok.Data;

@Data
public class Offer {

    Integer finskyOfferType;
    OfferPrice listPrice;
    OfferPrice retailPrice;
    Boolean giftable;
}
