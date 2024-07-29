package com.example.adproject;

public class Expense {
    private String date;
    private String category;
    private String subCategory;
    private double amount;

    public Expense(String date, String category, String subCategory, double amount) {
        this.date = date;
        this.category = category;
        this.subCategory = subCategory;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public double getAmount() {
        return amount;
    }
}