package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends BaseActivity {
    @Override protected void onCreate(Bundle state) { super.onCreate(state); setContentView(R.layout.activity_recipes); setNavigation(); }
    @Override protected void onResume() {
        super.onResume();
        List<PantryItem> pantry = database.getPantry();
        List<Recipe> matches = new ArrayList<>();
        for (Recipe recipe : database.getRecipes()) if (RecipeMatcher.canMake(recipe, pantry, LocalDate.now())) matches.add(recipe);
        ListView list = findViewById(R.id.recipeList);
        list.setEmptyView(findViewById(R.id.recipesEmpty)); list.setAdapter(new RecipeAdapter(this, matches));
        list.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra("recipe_id", matches.get(position).id); startActivity(intent);
        });
        ((TextView)findViewById(R.id.recipeSummary)).setText(matches.size() + " of 20 recipes available");
    }
}
