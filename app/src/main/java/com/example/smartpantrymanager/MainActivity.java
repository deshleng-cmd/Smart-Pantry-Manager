package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Buttons
    private Button btnAddIngredient;
    private Button btnSuggestedRecipes;
    private Button btnSettings;

    // Pantry display
    private ListView listPantry;
    private TextView txtEmptyPantry;

    // Database
    private DatabaseHelper databaseHelper;

    // List used to display ingredients
    private ArrayList<String> ingredientList;

    // Stores the database ID for each displayed ingredient
    private ArrayList<Integer> ingredientIds;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Connect buttons
        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnSuggestedRecipes = findViewById(R.id.btnSuggestedRecipes);
        btnSettings = findViewById(R.id.btnSettings);

        // Connect pantry components
        listPantry = findViewById(R.id.listPantry);
        txtEmptyPantry = findViewById(R.id.txtEmptyPantry);

        // Create database helper
        databaseHelper = new DatabaseHelper(this);

        // Create lists
        ingredientList = new ArrayList<>();
        ingredientIds = new ArrayList<>();

        // Connect ingredient list to ListView
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                ingredientList
        );

        listPantry.setAdapter(adapter);


        // ============================================
        // ADD INGREDIENT BUTTON
        // ============================================

        btnAddIngredient.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    AddEditIngredientActivity.class
            );

            startActivity(intent);
        });


        // ============================================
        // SUGGESTED RECIPES BUTTON
        // ============================================

        btnSuggestedRecipes.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(intent);
        });


        // ============================================
        // CLICK AN INGREDIENT TO EDIT IT
        // ============================================

        listPantry.setOnItemClickListener(
                (parent, view, position, id) -> {

                    int ingredientId = ingredientIds.get(position);

                    Intent intent = new Intent(
                            MainActivity.this,
                            AddEditIngredientActivity.class
                    );

                    // Send ingredient ID to the edit screen
                    intent.putExtra(
                            "INGREDIENT_ID",
                            ingredientId
                    );

                    startActivity(intent);
                }
        );
    }


    // ============================================
    // LOAD INGREDIENTS FROM DATABASE
    // ============================================

    private void loadIngredients() {

        ingredientList.clear();
        ingredientIds.clear();

        Cursor cursor = databaseHelper.getAllIngredients();

        if (cursor.getCount() == 0) {

            // Pantry is empty
            txtEmptyPantry.setVisibility(View.VISIBLE);
            listPantry.setVisibility(View.GONE);

        } else {

            // Pantry contains ingredients
            txtEmptyPantry.setVisibility(View.GONE);
            listPantry.setVisibility(View.VISIBLE);

            while (cursor.moveToNext()) {

                // Get ingredient ID
                int ingredientId = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_ID
                        )
                );

                // Get ingredient name
                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_NAME
                        )
                );

                // Get quantity
                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_QUANTITY
                        )
                );

                // Get unit
                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_UNIT
                        )
                );

                // Get expiry date
                String expiryDate = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_EXPIRY_DATE
                        )
                );

                // Create text that will appear in the ListView
                String ingredient =
                        name +
                                "\nQuantity: " + quantity + " " + unit +
                                "\nExpiry: " + expiryDate;

                // Add information to both lists
                ingredientList.add(ingredient);
                ingredientIds.add(ingredientId);
            }
        }

        cursor.close();

        // Refresh ListView
        adapter.notifyDataSetChanged();
    }


    // ============================================
    // REFRESH PANTRY
    // ============================================

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            loadIngredients();
        }
    }
}