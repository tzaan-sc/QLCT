package com.example.qlct;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.qlct.databinding.ActivityHomeBinding;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private DataManager         dataManager;
    private TransactionAdapter  adapter;
    private NumberFormat        nf;

    private static final int REQUEST_ADD = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding     = ActivityHomeBinding.inflate(getLayoutInflater());
        dataManager = DataManager.getInstance();
        nf          = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

        setContentView(binding.getRoot());
        setupRecyclerView();
        setupClickListeners();
        refreshUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    // ─── Setup ────────────────────────────────────────────────────────────

    private void setupRecyclerView() {
        List<Transaction> recent = dataManager.getRecentTransactions(20);
        adapter = new TransactionAdapter(recent);
        binding.recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewTransactions.setAdapter(adapter);
        binding.recyclerViewTransactions.setNestedScrollingEnabled(false);

        adapter.setOnItemClickListener(new TransactionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Transaction transaction) {
                // Could show detail dialog – kept simple
            }

            @Override
            public void onItemLongClick(Transaction transaction) {
                // Long-press to delete (simple implementation)
                new androidx.appcompat.app.AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Delete transaction?")
                        .setMessage("Remove \"" + transaction.getCategory() + "\" from " +
                                transaction.getNote() + "?")
                        .setPositiveButton("Delete", (d, w) -> {
                            dataManager.deleteTransaction(transaction.getId());
                            refreshUI();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }

    private void setupClickListeners() {
        binding.fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTransactionActivity.class);
            startActivityForResult(intent, REQUEST_ADD);
        });

        binding.tvSeeAll.setOnClickListener(v ->
                startActivity(new Intent(this, TransactionHistoryActivity.class)));

        binding.btnHistory.setOnClickListener(v ->
                startActivity(new Intent(this, TransactionHistoryActivity.class)));

        binding.btnCategories.setOnClickListener(v ->
                startActivity(new Intent(this, CategoryActivity.class)));
    }

    // ─── UI Refresh ───────────────────────────────────────────────────────

    private void refreshUI() {
        double balance = dataManager.getBalance();
        double income  = dataManager.getTotalIncome();
        double expense = dataManager.getTotalExpense();

        binding.tvBalance.setText("₫" + nf.format(balance));
        binding.tvTotalIncome.setText("₫" + nf.format(income));
        binding.tvTotalExpense.setText("₫" + nf.format(expense));

        List<Transaction> recent = dataManager.getRecentTransactions(20);

        // Update adapter list
        adapter = new TransactionAdapter(recent);
        binding.recyclerViewTransactions.setAdapter(adapter);

        // Reattach listeners
        adapter.setOnItemClickListener(new TransactionAdapter.OnItemClickListener() {
            @Override public void onItemClick(Transaction transaction) {}
            @Override
            public void onItemLongClick(Transaction transaction) {
                new androidx.appcompat.app.AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Delete transaction?")
                        .setPositiveButton("Delete", (d, w) -> {
                            dataManager.deleteTransaction(transaction.getId());
                            refreshUI();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        if (recent.isEmpty()) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
            binding.recyclerViewTransactions.setVisibility(View.GONE);
        } else {
            binding.tvEmpty.setVisibility(View.GONE);
            binding.recyclerViewTransactions.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD && resultCode == RESULT_OK) {
            refreshUI();
        }
    }
}
