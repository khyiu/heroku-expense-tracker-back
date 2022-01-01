package be.kuritsu.hetb.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import be.kuritsu.hetb.caching.CacheNames;
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

    @Cacheable(value = CacheNames.USER_BALANCE_CACHE, key = "@securityContextService.getAuthenticatedUserName()")
    @Override
    public BigDecimal getBalance() {
        return expenseRepository.getBalance(securityContextService.getAuthenticatedUserName());
    }
}
