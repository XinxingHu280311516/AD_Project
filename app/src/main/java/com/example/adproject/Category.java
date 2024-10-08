package com.example.adproject;

import org.intellij.lang.annotations.Identifier;

import java.util.List;

public class Category {

    private Integer id;
    private String name;
    private double budget;
    private User user;
    private int type;
    private List<Transaction> transactions;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double amount) {
        this.budget = amount;
    }

    public Category(String category) {}

    public Category(){}

    @Override
    public String toString() {
        return name; // 返回类别名称
    }
}
