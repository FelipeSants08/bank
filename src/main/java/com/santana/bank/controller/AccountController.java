package com.santana.bank.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.santana.bank.dto.PixDTO;
import com.santana.bank.dto.TransactionDTO;
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
        account.setActive(true);
        ValidationAccount(account);
        repository.add(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    //Busca conta por ID
    @GetMapping("/account/{id}/id")
    public ResponseEntity<Account> get(@PathVariable Long id) {
        log.info("Buscando conta por ID");
        return ResponseEntity.ok(getAccountId(id));
    }
    
    @GetMapping("/account/{cpf}/cpf")
    public ResponseEntity<Account> get(@PathVariable String cpf) {
        log.info("Buscando conta por CPF");
        return ResponseEntity.ok(getAccountCpf(cpf));
    }

    @PutMapping("/account/{id}/close")
    public ResponseEntity<Account> close(@PathVariable Long id) {
        log.info("Encerrando conta");
        Account account = getAccountId(id);
        account.setActive(false);
        return ResponseEntity.ok(account);
    }


    @PutMapping("/account/{id}/deposit")
    public ResponseEntity<Account> deposit(@PathVariable Long id, @RequestBody TransactionDTO transaction) {
        log.info("Fazendo deposito na conta: " + id);
        Account account = getAccountId(id);

        if (!account.getActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conta está inativa");
        }
    
        if (transaction.getValue() == null || transaction.getValue() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor de depósito inválido");
        }
    
        account.setBalance(account.getBalance() + transaction.getValue());
        return ResponseEntity.ok(account);
    }


    @PutMapping("/account/{id}/withdraw")
    public ResponseEntity<Account> withdraw(@PathVariable Long id, @RequestBody TransactionDTO transaction) {
        log.info("Realizando sague na conta" + id);
        Account account = getAccountId(id);

        if (!account.getActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conta está inativa");
        }
        if (transaction.getValue() == null || transaction.getValue() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor não pode ser negativo");
        }
        if (transaction.getValue() > account.getBalance()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente");
        }

        account.setBalance(account.getBalance() - transaction.getValue());
        return ResponseEntity.ok(account);
    }


    @PutMapping("/account/pix")
    public ResponseEntity<Account> transactionPix(@RequestBody PixDTO pix) {
        log.info("Realizando pix");
        Account origin = getAccountId(pix.getIdOrigin());
        Account destination = getAccountId(pix.getIdDestination());
        if (!origin.getActive() || !destination.getActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uma das contas está inativa");
        }
    
        if (pix.getValue() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor inválido para pix");
        }
    
        if (origin.getBalance() < pix.getValue()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente");
        }
        origin.setBalance(origin.getBalance() - pix.getValue());
        destination.setBalance(destination.getBalance() + pix.getValue());
        return ResponseEntity.ok(origin);
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

    private void ValidationAccount(Account account) {
        if (account.getName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome não pode ser nulo");
        }
        if (account.getCpf() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF não pode ser nulo");
        }
        if (account.getCpf().length() != 11) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF inválido");
        }
        if (account.getDateCreation().isAfter(LocalDate.now()) || account.getDateCreation() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data não pode ser no futuro ou nula");
        }
        if (account.getBalance() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo não pode ser negativo");
        }
        if (account.getType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo inválido(Corrente, poupanca ou salario)");
        }
    }
}
