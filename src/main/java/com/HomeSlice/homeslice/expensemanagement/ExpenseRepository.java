package com.HomeSlice.homeslice.expensemanagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findBySettled(boolean settled);

    List<Expense> findByCategory(ExpenseCategory category);

    // unsettled expenses due today or later, soonest first
    List<Expense> findBySettledFalseAndDueDateGreaterThanEqualOrderByDueDateAsc(LocalDate date);

    // unsettled expenses that are past their due date
    List<Expense> findBySettledFalseAndDueDateBefore(LocalDate date);

    // stop duplicate expenses from being added
    boolean existsByNameAndAmountAndPaidByAndExpenseDate(
            String name, double amount, String paidBy, LocalDate expenseDate);

}