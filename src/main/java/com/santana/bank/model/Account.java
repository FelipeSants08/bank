package com.santana.bank.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    private Long id;

    private int agency;

    private String name;

    private LocalDate dateCreation;

    private String cpf;

    private Double balance;

    private Boolean active;

    private AccountType type;
}
