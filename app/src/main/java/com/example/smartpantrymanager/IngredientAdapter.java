
package com.example.smartpantrymanager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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

        // ============================================
        // DISPLAY INGREDIENT INFORMATION
        // ============================================

        txtIngredientName.setText(
                ingredient.getName()
        );

        txtIngredientQuantity.setText(
                "Quantity: "
                        + ingredient.getQuantity()
                        + " "
                        + ingredient.getUnit()
        );

        // ============================================
        // CHECK EXPIRY DATE
        // ============================================

        if (isExpired(ingredient.getExpiryDate())) {

            // Show red warning for expired ingredient
            txtIngredientExpiry.setText(
                    "EXPIRED - " + ingredient.getExpiryDate()
            );

            txtIngredientExpiry.setTextColor(Color.RED);

        } else {

            // Show normal expiry date
            txtIngredientExpiry.setText(
                    "Expiry: " + ingredient.getExpiryDate()
            );

            // Use theme's normal text color
            android.util.TypedValue typedValue =
                    new android.util.TypedValue();

            context.getTheme().resolveAttribute(
                    android.R.attr.textColorSecondary,
                    typedValue,
                    true
            );

            txtIngredientExpiry.setTextColor(
                    context.getColor(
                            typedValue.resourceId
                    )
            );
        }

        return convertView;
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

            Date today = dateFormat.parse(
                    dateFormat.format(new Date())
            );

            return expiry.before(today);

        } catch (ParseException e) {

            return false;
        }
    }
}

