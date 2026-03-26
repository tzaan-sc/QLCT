package com.example.qlct;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DataManager – Singleton wrapper for DatabaseHelper.
 */
public class DataManager {

    private static DataManager instance;
    private DatabaseHelper dbHelper;

    private DataManager(Context context) {
        // Use application context to prevent memory leaks
        this.dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public static synchronized DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context);
        }
        return instance;
    }

    // Temporary fallback for places where getInstance() is called without context if any
    // It's better to update all callers, but we keep this to signal error if called incorrectly.
    public static synchronized DataManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DataManager is not initialized, call getInstance(Context) first.");
        }
        return instance;
    }

    // ─── CRUD – Transactions ────────────────────────────────────────────────

    public void addTransaction(Transaction t) {
        // Only used previously for seeding, but we can support it.
        dbHelper.addTransaction(t.getAmount(), t.getType(), t.getCategory(), t.getNote(), t.getDate(), t.getCategoryIconRes());
    }

    public void addNewTransaction(double amount, String type,
                                   String category, String note,
                                   Date date, int iconRes) {
        dbHelper.addTransaction(amount, type, category, note, date, iconRes);
    }

    public void deleteTransaction(long id) {
        dbHelper.deleteTransaction(id);
    }

    public List<Transaction> getAllTransactions() {
        return dbHelper.getAllTransactions();
    }

    public List<Transaction> getRecentTransactions(int count) {
        return dbHelper.getRecentTransactions(count);
    }

    public List<Transaction> getTransactionsByType(String type) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : dbHelper.getAllTransactions()) {
            if (t.getType().equals(type)) result.add(t);
        }
        return result;
    }

    public List<Transaction> getTransactionsByCategory(String category) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : dbHelper.getAllTransactions()) {
            if (t.getCategory().equalsIgnoreCase(category)) result.add(t);
        }
        return result;
    }

    // ─── CRUD – Categories ─────────────────────────────────────────────────

    public List<Category> getCategories() {
        return dbHelper.getAllCategories();
    }

    public void addCategory(String name, int iconRes, int color) {
        dbHelper.addCategory(name, iconRes, color);
    }

    public void updateCategory(long id, String name, int iconRes, int color) {
        dbHelper.updateCategory(id, name, iconRes, color);
    }

    public void deleteCategory(long id) {
        dbHelper.deleteCategory(id);
    }

    // ─── Aggregates ────────────────────────────────────────────────────────

    public double getTotalIncome() {
        double sum = 0;
        for (Transaction t : dbHelper.getAllTransactions()) {
            if (t.isIncome()) sum += t.getAmount();
        }
        return sum;
    }

    public double getTotalExpense() {
        double sum = 0;
        for (Transaction t : dbHelper.getAllTransactions()) {
            if (t.isExpense()) sum += t.getAmount();
        }
        return sum;
    }

    public double getBalance() {
        return getTotalIncome() - getTotalExpense();
    }

    public List<String> getCategoryNames() {
        List<String> names = new ArrayList<>();
        for (Category c : dbHelper.getAllCategories()) {
            names.add(c.getName());
        }
        return names;
    }

    public Category getCategoryByName(String name) {
        return dbHelper.getCategoryByName(name);
    }
}
