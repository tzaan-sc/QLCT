package com.example.qlct;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.qlct.databinding.ActivityCategoryBinding;
import com.example.qlct.databinding.DialogAddCategoryBinding;

import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private ActivityCategoryBinding binding;
    private DataManager             dataManager;
    private CategoryAdapter         adapter;

    // Available icons (name + resource pairs)
    private static final String[] ICON_NAMES = {
            "Food & Drinks", "Transport", "Shopping",
            "Health", "Entertainment", "Salary", "Other"
    };
    private static final int[] ICON_RES = {
            R.drawable.ic_cat_food,
            R.drawable.ic_cat_transport,
            R.drawable.ic_cat_shopping,
            R.drawable.ic_cat_health,
            R.drawable.ic_cat_entertainment,
            R.drawable.ic_cat_salary,
            R.drawable.ic_cat_other
    };
    private static final int[] ICON_COLORS = {
            Color.parseColor("#FF6F00"),
            Color.parseColor("#1565C0"),
            Color.parseColor("#6A1B9A"),
            Color.parseColor("#D32F2F"),
            Color.parseColor("#00838F"),
            Color.parseColor("#2E7D32"),
            Color.parseColor("#546E7A")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding     = ActivityCategoryBinding.inflate(getLayoutInflater());
        dataManager = DataManager.getInstance();
        setContentView(binding.getRoot());

        setupRecyclerView();
        setupClickListeners();
        refreshList();
    }

    // ─── Setup ────────────────────────────────────────────────────────────

    private void setupRecyclerView() {
        binding.recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnAddCategory.setOnClickListener(v -> showAddDialog(null));
        binding.fabAddCategory.setOnClickListener(v -> showAddDialog(null));
    }

    // ─── Refresh ──────────────────────────────────────────────────────────

    private void refreshList() {
        List<Category> categories = dataManager.getCategories();
        adapter = new CategoryAdapter(categories);
        binding.recyclerViewCategories.setAdapter(adapter);

        adapter.setOnCategoryActionListener(new CategoryAdapter.OnCategoryActionListener() {
            @Override
            public void onEditClick(Category category) {
                showAddDialog(category);
            }

            @Override
            public void onDeleteClick(Category category) {
                new AlertDialog.Builder(CategoryActivity.this)
                        .setTitle("Delete Category")
                        .setMessage("Delete \"" + category.getName() + "\"?")
                        .setPositiveButton("Delete", (d, w) -> {
                            dataManager.deleteCategory(category.getId());
                            refreshList();
                            Toast.makeText(CategoryActivity.this,
                                    "Category deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        if (categories.isEmpty()) {
            binding.tvEmpty.setVisibility(View.VISIBLE);
            binding.recyclerViewCategories.setVisibility(View.GONE);
        } else {
            binding.tvEmpty.setVisibility(View.GONE);
            binding.recyclerViewCategories.setVisibility(View.VISIBLE);
        }
    }

    // ─── Add / Edit Dialog ────────────────────────────────────────────────

    private void showAddDialog(Category editTarget) {
        DialogAddCategoryBinding dialogBinding =
                DialogAddCategoryBinding.inflate(LayoutInflater.from(this));

        // Populate icon spinner
        ArrayAdapter<String> iconAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, ICON_NAMES);
        iconAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogBinding.spinnerIcon.setAdapter(iconAdapter);

        // Pre-fill if editing
        if (editTarget != null) {
            dialogBinding.etCategoryName.setText(editTarget.getName());
            // Try to select matching icon
            for (int i = 0; i < ICON_RES.length; i++) {
                if (ICON_RES[i] == editTarget.getIconRes()) {
                    dialogBinding.spinnerIcon.setSelection(i);
                    break;
                }
            }
        }

        String title = editTarget == null ? "Add Category" : "Edit Category";

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(dialogBinding.getRoot())
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = dialogBinding.etCategoryName.getText() != null
                            ? dialogBinding.etCategoryName.getText().toString().trim()
                            : "";

                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int selectedIdx = dialogBinding.spinnerIcon.getSelectedItemPosition();
                    int iconRes     = ICON_RES[selectedIdx];
                    int color       = ICON_COLORS[selectedIdx];

                    if (editTarget == null) {
                        dataManager.addCategory(name, iconRes, color);
                        Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show();
                    } else {
                        dataManager.updateCategory(editTarget.getId(), name, iconRes, color);
                        Toast.makeText(this, "Category updated", Toast.LENGTH_SHORT).show();
                    }
                    refreshList();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
