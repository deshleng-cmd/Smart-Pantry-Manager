
package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    // Expiry alert switch
    private Switch switchExpiryAlerts;

    // Dark Mode switch
    private Switch switchDarkMode;

    // App Information TextViews
    private TextView txtAppVersion;
    private TextView txtAppBuild;

    // Used to save the user's settings
    private SharedPreferences preferences;

    private static final String PREFS_NAME =
            "SmartPantrySettings";

    private static final String KEY_EXPIRY_ALERTS =
            "expiryAlerts";

    private static final String KEY_DARK_MODE =
            "darkMode";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);


        // ============================================
        // CONNECT XML COMPONENTS
        // ============================================

        // Connect switches
        switchExpiryAlerts =
                findViewById(R.id.switchExpiryAlerts);

        switchDarkMode =
                findViewById(R.id.switchDarkMode);

        // Connect App Information TextViews
        txtAppVersion =
                findViewById(R.id.txtAppVersion);

        txtAppBuild =
                findViewById(R.id.txtAppBuild);


        // ============================================
        // SHARED PREFERENCES
        // ============================================

        // Open the saved settings
        preferences = getSharedPreferences(
                PREFS_NAME,
                MODE_PRIVATE
        );


        // ============================================
        // EXPIRY ALERTS
        // ============================================

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


        // ============================================
        // DARK MODE
        // ============================================

        // Load the saved Dark Mode setting
        boolean darkModeEnabled =
                preferences.getBoolean(
                        KEY_DARK_MODE,
                        false
                );

        // Set the switch to the saved value
        switchDarkMode.setChecked(darkModeEnabled);

        // Detect when the user changes Dark Mode
        switchDarkMode.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    // Save the user's choice
                    preferences.edit()
                            .putBoolean(
                                    KEY_DARK_MODE,
                                    isChecked
                            )
                            .apply();

                    // Change the application theme
                    if (isChecked) {

                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_YES
                        );

                    } else {

                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_NO
                        );
                    }
                }
        );


        // ============================================
        // APP INFORMATION
        // ============================================

        try {

            // Get application package information
            PackageInfo packageInfo =
                    getPackageManager().getPackageInfo(
                            getPackageName(),
                            0
                    );

            // Get application version
            String version = packageInfo.versionName;

            // Get application build number
            long buildNumber;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                buildNumber = packageInfo.getLongVersionCode();

            } else {

                buildNumber = packageInfo.versionCode;
            }

            // Display version and build number
            txtAppVersion.setText(
                    "Version: " + version
            );

            txtAppBuild.setText(
                    "Build: " + buildNumber
            );

        } catch (PackageManager.NameNotFoundException e) {

            // Display fallback information
            txtAppVersion.setText(
                    "Version: Unknown"
            );

            txtAppBuild.setText(
                    "Build: Unknown"
            );
        }
    }
}

