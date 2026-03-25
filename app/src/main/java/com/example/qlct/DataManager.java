package com.example.qlct;

import android.content.Context;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * DataManager – Singleton in-memory store for transactions and categories.
 * In a real app this would be backed by Room / SQLite.
 */
public class DataManager {

    private static DataManager instance;

    private final List<Transaction> transactions = new ArrayList<>();
    private final List<Category>    categories   = new ArrayList<>();
    private long nextTransId  = 100;
    private long nextCatId    = 10;

    private DataManager() {
        seedCategories();
        seedTransactions();
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) instance = new DataManager();
        return instance;
    }

    // ─── Categories ──────────────────────────────────────────────────────────

    private void seedCategories() {
        categories.add(new Category(1, "Food & Drinks",    R.drawable.ic_cat_food,          Color.parseColor("#FF6F00")));
        categories.add(new Category(2, "Transport",        R.drawable.ic_cat_transport,     Color.parseColor("#1565C0")));
        categories.add(new Category(3, "Shopping",         R.drawable.ic_cat_shopping,      Color.parseColor("#6A1B9A")));
        categories.add(new Category(4, "Health",           R.drawable.ic_cat_health,        Color.parseColor("#D32F2F")));
        categories.add(new Category(5, "Entertainment",    R.drawable.ic_cat_entertainment, Color.parseColor("#00838F")));
        categories.add(new Category(6, "Salary",           R.drawable.ic_cat_salary,        Color.parseColor("#2E7D32")));
        categories.add(new Category(7, "Other",            R.drawable.ic_cat_other,         Color.parseColor("#546E7A")));
    }

    private void seedTransactions() {
        Calendar cal = Calendar.getInstance();

        addTransaction(new Transaction(nextTransId++, 3_500_000, Transaction.TYPE_INCOME,
                "Salary", "Monthly salary", daysAgo(cal, 1),  R.drawable.ic_cat_salary));

        addTransaction(new Transaction(nextTransId++, 85_000,    Transaction.TYPE_EXPENSE,
                "Food & Drinks", "Lunch with colleagues", daysAgo(cal, 1), R.drawable.ic_cat_food));

        addTransaction(new Transaction(nextTransId++, 250_000,   Transaction.TYPE_EXPENSE,
                "Transport", "Taxi fare", daysAgo(cal, 2), R.drawable.ic_cat_transport));

        addTransaction(new Transaction(nextTransId++, 1_200_000, Transaction.TYPE_EXPENSE,
                "Shopping", "New clothes", daysAgo(cal, 3), R.drawable.ic_cat_shopping));

        addTransaction(new Transaction(nextTransId++, 500_000,   Transaction.TYPE_INCOME,
                "Other", "Freelance payment", daysAgo(cal, 4), R.drawable.ic_cat_other));

        addTransaction(new Transaction(nextTransId++, 150_000,   Transaction.TYPE_EXPENSE,
                "Health", "Pharmacy", daysAgo(cal, 5), R.drawable.ic_cat_health));

        addTransaction(new Transaction(nextTransId++, 200_000,   Transaction.TYPE_EXPENSE,
                "Entertainment", "Cinema & snacks", daysAgo(cal, 6), R.drawable.ic_cat_entertainment));

        addTransaction(new Transaction(nextTransId++, 60_000,    Transaction.TYPE_EXPENSE,
                "Food & Drinks", "Coffee", daysAgo(cal, 7), R.drawable.ic_cat_food));
    }

    private Date daysAgo(Calendar cal, int days) {
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -days);
        return cal.getTime();
    }

    // ─── CRUD – Transactions ────────────────────────────────────────────────

    public void addTransaction(Transaction t) {
        transactions.add(0, t); // newest first
    }

    public void addNewTransaction(double amount, String type,
                                   String category, String note,
                                   Date date, int iconRes) {
        Transaction t = new Transaction(nextTransId++, amount, type,
                category, note, date, iconRes);
        transactions.add(0, t);
    }

    public void deleteTransaction(long id) {
        transactions.removeIf(t -> t.getId() == id);
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    public List<Transaction> getRecentTransactions(int count) {
        int end = Math.min(count, transactions.size());
        return new ArrayList<>(transactions.subList(0, end));
    }

    public List<Transaction> getTransactionsByType(String type) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getType().equals(type)) result.add(t);
        }
        return result;
    }

    public List<Transaction> getTransactionsByCategory(String category) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getCategory().equalsIgnoreCase(category)) result.add(t);
        }
        return result;
    }

    // ─── CRUD – Categories ─────────────────────────────────────────────────

    public List<Category> getCategories() {
        return new ArrayList<>(categories);
    }

    public void addCategory(String name, int iconRes, int color) {
        categories.add(new Category(nextCatId++, name, iconRes, color));
    }

    public void updateCategory(long id, String name, int iconRes, int color) {
        for (Category c : categories) {
            if (c.getId() == id) {
                c.setName(name);
                c.setIconRes(iconRes);
                c.setColor(color);
                return;
            }
        }
    }

    public void deleteCategory(long id) {
        categories.removeIf(c -> c.getId() == id);
    }

    // ─── Aggregates ────────────────────────────────────────────────────────

    public double getTotalIncome() {
        double sum = 0;
        for (Transaction t : transactions) if (t.isIncome())  sum += t.getAmount();
        return sum;
    }

    public double getTotalExpense() {
        double sum = 0;
        for (Transaction t : transactions) if (t.isExpense()) sum += t.getAmount();
        return sum;
    }

    public double getBalance() {
        return getTotalIncome() - getTotalExpense();
    }

    public List<String> getCategoryNames() {
        List<String> names = new ArrayList<>();
        for (Category c : categories) names.add(c.getName());
        return names;
    }

    public Category getCategoryByName(String name) {
        for (Category c : categories) {
            if (c.getName().equalsIgnoreCase(name)) return c;
        }
        return null;
    }
}
