package com.rest_template.client;


import com.rest_template.dto.response.ExchangeRate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(url = "https://v6.exchangerate-api.com/", name = "currency-client")
public interface ExchangeRateClient {

    @GetMapping("v6/{apiKey}/latest/{baseCurrency}")
    ExchangeRate getAllCurrencies(@PathVariable("apiKey") String apiKey,
                                  @PathVariable("baseCurrency") String baseCurrency);
}
