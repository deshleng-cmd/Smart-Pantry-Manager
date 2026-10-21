
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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

        SharedPreferences preferences = getSharedPreferences(
                "SmartPantrySettings",
                MODE_PRIVATE
        );

        boolean darkModeEnabled = preferences.getBoolean(
                "darkMode",
                false
        );

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

        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnSuggestedRecipes = findViewById(R.id.btnSuggestedRecipes);
        btnSettings = findViewById(R.id.btnSettings);

        listPantry = findViewById(R.id.listPantry);
        txtEmptyPantry = findViewById(R.id.txtEmptyPantry);


        // ============================================
        // DATABASE
        // ============================================

        databaseHelper = new DatabaseHelper(this);


        // ============================================
        // CUSTOM ADAPTER
        // ============================================

        ingredientList = new ArrayList<>();

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
        // CLICK AN INGREDIENT
        // ============================================

        listPantry.setOnItemClickListener(
                (parent, view, position, id) -> {

                    // Get the selected ingredient
                    Ingredient ingredient =
                            ingredientList.get(position);

                    // Check whether ingredient is expired
                    if (isExpired(ingredient.getExpiryDate())) {

                        // Show Keep, Edit, Remove options
                        showExpiredIngredientDialog(ingredient);

                    } else {

                        // Open existing edit screen
                        editIngredient(ingredient);
                    }
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
    // CHECK IF INGREDIENT HAS EXPIRED
    // ============================================

    private boolean isExpired(String expiryDate) {

        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            return false;
        }

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        dateFormat.setLenient(false);

        try {

            Date expiry = dateFormat.parse(expiryDate);

            // Get today's date without time
            Date today = dateFormat.parse(
                    dateFormat.format(new Date())
            );

            // True if expiry date is before today
            return expiry.before(today);

        } catch (ParseException e) {

            return false;
        }
    }


    // ============================================
    // OPEN INGREDIENT EDIT SCREEN
    // ============================================

    private void editIngredient(Ingredient ingredient) {

        Intent intent = new Intent(
                MainActivity.this,
                AddEditIngredientActivity.class
        );

        // Send selected ingredient ID
        intent.putExtra(
                "INGREDIENT_ID",
                ingredient.getId()
        );

        startActivity(intent);
    }


    // ============================================
    // EXPIRED INGREDIENT DIALOG
    // ============================================

    private void showExpiredIngredientDialog(
            Ingredient ingredient) {

        new AlertDialog.Builder(this)

                .setTitle("Expired Ingredient")

                .setMessage(
                        ingredient.getName()
                                + " expired on "
                                + ingredient.getExpiryDate()
                                + ".\n\nWhat would you like to do?"
                )

                // KEEP INGREDIENT
                .setNeutralButton("Keep", (dialog, which) -> {

                    dialog.dismiss();
                })

                // EDIT INGREDIENT
                .setNegativeButton("Edit", (dialog, which) -> {

                    editIngredient(ingredient);
                })

                // REMOVE INGREDIENT
                .setPositiveButton("Remove", (dialog, which) -> {

                    // Ask for confirmation before deleting
                    new AlertDialog.Builder(MainActivity.this)

                            .setTitle("Confirm Removal")

                            .setMessage(
                                    "Are you sure you want to remove "
                                            + ingredient.getName()
                                            + " from your pantry?"
                            )

                            .setNegativeButton("Cancel", null)

                            .setPositiveButton(
                                    "Delete",
                                    (confirmDialog, confirmWhich) -> {

                                        // Delete selected ingredient
                                        databaseHelper.deleteIngredient(
                                                ingredient.getId()
                                        );

                                        // Refresh pantry list
                                        loadIngredients();
                                    }
                            )

                            .show();
                })

                .show();
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
