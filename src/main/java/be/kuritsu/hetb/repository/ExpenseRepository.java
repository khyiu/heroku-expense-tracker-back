package be.kuritsu.hetb.repository;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import be.kuritsu.hetb.domain.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    long countByOwnerAndDate(String owner, LocalDate date);
}
