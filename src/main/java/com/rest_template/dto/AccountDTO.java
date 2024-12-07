package com.rest_template.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rest_template.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDTO {
    @JsonIgnore
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long accountNumber;
    private AccountType accountType;
    private String baseCurrency;
    private BigDecimal balance;
    private String username;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Map<String, BigDecimal> otherCurrencies;
}
