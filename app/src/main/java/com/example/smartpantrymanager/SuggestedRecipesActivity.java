package com.example.smartpantrymanager;

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

    private ArrayList<String> recipeList;
    private ArrayList<String> pantryIngredients;

    private ArrayAdapter<String> adapter;

    private DatabaseHelper databaseHelper;

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
        pantryIngredients = new ArrayList<>();

        // Create adapter
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                recipeList
        );

        listRecipes.setAdapter(adapter);

        // Load pantry ingredients
        loadPantryIngredients();

        // Generate recipe suggestions
        suggestRecipes();
    }


    // Load ingredient names from SQLite
    private void loadPantryIngredients() {

        pantryIngredients.clear();

        Cursor cursor = databaseHelper.getAllIngredients();

        while (cursor.moveToNext()) {

            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                            DatabaseHelper.COLUMN_NAME
                    )
            );

            // Convert to lowercase to make matching easier
            pantryIngredients.add(name.toLowerCase());
        }

        cursor.close();
    }


    // Check whether an ingredient exists
    private boolean hasIngredient(String ingredient) {

        for (String pantryItem : pantryIngredients) {

            if (pantryItem.equalsIgnoreCase(ingredient)) {
                return true;
            }
        }

        return false;
    }


    // Suggest recipes
    private void suggestRecipes() {

        recipeList.clear();


        // Eggs + Bread
        if (hasIngredient("eggs") &&
                hasIngredient("bread")) {

            recipeList.add(
                    "Egg Toast\n" +
                            "Uses: Eggs, Bread"
            );
        }


        // Rice + Eggs
        if (hasIngredient("rice") &&
                hasIngredient("eggs")) {

            recipeList.add(
                    "Egg Fried Rice\n" +
                            "Uses: Rice, Eggs"
            );
        }


        // Tomato + Onion
        if (hasIngredient("tomato") &&
                hasIngredient("onion")) {

            recipeList.add(
                    "Tomato and Onion Salad\n" +
                            "Uses: Tomato, Onion"
            );
        }


        // Chicken + Rice
        if (hasIngredient("chicken") &&
                hasIngredient("rice")) {

            recipeList.add(
                    "Chicken and Rice\n" +
                            "Uses: Chicken, Rice"
            );
        }


        // Potato + Onion
        if (hasIngredient("potato") &&
                hasIngredient("onion")) {

            recipeList.add(
                    "Potato and Onion Fry\n" +
                            "Uses: Potato, Onion"
            );
        }


        // No matching recipes
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
}