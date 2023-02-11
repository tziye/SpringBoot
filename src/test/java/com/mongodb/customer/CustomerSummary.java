package com.mongodb.customer;

import org.springframework.beans.factory.annotation.Value;

public interface CustomerSummary {

    @Value("#{target.firstname + ' ' + target.lastname}")
    String getFullName();

    Address getAddress();
}
