package com.santana.bank.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class OrganizationName {

    private static final String organization = "BANK-API"; 

    @GetMapping
    public String details() {
        return "Felipe de Santana Santos RM558916 " + "- " + organization;
    }


}
