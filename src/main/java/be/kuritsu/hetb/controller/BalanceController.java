package be.kuritsu.hetb.controller;

import static be.kuritsu.hetb.config.SecurityConfig.ROLE_EXPENSE_TRACKER_USER;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;

import be.kuritsu.het.api.BalanceApi;
import be.kuritsu.hetb.service.BalanceService;

@RestController
public class BalanceController implements BalanceApi {

    private BalanceService balanceService;

    @Autowired
    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @Secured(ROLE_EXPENSE_TRACKER_USER)
    @Override
    public ResponseEntity<BigDecimal> getBalance() {
        return ResponseEntity.ok(balanceService.getBalance());
    }
}
