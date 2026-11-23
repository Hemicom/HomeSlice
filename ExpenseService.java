package com.HomeSlice.homeslice.expensemanagement;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    // Constructor
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    // Get all expenses
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    // Get a specific expense
    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Expense not found with id " + id));
    }

    // Add a new expense
    public Expense addExpense(Expense expense) {
        checkExpense(expense);

        expense.setId(null);
        expense.setSettled(false);
        if (expense.getExpenseDate() == null) {
            expense.setExpenseDate(LocalDate.now());
        }

        // don't allow the exact same expense to be entered twice
        boolean duplicate = expenseRepository.existsByNameAndAmountAndPaidByAndExpenseDate(
                expense.getName(), expense.getAmount(),
                expense.getPaidBy(), expense.getExpenseDate());
        if (duplicate) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This expense has already been added");
        }

        return expenseRepository.save(expense);
    }

    // Update an existing expense
    public Expense updateExpense(Long id, Expense updated) {
        Expense existing = getExpenseById(id);
        checkExpense(updated);

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setCategory(updated.getCategory());
        existing.setAmount(updated.getAmount());
        existing.setPaidBy(updated.getPaidBy());
        existing.setMembers(new ArrayList<>(updated.getMembers()));
        existing.setPercentages(new HashMap<>(updated.getPercentages()));
        existing.setDueDate(updated.getDueDate());
        if (updated.getExpenseDate() != null) {
            existing.setExpenseDate(updated.getExpenseDate());
        }

        return expenseRepository.save(existing);
    }

    // Delete an expense
    public void deleteExpense(Long id) {
        getExpenseById(id); // gives a 404 if it doesn't exist
        expenseRepository.deleteById(id);
    }

    // Mark an expense as settled
    public Expense settleExpense(Long id) {
        Expense expense = getExpenseById(id);
        expense.setSettled(true);
        return expenseRepository.save(expense);
    }

    // Get settled expenses
    public List<Expense> getSettledExpenses() {
        return expenseRepository.findBySettled(true);
    }

    // Get unsettled expenses
    public List<Expense> getUnsettledExpenses() {
        return expenseRepository.findBySettled(false);
    }

    // Get expenses due soon
    public List<Expense> getUpcomingExpenses() {
        return expenseRepository
                .findBySettledFalseAndDueDateGreaterThanEqualOrderByDueDateAsc(LocalDate.now());
    }

    // Get expenses that are past due
    public List<Expense> getOverdueExpenses() {
        return expenseRepository.findBySettledFalseAndDueDateBefore(LocalDate.now());
    }

    // Get expenses by category
    public List<Expense> getExpensesByCategory(ExpenseCategory category) {
        return expenseRepository.findByCategory(category);
    }

    // Get every expense a roommate paid for or is part of
    public List<Expense> getExpensesByMember(String member) {
        List<Expense> result = new ArrayList<>();

        for (Expense expense : expenseRepository.findAll()) {
            boolean involved = expense.getPaidBy().equalsIgnoreCase(member);

            for (String name : expense.getMembers()) {
                if (name.equalsIgnoreCase(member)) {
                    involved = true;
                }
            }

            if (involved) {
                result.add(expense);
            }
        }
        return result;
    }

    // Work out how much each roommate owes or is owed.
    // Positive = they are owed money, negative = they owe money.
    public Map<String, Double> getBalances() {
        Map<String, Double> balances = new HashMap<>();

        for (Expense expense : expenseRepository.findBySettled(false)) {
            String payer = expense.getPaidBy();

            for (String member : expense.getMembers()) {
                // the person who paid doesn't owe themselves
                if (member.equalsIgnoreCase(payer)) {
                    continue;
                }

                double share = getShare(expense, member);
                balances.put(member, balances.getOrDefault(member, 0.0) - share);
                balances.put(payer, balances.getOrDefault(payer, 0.0) + share);
            }
        }

        // round everything to 2 decimal places
        for (String name : balances.keySet()) {
            balances.put(name, round(balances.get(name)));
        }
        return balances;
    }

    // Balance for just one roommate (for the dashboard)
    public double getBalanceForMember(String member) {
        Map<String, Double> balances = getBalances();

        for (String name : balances.keySet()) {
            if (name.equalsIgnoreCase(member)) {
                return balances.get(name);
            }
        }
        return 0.0;
    }

    // How much one roommate owes for an expense
    private double getShare(Expense expense, String member) {
        if (!expense.getPercentages().isEmpty()) {
            double percent = expense.getPercentages().get(member);
            return expense.getAmount() * percent / 100.0;
        }
        // no percentages, so split equally
        return expense.getAmount() / expense.getMembers().size();
    }

    // Checks that the expense has everything it needs
    private void checkExpense(Expense expense) {
        if (expense.getName() == null || expense.getName().trim().isEmpty()) {
            throw badRequest("Expense name is required");
        }
        if (expense.getAmount() <= 0) {
            throw badRequest("Amount must be greater than 0");
        }
        if (expense.getPaidBy() == null || expense.getPaidBy().trim().isEmpty()) {
            throw badRequest("Please say who paid the expense");
        }
        if (expense.getCategory() == null) {
            expense.setCategory(ExpenseCategory.OTHER);
        }
        if (expense.getPercentages() == null) {
            expense.setPercentages(new HashMap<>());
        }
        if (expense.getMembers() == null) {
            expense.setMembers(new ArrayList<>());
        }

        if (!expense.getPercentages().isEmpty()) {
            // splitting by percentage, so the percentages must add up to 100
            double total = 0;
            for (double percent : expense.getPercentages().values()) {
                if (percent <= 0) {
                    throw badRequest("Each percentage must be greater than 0");
                }
                total += percent;
            }
            if (Math.abs(total - 100.0) > 0.01) {
                throw badRequest("Percentages must add up to 100");
            }
            // the members are whoever has a percentage
            expense.setMembers(new ArrayList<>(expense.getPercentages().keySet()));
        } else if (expense.getMembers().isEmpty()) {
            throw badRequest("Please add at least one roommate to split the expense with");
        }
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}