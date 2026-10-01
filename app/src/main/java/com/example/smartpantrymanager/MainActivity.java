package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
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

    // Stores Ingredient objects
    private ArrayList<Ingredient> ingredientList;

    // Custom adapter
    private IngredientAdapter adapter;

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

        // Create ingredient list
        ingredientList = new ArrayList<>();

        // Use our CUSTOM ADAPTER
        adapter = new IngredientAdapter(
                this,
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
        // SETTINGS BUTTON
        // ============================================

        btnSettings.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );

            startActivity(intent);
        });


        // ============================================
        // CLICK AN INGREDIENT TO EDIT IT
        // ============================================

        listPantry.setOnItemClickListener(
                (parent, view, position, id) -> {

                    // Get the ingredient that was clicked
                    Ingredient ingredient =
                            ingredientList.get(position);

                    Intent intent = new Intent(
                            MainActivity.this,
                            AddEditIngredientActivity.class
                    );

                    // Send ingredient ID to edit screen
                    intent.putExtra(
                            "INGREDIENT_ID",
                            ingredient.getId()
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

                int ingredientId = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_ID
                        )
                );

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

                // Create an Ingredient object
                Ingredient ingredient = new Ingredient(
                        ingredientId,
                        name,
                        quantity,
                        unit,
                        expiryDate
                );

                // Add it to the list
                ingredientList.add(ingredient);
            }
        }

        cursor.close();

        // Refresh custom ListView
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