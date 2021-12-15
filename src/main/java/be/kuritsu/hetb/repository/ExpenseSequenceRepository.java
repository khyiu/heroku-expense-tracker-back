package be.kuritsu.hetb.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Profile("!test")
@Repository
public interface ExpenseSequenceRepository extends ExpenseRepository{

    @Query(nativeQuery = true, value = "select het.expense_order_seq.nextval from dual")
    int getNextOrderSequenceValue();
}
