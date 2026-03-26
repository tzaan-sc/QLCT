package com.example.qlct;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "qlct.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_TRANSACTIONS = "transactions";

    // Common columns
    private static final String KEY_ID = "id";

    // CATEGORIES Table - column names
    private static final String KEY_CAT_NAME = "name";
    private static final String KEY_CAT_ICON_RES = "iconRes";
    private static final String KEY_CAT_COLOR = "color";

    // TRANSACTIONS Table - column names
    private static final String KEY_TRANS_AMOUNT = "amount";
    private static final String KEY_TRANS_TYPE = "type";
    private static final String KEY_TRANS_CATEGORY = "category";
    private static final String KEY_TRANS_NOTE = "note";
    private static final String KEY_TRANS_DATE = "date";
    private static final String KEY_TRANS_CAT_ICON = "categoryIconRes";

    // Table Create Statements
    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES
            + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_CAT_NAME + " TEXT,"
            + KEY_CAT_ICON_RES + " INTEGER,"
            + KEY_CAT_COLOR + " INTEGER"
            + ")";

    private static final String CREATE_TABLE_TRANSACTIONS = "CREATE TABLE " + TABLE_TRANSACTIONS
            + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_TRANS_AMOUNT + " REAL,"
            + KEY_TRANS_TYPE + " TEXT,"
            + KEY_TRANS_CATEGORY + " TEXT,"
            + KEY_TRANS_NOTE + " TEXT,"
            + KEY_TRANS_DATE + " INTEGER,"
            + KEY_TRANS_CAT_ICON + " INTEGER"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_TRANSACTIONS);

        seedCategories(db);
        seedTransactions(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        onCreate(db);
    }

    // ─── Categories ──────────────────────────────────────────────────────────

    private void seedCategories(SQLiteDatabase db) {
        insertCategoryInternal(db, "Ăn & Uống", R.drawable.ic_cat_food, Color.parseColor("#FF6F00"));
        insertCategoryInternal(db, "Di Chuyển", R.drawable.ic_cat_transport, Color.parseColor("#1565C0"));
        insertCategoryInternal(db, "Mua Sắm", R.drawable.ic_cat_shopping, Color.parseColor("#6A1B9A"));
        insertCategoryInternal(db, "Sức Khỏe", R.drawable.ic_cat_health, Color.parseColor("#D32F2F"));
        insertCategoryInternal(db, "Giải Trí", R.drawable.ic_cat_entertainment, Color.parseColor("#00838F"));
        insertCategoryInternal(db, "Lương", R.drawable.ic_cat_salary, Color.parseColor("#2E7D32"));
        insertCategoryInternal(db, "Khác", R.drawable.ic_cat_other, Color.parseColor("#546E7A"));
    }

    private void insertCategoryInternal(SQLiteDatabase db, String name, int iconRes, int color) {
        ContentValues values = new ContentValues();
        values.put(KEY_CAT_NAME, name);
        values.put(KEY_CAT_ICON_RES, iconRes);
        values.put(KEY_CAT_COLOR, color);
        db.insert(TABLE_CATEGORIES, null, values);
    }

    public void addCategory(String name, int iconRes, int color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CAT_NAME, name);
        values.put(KEY_CAT_ICON_RES, iconRes);
        values.put(KEY_CAT_COLOR, color);
        db.insert(TABLE_CATEGORIES, null, values);
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CATEGORIES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Category cat = new Category(
                        c.getLong(c.getColumnIndexOrThrow(KEY_ID)),
                        c.getString(c.getColumnIndexOrThrow(KEY_CAT_NAME)),
                        c.getInt(c.getColumnIndexOrThrow(KEY_CAT_ICON_RES)),
                        c.getInt(c.getColumnIndexOrThrow(KEY_CAT_COLOR))
                );
                categories.add(cat);
            } while (c.moveToNext());
        }
        c.close();
        return categories;
    }

    public void updateCategory(long id, String name, int iconRes, int color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CAT_NAME, name);
        values.put(KEY_CAT_ICON_RES, iconRes);
        values.put(KEY_CAT_COLOR, color);
        db.update(TABLE_CATEGORIES, values, KEY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deleteCategory(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORIES, KEY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public Category getCategoryByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_CATEGORIES, null, KEY_CAT_NAME + " = ? COLLATE NOCASE", new String[]{name}, null, null, null);
        Category cat = null;
        if (c != null && c.moveToFirst()) {
            cat = new Category(
                    c.getLong(c.getColumnIndexOrThrow(KEY_ID)),
                    c.getString(c.getColumnIndexOrThrow(KEY_CAT_NAME)),
                    c.getInt(c.getColumnIndexOrThrow(KEY_CAT_ICON_RES)),
                    c.getInt(c.getColumnIndexOrThrow(KEY_CAT_COLOR))
            );
        }
        if (c != null) {
            c.close();
        }
        return cat;
    }

    // ─── Transactions ────────────────────────────────────────────────────────

    private Date daysAgo(Calendar cal, int days) {
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, -days);
        return cal.getTime();
    }

    private void seedTransactions(SQLiteDatabase db) {
        Calendar cal = Calendar.getInstance();

        insertTransactionInternal(db, 3_500_000, Transaction.TYPE_INCOME, "Lương", "Lương tháng", daysAgo(cal, 1).getTime(), R.drawable.ic_cat_salary);
        insertTransactionInternal(db, 85_000, Transaction.TYPE_EXPENSE, "Ăn & Uống", "Ăn trưa cùng đồng nghiệp", daysAgo(cal, 1).getTime(), R.drawable.ic_cat_food);
        insertTransactionInternal(db, 250_000, Transaction.TYPE_EXPENSE, "Di Chuyển", "Tiền taxi", daysAgo(cal, 2).getTime(), R.drawable.ic_cat_transport);
        insertTransactionInternal(db, 1_200_000, Transaction.TYPE_EXPENSE, "Mua Sắm", "Quần áo mới", daysAgo(cal, 3).getTime(), R.drawable.ic_cat_shopping);
        insertTransactionInternal(db, 500_000, Transaction.TYPE_INCOME, "Khác", "Thanh toán freelance", daysAgo(cal, 4).getTime(), R.drawable.ic_cat_other);
        insertTransactionInternal(db, 150_000, Transaction.TYPE_EXPENSE, "Sức Khỏe", "Nhà thuốc", daysAgo(cal, 5).getTime(), R.drawable.ic_cat_health);
        insertTransactionInternal(db, 200_000, Transaction.TYPE_EXPENSE, "Giải Trí", "Xem phim & ăn vặt", daysAgo(cal, 6).getTime(), R.drawable.ic_cat_entertainment);
        insertTransactionInternal(db, 60_000, Transaction.TYPE_EXPENSE, "Ăn & Uống", "Cà phê", daysAgo(cal, 7).getTime(), R.drawable.ic_cat_food);
    }

    private void insertTransactionInternal(SQLiteDatabase db, double amount, String type, String category, String note, long dateMs, int iconRes) {
        ContentValues values = new ContentValues();
        values.put(KEY_TRANS_AMOUNT, amount);
        values.put(KEY_TRANS_TYPE, type);
        values.put(KEY_TRANS_CATEGORY, category);
        values.put(KEY_TRANS_NOTE, note);
        values.put(KEY_TRANS_DATE, dateMs);
        values.put(KEY_TRANS_CAT_ICON, iconRes);
        db.insert(TABLE_TRANSACTIONS, null, values);
    }

    public void addTransaction(double amount, String type, String category, String note, Date date, int iconRes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TRANS_AMOUNT, amount);
        values.put(KEY_TRANS_TYPE, type);
        values.put(KEY_TRANS_CATEGORY, category);
        values.put(KEY_TRANS_NOTE, note);
        values.put(KEY_TRANS_DATE, date.getTime());
        values.put(KEY_TRANS_CAT_ICON, iconRes);
        db.insert(TABLE_TRANSACTIONS, null, values);
    }

    public void deleteTransaction(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTIONS, KEY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public List<Transaction> getAllTransactions() {
        return getTransactions(null, null, KEY_TRANS_DATE + " DESC, " + KEY_ID + " DESC");
    }

    public List<Transaction> getRecentTransactions(int count) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Transaction> transactions = new ArrayList<>();
        Cursor c = db.query(TABLE_TRANSACTIONS, null, null, null, null, null, KEY_TRANS_DATE + " DESC, " + KEY_ID + " DESC", String.valueOf(count));
        
        if (c.moveToFirst()) {
            do {
                transactions.add(cursorToTransaction(c));
            } while (c.moveToNext());
        }
        c.close();
        return transactions;
    }

    private List<Transaction> getTransactions(String selection, String[] selectionArgs, String orderBy) {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_TRANSACTIONS, null, selection, selectionArgs, null, null, orderBy);

        if (c.moveToFirst()) {
            do {
                transactions.add(cursorToTransaction(c));
            } while (c.moveToNext());
        }
        c.close();
        return transactions;
    }

    private Transaction cursorToTransaction(Cursor c) {
        return new Transaction(
                c.getLong(c.getColumnIndexOrThrow(KEY_ID)),
                c.getDouble(c.getColumnIndexOrThrow(KEY_TRANS_AMOUNT)),
                c.getString(c.getColumnIndexOrThrow(KEY_TRANS_TYPE)),
                c.getString(c.getColumnIndexOrThrow(KEY_TRANS_CATEGORY)),
                c.getString(c.getColumnIndexOrThrow(KEY_TRANS_NOTE)),
                new Date(c.getLong(c.getColumnIndexOrThrow(KEY_TRANS_DATE))),
                c.getInt(c.getColumnIndexOrThrow(KEY_TRANS_CAT_ICON))
        );
    }
}
