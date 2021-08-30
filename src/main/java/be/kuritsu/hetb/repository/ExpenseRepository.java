package be.kuritsu.hetb.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import be.kuritsu.hetb.domain.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
}
