package be.kuritsu.hetb.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import be.kuritsu.hetb.repository.ExpenseRepository;

@Service
public class BalanceServiceImpl implements BalanceService {

    private final ExpenseRepository expenseRepository;

    @Autowired
    public BalanceServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public BigDecimal getBalance() {
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        return expenseRepository.getBalance(currentUserName);
    }
}
