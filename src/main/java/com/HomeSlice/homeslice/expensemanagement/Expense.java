package com.HomeSlice.homeslice.expensemanagement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private ExpenseCategory category;

    private double amount;
    private String paidBy;

    // the roommates who share this expense
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "expense_members",
                     joinColumns = @JoinColumn(name = "expense_id"))
    @Column(name = "member_name")
    private List<String> members = new ArrayList<>();

    // optional: percentage each roommate owes (leave empty to automatcally split equally)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "expense_percentages",
                     joinColumns = @JoinColumn(name = "expense_id"))
    @MapKeyColumn(name = "member_name")
    @Column(name = "percentage")
    private Map<String, Double> percentages = new HashMap<>();

    private LocalDate expenseDate;
    private LocalDate dueDate;
    private boolean settled;

    // Constructor
    public Expense() {
    }

    public Expense(String name, String description, ExpenseCategory category,
                   double amount, String paidBy, List<String> members,
                   LocalDate dueDate) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.paidBy = paidBy;
        this.members = members;
        this.dueDate = dueDate;
        this.expenseDate = LocalDate.now();
        this.settled = false;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public Map<String, Double> getPercentages() {
        return percentages;
    }

    public void setPercentages(Map<String, Double> percentages) {
        this.percentages = percentages;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isSettled() {
        return settled;
    }

    public void setSettled(boolean settled) {
        this.settled = settled;
    }
}