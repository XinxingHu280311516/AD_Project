package com.example.adproject;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class BudgetPlanActivity extends AppCompatActivity {

    private LinearLayout categoriesList;
    private TextView totalAmountTextView;
    private TextView remainingBalanceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_plan);

        categoriesList = findViewById(R.id.categories_list);
        totalAmountTextView = findViewById(R.id.total_amount);
        remainingBalanceTextView = findViewById(R.id.remaining_balance);

        Button addNewBudgetPlanButton = findViewById(R.id.add_new_budget_plan_button);
        addNewBudgetPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BudgetPlanActivity.this, ExpenseCategoryManagementActivity.class);
                startActivity(intent);
            }
        });

    }

    }

