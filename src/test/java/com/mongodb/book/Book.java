package com.mongodb.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    String id;
    String kind;
    VolumeInfo volumeInfo;
    SaleInfo saleInfo;
    SearchInfo searchInfo;
}
