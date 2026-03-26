package com.example.qlct;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qlct.databinding.ActivityAddTransactionBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private ActivityAddTransactionBinding binding;
    private DataManager dataManager;
    private Calendar    selectedDate;
    private SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding     = ActivityAddTransactionBinding.inflate(getLayoutInflater());
        dataManager = DataManager.getInstance(this);
        sdf         = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        selectedDate = Calendar.getInstance();

        setContentView(binding.getRoot());

        setupCategorySpinner();
        setupDateField();
        setupClickListeners();
        updateDateDisplay();
    }

    // ─── Setup ────────────────────────────────────────────────────────────

    private void setupCategorySpinner() {
        List<String> catNames = dataManager.getCategoryNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, catNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(adapter);
    }

    private void setupDateField() {
        View.OnClickListener showPicker = v -> showDatePicker();
        binding.etDate.setOnClickListener(showPicker);
        binding.tilDate.setEndIconOnClickListener(showPicker);
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSave.setOnClickListener(v -> {
            if (validateAndSave()) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    // ─── Date Picker ──────────────────────────────────────────────────────

    private void showDatePicker() {
        int y = selectedDate.get(Calendar.YEAR);
        int m = selectedDate.get(Calendar.MONTH);
        int d = selectedDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, day) -> {
            selectedDate.set(year, month, day);
            updateDateDisplay();
        }, y, m, d);
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void updateDateDisplay() {
        binding.etDate.setText(sdf.format(selectedDate.getTime()));
    }

    // ─── Validation & Save ────────────────────────────────────────────────

    private boolean validateAndSave() {
        // Amount
        String amountStr = "";
        if (binding.etAmount.getText() != null) {
            amountStr = binding.etAmount.getText().toString().trim();
        }
        if (TextUtils.isEmpty(amountStr)) {
            binding.tilAmount.setError("Vui lòng nhập số tiền");
            return false;
        }
        binding.tilAmount.setError(null);

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            binding.tilAmount.setError("Số tiền không hợp lệ");
            return false;
        }
        if (amount <= 0) {
            binding.tilAmount.setError("Số tiền phải lớn hơn 0");
            return false;
        }

        // Type
        boolean isIncome = binding.radioIncome.isChecked();
        String type = isIncome ? Transaction.TYPE_INCOME : Transaction.TYPE_EXPENSE;

        // Category
        String category = (String) binding.spinnerCategory.getSelectedItem();
        if (category == null) {
            Toast.makeText(this, "Vui lòng chọn danh mục", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Note (optional)
        String note = "";
        if (binding.etNote.getText() != null) {
            note = binding.etNote.getText().toString().trim();
        }

        // Date
        Date date = selectedDate.getTime();

        // Icon
        Category cat = dataManager.getCategoryByName(category);
        int iconRes = cat != null ? cat.getIconRes() : R.drawable.ic_cat_other;

        dataManager.addNewTransaction(amount, type, category, note, date, iconRes);
        Toast.makeText(this, "Đã lưu giao dịch!", Toast.LENGTH_SHORT).show();
        return true;
    }
}
