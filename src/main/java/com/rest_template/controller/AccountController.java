package com.rest_template.controller;

import com.rest_template.dto.AccountDTO;
import com.rest_template.dto.ResponseWrapper;
import com.rest_template.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseWrapper> getAllAccounts(){

        return ResponseEntity.ok(ResponseWrapper.builder()
                .success(true)
                .message("All Accounts successfully retrieved")
                .code(HttpStatus.OK.value())
                .data(accountService.findAll()).build());
    }

    @GetMapping("/{username}")
    public ResponseEntity<ResponseWrapper> getAllAccountByUsername(@PathVariable("username") String username){

        return ResponseEntity.ok(ResponseWrapper.builder()
                .success(true)
                .message("All Accounts successfully retrieved")
                .code(HttpStatus.OK.value())
                .data(accountService.findAllByUsername(username)).build());
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseWrapper> createNewAccount(@RequestBody AccountDTO accountDTO){

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.builder()
                        .success(true)
                        .message("Account: "+ accountDTO.getAccountNumber()+ "created")
                        .code(HttpStatus.CREATED.value())
                        .data(accountService.create(accountDTO)).build());
    }

    @GetMapping("/currency/{username}")
    public ResponseEntity<ResponseWrapper> getAllAccountsByUsernameWithSelectedCurrencies(@PathVariable("username") String username,
                                                                                          @RequestParam(name="currencies") List<String> currencies){
        return ResponseEntity.ok(ResponseWrapper.builder()
                .success(true)
                .message("Balance successfully converted to selected currencies")
                .code(HttpStatus.OK.value())
                .data(accountService.findAllByUsernameAndCurrencyList(username, currencies)).build());
    }
}