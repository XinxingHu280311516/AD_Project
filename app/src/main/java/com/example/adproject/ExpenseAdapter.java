package com.example.adproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenseList;

    public ExpenseAdapter(List<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.dateTextView.setText(expense.getDate());
        holder.categoryTextView.setText(expense.getCategory());
        holder.subCategoryTextView.setText(expense.getSubCategory());
        holder.amountTextView.setText("$" + String.format("%.2f", expense.getAmount()));
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        TextView dateTextView;
        TextView categoryTextView;
        TextView subCategoryTextView;
        TextView amountTextView;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            categoryTextView = itemView.findViewById(R.id.category_text_view);
            subCategoryTextView = itemView.findViewById(R.id.sub_category_text_view);
            amountTextView = itemView.findViewById(R.id.amount_text_view);
        }
    }
}