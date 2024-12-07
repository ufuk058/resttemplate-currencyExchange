package com.rest_template.service.impl;

import com.rest_template.client.ExchangeRateClient;
import com.rest_template.dto.AccountDTO;
import com.rest_template.dto.UserDTO;
import com.rest_template.dto.response.ExchangeRate;
import com.rest_template.entity.Account;
import com.rest_template.entity.User;
import com.rest_template.repository.AccountRepository;
import com.rest_template.service.AccountService;
import com.rest_template.service.UserService;
import com.rest_template.util.MapperUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Arrays;
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
    private final RestTemplate restTemplate;

    public AccountServiceImpl(AccountRepository accountRepository, MapperUtil mapperUtil, UserService userService, ExchangeRateClient client, RestTemplate restTemplate) {
        this.accountRepository = accountRepository;
        this.mapperUtil = mapperUtil;
        this.userService = userService;
        this.client = client;
        this.restTemplate = restTemplate;
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

        /// 1. Feign Client
        //Map<String, BigDecimal> exchangeRates=client.getAllCurrencies(apiKey,baseCurrency).getConversion_rates();



        /// 2. RestTemplate
        //https://v6.exchangerate-api.com/v6/5c2e33d19f331a5934be65b3/latest/GBP
        String baseURL="https://v6.exchangerate-api.com";
        String URI=baseURL+ "/v6/"+apiKey+"/latest/"+baseCurrency;

        //        Comment Out from HERE
//        /// If We need to add a Query parameter to the Client URI with rest template
//        UriComponentsBuilder builder= UriComponentsBuilder.fromUriString(URI)
//                .queryParam("baseCurrency", String.join(baseCurrency));
//
//        /// Adding headers
//        HttpHeaders headers= new HttpHeaders();
//        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON)); // this is for adding data type
//        headers.set("apiKey",apiKey); // this for adding security api key in header
//
//
//        HttpEntity<String> entity= new HttpEntity<>(headers); // headers can not directly pass to the exchange method
//        ResponseEntity<ExchangeRate>  allCurrencies= restTemplate.exchange(
//           builder.toUriString(),
//                HttpMethod.GET,
//                entity,
//                ExchangeRate.class
//        );

          //       to  HERE

        ExchangeRate rateResponse= restTemplate.getForObject(URI,ExchangeRate.class);
        Map<String, BigDecimal> exchangeRates= rateResponse.getConversion_rates();
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

        /// 1. Feign Client
       // Map<String, BigDecimal> exchangeRates=client.getAllCurrencies(apiKey,baseCurrency).getConversion_rates();

        /// 2. Rest Template;
        String baseURL="https://v6.exchangerate-api.com";
        String URI=baseURL+ "/v6/"+apiKey+"/latest/"+baseCurrency;

        ExchangeRate rateResponse= restTemplate.getForObject(URI,ExchangeRate.class);
        Map<String, BigDecimal> exchangeRates= rateResponse.getConversion_rates();

        Map<String, BigDecimal> otherCurrencies= new HashMap<>();
        otherCurrencies=exchangeRates.entrySet().stream().filter(entry ->currencies.
                        contains(entry.getKey())).
                collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> balance.multiply(entry.getValue())));
        return otherCurrencies;
    }



}
