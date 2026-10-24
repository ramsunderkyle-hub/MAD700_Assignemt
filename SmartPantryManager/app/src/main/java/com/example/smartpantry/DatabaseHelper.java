package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final Context context;
    public DatabaseHelper(Context context) {
        super(context.getApplicationContext(), "pantry.db", null, 1);
        this.context = context.getApplicationContext();
    }
    @Override public void onConfigure(SQLiteDatabase db) { db.setForeignKeyConstraintsEnabled(true); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry_items (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, quantity REAL NOT NULL CHECK(quantity > 0), unit TEXT NOT NULL, expiry TEXT)");
        db.execSQL("CREATE TABLE recipes (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE, steps TEXT NOT NULL)");
        db.execSQL("CREATE TABLE recipe_ingredients (id INTEGER PRIMARY KEY AUTOINCREMENT, recipe_id INTEGER NOT NULL REFERENCES recipes(id) ON DELETE CASCADE, name TEXT NOT NULL, quantity REAL NOT NULL CHECK(quantity > 0), unit TEXT NOT NULL)");
        db.execSQL("CREATE INDEX recipe_index ON recipe_ingredients(recipe_id)");
        // onCreate runs inside SQLiteOpenHelper's creation transaction: seed all or nothing.
        try (InputStream stream = context.getAssets().open("recipes.json")) {
            java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
            byte[] bytes = new byte[4096]; int length;
            while ((length = stream.read(bytes)) != -1) buffer.write(bytes, 0, length);
            JSONArray recipes = new JSONArray(buffer.toString(StandardCharsets.UTF_8.name()));
            for (int i = 0; i < recipes.length(); i++) {
                JSONObject recipe = recipes.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put("name", recipe.getString("name")); values.put("steps", recipe.getString("steps"));
                long id = db.insertOrThrow("recipes", null, values);
                JSONArray ingredients = recipe.getJSONArray("ingredients");
                for (int n = 0; n < ingredients.length(); n++) {
                    JSONObject item = ingredients.getJSONObject(n);
                    ContentValues row = new ContentValues();
                    row.put("recipe_id", id); row.put("name", IngredientRules.normalize(item.getString("name")));
                    row.put("quantity", item.getDouble("quantity")); row.put("unit", item.getString("unit"));
                    db.insertOrThrow("recipe_ingredients", null, row);
                }
            }
        } catch (Exception e) { throw new IllegalStateException("Could not load recipe collection", e); }
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Version 1 has no migrations. Future schema changes must preserve the pantry.
        throw new IllegalStateException("A database migration is required");
    }
    public long save(PantryItem item) {
        String error = IngredientRules.validate(item.name, item.quantity, item.unit);
        if (error != null) throw new IllegalArgumentException(error);
        if (item.expiry != null && !item.expiry.isEmpty()) java.time.LocalDate.parse(item.expiry);
        ContentValues values = new ContentValues();
        values.put("name", IngredientRules.normalize(item.name)); values.put("quantity", item.quantity);
        values.put("unit", item.unit); values.put("expiry", item.expiry);
        if (item.id == -1) return getWritableDatabase().insertOrThrow("pantry_items", null, values);
        return getWritableDatabase().update("pantry_items", values, "id = ?", new String[]{String.valueOf(item.id)}) == 1 ? item.id : -1;
    }
    public boolean delete(long id) {
        return getWritableDatabase().delete("pantry_items", "id = ?", new String[]{String.valueOf(id)}) == 1;
    }
    public List<PantryItem> getPantry() {
        List<PantryItem> items = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT id,name,quantity,unit,expiry FROM pantry_items ORDER BY name COLLATE NOCASE,id", null)) {
            while (cursor.moveToNext()) items.add(new PantryItem(cursor.getLong(0), cursor.getString(1), cursor.getDouble(2), cursor.getString(3), cursor.getString(4)));
        }
        return items;
    }
    public PantryItem getItem(long id) {
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT id,name,quantity,unit,expiry FROM pantry_items WHERE id = ?", new String[]{String.valueOf(id)})) {
            return cursor.moveToFirst() ? new PantryItem(cursor.getLong(0), cursor.getString(1), cursor.getDouble(2), cursor.getString(3), cursor.getString(4)) : null;
        }
    }
    public List<Recipe> getRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT id,name,steps FROM recipes ORDER BY name", null)) {
            while (cursor.moveToNext()) {
                Recipe recipe = new Recipe(cursor.getLong(0), cursor.getString(1), cursor.getString(2));
                try (Cursor ingredients = getReadableDatabase().rawQuery("SELECT name,quantity,unit FROM recipe_ingredients WHERE recipe_id = ? ORDER BY id", new String[]{String.valueOf(recipe.id)})) {
                    while (ingredients.moveToNext()) recipe.ingredients.add(new PantryItem(-1, ingredients.getString(0), ingredients.getDouble(1), ingredients.getString(2), null));
                }
                recipes.add(recipe);
            }
        }
        return recipes;
    }
    public Recipe getRecipe(long id) {
        for (Recipe recipe : getRecipes()) if (recipe.id == id) return recipe;
        return null;
    }
}
