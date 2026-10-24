package com.example.smartpantry;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import java.time.LocalDate;

public class RecipeDetailActivity extends BaseActivity {
    private Recipe recipe;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_detail); setNavigation();
        recipe = database.getRecipe(getIntent().getLongExtra("recipe_id", -1));
        if (recipe == null) { Toast.makeText(this,"Recipe not found",Toast.LENGTH_SHORT).show(); finish(); return; }
        ((TextView)findViewById(R.id.detailTitle)).setText(recipe.name);
        StringBuilder ingredients = new StringBuilder();
        for (PantryItem item : recipe.ingredients) ingredients.append("• ").append(item.name).append(" - ").append(IngredientRules.amount(item.quantity)).append(" ").append(item.unit).append("\n");
        ((TextView)findViewById(R.id.detailIngredients)).setText(ingredients.toString().trim());
        ((TextView)findViewById(R.id.detailSteps)).setText(recipe.steps);
        findViewById(R.id.backRecipes).setOnClickListener(v -> finish());
    }
    @Override protected void onResume() {
        super.onResume();
        if (recipe != null) ((TextView)findViewById(R.id.detailStatus)).setText(RecipeMatcher.canMake(recipe, database.getPantry(), LocalDate.now())
                ? "You have everything needed for this recipe." : "Your pantry has changed. This recipe is no longer available.");
    }
}
