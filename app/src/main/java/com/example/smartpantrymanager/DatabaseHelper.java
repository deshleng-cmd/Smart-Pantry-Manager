package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    // ============================================
    // DATABASE INFORMATION
    // ============================================

    private static final String DATABASE_NAME = "SmartPantry.db";

    // Changed from version 1 to version 2
    private static final int DATABASE_VERSION = 2;


    // ============================================
    // INGREDIENTS TABLE
    // ============================================

    public static final String TABLE_INGREDIENTS = "ingredients";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_EXPIRY_DATE = "expiry_date";


    // ============================================
    // RECIPES TABLE
    // ============================================

    public static final String TABLE_RECIPES = "recipes";

    public static final String RECIPE_ID = "recipe_id";
    public static final String RECIPE_NAME = "recipe_name";
    public static final String RECIPE_STEPS = "recipe_steps";


    // ============================================
    // RECIPE INGREDIENTS TABLE
    // ============================================

    public static final String TABLE_RECIPE_INGREDIENTS =
            "recipe_ingredients";

    public static final String RI_ID = "recipe_ingredient_id";
    public static final String RI_RECIPE_ID = "recipe_id";
    public static final String RI_INGREDIENT_NAME = "ingredient_name";
    public static final String RI_QUANTITY = "required_quantity";
    public static final String RI_UNIT = "required_unit";


    public DatabaseHelper(Context context) {

        super(
                context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION
        );
    }


    // ============================================
    // CREATE DATABASE
    // ============================================

    @Override
    public void onCreate(SQLiteDatabase db) {

        createIngredientsTable(db);

        createRecipeTables(db);

        // Add the default recipes when the database
        // is created for the first time
        seedRecipes(db);
    }


    // ============================================
    // DATABASE UPGRADE
    // ============================================

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        // Upgrade version 1 to version 2
        // WITHOUT deleting existing pantry ingredients
        if (oldVersion < 2) {

            createRecipeTables(db);

            seedRecipes(db);
        }
    }


    // ============================================
    // CREATE INGREDIENTS TABLE
    // ============================================

    private void createIngredientsTable(SQLiteDatabase db) {

        String createTable =
                "CREATE TABLE IF NOT EXISTS " +
                        TABLE_INGREDIENTS + " (" +

                        COLUMN_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        COLUMN_NAME +
                        " TEXT NOT NULL, " +

                        COLUMN_QUANTITY +
                        " REAL NOT NULL, " +

                        COLUMN_UNIT +
                        " TEXT NOT NULL, " +

                        COLUMN_EXPIRY_DATE +
                        " TEXT NOT NULL)";

        db.execSQL(createTable);
    }


    // ============================================
    // CREATE RECIPE TABLES
    // ============================================

    private void createRecipeTables(SQLiteDatabase db) {

        String createRecipesTable =
                "CREATE TABLE IF NOT EXISTS " +
                        TABLE_RECIPES + " (" +

                        RECIPE_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        RECIPE_NAME +
                        " TEXT NOT NULL, " +

                        RECIPE_STEPS +
                        " TEXT NOT NULL)";

        db.execSQL(createRecipesTable);


        String createRecipeIngredientsTable =
                "CREATE TABLE IF NOT EXISTS " +
                        TABLE_RECIPE_INGREDIENTS + " (" +

                        RI_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        RI_RECIPE_ID +
                        " INTEGER NOT NULL, " +

                        RI_INGREDIENT_NAME +
                        " TEXT NOT NULL, " +

                        RI_QUANTITY +
                        " REAL NOT NULL, " +

                        RI_UNIT +
                        " TEXT NOT NULL, " +

                        "FOREIGN KEY(" + RI_RECIPE_ID + ") " +
                        "REFERENCES " + TABLE_RECIPES +
                        "(" + RECIPE_ID + "))";

        db.execSQL(createRecipeIngredientsTable);
    }


    // ============================================
    // PANTRY - CREATE
    // ============================================

    public boolean addIngredient(
            String name,
            double quantity,
            String unit,
            String expiryDate) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(COLUMN_NAME, name);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_UNIT, unit);
        values.put(COLUMN_EXPIRY_DATE, expiryDate);

        long result = db.insert(
                TABLE_INGREDIENTS,
                null,
                values
        );

        db.close();

        return result != -1;
    }


    // ============================================
    // PANTRY - READ ALL
    // ============================================

    public Cursor getAllIngredients() {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " +
                        TABLE_INGREDIENTS +
                        " ORDER BY " +
                        COLUMN_ID +
                        " DESC",
                null
        );
    }


    // ============================================
    // PANTRY - READ ONE
    // ============================================

    public Cursor getIngredientById(int id) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.query(
                TABLE_INGREDIENTS,
                null,
                COLUMN_ID + " = ?",
                new String[]{
                        String.valueOf(id)
                },
                null,
                null,
                null
        );
    }


    // ============================================
    // PANTRY - UPDATE
    // ============================================

    public boolean updateIngredient(
            int id,
            String name,
            double quantity,
            String unit,
            String expiryDate) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(COLUMN_NAME, name);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_UNIT, unit);
        values.put(COLUMN_EXPIRY_DATE, expiryDate);

        int result = db.update(
                TABLE_INGREDIENTS,
                values,
                COLUMN_ID + " = ?",
                new String[]{
                        String.valueOf(id)
                }
        );

        db.close();

        return result > 0;
    }


    // ============================================
    // PANTRY - DELETE
    // ============================================

    public boolean deleteIngredient(int id) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        int result = db.delete(
                TABLE_INGREDIENTS,
                COLUMN_ID + " = ?",
                new String[]{
                        String.valueOf(id)
                }
        );

        db.close();

        return result > 0;
    }


    // ============================================
    // GET ALL RECIPES
    // ============================================

    public Cursor getAllRecipes() {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " +
                        TABLE_RECIPES +
                        " ORDER BY " +
                        RECIPE_NAME,
                null
        );
    }


    // ============================================
    // GET ONE RECIPE
    // ============================================

    public Cursor getRecipeById(int recipeId) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.query(
                TABLE_RECIPES,
                null,
                RECIPE_ID + " = ?",
                new String[]{
                        String.valueOf(recipeId)
                },
                null,
                null,
                null
        );
    }


    // ============================================
    // GET INGREDIENTS REQUIRED BY A RECIPE
    // ============================================

    public Cursor getRecipeIngredients(int recipeId) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.query(
                TABLE_RECIPE_INGREDIENTS,
                null,
                RI_RECIPE_ID + " = ?",
                new String[]{
                        String.valueOf(recipeId)
                },
                null,
                null,
                RI_ID
        );
    }


    // ============================================
    // ADD A RECIPE
    // ============================================

    private long addRecipe(
            SQLiteDatabase db,
            String name,
            String steps) {

        ContentValues values =
                new ContentValues();

        values.put(RECIPE_NAME, name);
        values.put(RECIPE_STEPS, steps);

        return db.insert(
                TABLE_RECIPES,
                null,
                values
        );
    }


    // ============================================
    // ADD REQUIRED INGREDIENT TO A RECIPE
    // ============================================

    private void addRecipeIngredient(
            SQLiteDatabase db,
            long recipeId,
            String ingredientName,
            double quantity,
            String unit) {

        ContentValues values =
                new ContentValues();

        values.put(
                RI_RECIPE_ID,
                recipeId
        );

        values.put(
                RI_INGREDIENT_NAME,
                ingredientName
        );

        values.put(
                RI_QUANTITY,
                quantity
        );

        values.put(
                RI_UNIT,
                unit
        );

        db.insert(
                TABLE_RECIPE_INGREDIENTS,
                null,
                values
        );
    }


    // ============================================
    // SEED DEFAULT RECIPES
    // ============================================

    private void seedRecipes(SQLiteDatabase db) {

        // Prevent duplicate recipes
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " +
                        TABLE_RECIPES,
                null
        );

        if (cursor.moveToFirst() &&
                cursor.getInt(0) > 0) {

            cursor.close();
            return;
        }

        cursor.close();


        // ----------------------------------------
        // 1. SCRAMBLED EGGS
        // ----------------------------------------

        long recipeId = addRecipe(
                db,
                "Scrambled Eggs",
                "1. Beat the eggs.\n" +
                        "2. Add the milk.\n" +
                        "3. Cook in a pan until set."
        );

        addRecipeIngredient(
                db,
                recipeId,
                "egg",
                2,
                "items"
        );

        addRecipeIngredient(
                db,
                recipeId,
                "milk",
                0.05,
                "litres"
        );


        // ----------------------------------------
        // 2. EGG TOAST
        // ----------------------------------------

        recipeId = addRecipe(
                db,
                "Egg Toast",
                "1. Cook the eggs.\n" +
                        "2. Toast the bread.\n" +
                        "3. Place the eggs on the toast."
        );

        addRecipeIngredient(
                db,
                recipeId,
                "egg",
                2,
                "items"
        );

        addRecipeIngredient(
                db,
                recipeId,
                "bread",
                2,
                "slices"
        );


        // ----------------------------------------
        // 3. TOMATO SALAD
        // ----------------------------------------

        recipeId = addRecipe(
                db,
                "Tomato Salad",
                "1. Chop the tomatoes and onion.\n" +
                        "2. Mix together and serve."
        );

        addRecipeIngredient(
                db,
                recipeId,
                "tomato",
                2,
                "items"
        );

        addRecipeIngredient(
                db,
                recipeId,
                "onion",
                1,
                "items"
        );


        // ----------------------------------------
        // 4. EGG FRIED RICE
        // ----------------------------------------

        recipeId = addRecipe(
                db,
                "Egg Fried Rice",
                "1. Cook the egg.\n" +
                        "2. Add cooked rice.\n" +
                        "3. Stir together until hot."
        );

        addRecipeIngredient(
                db,
                recipeId,
                "rice",
                0.25,
                "kg"
        );

        addRecipeIngredient(
                db,
                recipeId,
                "egg",
                2,
                "items"
        );


        // ----------------------------------------
        // 5. CHICKEN AND RICE
        // ----------------------------------------

        recipeId = addRecipe(
                db,
                "Chicken and Rice",
                "1. Cook the chicken thoroughly.\n" +
                        "2. Cook the rice.\n" +
                        "3. Serve together."
        );

        addRecipeIngredient(
                db,
                recipeId,
                "chicken",
                0.25,
                "kg"
        );

        addRecipeIngredient(
                db,
                recipeId,
                "rice",
                0.20,
                "kg"
        );


        // ----------------------------------------
        // 6. POTATO AND ONION FRY
        // ----------------------------------------

        recipeId = addRecipe(
                db,
                "Potato and Onion Fry",
                "1. Slice the potatoes and onion.\n" +
                        "2. Cook until the potatoes are tender."
        );

        addRecipeIngredient(
                db,
                recipeId,
                "potato",
                3,
                "items"
        );

        addRecipeIngredient(
                db,
                recipeId,
                "onion",
                1,
                "items"
        );


        // ----------------------------------------
        // 7. BANANA MILK
        // ----------------------------------------

        recipeId = addRecipe(
                db,
                "Banana Milk",
                "1. Peel the banana.\n" +
                        "2. Blend the banana with milk.\n" +
                        "3. Serve immediately."
        );

        addRecipeIngredient(
                db,
                recipeId,
                "banana",
                1,
                "items"
        );

        addRecipeIngredient(
                db,
                recipeId,
                "milk",
                0.25,
                "litres"
        );


        // ----------------------------------------
        // 8. CHEESE TOAST
        // ----------------------------------------

        recipeId = addRecipe(
                db,
                "Cheese Toast",
                "1. Place cheese on the bread.\n" +
                        "2. Toast until the cheese melts."
        );

        addRecipeIngredient(
                db,
                recipeId,
                "bread",
                2,
                "slices"
        );

        addRecipeIngredient(
                db,
                recipeId,
                "cheese",
                2,
                "slices"
        );


        // ----------------------------------------
        // 9. TOMATO CHEESE TOAST
        // ----------------------------------------

        recipeId = addRecipe(
                db,
                "Tomato Cheese Toast",
                "1. Slice the tomato.\n" +
                        "2. Place tomato and cheese on bread.\n" +
                        "3. Toast until warm."
        );

        addRecipeIngredient(
                db,
                recipeId,
                "bread",
                2,
                "slices"
        );

        addRecipeIngredient(
                db,
                recipeId,
                "tomato",
                1,
                "items"
        );

        addRecipeIngredient(
                db,
                recipeId,
                "cheese",
                2,
                "slices"
        );


        // ----------------------------------------
        // 10. BOILED EGGS
        // ----------------------------------------

        recipeId = addRecipe(
                db,
                "Boiled Eggs",
                "1. Place the eggs in water.\n" +
                        "2. Boil until cooked.\n" +
                        "3. Cool and peel."
        );

        addRecipeIngredient(
                db,
                recipeId,
                "egg",
                2,
                "items"
        );


        // ----------------------------------------
        // 11. MASHED POTATO
        // ----------------------------------------

        recipeId = addRecipe(
                db,
                "Mashed Potato",
                "1. Boil the potatoes.\n" +
                        "2. Add milk.\n" +
                        "3. Mash until smooth."
        );

        addRecipeIngredient(
                db,
                recipeId,
                "potato",
                3,
                "items"
        );

        addRecipeIngredient(
                db,
                recipeId,
                "milk",
                0.10,
                "litres"
        );


        // ----------------------------------------
        // 12. BANANA TOAST
        // ----------------------------------------

        recipeId = addRecipe(
                db,
                "Banana Toast",
                "1. Toast the bread.\n" +
                        "2. Slice the banana.\n" +
                        "3. Place banana on the toast."
        );

        addRecipeIngredient(
                db,
                recipeId,
                "bread",
                2,
                "slices"
        );

        addRecipeIngredient(
                db,
                recipeId,
                "banana",
                1,
                "items"
        );


        // ----------------------------------------
        // 13. CHICKEN POTATO
        // ----------------------------------------

        recipeId = addRecipe(
                db,
                "Chicken and Potato",
                "1. Cut the chicken and potatoes.\n" +
                        "2. Cook the chicken thoroughly.\n" +
                        "3. Add potatoes and cook until tender."
        );

        addRecipeIngredient(
                db,
                recipeId,
                "chicken",
                0.25,
                "kg"
        );

        addRecipeIngredient(
                db,
                recipeId,
                "potato",
                2,
                "items"
        );


        // ----------------------------------------
        // 14. TOMATO RICE
        // ----------------------------------------

        recipeId = addRecipe(
                db,
                "Tomato Rice",
                "1. Chop the tomato.\n" +
                        "2. Add it to cooked rice.\n" +
                        "3. Mix and heat thoroughly."
        );

        addRecipeIngredient(
                db,
                recipeId,
                "rice",
                0.20,
                "kg"
        );

        addRecipeIngredient(
                db,
                recipeId,
                "tomato",
                2,
                "items"
        );


        // ----------------------------------------
        // 15. CHEESE OMELETTE
        // ----------------------------------------

        recipeId = addRecipe(
                db,
                "Cheese Omelette",
                "1. Beat the eggs.\n" +
                        "2. Cook in a pan.\n" +
                        "3. Add cheese and fold the omelette."
        );

        addRecipeIngredient(
                db,
                recipeId,
                "egg",
                2,
                "items"
        );

        addRecipeIngredient(
                db,
                recipeId,
                "cheese",
                2,
                "slices"
        );


        // ----------------------------------------
        // 16. TOMATO OMELETTE
        // ----------------------------------------

        recipeId = addRecipe(
                db,
                "Tomato Omelette",
                "1. Beat the eggs.\n" +
                        "2. Chop the tomato.\n" +
                        "3. Cook together in a pan."
        );

        addRecipeIngredient(
                db,
                recipeId,
                "egg",
                2,
                "items"
        );

        addRecipeIngredient(
                db,
                recipeId,
                "tomato",
                1,
                "items"
        );


        // ----------------------------------------
        // 17. CHICKEN TOMATO RICE
        // ----------------------------------------

        recipeId = addRecipe(
                db,
                "Chicken Tomato Rice",
                "1. Cook the chicken thoroughly.\n" +
                        "2. Add chopped tomato.\n" +
                        "3. Serve with cooked rice."
        );

        addRecipeIngredient(
                db,
                recipeId,
                "chicken",
                0.25,
                "kg"
        );

        addRecipeIngredient(
                db,
                recipeId,
                "tomato",
                1,
                "items"
        );

        addRecipeIngredient(
                db,
                recipeId,
                "rice",
                0.20,
                "kg"
        );


        // ----------------------------------------
        // 18. POTATO OMELETTE
        // ----------------------------------------

        recipeId = addRecipe(
                db,
                "Potato Omelette",
                "1. Cook the potato until soft.\n" +
                        "2. Beat the eggs.\n" +
                        "3. Combine and cook in a pan."
        );

        addRecipeIngredient(
                db,
                recipeId,
                "potato",
                2,
                "items"
        );

        addRecipeIngredient(
                db,
                recipeId,
                "egg",
                2,
                "items"
        );


        // ----------------------------------------
        // 19. BANANA AND MILK BOWL
        // ----------------------------------------

        recipeId = addRecipe(
                db,
                "Banana and Milk Bowl",
                "1. Slice the bananas.\n" +
                        "2. Place them in a bowl.\n" +
                        "3. Pour milk over the bananas."
        );

        addRecipeIngredient(
                db,
                recipeId,
                "banana",
                2,
                "items"
        );

        addRecipeIngredient(
                db,
                recipeId,
                "milk",
                0.20,
                "litres"
        );


        // ----------------------------------------
        // 20. CHEESY POTATO
        // ----------------------------------------

        recipeId = addRecipe(
                db,
                "Cheesy Potato",
                "1. Cook the potatoes until tender.\n" +
                        "2. Add cheese.\n" +
                        "3. Heat until the cheese melts."
        );

        addRecipeIngredient(
                db,
                recipeId,
                "potato",
                3,
                "items"
        );

        addRecipeIngredient(
                db,
                recipeId,
                "cheese",
                2,
                "slices"
        );
    }


    // ============================================
    // RECIPE AVAILABILITY AND COOKING
    // ============================================

    private static final double EPSILON = 0.0000001;

    // Stores one pantry ingredient batch.
    // Quantities are converted to base units internally.
    private static class PantryStock {

        int id;
        double quantity;
        Date expiry;
        double conversionFactor;

        PantryStock(int id, double quantity,
                    Date expiry, double conversionFactor) {

            this.id = id;
            this.quantity = quantity;
            this.expiry = expiry;
            this.conversionFactor = conversionFactor;
        }
    }

    // ============================================
    // NORMALIZE INGREDIENT NAMES
    // ============================================

    private String normalizeIngredientName(String name) {

        if (name == null) {
            return "";
        }

        String result = name.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");

        // Common irregular plural names
        if (result.equals("tomatoes")) {
            return "tomato";
        }

        if (result.equals("potatoes")) {
            return "potato";
        }

        if (result.equals("loaves")) {
            return "loaf";
        }

        // Common regular plurals
        if (result.equals("eggs")) {
            return "egg";
        }

        if (result.equals("bananas")) {
            return "banana";
        }

        if (result.equals("onions")) {
            return "onion";
        }

        if (result.equals("carrots")) {
            return "carrot";
        }

        // General plural handling
        if (result.endsWith("s")
                && result.length() > 3
                && !result.endsWith("ss")) {

            result = result.substring(
                    0,
                    result.length() - 1
            );
        }

        return result;
    }

    // ============================================
    // NORMALIZE MEASUREMENT UNITS
    // ============================================

    private String normalizeUnit(String unit) {

        if (unit == null) {
            return "";
        }

        String result = unit.trim()
                .toLowerCase(Locale.ROOT);

        switch (result) {

            case "kg":
            case "kgs":
            case "kilogram":
            case "kilograms":
            case "g":
            case "gram":
            case "grams":
                return "g";

            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
            case "ml":
            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
                return "ml";

            case "item":
            case "items":
            case "piece":
            case "pieces":
            case "pcs":
                return "items";

            case "slice":
            case "slices":
                return "slices";

            default:
                return result;
        }
    }

    // ============================================
    // CONVERT QUANTITY INTO BASE UNITS
    // ============================================

    private double convertQuantity(double quantity,
                                   String unit) {

        if (unit == null) {
            return quantity;
        }

        String result = unit.trim()
                .toLowerCase(Locale.ROOT);

        switch (result) {

            case "kg":
            case "kgs":
            case "kilogram":
            case "kilograms":
                return quantity * 1000.0;

            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return quantity * 1000.0;

            default:
                return quantity;
        }
    }

    // ============================================
    // CREATE MATCHING KEY
    // ============================================

    private String ingredientKey(String name,
                                 String unit) {

        return normalizeIngredientName(name)
                + "\u0001"
                + normalizeUnit(unit);
    }

    // ============================================
    // PARSE EXPIRY DATE
    // ============================================

    private Date parseExpiry(String value) {

        if (value == null) {
            return null;
        }

        SimpleDateFormat format =
                new SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.US
                );

        format.setLenient(false);

        try {
            return format.parse(value.trim());

        } catch (ParseException e) {
            return null;
        }
    }

    // ============================================
    // GET RECIPE REQUIREMENTS
    // ============================================

    private Map<String, Double> getRequirements(
            SQLiteDatabase db, int recipeId) {

        Map<String, Double> requirements =
                new HashMap<>();

        Cursor cursor = db.query(
                TABLE_RECIPE_INGREDIENTS,
                new String[]{
                        RI_INGREDIENT_NAME,
                        RI_QUANTITY,
                        RI_UNIT
                },
                RI_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                null
        );

        try {

            while (cursor.moveToNext()) {

                String name = cursor.getString(0);
                double quantity = cursor.getDouble(1);
                String unit = cursor.getString(2);

                String key = ingredientKey(name, unit);

                double convertedQuantity =
                        convertQuantity(quantity, unit);

                double previous =
                        requirements.containsKey(key)
                                ? requirements.get(key)
                                : 0;

                requirements.put(
                        key,
                        previous + convertedQuantity
                );
            }

        } finally {
            cursor.close();
        }

        return requirements;
    }

    // ============================================
    // GET USABLE PANTRY STOCK
    // ============================================

    private Map<String, ArrayList<PantryStock>>
    getUsableStock(SQLiteDatabase db) {

        Map<String, ArrayList<PantryStock>> stock =
                new HashMap<>();

        SimpleDateFormat format =
                new SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.US
                );

        Date today =
                parseExpiry(format.format(new Date()));

        Cursor cursor = db.query(
                TABLE_INGREDIENTS,
                new String[]{
                        COLUMN_ID,
                        COLUMN_NAME,
                        COLUMN_QUANTITY,
                        COLUMN_UNIT,
                        COLUMN_EXPIRY_DATE
                },
                null,
                null,
                null,
                null,
                null
        );

        try {

            while (cursor.moveToNext()) {

                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                double quantity = cursor.getDouble(2);
                String unit = cursor.getString(3);
                String expiryDate = cursor.getString(4);

                Date expiry = parseExpiry(expiryDate);

                // Ignore expired or empty stock
                if (expiry == null
                        || expiry.before(today)
                        || quantity <= 0) {
                    continue;
                }

                String key = ingredientKey(name, unit);

                double convertedQuantity =
                        convertQuantity(quantity, unit);

                // Conversion factor is used when saving
                // the remaining quantity back to SQLite.
                double conversionFactor =
                        convertQuantity(1.0, unit);

                if (!stock.containsKey(key)) {
                    stock.put(key, new ArrayList<>());
                }

                stock.get(key).add(
                        new PantryStock(
                                id,
                                convertedQuantity,
                                expiry,
                                conversionFactor
                        )
                );
            }

        } finally {
            cursor.close();
        }

        // Use earliest-expiring stock first
        for (ArrayList<PantryStock> batches
                : stock.values()) {

            Collections.sort(
                    batches,
                    (a, b) -> {
                        int comparison =
                                a.expiry.compareTo(b.expiry);

                        if (comparison != 0) {
                            return comparison;
                        }

                        return Integer.compare(a.id, b.id);
                    }
            );
        }

        return stock;
    }

    // ============================================
    // FORMAT QUANTITY
    // ============================================

    private String formatQuantity(double value) {

        return String.format(
                        Locale.US,
                        "%.3f",
                        value
                ).replaceAll("0+$", "")
                .replaceAll("\\.$", "");
    }

    // ============================================
    // GET MISSING INGREDIENTS
    // ============================================

    public String getMissingIngredients(int recipeId) {

        SQLiteDatabase db = getReadableDatabase();

        Map<String, Double> requirements =
                getRequirements(db, recipeId);

        Map<String, ArrayList<PantryStock>> stock =
                getUsableStock(db);

        if (requirements.isEmpty()) {
            return "Recipe ingredients are unavailable.";
        }

        StringBuilder missing = new StringBuilder();

        for (Map.Entry<String, Double> requirement
                : requirements.entrySet()) {

            String key = requirement.getKey();
            double needed = requirement.getValue();
            double available = 0;

            ArrayList<PantryStock> batches =
                    stock.get(key);

            if (batches != null) {

                for (PantryStock batch : batches) {
                    available += batch.quantity;
                }
            }

            if (available + EPSILON < needed) {

                String[] parts = key.split("\u0001", 2);

                missing.append("• ")
                        .append(parts[0])
                        .append(": need ")
                        .append(
                                formatQuantity(
                                        needed - available
                                )
                        )
                        .append(" more ")
                        .append(parts[1])
                        .append("\n");
            }
        }

        return missing.toString();
    }

    // ============================================
    // MAKE RECIPE
    // ============================================

    public boolean makeRecipe(int recipeId) {

        SQLiteDatabase db = getWritableDatabase();

        // All changes happen in one transaction
        db.beginTransaction();

        try {

            Map<String, Double> requirements =
                    getRequirements(db, recipeId);

            if (requirements.isEmpty()) {
                return false;
            }

            Map<String, ArrayList<PantryStock>> stock =
                    getUsableStock(db);

            // ========================================
            // CHECK ALL INGREDIENTS FIRST
            // ========================================

            for (Map.Entry<String, Double> requirement
                    : requirements.entrySet()) {

                double available = 0;

                ArrayList<PantryStock> batches =
                        stock.get(requirement.getKey());

                if (batches != null) {

                    for (PantryStock batch : batches) {
                        available += batch.quantity;
                    }
                }

                if (available + EPSILON
                        < requirement.getValue()) {

                    return false;
                }
            }

            // ========================================
            // DEDUCT INGREDIENT QUANTITIES
            // ========================================

            for (Map.Entry<String, Double> requirement
                    : requirements.entrySet()) {

                double remaining =
                        requirement.getValue();

                ArrayList<PantryStock> batches =
                        stock.get(requirement.getKey());

                for (PantryStock batch : batches) {

                    if (remaining <= EPSILON) {
                        break;
                    }

                    double used =
                            Math.min(
                                    batch.quantity,
                                    remaining
                            );

                    double newQuantity =
                            batch.quantity - used;

                    if (newQuantity <= EPSILON) {

                        // Delete the ingredient when empty
                        int deleted = db.delete(
                                TABLE_INGREDIENTS,
                                COLUMN_ID + "=?",
                                new String[]{
                                        String.valueOf(batch.id)
                                }
                        );

                        if (deleted != 1) {
                            return false;
                        }

                    } else {

                        // Convert back to the original unit
                        double originalUnitQuantity =
                                newQuantity
                                        / batch.conversionFactor;

                        ContentValues values =
                                new ContentValues();

                        values.put(
                                COLUMN_QUANTITY,
                                originalUnitQuantity
                        );

                        int updated = db.update(
                                TABLE_INGREDIENTS,
                                values,
                                COLUMN_ID + "=?",
                                new String[]{
                                        String.valueOf(batch.id)
                                }
                        );

                        if (updated != 1) {
                            return false;
                        }
                    }

                    remaining -= used;
                }
            }

            db.setTransactionSuccessful();

            return true;

        } catch (Exception e) {

            android.util.Log.e(
                    "DatabaseHelper",
                    "Could not make recipe",
                    e
            );

            return false;

        } finally {

            db.endTransaction();
        }
    }


}
