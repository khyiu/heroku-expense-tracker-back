package be.kuritsu.hetb.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import be.kuritsu.het.api.BalanceApi;

@RestController
public class BalanceController implements BalanceApi {

    @Override
    public ResponseEntity<BigDecimal> getBalance() {
        return null;
    }
}
