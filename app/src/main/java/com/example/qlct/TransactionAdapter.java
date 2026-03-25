package com.example.qlct;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlct.databinding.ItemTransactionBinding;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
        void onItemLongClick(Transaction transaction);
    }

    private final List<Transaction>  items;
    private final NumberFormat       nf;
    private final SimpleDateFormat   sdf;
    private       OnItemClickListener listener;

    public TransactionAdapter(List<Transaction> items) {
        this.items = items;
        this.nf    = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        this.sdf   = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    }

    public void setOnItemClickListener(OnItemClickListener l) { this.listener = l; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransactionBinding binding =
                ItemTransactionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() { return items.size(); }

    // ─── Utils ────────────────────────────────────────────────────────────

    private String formatAmount(Transaction t) {
        String prefix = t.isIncome() ? "+ ₫" : "- ₫";
        return prefix + nf.format(t.getAmount());
    }

    // ─── ViewHolder ───────────────────────────────────────────────────────

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemTransactionBinding b;

        ViewHolder(ItemTransactionBinding binding) {
            super(binding.getRoot());
            b = binding;

            b.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(items.get(getAdapterPosition()));
            });
            b.getRoot().setOnLongClickListener(v -> {
                if (listener != null) listener.onItemLongClick(items.get(getAdapterPosition()));
                return true;
            });
        }

        void bind(Transaction t) {
            b.tvCategory.setText(t.getCategory());
            b.tvNote.setText(t.getNote() != null && !t.getNote().isEmpty()
                    ? t.getNote() : "—");
            b.tvDate.setText(sdf.format(t.getDate()));
            b.tvAmount.setText(formatAmount(t));

            int amountColor = t.isIncome()
                    ? Color.parseColor("#00C853")
                    : Color.parseColor("#FF1744");
            b.tvAmount.setTextColor(amountColor);

            // Category icon
            b.ivCategoryIcon.setImageResource(t.getCategoryIconRes());

            // Circle bg tint based on type
            int bgColor = t.isIncome()
                    ? Color.parseColor("#1A00C853")
                    : Color.parseColor("#1AFF1744");
            b.ivCategoryIcon.setBackgroundTintList(ColorStateList.valueOf(bgColor));
        }
    }
}
