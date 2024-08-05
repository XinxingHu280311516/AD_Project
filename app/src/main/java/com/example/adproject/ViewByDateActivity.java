package com.example.adproject;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
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

        DatePicker datePicker = findViewById(R.id.date_picker);
        datePicker.init(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH), (view, year, monthOfYear, dayOfMonth) -> {
                    LocalDate selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                    filterExpensesByDate(selectedDate);
                });
    }

private void filterExpensesByDate(LocalDate date) {
    // 根据选定的日期过滤消费记录并更新 RecyclerView
    apiService.getTransactionByUserId(1).enqueue(new Callback<List<Transaction>>() {
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
    // 处理编辑按钮点击事件
    Transaction transaction = transactions.get(position);
    // 实现修改逻辑，例如显示一个对话框以修改交易记录
}

@Override
public void onDeleteClick(int position) {
    // 处理删除按钮点击事件
    int transactionId = transactions.get(position).getId();
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
