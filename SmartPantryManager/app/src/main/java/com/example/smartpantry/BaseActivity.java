package com.example.smartpantry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public abstract class BaseActivity extends Activity {
    protected DatabaseHelper database;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        database = new DatabaseHelper(this);
    }
    protected void setNavigation() {
        findViewById(R.id.navPantry).setOnClickListener(v -> navigate(MainActivity.class));
        findViewById(R.id.navRecipes).setOnClickListener(v -> navigate(SuggestedRecipesActivity.class));
        findViewById(R.id.navSettings).setOnClickListener(v -> navigate(SettingsActivity.class));
    }
    protected void navigate(Class<?> target) {
        if (getClass() == target) return;
        Intent intent = new Intent(this, target);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
    @Override protected void onDestroy() { database.close(); super.onDestroy(); }
}
