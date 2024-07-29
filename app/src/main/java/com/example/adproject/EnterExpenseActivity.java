package com.example.adproject;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EnterExpenseActivity extends AppCompatActivity {

    private EditText amountEditText;
    private Spinner categorySpinner;
    private Spinner dateSpinner;
    private EditText notesEditText;
    private Button saveExpenseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_expense);

        amountEditText = findViewById(R.id.amount_edit_text);
        categorySpinner = findViewById(R.id.category_spinner);
        dateSpinner = findViewById(R.id.date_spinner);
        notesEditText = findViewById(R.id.notes_edit_text);
        saveExpenseButton = findViewById(R.id.save_expense_button);

        // 初始化日期下拉框
        initializeDateSpinner();

        saveExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExpense();
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
        String category = categorySpinner.getSelectedItem().toString();
        String date = dateSpinner.getSelectedItem().toString();
        String notes = notesEditText.getText().toString().trim();

        if (amount.isEmpty() || category.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
        } else {
            // 保存费用记录逻辑（例如保存到数据库或显示Toast消息）
            Toast.makeText(this, "Expense saved: " + amount + ", " + category + ", " + date + ", " + notes, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(EnterExpenseActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }
}
