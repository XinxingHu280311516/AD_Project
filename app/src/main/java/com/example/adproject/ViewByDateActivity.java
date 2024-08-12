package com.example.adproject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ViewByDateActivity extends AppCompatActivity implements ExpenseAdapter2.OnItemClickListener {

    private RecyclerView recentExpensesRecyclerView;
    private ExpenseAdapter2 adapter;
    private List<Transaction> transactions;
    private ApiService apiService;
    private DatePicker datePicker;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_by_date);

        recentExpensesRecyclerView = findViewById(R.id.recent_expenses_recyclerview);
        recentExpensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        transactions = new ArrayList<>();
        adapter = new ExpenseAdapter2(transactions, this);
        recentExpensesRecyclerView.setAdapter(adapter);

        apiService = ApiClient.getApiService();

        datePicker = findViewById(R.id.date_picker);

        ImageButton backButton = findViewById(R.id.back_button5);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewByDateActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences pref = getSharedPreferences("userId", MODE_PRIVATE);
        userId = pref.getInt("UserId", -1);

        Button btnSearch = findViewById(R.id.btn_search);

        btnSearch.setOnClickListener(v -> {
            int year = datePicker.getYear();
            int month = datePicker.getMonth();
            int day = datePicker.getDayOfMonth();
            LocalDate selectedDate = LocalDate.of(year, month+1, day);
            System.out.println(selectedDate);
            filterExpensesByDate(selectedDate);
        });
    }


    private void filterExpensesByDate(LocalDate date) {
    // 根据选定的日期过滤消费记录并更新 RecyclerView
        System.out.println(userId);
        apiService.getTransactionByUserId(userId).enqueue(new Callback<List<Transaction>>() {
        @Override
        public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
            if (response.isSuccessful()) {
                transactions.clear();
                for (Transaction transaction : response.body()) {
                    if (transaction.getCreated_at().equals(date)) {
                        transactions.add(transaction);
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(ViewByDateActivity.this, "获取交易记录失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<List<Transaction>> call, Throwable t) {
            Toast.makeText(ViewByDateActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
        }
    });
}

    @Override
    public void onEditClick(int position) {
        // 获取要编辑的交易记录
        Transaction transaction = transactions.get(position);

        // 创建并显示编辑对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_transaction, null);
        builder.setView(dialogView);

        EditText editAmount = dialogView.findViewById(R.id.edit_amount);
        EditText editDescription = dialogView.findViewById(R.id.edit_description);

        // 预填充当前交易记录的详细信息
        editAmount.setText(String.valueOf(transaction.getAmount()));
        editDescription.setText(transaction.getDescription());

        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // 覆盖保存按钮点击事件
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String newAmount = editAmount.getText().toString().trim();
            String newDescription = editDescription.getText().toString().trim();

            if (newAmount.isEmpty() || newDescription.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // 更新交易记录
                transaction.setAmount(Double.parseDouble(newAmount));
                transaction.setDescription(newDescription);

                // 调用 API 更新交易记录
                apiService.updateTransaction(transaction,transaction.getId()).enqueue(new Callback<Transaction>() {
                    @Override
                    public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                        if (response.isSuccessful()) {
                            transactions.set(position, response.body());
                            adapter.notifyItemChanged(position);
                            dialog.dismiss();
                            Toast.makeText(ViewByDateActivity.this, "Transaction updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ViewByDateActivity.this, "Failed to update transaction", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Transaction> call, Throwable t) {
                        Toast.makeText(ViewByDateActivity.this, "Network request failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
@Override
public void onDeleteClick(int position) {
    // 处理删除按钮点击事件
    int transactionId = transactions.get(position).getId();
    System.out.println(transactionId);
    apiService.deleteTransaction(transactionId).enqueue(new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (response.isSuccessful()) {
                transactions.remove(position);
                adapter.notifyItemRemoved(position);
            } else {
                Toast.makeText(ViewByDateActivity.this, "删除交易记录失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            Toast.makeText(ViewByDateActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
        }
    });
}
}
