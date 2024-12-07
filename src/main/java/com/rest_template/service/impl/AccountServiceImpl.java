package com.rest_template.service.impl;

import com.rest_template.client.ExchangeRateClient;
import com.rest_template.dto.AccountDTO;
import com.rest_template.dto.UserDTO;
import com.rest_template.entity.Account;
import com.rest_template.entity.User;
import com.rest_template.repository.AccountRepository;
import com.rest_template.service.AccountService;
import com.rest_template.service.UserService;
import com.rest_template.util.MapperUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {
    @Value("${api.key}")
    private String apiKey;
    private final AccountRepository accountRepository;
    private final MapperUtil mapperUtil;
    private final UserService userService;
    private final ExchangeRateClient client;

    public AccountServiceImpl(AccountRepository accountRepository, MapperUtil mapperUtil, UserService userService, ExchangeRateClient client) {
        this.accountRepository = accountRepository;
        this.mapperUtil = mapperUtil;
        this.userService = userService;
        this.client = client;
    }




    @Override
    public List<AccountDTO> findAllByUsername(String username) {

        return accountRepository.findAllByUser_Username(username).stream().map(account-> {
            AccountDTO accountDTO=mapperUtil.convert(account, new AccountDTO());
            accountDTO.setUsername(username);
            accountDTO.getBaseCurrency();
            Map<String, BigDecimal> otherCurrencies= new HashMap<>();
            accountDTO.setOtherCurrencies(getAllCurrenciesByBalance(accountDTO.getBalance(), accountDTO.getBaseCurrency()));
            return accountDTO;
        }).collect(Collectors.toList());
    }

    private Map<String, BigDecimal> getAllCurrenciesByBalance(BigDecimal balance,String baseCurrency){

        Map<String, BigDecimal> exchangeRates=client.getAllCurrencies(apiKey,baseCurrency).getConversion_rates();
        Map<String, BigDecimal> otherCurrencies= new HashMap<>();
        otherCurrencies=exchangeRates.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> balance.multiply(entry.getValue())
        ));
        return otherCurrencies;
    }












    @Override
    public AccountDTO create(AccountDTO accountDTO) {
        /// Find the user by username
        UserDTO userDto = userService.findByUsername(accountDTO.getUsername());

        ///Convert AccountDTO to Account
        Account accountToSave=mapperUtil.convert(accountDTO, new Account());

        ///Set the user of the account y converting UserDTO to user
        accountToSave.setUser(mapperUtil.convert(userDto, new User()));

        /// Generate a random accountNumber for the new account
        accountToSave.setAccountNumber(generateAccountNumber());

        /// Save the newly created account to the database
        Account newAccount=accountRepository.save(accountToSave);

        /// Convert the saved account Entity back to AccountDTO
        AccountDTO accountToReturn= mapperUtil.convert(newAccount, new AccountDTO());

        /// Set the username in the returned AccountDTO for consistency
        accountToReturn.setUsername(userDto.getUsername());

        return accountToReturn;
    }

    private Long generateAccountNumber(){

        return (long) (Math.random() *1000000000000L);
    }

    @Override
    public List<AccountDTO> findAll() {
        return accountRepository.findAll().stream().map(account ->
                mapperUtil.convert(account, new AccountDTO())).collect(Collectors.toList());
    }

    @Override
    public List<AccountDTO> findAllByUsernameAndCurrencyList(String username, List<String> currencyList) {
        return accountRepository.findAllByUser_Username(username)
                .stream().map(account ->{
                    AccountDTO accountDTO= mapperUtil.convert(account, new AccountDTO());
                    accountDTO.setUsername(username);
                    Map<String,BigDecimal> otherCurrencies= new HashMap<>();
                    accountDTO.setOtherCurrencies(getSelectedCurrenciesByBalance(accountDTO.getBalance(),
                            accountDTO.getBaseCurrency(),currencyList));
                    return accountDTO;

                }).collect(Collectors.toList());
    }

    private Map<String, BigDecimal> getSelectedCurrenciesByBalance(BigDecimal balance,String baseCurrency, List<String> currencies){

        Map<String, BigDecimal> exchangeRates=client.getAllCurrencies(apiKey,baseCurrency).getConversion_rates();
        Map<String, BigDecimal> otherCurrencies= new HashMap<>();
        otherCurrencies=exchangeRates.entrySet().stream().filter(entry ->currencies.
                        contains(entry.getKey())).
                collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> balance.multiply(entry.getValue())));
        return otherCurrencies;
    }



}
