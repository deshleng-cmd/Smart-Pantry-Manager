
package com.example.smartpantrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

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

        // ============================================
        // DARK MODE - LOAD SAVED USER PREFERENCE
        // ============================================

        // Open the same settings used by SettingsActivity
        SharedPreferences preferences = getSharedPreferences(
                "SmartPantrySettings",
                MODE_PRIVATE
        );

        // Check whether Dark Mode was previously enabled
        // Default is false, so the app starts in Light Mode
        boolean darkModeEnabled = preferences.getBoolean(
                "darkMode",
                false
        );

        // Apply the saved theme before creating the screen
        AppCompatDelegate.setDefaultNightMode(
                darkModeEnabled
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        // ============================================
        // TOOLBAR
        // ============================================

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        // ============================================
        // CONNECT XML COMPONENTS
        // ============================================

        // Connect buttons
        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnSuggestedRecipes = findViewById(R.id.btnSuggestedRecipes);
        btnSettings = findViewById(R.id.btnSettings);

        // Connect pantry components
        listPantry = findViewById(R.id.listPantry);
        txtEmptyPantry = findViewById(R.id.txtEmptyPantry);


        // ============================================
        // DATABASE
        // ============================================

        databaseHelper = new DatabaseHelper(this);


        // ============================================
        // CUSTOM ADAPTER
        // ============================================

        // Create ingredient list
        ingredientList = new ArrayList<>();

        // Use our custom adapter
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

                // Get ingredient quantity
                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_QUANTITY
                        )
                );

                // Get ingredient unit
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


                // Create Ingredient object
                Ingredient ingredient = new Ingredient(
                        ingredientId,
                        name,
                        quantity,
                        unit,
                        expiryDate
                );


                // Add ingredient to list
                ingredientList.add(ingredient);
            }
        }

        cursor.close();

        // Refresh custom ListView
        adapter.notifyDataSetChanged();
    }


    // ============================================
    // REFRESH PANTRY WHEN RETURNING TO SCREEN
    // ============================================

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            loadIngredients();
        }
    }


    // ============================================
    // CREATE NAVIGATION MENU
    // ============================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(
                R.menu.menu_main,
                menu
        );

        return true;
    }


    // ============================================
    // NAVIGATION MENU OPTIONS
    // ============================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();


        // --------------------------------------------
        // PANTRY
        // --------------------------------------------

        if (itemId == R.id.menuPantry) {

            // Already on Pantry screen
            return true;
        }


        // --------------------------------------------
        // SUGGESTED RECIPES
        // --------------------------------------------

        if (itemId == R.id.menuRecipes) {

            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(intent);

            return true;
        }


        // --------------------------------------------
        // SETTINGS
        // --------------------------------------------

        if (itemId == R.id.menuSettings) {

            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );

            startActivity(intent);

            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
