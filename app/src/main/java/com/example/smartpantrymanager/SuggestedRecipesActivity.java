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

    private ListView listRecipes;
    private TextView txtRecipeMessage;

    private DatabaseHelper databaseHelper;

    // Stores the names of recipes that can be made
    private ArrayList<String> recipeList;

    // Stores the database ID of each displayed recipe
    private ArrayList<Integer> recipeIds;

    // Connects recipeList to the ListView
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_suggested_recipes);

        // Connect XML components
        listRecipes = findViewById(R.id.listRecipes);
        txtRecipeMessage = findViewById(R.id.txtRecipeMessage);

        // Connect to database
        databaseHelper = new DatabaseHelper(this);

        // Create lists
        recipeList = new ArrayList<>();
        recipeIds = new ArrayList<>();

        // Create adapter
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                recipeList
        );

        listRecipes.setAdapter(adapter);

        // ============================================
        // CLICK A RECIPE TO VIEW ITS DETAILS
        // ============================================

        listRecipes.setOnItemClickListener(
                (parent, view, position, id) -> {

                    // Get the database ID of the selected recipe
                    int selectedRecipeId =
                            recipeIds.get(position);

                    // Open RecipeDetailsActivity
                    Intent intent = new Intent(
                            SuggestedRecipesActivity.this,
                            RecipeDetailsActivity.class
                    );

                    // Send recipe ID to RecipeDetailsActivity
                    intent.putExtra(
                            "RECIPE_ID",
                            selectedRecipeId
                    );

                    startActivity(intent);
                }
        );

        // Find recipes that match pantry ingredients
        loadSuggestedRecipes();
    }


    // ============================================
    // LOAD SUGGESTED RECIPES
    // ============================================

    private void loadSuggestedRecipes() {

        recipeList.clear();
        recipeIds.clear();

        Cursor recipeCursor =
                databaseHelper.getAllRecipes();

        while (recipeCursor.moveToNext()) {

            int recipeId =
                    recipeCursor.getInt(
                            recipeCursor.getColumnIndexOrThrow(
                                    DatabaseHelper.RECIPE_ID
                            )
                    );

            String recipeName =
                    recipeCursor.getString(
                            recipeCursor.getColumnIndexOrThrow(
                                    DatabaseHelper.RECIPE_NAME
                            )
                    );

            // Check whether pantry has everything needed
            if (canMakeRecipe(recipeId)) {

                // Display recipe name
                recipeList.add(recipeName);

                // Store recipe ID at the same position
                recipeIds.add(recipeId);
            }
        }

        recipeCursor.close();

        // Display appropriate message
        if (recipeList.isEmpty()) {

            txtRecipeMessage.setText(
                    "No recipes match your current pantry ingredients."
            );

        } else {

            txtRecipeMessage.setText(
                    "Recipes you can make with your pantry ingredients:"
            );
        }

        // Refresh ListView
        adapter.notifyDataSetChanged();
    }


    // ============================================
    // CHECK IF A RECIPE CAN BE MADE
    // ============================================

    private boolean canMakeRecipe(int recipeId) {

        Cursor requiredIngredients =
                databaseHelper.getRecipeIngredients(recipeId);

        while (requiredIngredients.moveToNext()) {

            String requiredName =
                    requiredIngredients.getString(
                            requiredIngredients.getColumnIndexOrThrow(
                                    DatabaseHelper.RI_INGREDIENT_NAME
                            )
                    );

            double requiredQuantity =
                    requiredIngredients.getDouble(
                            requiredIngredients.getColumnIndexOrThrow(
                                    DatabaseHelper.RI_QUANTITY
                            )
                    );

            String requiredUnit =
                    requiredIngredients.getString(
                            requiredIngredients.getColumnIndexOrThrow(
                                    DatabaseHelper.RI_UNIT
                            )
                    );

            // Check pantry for this ingredient
            if (!pantryHasIngredient(
                    requiredName,
                    requiredQuantity,
                    requiredUnit)) {

                requiredIngredients.close();
                return false;
            }
        }

        requiredIngredients.close();

        return true;
    }


    // ============================================
    // CHECK PANTRY INGREDIENT
    // ============================================

    private boolean pantryHasIngredient(
            String requiredName,
            double requiredQuantity,
            String requiredUnit) {

        Cursor pantryCursor =
                databaseHelper.getAllIngredients();

        while (pantryCursor.moveToNext()) {

            String pantryName =
                    pantryCursor.getString(
                            pantryCursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COLUMN_NAME
                            )
                    );

            double pantryQuantity =
                    pantryCursor.getDouble(
                            pantryCursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COLUMN_QUANTITY
                            )
                    );

            String pantryUnit =
                    pantryCursor.getString(
                            pantryCursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COLUMN_UNIT
                            )
                    );

            // Compare name, quantity and unit
            if (pantryName.equalsIgnoreCase(requiredName)
                    && pantryUnit.equalsIgnoreCase(requiredUnit)
                    && pantryQuantity >= requiredQuantity) {

                pantryCursor.close();
                return true;
            }
        }

        pantryCursor.close();

        return false;
    }
}