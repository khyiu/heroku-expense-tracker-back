package be.kuritsu.hetb.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import be.kuritsu.hetb.domain.Expense;
import be.kuritsu.hetb.security.SecuritySubject;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID>, JpaSpecificationExecutor<Expense> {

    @SecuritySubject
    @Override
    Expense getById(UUID uuid);
}
