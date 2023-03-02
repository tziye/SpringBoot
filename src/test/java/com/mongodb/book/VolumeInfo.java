package com.mongodb.book;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class VolumeInfo {

    String title;
    String subtitle;
    List<String> authors;
    String publisher;
    Date publishedDate;
    String description;
    List<IndustryIdentifier> industryIdentifiers;
    ReadingMode readingModes;
    Integer pageCount;
    String printType;
    List<String> categories;
    Double averageRating;
    Integer ratingsCount;
    String maturityRating;
    Boolean allowAnonLogging;
    String contentVersion;
    ImageLink imageLinks;
    String language;
    String previewLink;
    String infoLink;
    String canonicalVolumeLink;
}