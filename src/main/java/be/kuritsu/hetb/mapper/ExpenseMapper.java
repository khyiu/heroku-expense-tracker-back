package be.kuritsu.hetb.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import be.kuritsu.het.model.ExpenseRequest;
import be.kuritsu.hetb.domain.Expense;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ExpenseMapper {

    Expense expenseRequestToRequest(ExpenseRequest expenseRequest);
}