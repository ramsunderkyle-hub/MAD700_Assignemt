package com.example.smartpantry;

import android.os.Bundle;
import android.widget.Switch;
public class SettingsActivity extends BaseActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_settings); setNavigation();
        Switch alerts = findViewById(R.id.expirySwitch);
        alerts.setChecked(getSharedPreferences("settings", MODE_PRIVATE).getBoolean("expiry_alerts", true));
        alerts.setOnCheckedChangeListener((button, checked) -> getSharedPreferences("settings", MODE_PRIVATE).edit().putBoolean("expiry_alerts", checked).apply());
    }
}
