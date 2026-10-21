
package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailsActivity extends AppCompatActivity {

    // ============================================
    // DECLARE COMPONENTS
    // ============================================

    private TextView txtRecipeName;
    private TextView txtIngredients;
    private TextView txtSteps;
    private TextView txtRecipeAvailability;

    private Button btnMakeRecipe;

    // Database
    private DatabaseHelper databaseHelper;

    // Selected recipe ID
    private int recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe_details);

        // ============================================
        // CONNECT XML COMPONENTS
        // ============================================

        txtRecipeName = findViewById(R.id.txtRecipeName);

        txtIngredients = findViewById(R.id.txtIngredients);

        txtSteps = findViewById(R.id.txtSteps);

        txtRecipeAvailability =
                findViewById(R.id.txtRecipeAvailability);

        btnMakeRecipe = findViewById(R.id.btnMakeRecipe);

        // ============================================
        // CONNECT TO DATABASE
        // ============================================

        databaseHelper = new DatabaseHelper(this);

        // ============================================
        // GET SELECTED RECIPE ID
        // ============================================

        recipeId = getIntent().getIntExtra("RECIPE_ID", -1);

        if (recipeId != -1) {

            loadRecipeDetails();

            checkIngredientAvailability();

        } else {

            txtRecipeName.setText("Recipe not found");

            txtRecipeAvailability.setText("Invalid recipe.");

            btnMakeRecipe.setEnabled(false);
        }

        // ============================================
        // MAKE RECIPE BUTTON
        // ============================================

        btnMakeRecipe.setOnClickListener(v -> {

            if (recipeId == -1) {
                return;
            }

            // Check whether ingredients are available
            String missing =
                    databaseHelper.getMissingIngredients(recipeId);

            // If ingredients are missing, show a message
            if (!missing.isEmpty()) {

                showMissingIngredients(missing);

                checkIngredientAvailability();

                return;
            }

            // Ask user to confirm before deducting stock
            new AlertDialog.Builder(this)

                    .setTitle("Make Recipe")

                    .setMessage(
                            "Make " + txtRecipeName.getText()
                                    + "?\n\n"
                                    + "The required ingredients will "
                                    + "be deducted from your pantry."
                    )

                    .setNegativeButton("Cancel", null)

                    .setPositiveButton(
                            "Make Recipe",
                            (dialog, which) -> {

                                // Deduct ingredients from pantry
                                boolean success =
                                        databaseHelper.makeRecipe(recipeId);

                                if (success) {

                                    // Show success message
                                    new AlertDialog.Builder(this)

                                            .setTitle("Recipe Made!")

                                            .setMessage(
                                                    "Your pantry quantities "
                                                            + "have been updated."
                                            )

                                            .setPositiveButton(
                                                    "OK", null
                                            )

                                            .show();

                                } else {

                                    // Show failure message
                                    Toast.makeText(
                                            this,
                                            "Recipe could not be made. "
                                                    + "Please check your pantry.",
                                            Toast.LENGTH_LONG
                                    ).show();
                                }

                                // Refresh availability
                                checkIngredientAvailability();
                            }
                    )

                    .show();
        });
    }

    // ============================================
    // CHECK INGREDIENT AVAILABILITY
    // ============================================

    private void checkIngredientAvailability() {

        if (recipeId == -1) {

            btnMakeRecipe.setEnabled(false);

            return;
        }

        String missing =
                databaseHelper.getMissingIngredients(recipeId);

        if (missing.isEmpty()) {

            // All ingredients are available
            txtRecipeAvailability.setText(
                    "✓ All ingredients available!\n"
                            + "You can make this recipe."
            );

            btnMakeRecipe.setEnabled(true);

        } else {

            // Some ingredients are missing
            txtRecipeAvailability.setText(
                    "Missing Ingredients:\n\n" + missing
            );

            btnMakeRecipe.setEnabled(false);
        }
    }

    // ============================================
    // SHOW MISSING INGREDIENTS
    // ============================================

    private void showMissingIngredients(String missing) {

        new AlertDialog.Builder(this)

                .setTitle("Not Enough Ingredients")

                .setMessage(
                        "You still need:\n\n" + missing
                )

                .setPositiveButton("OK", null)

                .show();
    }

    // ============================================
    // LOAD RECIPE DETAILS
    // ============================================

    private void loadRecipeDetails() {

        // Get selected recipe from database
        Cursor recipeCursor =
                databaseHelper.getRecipeById(recipeId);

        if (recipeCursor.moveToFirst()) {

            // Get recipe name
            String recipeName =
                    recipeCursor.getString(
                            recipeCursor.getColumnIndexOrThrow(
                                    DatabaseHelper.RECIPE_NAME
                            )
                    );

            // Get recipe instructions
            String recipeSteps =
                    recipeCursor.getString(
                            recipeCursor.getColumnIndexOrThrow(
                                    DatabaseHelper.RECIPE_STEPS
                            )
                    );

            // Display recipe information
            txtRecipeName.setText(recipeName);

            txtSteps.setText(recipeSteps);

        } else {

            txtRecipeName.setText("Recipe not found");

            recipeId = -1;
        }

        recipeCursor.close();

        if (recipeId == -1) {
            return;
        }

        // ============================================
        // LOAD REQUIRED INGREDIENTS
        // ============================================

        Cursor ingredientCursor =
                databaseHelper.getRecipeIngredients(recipeId);

        StringBuilder ingredients = new StringBuilder();

        while (ingredientCursor.moveToNext()) {

            // Get ingredient name
            String name =
                    ingredientCursor.getString(
                            ingredientCursor.getColumnIndexOrThrow(
                                    DatabaseHelper.RI_INGREDIENT_NAME
                            )
                    );

            // Get required quantity
            double quantity =
                    ingredientCursor.getDouble(
                            ingredientCursor.getColumnIndexOrThrow(
                                    DatabaseHelper.RI_QUANTITY
                            )
                    );

            // Get ingredient unit
            String unit =
                    ingredientCursor.getString(
                            ingredientCursor.getColumnIndexOrThrow(
                                    DatabaseHelper.RI_UNIT
                            )
                    );

            // Add ingredient to display
            ingredients.append("• ")
                    .append(name)
                    .append(" - ")
                    .append(quantity)
                    .append(" ")
                    .append(unit)
                    .append("\n");
        }

        ingredientCursor.close();

        // Display all required ingredients
        txtIngredients.setText(ingredients.toString());
    }

    // ============================================
    // REFRESH AVAILABILITY
    // ============================================

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null && recipeId != -1) {

            checkIngredientAvailability();
        }
    }
}
