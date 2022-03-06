package be.kuritsu.hetb.mapper;

import java.util.UUID;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import be.kuritsu.het.model.ExpenseRequest;
import be.kuritsu.het.model.ExpenseResponse;
import be.kuritsu.hetb.domain.Expense;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = TagMapper.class)
public interface ExpenseMapper {

    Expense expenseRequestToRequest(ExpenseRequest expenseRequest);

    ExpenseResponse expenseToExpenseResponse(Expense expense);

    default String fromUUID(UUID id) {
        return id.toString();
    }
}