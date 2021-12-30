package be.kuritsu.hetb.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import be.kuritsu.hetb.repository.ExpenseRepository;
import be.kuritsu.hetb.security.SecurityContextService;

@Service
public class BalanceServiceImpl implements BalanceService {

    private final SecurityContextService securityContextService;
    private final ExpenseRepository expenseRepository;

    @Autowired
    public BalanceServiceImpl(SecurityContextService securityContextService,
            ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
        this.securityContextService = securityContextService;
    }

    @Override
    public BigDecimal getBalance() {
        return expenseRepository.getBalance(securityContextService.getAuthenticatedUserName());
    }
}
