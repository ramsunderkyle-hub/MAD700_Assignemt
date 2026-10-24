package com.example.smartpantry;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeMatcher {
    private static String key(PantryItem item) {
        return IngredientRules.normalize(item.name) + "|" + IngredientRules.dimension(item.unit);
    }
    public static boolean canMake(Recipe recipe, List<PantryItem> pantry, LocalDate today) {
        if (recipe.ingredients.isEmpty()) return false;
        Map<String, Double> available = new HashMap<>();
        Map<String, Double> required = new HashMap<>();
        // Combine separate unexpired batches, but never convert grams into millilitres.
        for (PantryItem item : pantry) {
            if (!IngredientRules.expired(item, today)) {
                available.merge(key(item), item.quantity * IngredientRules.factor(item.unit), Double::sum);
            }
        }
        for (PantryItem item : recipe.ingredients) {
            required.merge(key(item), item.quantity * IngredientRules.factor(item.unit), Double::sum);
        }
        for (String ingredient : required.keySet()) {
            double need = required.get(ingredient);
            double have = available.getOrDefault(ingredient, 0.0);
            // A tiny tolerance handles binary floating point rounding only.
            if (have + 1e-9 < need) return false;
        }
        return true;
    }
}
