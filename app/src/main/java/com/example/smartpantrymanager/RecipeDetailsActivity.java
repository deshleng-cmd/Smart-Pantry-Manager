package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailsActivity extends AppCompatActivity {

    private TextView txtRecipeName;
    private TextView txtIngredients;
    private TextView txtSteps;

    private DatabaseHelper databaseHelper;

    private int recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_details);

        // Connect XML components
        txtRecipeName = findViewById(R.id.txtRecipeName);
        txtIngredients = findViewById(R.id.txtIngredients);
        txtSteps = findViewById(R.id.txtSteps);

        // Connect to database
        databaseHelper = new DatabaseHelper(this);

        // Get recipe ID sent from SuggestedRecipesActivity
        recipeId = getIntent().getIntExtra("RECIPE_ID", -1);

        // Make sure a valid recipe ID was received
        if (recipeId != -1) {

            loadRecipeDetails();

        } else {

            txtRecipeName.setText("Recipe not found");
        }
    }


    // ============================================
    // LOAD RECIPE DETAILS
    // ============================================

    private void loadRecipeDetails() {

        Cursor recipeCursor =
                databaseHelper.getRecipeById(recipeId);

        if (recipeCursor.moveToFirst()) {

            String recipeName =
                    recipeCursor.getString(
                            recipeCursor.getColumnIndexOrThrow(
                                    DatabaseHelper.RECIPE_NAME
                            )
                    );

            String recipeSteps =
                    recipeCursor.getString(
                            recipeCursor.getColumnIndexOrThrow(
                                    DatabaseHelper.RECIPE_STEPS
                            )
                    );

            txtRecipeName.setText(recipeName);
            txtSteps.setText(recipeSteps);
        }

        recipeCursor.close();


        // ============================================
        // LOAD REQUIRED INGREDIENTS
        // ============================================

        Cursor ingredientCursor =
                databaseHelper.getRecipeIngredients(recipeId);

        StringBuilder ingredients =
                new StringBuilder();

        while (ingredientCursor.moveToNext()) {

            String name =
                    ingredientCursor.getString(
                            ingredientCursor.getColumnIndexOrThrow(
                                    DatabaseHelper.RI_INGREDIENT_NAME
                            )
                    );

            double quantity =
                    ingredientCursor.getDouble(
                            ingredientCursor.getColumnIndexOrThrow(
                                    DatabaseHelper.RI_QUANTITY
                            )
                    );

            String unit =
                    ingredientCursor.getString(
                            ingredientCursor.getColumnIndexOrThrow(
                                    DatabaseHelper.RI_UNIT
                            )
                    );

            ingredients.append("• ")
                    .append(name)
                    .append(" - ")
                    .append(quantity)
                    .append(" ")
                    .append(unit)
                    .append("\n");
        }

        ingredientCursor.close();

        txtIngredients.setText(ingredients.toString());
    }
}