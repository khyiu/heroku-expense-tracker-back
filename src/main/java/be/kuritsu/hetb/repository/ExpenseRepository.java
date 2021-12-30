package be.kuritsu.hetb.repository;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import be.kuritsu.hetb.domain.Expense;
import be.kuritsu.hetb.security.SecuritySubject;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID>, JpaSpecificationExecutor<Expense> {

    @SecuritySubject
    @Override
    Expense getById(UUID uuid);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e where e.owner = :owner")
    BigDecimal getBalance(@Param("owner") String owner);
}
