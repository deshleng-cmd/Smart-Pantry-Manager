package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    // Expiry alert switch
    private Switch switchExpiryAlerts;

    // Used to save the user's setting
    private SharedPreferences preferences;

    private static final String PREFS_NAME =
            "SmartPantrySettings";

    private static final String KEY_EXPIRY_ALERTS =
            "expiryAlerts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        // Connect the switch to the XML
        switchExpiryAlerts =
                findViewById(R.id.switchExpiryAlerts);

        // Open the saved settings
        preferences = getSharedPreferences(
                PREFS_NAME,
                MODE_PRIVATE
        );

        // Load the saved setting
        // Default value is true, so alerts start ON
        boolean alertsEnabled =
                preferences.getBoolean(
                        KEY_EXPIRY_ALERTS,
                        true
                );

        // Set the switch to the saved value
        switchExpiryAlerts.setChecked(alertsEnabled);

        // Detect when the user changes the switch
        switchExpiryAlerts.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    // Save the new setting
                    preferences.edit()
                            .putBoolean(
                                    KEY_EXPIRY_ALERTS,
                                    isChecked
                            )
                            .apply();

                    // Show confirmation message
                    if (isChecked) {

                        Toast.makeText(
                                SettingsActivity.this,
                                "Expiry alerts enabled",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        Toast.makeText(
                                SettingsActivity.this,
                                "Expiry alerts disabled",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }
}