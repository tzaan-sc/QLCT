package com.example.qlct;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlct.databinding.ItemCategoryBinding;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    public interface OnCategoryActionListener {
        void onEditClick(Category category);
        void onDeleteClick(Category category);
    }

    private final List<Category>          items;
    private       OnCategoryActionListener listener;

    public CategoryAdapter(List<Category> items) {
        this.items = items;
    }

    public void setOnCategoryActionListener(OnCategoryActionListener l) {
        this.listener = l;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding =
                ItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() { return items.size(); }

    // ─── ViewHolder ───────────────────────────────────────────────────────

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryBinding b;

        ViewHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            b = binding;
        }

        void bind(Category c) {
            b.tvCategoryName.setText(c.getName());
            b.ivCategoryIcon.setImageResource(c.getIconRes());
            b.ivCategoryIcon.setBackgroundTintList(
                    ColorStateList.valueOf(c.getColor() & 0x33FFFFFF | 0x1A000000));

            b.btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEditClick(c);
            });
            b.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteClick(c);
            });
        }
    }
}
