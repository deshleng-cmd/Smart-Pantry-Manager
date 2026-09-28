package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText etIngredientName;
    private EditText etQuantity;
    private EditText etUnit;
    private EditText etExpiryDate;

    private Button btnSaveIngredient;
    private Button btnDeleteIngredient;

    private DatabaseHelper databaseHelper;

    // -1 means we are adding a new ingredient
    private int ingredientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_edit_ingredient);

        // Connect Java variables to XML
        etIngredientName = findViewById(R.id.etIngredientName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiryDate = findViewById(R.id.etExpiryDate);

        btnSaveIngredient = findViewById(R.id.btnSaveIngredient);
        btnDeleteIngredient = findViewById(R.id.btnDeleteIngredient);

        // Connect to database
        databaseHelper = new DatabaseHelper(this);

        // Check whether an ingredient ID was sent from MainActivity
        ingredientId = getIntent().getIntExtra(
                "INGREDIENT_ID",
                -1
        );

        // If an ID exists, we are editing
        if (ingredientId != -1) {

            loadIngredient();

            btnSaveIngredient.setText("Update Ingredient");
            btnDeleteIngredient.setVisibility(View.VISIBLE);

        } else {

            // We are adding a new ingredient
            btnSaveIngredient.setText("Save Ingredient");
            btnDeleteIngredient.setVisibility(View.GONE);
        }

        // Date picker
        etExpiryDate.setOnClickListener(
                v -> showDatePicker()
        );

        // Save or update
        btnSaveIngredient.setOnClickListener(
                v -> saveIngredient()
        );

        // Delete ingredient
        btnDeleteIngredient.setOnClickListener(
                v -> confirmDelete()
        );
    }

    // Load the selected ingredient from SQLite
    private void loadIngredient() {

        Cursor cursor =
                databaseHelper.getIngredientById(ingredientId);

        if (cursor.moveToFirst()) {

            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                            DatabaseHelper.COLUMN_NAME
                    )
            );

            double quantity = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(
                            DatabaseHelper.COLUMN_QUANTITY
                    )
            );

            String unit = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                            DatabaseHelper.COLUMN_UNIT
                    )
            );

            String expiryDate = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                            DatabaseHelper.COLUMN_EXPIRY_DATE
                    )
            );

            etIngredientName.setText(name);
            etQuantity.setText(String.valueOf(quantity));
            etUnit.setText(unit);
            etExpiryDate.setText(expiryDate);
        }

        cursor.close();
    }

    // Show calendar
    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        this,
                        (view,
                         selectedYear,
                         selectedMonth,
                         selectedDay) -> {

                            String date =
                                    selectedDay + "/" +
                                            (selectedMonth + 1) + "/" +
                                            selectedYear;

                            etExpiryDate.setText(date);
                        },
                        year,
                        month,
                        day
                );

        datePickerDialog.show();
    }

    // Save new ingredient OR update existing ingredient
    private void saveIngredient() {

        String name =
                etIngredientName.getText()
                        .toString()
                        .trim();

        String quantityText =
                etQuantity.getText()
                        .toString()
                        .trim();

        String unit =
                etUnit.getText()
                        .toString()
                        .trim();

        String expiryDate =
                etExpiryDate.getText()
                        .toString()
                        .trim();

        // Validate fields
        if (name.isEmpty() ||
                quantityText.isEmpty() ||
                unit.isEmpty() ||
                expiryDate.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please complete all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        double quantity;

        try {

            quantity =
                    Double.parseDouble(quantityText);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter a valid quantity",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // ADD MODE
        if (ingredientId == -1) {

            boolean inserted =
                    databaseHelper.addIngredient(
                            name,
                            quantity,
                            unit,
                            expiryDate
                    );

            if (inserted) {

                Toast.makeText(
                        this,
                        "Ingredient saved successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        this,
                        "Failed to save ingredient",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } else {

            // EDIT MODE
            boolean updated =
                    databaseHelper.updateIngredient(
                            ingredientId,
                            name,
                            quantity,
                            unit,
                            expiryDate
                    );

            if (updated) {

                Toast.makeText(
                        this,
                        "Ingredient updated successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        this,
                        "Failed to update ingredient",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    // Ask before deleting
    private void confirmDelete() {

        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage(
                        "Are you sure you want to delete this ingredient?"
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> deleteIngredient()
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }

    // Delete ingredient from SQLite
    private void deleteIngredient() {

        boolean deleted =
                databaseHelper.deleteIngredient(
                        ingredientId
                );

        if (deleted) {

            Toast.makeText(
                    this,
                    "Ingredient deleted successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to delete ingredient",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}