package com.example.qlct;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.qlct.databinding.ActivityTransactionHistoryBinding;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryActivity extends AppCompatActivity {

    private ActivityTransactionHistoryBinding binding;
    private DataManager       dataManager;
    private TransactionAdapter adapter;

    // filter state
    private String currentType     = "ALL";       // ALL | INCOME | EXPENSE
    private String currentCategory = "Tất Cả";   // tên danh mục hoặc "Tất Cả"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding     = ActivityTransactionHistoryBinding.inflate(getLayoutInflater());
        dataManager = DataManager.getInstance();
        setContentView(binding.getRoot());

        setupRecyclerView();
        setupChipFilter();
        setupCategorySpinner();
        binding.btnBack.setOnClickListener(v -> finish());

        applyFilters();
    }

    // ─── RecyclerView ────────────────────────────────────────────────────

    private void setupRecyclerView() {
        binding.recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(new ArrayList<>());
        binding.recyclerViewHistory.setAdapter(adapter);

        adapter.setOnItemClickListener(new TransactionAdapter.OnItemClickListener() {
            @Override public void onItemClick(Transaction t) {}
            @Override
            public void onItemLongClick(Transaction t) {
                new androidx.appcompat.app.AlertDialog.Builder(TransactionHistoryActivity.this)
                        .setTitle("Xóa giao dịch?")
                        .setPositiveButton("Xóa", (d, w) -> {
                            dataManager.deleteTransaction(t.getId());
                            applyFilters();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
    }

    // ─── Chip filter ─────────────────────────────────────────────────────

    private void setupChipFilter() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipAll)     currentType = "ALL";
            else if (id == R.id.chipIncome)  currentType = Transaction.TYPE_INCOME;
            else if (id == R.id.chipExpense) currentType = Transaction.TYPE_EXPENSE;
            applyFilters();
        });
    }

    // ─── Category Spinner ────────────────────────────────────────────────

    private void setupCategorySpinner() {
        List<String> names = new ArrayList<>();
        names.add("Tất Cả");
        names.addAll(dataManager.getCategoryNames());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategoryFilter.setAdapter(adapter);

        binding.spinnerCategoryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                currentCategory = (String) parent.getItemAtPosition(pos);
                applyFilters();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // ─── Filter logic ────────────────────────────────────────────────────

    private void applyFilters() {
        List<Transaction> all = dataManager.getAllTransactions();
        List<Transaction> filtered = new ArrayList<>();

        for (Transaction t : all) {
            boolean typeMatch = currentType.equals("ALL") || t.getType().equals(currentType);
            boolean catMatch  = currentCategory.equals("Tất Cả")
                    || t.getCategory().equalsIgnoreCase(currentCategory);
            if (typeMatch && catMatch) filtered.add(t);
        }

        adapter = new TransactionAdapter(filtered);
        binding.recyclerViewHistory.setAdapter(adapter);

        // Reattach delete listener
        adapter.setOnItemClickListener(new TransactionAdapter.OnItemClickListener() {
            @Override public void onItemClick(Transaction t) {}
            @Override
            public void onItemLongClick(Transaction t) {
                new androidx.appcompat.app.AlertDialog.Builder(TransactionHistoryActivity.this)
                        .setTitle("Xóa giao dịch?")
                        .setPositiveButton("Xóa", (d, w) -> {
                            dataManager.deleteTransaction(t.getId());
                            applyFilters();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });

        if (filtered.isEmpty()) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
            binding.recyclerViewHistory.setVisibility(View.GONE);
        } else {
            binding.tvEmpty.setVisibility(View.GONE);
            binding.recyclerViewHistory.setVisibility(View.VISIBLE);
        }
    }
}
