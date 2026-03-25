package com.example.qlct;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {

    public static final String TYPE_INCOME  = "INCOME";
    public static final String TYPE_EXPENSE = "EXPENSE";

    private long     id;
    private double   amount;
    private String   type;        // TYPE_INCOME | TYPE_EXPENSE
    private String   category;
    private String   note;
    private Date     date;
    private int      categoryIconRes;

    public Transaction(long id, double amount, String type,
                       String category, String note,
                       Date date, int categoryIconRes) {
        this.id              = id;
        this.amount          = amount;
        this.type            = type;
        this.category        = category;
        this.note            = note;
        this.date            = date;
        this.categoryIconRes = categoryIconRes;
    }

    // Getters & Setters
    public long getId()                    { return id; }
    public void setId(long id)             { this.id = id; }

    public double getAmount()              { return amount; }
    public void setAmount(double amount)   { this.amount = amount; }

    public String getType()                { return type; }
    public void setType(String type)       { this.type = type; }

    public String getCategory()            { return category; }
    public void setCategory(String cat)    { this.category = cat; }

    public String getNote()                { return note; }
    public void setNote(String note)       { this.note = note; }

    public Date getDate()                  { return date; }
    public void setDate(Date date)         { this.date = date; }

    public int getCategoryIconRes()                  { return categoryIconRes; }
    public void setCategoryIconRes(int iconRes)      { this.categoryIconRes = iconRes; }

    public boolean isIncome()  { return TYPE_INCOME.equals(type); }
    public boolean isExpense() { return TYPE_EXPENSE.equals(type); }
}
