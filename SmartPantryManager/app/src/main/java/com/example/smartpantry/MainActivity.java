package com.example.smartpantry;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class MainActivity extends BaseActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_main); setNavigation();
        findViewById(R.id.addIngredient).setOnClickListener(v -> startActivity(new Intent(this, IngredientActivity.class)));
    }
    // Refresh when returning from Add/Edit or Settings; data is read from SQLite each time.
    @Override protected void onResume() { super.onResume(); refresh(); }
    private void refresh() {
        List<PantryItem> items = database.getPantry();
        ListView list = findViewById(R.id.pantryList);
        list.setEmptyView(findViewById(R.id.pantryEmpty));
        list.setAdapter(new PantryAdapter(this, items, new PantryAdapter.Actions() {
            public void edit(PantryItem item) {
                Intent intent = new Intent(MainActivity.this, IngredientActivity.class);
                intent.putExtra("item_id", item.id); startActivity(intent);
            }
            public void delete(PantryItem item) {
                new AlertDialog.Builder(MainActivity.this).setTitle("Delete ingredient?")
                    .setMessage("Remove " + item.name + " from your pantry?")
                    .setNegativeButton("Cancel", null).setPositiveButton("Delete", (dialog, which) -> {
                        boolean removed = database.delete(item.id);
                        Toast.makeText(MainActivity.this, removed ? "Ingredient deleted" : "Item no longer exists", Toast.LENGTH_SHORT).show(); refresh();
                    }).show();
            }
        }));
        ((TextView)findViewById(R.id.pantrySummary)).setText(items.size() + " pantry batches | Tap Edit to update an item");
    }
}
