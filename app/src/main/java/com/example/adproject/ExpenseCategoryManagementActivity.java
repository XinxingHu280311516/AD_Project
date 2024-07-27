package com.example.adproject;



import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ExpenseCategoryManagementActivity extends AppCompatActivity {

    private LinearLayout categoriesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_category_management);

        EditText categoryNameEditText = findViewById(R.id.category_name_edit_text);
        EditText amountEditText = findViewById(R.id.amount_edit_text);
        Button addCategoryButton = findViewById(R.id.add_category_button);
        Button saveButton = findViewById(R.id.save_button);
        categoriesList = findViewById(R.id.categories_list);

        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName = categoryNameEditText.getText().toString().trim();
                String amount = amountEditText.getText().toString().trim();

                if (categoryName.isEmpty() || amount.isEmpty()) {
                    Toast.makeText(ExpenseCategoryManagementActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                } else {
                    addCategory(categoryName, amount);
                    categoryNameEditText.setText("");
                    amountEditText.setText("");
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExpenseCategoryManagementActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addCategory(String categoryName, String amount) {
        LinearLayout newCategoryItem = new LinearLayout(this);
        newCategoryItem.setOrientation(LinearLayout.HORIZONTAL);
        newCategoryItem.setPadding(8, 8, 8, 8);
        newCategoryItem.setBackground(getResources().getDrawable(R.drawable.category_item_background));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);
        newCategoryItem.setLayoutParams(params);

        TextView categoryNameTextView = new TextView(this);
        categoryNameTextView.setText(categoryName);
        categoryNameTextView.setTextSize(16);
        categoryNameTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView amountTextView = new TextView(this);
        amountTextView.setText("$" + amount);
        amountTextView.setTextSize(16);
        amountTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        amountTextView.setPadding(16, 0, 16, 0);

        Button editButton = new Button(this);
        editButton.setText("Edit");
        editButton.setBackgroundTintList(getResources().getColorStateList(R.color.red_color));
        editButton.setTextColor(getResources().getColor(R.color.white_color));
        editButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        editButton.setPadding(16, 0, 16, 0);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog(categoryNameTextView, amountTextView);
            }
        });

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setBackgroundTintList(getResources().getColorStateList(R.color.red_color));
        deleteButton.setTextColor(getResources().getColor(R.color.white_color));
        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        deleteButton.setPadding(16, 0, 16, 0);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoriesList.removeView(newCategoryItem);
            }
        });

        newCategoryItem.addView(categoryNameTextView);
        newCategoryItem.addView(amountTextView);
        newCategoryItem.addView(editButton);
        newCategoryItem.addView(deleteButton);

        categoriesList.addView(newCategoryItem);
    }

    private void showEditDialog(final TextView categoryNameTextView, final TextView amountTextView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Category");

        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_edit_category, null);
        builder.setView(customLayout);

        final EditText editCategoryName = customLayout.findViewById(R.id.edit_category_name);
        final EditText editAmount = customLayout.findViewById(R.id.edit_amount);

        editCategoryName.setText(categoryNameTextView.getText());
        editAmount.setText(amountTextView.getText().toString().replace("$", ""));

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newCategoryName = editCategoryName.getText().toString().trim();
                String newAmount = editAmount.getText().toString().trim();

                if (!newCategoryName.isEmpty() && !newAmount.isEmpty()) {
                    categoryNameTextView.setText(newCategoryName);
                    amountTextView.setText("$" + newAmount);
                } else {
                    Toast.makeText(ExpenseCategoryManagementActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
