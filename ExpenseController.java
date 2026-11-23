package com.HomeSlice.homeslice.expensemanagement;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    // Constructor
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // Get all expenses
    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    // Get a specific expense
    @GetMapping("/{id}")
    public Expense getExpenseById(@PathVariable Long id) {
        return expenseService.getExpenseById(id);
    }

    // Create a new expense
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Expense addExpense(@RequestBody Expense expense) {
        return expenseService.addExpense(expense);
    }

    // Update an existing expense
    @PutMapping("/{id}")
    public Expense updateExpense(
            @PathVariable Long id,
            @RequestBody Expense expense) {
        return expenseService.updateExpense(id, expense);
    }

    // Delete an expense
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    // Mark an expense as settled
    @PutMapping("/{id}/settle")
    public Expense settleExpense(@PathVariable Long id) {
        return expenseService.settleExpense(id);
    }

    // Get settled expenses
    @GetMapping("/settled")
    public List<Expense> getSettledExpenses() {
        return expenseService.getSettledExpenses();
    }

    // Get unsettled expenses
    @GetMapping("/unsettled")
    public List<Expense> getUnsettledExpenses() {
        return expenseService.getUnsettledExpenses();
    }

    // Get expenses that are due soon
    @GetMapping("/upcoming")
    public List<Expense> getUpcomingExpenses() {
        return expenseService.getUpcomingExpenses();
    }

    // Get expenses that are past due
    @GetMapping("/overdue")
    public List<Expense> getOverdueExpenses() {
        return expenseService.getOverdueExpenses();
    }

    // Get expenses by category
    @GetMapping("/category/{category}")
    public List<Expense> getExpensesByCategory(@PathVariable ExpenseCategory category) {
        return expenseService.getExpensesByCategory(category);
    }

    // Get expenses for one roommate
    @GetMapping("/member/{member}")
    public List<Expense> getExpensesByMember(@PathVariable String member) {
        return expenseService.getExpensesByMember(member);
    }

    // Get what everyone owes or is owed
    @GetMapping("/balances")
    public Map<String, Double> getBalances() {
        return expenseService.getBalances();
    }

    // Get the balance for one roommate
    @GetMapping("/balances/{member}")
    public double getBalanceForMember(@PathVariable String member) {
        return expenseService.getBalanceForMember(member);
    }
}