package com.example.smartpantrymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class IngredientAdapter extends ArrayAdapter<Ingredient> {

    private Context context;
    private ArrayList<Ingredient> ingredients;

    public IngredientAdapter(
            Context context,
            ArrayList<Ingredient> ingredients) {

        super(context, 0, ingredients);

        this.context = context;
        this.ingredients = ingredients;
    }

    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent) {

        // Reuse an existing row if possible
        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_ingredient,
                    parent,
                    false
            );
        }

        // Get the ingredient for this row
        Ingredient ingredient = ingredients.get(position);

        // Connect TextViews
        TextView txtIngredientName =
                convertView.findViewById(
                        R.id.txtIngredientName
                );

        TextView txtIngredientQuantity =
                convertView.findViewById(
                        R.id.txtIngredientQuantity
                );

        TextView txtIngredientExpiry =
                convertView.findViewById(
                        R.id.txtIngredientExpiry
                );

        // Display ingredient information
        txtIngredientName.setText(
                ingredient.getName()
        );

        txtIngredientQuantity.setText(
                "Quantity: "
                        + ingredient.getQuantity()
                        + " "
                        + ingredient.getUnit()
        );

        txtIngredientExpiry.setText(
                "Expiry: "
                        + ingredient.getExpiryDate()
        );

        return convertView;
    }
}
