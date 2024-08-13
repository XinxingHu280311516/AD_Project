package com.example.adproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnterExpenseActivity extends AppCompatActivity {

    private EditText amountEditText;
    private Spinner categorySpinner;
    private Spinner dateSpinner;
    private EditText notesEditText;
    private Button saveExpenseButton;
    private ApiService apiService;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_expense);

        amountEditText = findViewById(R.id.amount_edit_text);
        categorySpinner = findViewById(R.id.category_spinner);
        dateSpinner = findViewById(R.id.date_spinner);
        notesEditText = findViewById(R.id.notes_edit_text);
        saveExpenseButton = findViewById(R.id.save_expense_button);
        ImageButton backButton = findViewById(R.id.back_button3);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EnterExpenseActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        apiService = ApiClient.getApiService();

        // 初始化日期下拉框
        initializeDateSpinner();

        Intent intent = getIntent();
        if (intent != null) {
            String amount = intent.getStringExtra("amount");
            String category = intent.getStringExtra("category");
            String date = intent.getStringExtra("date");
            String notes = intent.getStringExtra("notes");
            SharedPreferences pref = getSharedPreferences("userId", MODE_PRIVATE);
            userId = pref.getInt("UserId", -1);


            amountEditText.setText(amount);
            notesEditText.setText(notes);

            // 动态加载用户类别
            loadUserCategories(category);

            System.out.println(date);

            // 设置dateSpinner的选择
            ArrayAdapter<String> dateAdapter = (ArrayAdapter<String>) dateSpinner.getAdapter();
            if (date != null) {
                int datePosition = dateAdapter.getPosition(date);
                dateSpinner.setSelection(datePosition);
            }
        }

        saveExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndSaveExpense();
            }
        });
    }


    private void loadUserCategories(String selectedCategoryName) {
        Call<List<Category>> call = apiService.getUserCategories(userId);
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    List<Category> categories = response.body();

                    if (categories != null) {
                        ArrayAdapter<Category> adapter = new ArrayAdapter<>(EnterExpenseActivity.this, android.R.layout.simple_spinner_item, categories);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        categorySpinner.setAdapter(adapter);

                        if (selectedCategoryName != null) {
                            int spinnerPosition = -1;
                            for (int i = 0; i < categories.size(); i++) {
                                if (categories.get(i).getName().equals(selectedCategoryName)) {
                                    spinnerPosition = i;
                                    break;
                                }
                            }

                            if (spinnerPosition >= 0) {
                                categorySpinner.setSelection(spinnerPosition);
                            }
                        }
                    }
                } else {
                    Toast.makeText(EnterExpenseActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(EnterExpenseActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void initializeDateSpinner() {
        List<String> dateList = generateDateList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dateList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(adapter);

        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 处理日期选择
                String selectedDate = parent.getItemAtPosition(position).toString();
                Toast.makeText(EnterExpenseActivity.this, "Selected date: " + selectedDate, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 未选择任何日期
            }
        });
    }

    private List<String> generateDateList() {
        List<String> dateList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();

        // 生成过去7天的日期
        for (int i = 0; i < 7; i++) {
            dateList.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }

        return dateList;
    }

    private void saveExpense() {
        String amount = amountEditText.getText().toString().trim();
        Category category = (Category)categorySpinner.getSelectedItem();
//        String date = dateSpinner.getSelectedItem().toString();
        String date = dateSpinner.getSelectedItem().toString().split("T")[0];
        //System.out.println(date);
        String notes = notesEditText.getText().toString().trim();

        if (amount.isEmpty() || category==null || date.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
        } else {
            // 保存费用记录逻辑（例如保存到数据库或显示Toast消息）
            Transaction transaction = new Transaction();
            transaction.setAmount(Double.parseDouble(amount));
            transaction.setDescription(notes);
            transaction.setCreated_at(LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            //transaction.setUpdated_at(LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            transaction.setCategory(new Category());
            transaction.setCategory(category);
            transaction.setUser(new User());
            transaction.getUser().setId(userId);

            System.out.println(transaction.getCategory().getId());

            Call<Transaction> call = apiService.addTransaction(transaction, userId);
            call.enqueue(new Callback<Transaction>() {
                @Override
                public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                    if (response.isSuccessful()) {
                        System.out.println(response.code());
                        Toast.makeText(EnterExpenseActivity.this, "Expense saved: " + amount + ", " + category + ", " + date + ", " + notes, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(EnterExpenseActivity.this, HomeActivity.class);
                        startActivity(intent);
                    } else {
                        System.out.println(response.code());
                        Toast.makeText(EnterExpenseActivity.this, "Failed to save expense", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Transaction> call, Throwable t) {
                    System.out.println(t.getMessage());
//                    Toast.makeText(EnterExpenseActivity.this, "Expense saved: " + amount + ", " + category + ", " + date + ", " + notes, Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(EnterExpenseActivity.this, HomeActivity.class);
//                    startActivity(intent);
                    Toast.makeText(EnterExpenseActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void checkAndSaveExpense() {
        String amount = amountEditText.getText().toString().trim();
        Category category = (Category) categorySpinner.getSelectedItem();
        String date = dateSpinner.getSelectedItem().toString().split("T")[0];
        String notes = notesEditText.getText().toString().trim();

        if (amount.isEmpty() || category == null || date.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double enteredAmount = Double.parseDouble(amount);

        // 获取当前类别的预算和实际支出
        Call<List<Map<String, Object>>> call = apiService.getTotalSpendingByCategoryForCurrentMonth(userId);
        call.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> categorySpendings = response.body();
                    double totalSpending = 0;
                    double budget = category.getBudget();

                    for (Map<String, Object> spending : categorySpendings) {
                        Number categoryIdNumber = (Number) spending.get("categoryId");
                        if (categoryIdNumber != null && categoryIdNumber.intValue() == category.getId()) {
                            totalSpending = ((Number) spending.get("totalSpending")).doubleValue();
                            break;
                        }
                    }

                    // 检查是否超出预算
                    if ((totalSpending + enteredAmount) > budget) {
                        Toast.makeText(EnterExpenseActivity.this, "This transaction exceeds the budget for " + category.getName(), Toast.LENGTH_LONG).show();
                        saveExpense();
                    } else {
                        saveExpense();
                    }
                } else {
                    Toast.makeText(EnterExpenseActivity.this, "Failed to check budget", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(EnterExpenseActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
