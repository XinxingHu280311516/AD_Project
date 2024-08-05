package com.example.adproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ExpenseAdapter2 extends RecyclerView.Adapter<ExpenseAdapter2.ExpenseViewHolder> {


        private List<Transaction> transactions;
        private OnItemClickListener listener;

        public interface OnItemClickListener {
            void onEditClick(int position);
            void onDeleteClick(int position);
        }

        public ExpenseAdapter2(List<Transaction> transactions, OnItemClickListener listener) {
            this.transactions = transactions;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_item, parent, false);
            return new ExpenseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
            Transaction transaction = transactions.get(position);
            holder.tvDate.setText(transaction.getCreated_at().toString());
            holder.tvCategory.setText(transaction.getCategoryName());
            holder.tvStore.setText(transaction.getDescription());
            holder.tvAmount.setText(String.format("$%.2f", transaction.getAmount()));
        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }

        public class ExpenseViewHolder extends RecyclerView.ViewHolder {
            public TextView tvDate, tvCategory, tvStore, tvAmount;
            public Button btnEdit, btnDelete;

            public ExpenseViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvCategory = itemView.findViewById(R.id.tv_category);
                tvAmount = itemView.findViewById(R.id.tv_amount);
                btnEdit = itemView.findViewById(R.id.btn_edit);
                btnDelete = itemView.findViewById(R.id.btn_delete);

                btnEdit.setOnClickListener(v -> {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onEditClick(position);
                        }
                    }
                });

                btnDelete.setOnClickListener(v -> {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                });
            }
        }
    }
