
package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SuggestedRecipesActivity extends AppCompatActivity {

    // ============================================
    // DECLARE COMPONENTS
    // ============================================

    private ListView listRecipes;
    private TextView txtRecipeMessage;

    // Database
    private DatabaseHelper databaseHelper;

    // Stores the names of suggested recipes
    private ArrayList<String> recipeList;

    // Stores the IDs of suggested recipes
    private ArrayList<Integer> recipeIds;

    // Adapter for displaying recipes
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_suggested_recipes);

        // ============================================
        // CONNECT XML COMPONENTS
        // ============================================

        listRecipes = findViewById(R.id.listRecipes);

        txtRecipeMessage = findViewById(R.id.txtRecipeMessage);

        // ============================================
        // CONNECT TO DATABASE
        // ============================================

        databaseHelper = new DatabaseHelper(this);

        // ============================================
        // INITIALIZE LISTS
        // ============================================

        recipeList = new ArrayList<>();

        recipeIds = new ArrayList<>();

        // ============================================
        // CREATE ADAPTER
        // ============================================

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                recipeList
        );

        listRecipes.setAdapter(adapter);

        // ============================================
        // CLICK A SUGGESTED RECIPE
        // ============================================

        listRecipes.setOnItemClickListener(
                (parent, view, position, id) -> {

                    // Get the selected recipe ID
                    int selectedRecipeId =
                            recipeIds.get(position);

                    // Open RecipeDetailsActivity
                    Intent intent = new Intent(
                            SuggestedRecipesActivity.this,
                            RecipeDetailsActivity.class
                    );

                    // Send the recipe ID
                    intent.putExtra(
                            "RECIPE_ID",
                            selectedRecipeId
                    );

                    startActivity(intent);
                }
        );

        // Load suggested recipes
        loadSuggestedRecipes();
    }

    // ============================================
    // LOAD STRICTLY MATCHING RECIPES
    // ============================================

    private void loadSuggestedRecipes() {

        // Clear old recipes
        recipeList.clear();
        recipeIds.clear();

        // Get all recipes from SQLite
        Cursor recipeCursor =
                databaseHelper.getAllRecipes();

        try {

            while (recipeCursor.moveToNext()) {

                // Get recipe ID
                int recipeId =
                        recipeCursor.getInt(
                                recipeCursor.getColumnIndexOrThrow(
                                        DatabaseHelper.RECIPE_ID
                                )
                        );

                // Get recipe name
                String recipeName =
                        recipeCursor.getString(
                                recipeCursor.getColumnIndexOrThrow(
                                        DatabaseHelper.RECIPE_NAME
                                )
                        );

                // ============================================
                // STRICT MATCHING RULE
                // ============================================

                // Check whether ANY ingredients are missing
                String missingIngredients =
                        databaseHelper.getMissingIngredients(recipeId);

                // Only display the recipe if ALL ingredients
                // are available in sufficient quantities
                if (missingIngredients.isEmpty()) {

                    recipeList.add(recipeName);

                    // Store ID only for matching recipes
                    recipeIds.add(recipeId);
                }
            }

        } finally {

            recipeCursor.close();
        }

        // ============================================
        // DISPLAY APPROPRIATE MESSAGE
        // ============================================

        if (recipeList.isEmpty()) {

            txtRecipeMessage.setText(
                    "No recipes match your pantry yet.\n\n"
                            + "Add more ingredients to discover "
                            + "recipes you can make!"
            );

        } else {

            txtRecipeMessage.setText(
                    "Recipes you can make with the "
                            + "ingredients currently in your pantry:"
            );
        }

        // Refresh ListView
        adapter.notifyDataSetChanged();
    }

    // ============================================
    // REFRESH SUGGESTIONS WHEN RETURNING
    // ============================================

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            loadSuggestedRecipes();
        }
    }
}

