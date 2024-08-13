package com.example.adproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseCategoryManagementActivity extends AppCompatActivity {

    private LinearLayout categoriesList;
    private ApiService apiService;
    private int userId; // Assuming this is passed from previous activity or retrieved from SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_category_management);

        EditText categoryNameEditText = findViewById(R.id.category_name_edit_text);
        EditText amountEditText = findViewById(R.id.amount_edit_text);
        Button addCategoryButton = findViewById(R.id.add_category_button);
        Button saveButton = findViewById(R.id.save_button);
        categoriesList = findViewById(R.id.categories_list);
        apiService = ApiClient.getApiService();

        ImageButton backButton = findViewById(R.id.back_button4);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExpenseCategoryManagementActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        // Retrieve userId from Intent or SharedPreferences
        SharedPreferences pref = getSharedPreferences("userId", MODE_PRIVATE);
        userId = pref.getInt("UserId", -1);


        // Load existing categories
        loadCategories();

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

    private void loadCategories() {
//        Call<List<Category>> call = apiService.getUserCategories(userId);
//        call.enqueue(new Callback<List<Category>>() {
//            @Override
//            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    for (Category category : response.body()) {
//                        addCategoryToList(category);
//                    }
//                } else {
//                    Toast.makeText(ExpenseCategoryManagementActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Category>> call, Throwable t) {
//                Toast.makeText(ExpenseCategoryManagementActivity.this, "Network error", Toast.LENGTH_SHORT).show();
//            }
//        });
//
        // Load system categories
        Call<List<Category>> systemCategoriesCall = apiService.getCategoriesByType(0);
        systemCategoriesCall.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Category systemCategory : response.body()) {
                        copySystemCategoryToUser(systemCategory);
                    }
                } else {
                    Toast.makeText(ExpenseCategoryManagementActivity.this, "Failed to load system categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(ExpenseCategoryManagementActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void copySystemCategoryToUser(Category systemCategory) {
        Call<List<Category>> userCategoriesCall = apiService.getUserCategories(userId);
        userCategoriesCall.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean categoryExists = false;
                    for (Category userCategory : response.body()) {
                        if (userCategory.getName().equals(systemCategory.getName())) {
                            categoryExists = true;
                            break;
                        }
                    }
                    if (!categoryExists) {
                        Category newCategory = new Category();
                        newCategory.setName(systemCategory.getName());
                        newCategory.setBudget(systemCategory.getBudget());
                        newCategory.setUser(new User(userId));
                        newCategory.setType(1); // Set type to 1 for user category

                        Call<Category> callAddCategory = apiService.addCategory(newCategory, userId);
                        callAddCategory.enqueue(new Callback<Category>() {
                            @Override
                            public void onResponse(Call<Category> call, Response<Category> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    addCategoryToList(response.body());
                                } else {
                                    Toast.makeText(ExpenseCategoryManagementActivity.this, "Failed to add system category to user categories", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Category> call, Throwable t) {
                                Toast.makeText(ExpenseCategoryManagementActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(ExpenseCategoryManagementActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCategory(String categoryName, String amount) {
        Category category = new Category();
        category.setName(categoryName);
        category.setBudget(Double.parseDouble(amount));
        category.setUser(new User(userId)); // Assuming you have a User constructor that accepts an ID

        Call<Category> call = apiService.addCategory(category, userId);
        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful() && response.body() != null) {
                    addCategoryToList(response.body());
                } else {
                    Toast.makeText(ExpenseCategoryManagementActivity.this, "Failed to add category", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                Toast.makeText(ExpenseCategoryManagementActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCategoryToList(Category category) {
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
        categoryNameTextView.setText(category.getName());
        categoryNameTextView.setTextSize(16);
        categoryNameTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView amountTextView = new TextView(this);
        amountTextView.setText("$" + category.getBudget());
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
                showEditDialog(category, categoryNameTextView, amountTextView);
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
                deleteCategory(category, newCategoryItem);
            }
        });

        newCategoryItem.addView(categoryNameTextView);
        newCategoryItem.addView(amountTextView);
        newCategoryItem.addView(editButton);
        newCategoryItem.addView(deleteButton);

        categoriesList.addView(newCategoryItem);
    }

    private void deleteCategory(Category category, View view) {
        Call<Void> call = apiService.deleteCategory(category.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    categoriesList.removeView(view);
                } else {
                    Toast.makeText(ExpenseCategoryManagementActivity.this, "Failed to delete category", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ExpenseCategoryManagementActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDialog(Category category, final TextView categoryNameTextView, final TextView amountTextView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Category");

        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_edit_category, null);
        builder.setView(customLayout);

        final EditText editCategoryName = customLayout.findViewById(R.id.edit_category_name);
        final EditText editAmount = customLayout.findViewById(R.id.edit_amount);

        editCategoryName.setText(category.getName());
        editAmount.setText(String.valueOf(category.getBudget()));

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newCategoryName = editCategoryName.getText().toString().trim();
                String newAmount = editAmount.getText().toString().trim();

                if (!newCategoryName.isEmpty() && !newAmount.isEmpty()) {
                    category.setName(newCategoryName);
                    category.setBudget(Double.parseDouble(newAmount));

                    updateCategory(category, categoryNameTextView, amountTextView);
                } else {
                    Toast.makeText(ExpenseCategoryManagementActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateCategory(Category category, final TextView categoryNameTextView, final TextView amountTextView) {
        Call<Category> call = apiService.updateCategory(category, category.getId());
        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryNameTextView.setText(response.body().getName());
                    amountTextView.setText("$" + response.body().getBudget());
                } else {
                    Toast.makeText(ExpenseCategoryManagementActivity.this, "Failed to update category", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                Toast.makeText(ExpenseCategoryManagementActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
