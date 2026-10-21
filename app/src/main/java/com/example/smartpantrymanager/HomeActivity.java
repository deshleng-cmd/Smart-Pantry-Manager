
package com.example.smartpantrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class HomeActivity extends AppCompatActivity {

    // Home Page buttons
    private Button btnHomeAddIngredient;
    private Button btnHomeRecipes;
    private Button btnHomeSettings;
    private Button btnHomePantry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // ============================================
        // LOAD SAVED DARK MODE
        // ============================================

        SharedPreferences preferences =
                getSharedPreferences(
                        "SmartPantrySettings",
                        MODE_PRIVATE
                );

        boolean darkModeEnabled =
                preferences.getBoolean("darkMode", false);

        AppCompatDelegate.setDefaultNightMode(
                darkModeEnabled
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        // ============================================
        // CONNECT BUTTONS
        // ============================================

        btnHomeAddIngredient =
                findViewById(R.id.btnHomeAddIngredient);

        btnHomeRecipes =
                findViewById(R.id.btnHomeRecipes);

        btnHomeSettings =
                findViewById(R.id.btnHomeSettings);

        btnHomePantry =
                findViewById(R.id.btnHomePantry);

        // ============================================
        // ADD INGREDIENT
        // ============================================

        btnHomeAddIngredient.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    AddEditIngredientActivity.class
            );

            startActivity(intent);
        });

        // ============================================
        // SUGGESTED RECIPES
        // ============================================

        btnHomeRecipes.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(intent);
        });

        // ============================================
        // SETTINGS
        // ============================================

        btnHomeSettings.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    SettingsActivity.class
            );

            startActivity(intent);
        });

        // ============================================
        // VIEW MY PANTRY
        // ============================================

        btnHomePantry.setOnClickListener(v -> {

            Intent intent = new Intent(
                    HomeActivity.this,
                    MainActivity.class
            );

            startActivity(intent);
        });
    }
}

