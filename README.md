Smart Pantry Manager

Description of the Project

An Android app called Smart Pantry Manager was created for the Mobile App Development 700 assignment.

Users may find recipes they can make, keep an eye on expiration dates, manage supplies in their pantry, and minimize food waste with the use of this program.

Features:

-Add, view, edit, and delete pantry ingredients.
-Store ingredient names, quantities, units, and expiry dates.
-Save pantry information using SQLite.
-Access 20 preloaded recipes.
-Suggest recipes only when all required ingredients are available in sufficient quantities.
-Match common singular and plural ingredient names.
-Support common measurement conversions such as kilograms to grams and litres to millilitres.
-View recipe ingredients and preparation instructions.
-Make recipes and automatically deduct used ingredients from the pantry.
-Exclude expired ingredients from recipe matching.
-Access application settings, including dark mode preferences.

Technologies Used:

-Java
-XML
-Android Studio
-SQLite
-SQLiteOpenHelper
-Git and GitHub

Database Implementation:

The application uses SQLite for local data storage.
The database contains three main tables:
ingredients — Stores pantry ingredient details, including quantity, unit, and expiry date.
recipes — Stores recipe names and preparation instructions.
recipe_ingredients — Stores the ingredients and quantities required for each recipe.
SQLite was selected because it provides persistent local storage without requiring an internet connection or external database server.


How to Run the Application:

1. Download the repository using Code → Download ZIP.
2. Extract the downloaded ZIP file.
3. Open Android Studio.
4. Select Open and choose the extracted project folder.
5. Allow Gradle to synchronize.
6. Start an Android emulator or connect an Android device with USB debugging enabled.
7. Click Run to launch the application.
8. The database and preloaded recipes are created automatically when the application initializes.

How to Use:

-Open Smart Pantry Manager.
-Select Add Ingredient to enter pantry items.
-Open View My Pantry to review or manage ingredients.
-Select Suggested Recipes to see recipes that can be prepared using available ingredients.
-Select a recipe to view its details and instructions.
-Press Make Recipe to deduct the required ingredients from the pantry.
-Use Settings to adjust available application preferences.
