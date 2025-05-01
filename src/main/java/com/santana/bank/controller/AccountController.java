package com.santana.bank.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.santana.bank.model.Account;




@RestController
public class AccountController {
    

    private final Logger log = LoggerFactory.getLogger(getClass());

    private List<Account> repository = new ArrayList<>();


    //Busca todas as contas
    @GetMapping("/account")
    public List<Account> getAll() {
        log.info("Buscando contas...");
        return repository;
    }

    //Cadastra novas contas
    @PostMapping("/account")
    public ResponseEntity<Account> create(@RequestBody Account account) {
        log.info("Cadastrando conta " + account.getName());
        repository.add(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    //Busca conta por ID
    @GetMapping("/account/id/{id}")
    public ResponseEntity<Account> get(@PathVariable Long id) {
        log.info("Buscando conta por ID");
        return ResponseEntity.ok(getAccountId(id));
    }
    
    @GetMapping("/account/cpf/{cpf}")
    public ResponseEntity<Account> get(@PathVariable String cpf) {
        log.info("Buscando conta por CPF");
        return ResponseEntity.ok(getAccountCpf(cpf));
    }




    private Account getAccountCpf(String cpf) {
        return repository.stream()
        .filter(c -> c.getCpf().equals(cpf))
        .findFirst()
        .orElseThrow(
            () -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Conta não encontrada"));
    }



    private Account getAccountId(Long id) {
        return repository.stream()
        .filter(a -> a.getId().equals(id))
        .findFirst()
        .orElseThrow(
            () -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Conta não encontrada"));
    }
}
