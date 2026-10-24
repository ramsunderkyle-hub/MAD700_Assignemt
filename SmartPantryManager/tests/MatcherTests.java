package com.example.smartpantry;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// Run with a normal JDK. This tests the production matcher without Android stubs.
public class MatcherTests {
    static int passed;
    static final LocalDate TODAY = LocalDate.of(2026, 10, 24);
    static PantryItem item(String name, double quantity, String unit) {
        return new PantryItem(-1, name, quantity, unit, "");
    }
    static Recipe recipe(PantryItem... items) {
        Recipe recipe = new Recipe(1, "Test recipe", "Test method");
        recipe.ingredients.addAll(Arrays.asList(items)); return recipe;
    }
    static void check(String name, boolean condition) {
        if (!condition) throw new AssertionError(name);
        System.out.println("PASS: " + name); passed++;
    }
    static boolean match(Recipe recipe, PantryItem... pantry) {
        return RecipeMatcher.canMake(recipe, Arrays.asList(pantry), TODAY);
    }
    public static void main(String[] args) {
        Recipe scramble = recipe(item("egg", 2, "piece"), item("tomato", 1, "piece"), item("oil", 5, "ml"));
        check("Missing one required ingredient is excluded", !match(scramble, item("egg",2,"piece"),item("tomato",1,"piece")));
        check("All ingredients at exact quantity qualify", match(scramble,item("egg",2,"piece"),item("tomato",1,"piece"),item("oil",5,"ml")));
        check("Insufficient quantity is excluded", !match(scramble,item("egg",1,"piece"),item("tomato",1,"piece"),item("oil",5,"ml")));
        check("Empty pantry is excluded", !match(scramble));
        check("Empty recipe is excluded", !match(recipe(),item("egg",1,"piece")));
        check("Plural and case normalize", match(scramble,item(" EGGS ",2,"piece"),item("Tomatoes",1,"piece"),item("oil",5,"ml")));
        check("Extra pantry ingredients are allowed", match(scramble,item("egg",3,"piece"),item("tomato",2,"piece"),item("oil",10,"ml"),item("apple",1,"piece")));
        check("Kilograms convert to grams", match(recipe(item("cheese",500,"g")),item("cheese",0.5,"kg")));
        check("Grams convert to kilograms", match(recipe(item("cheese",0.5,"kg")),item("cheese",500,"g")));
        check("Litres convert to millilitres", match(recipe(item("milk",250,"ml")),item("milk",0.25,"l")));
        check("Millilitres convert to litres", match(recipe(item("milk",0.25,"l")),item("milk",250,"ml")));
        check("Teaspoons convert to millilitres", match(recipe(item("oil",5,"ml")),item("oil",1,"tsp")));
        check("Tablespoons convert to millilitres", match(recipe(item("oil",15,"ml")),item("oil",1,"tbsp")));
        check("Metric cup converts to millilitres", match(recipe(item("milk",250,"ml")),item("milk",1,"cup")));
        check("Mass cannot replace volume", !match(recipe(item("milk",250,"ml")),item("milk",250,"g")));
        check("Count cannot replace mass", !match(recipe(item("tomato",100,"g")),item("tomato",100,"piece")));
        check("Batches add together", match(recipe(item("milk",250,"ml")),item("milk",100,"ml"),item("milk",0.15,"l")));
        check("Incompatible batches do not add together", !match(recipe(item("milk",250,"ml")),item("milk",100,"ml"),item("milk",150,"g")));
        PantryItem expired = item("milk",250,"ml"); expired.expiry="2026-10-23";
        check("Expired food is excluded", !match(recipe(item("milk",250,"ml")),expired));
        expired.expiry="2026-10-24";
        check("Food expiring today is included", match(recipe(item("milk",250,"ml")),expired));
        expired.expiry="2026-10-25";
        check("Future expiry is included", match(recipe(item("milk",250,"ml")),expired));
        expired.expiry="2026-10-23";
        check("Expired batch cannot top up a shortage", !match(recipe(item("milk",250,"ml")),expired,item("milk",100,"ml")));
        check("Duplicate recipe requirements are totalled", !match(recipe(item("milk",150,"ml"),item("milk",150,"ml")),item("milk",250,"ml")));
        check("Duplicate requirements with enough stock qualify", match(recipe(item("milk",150,"ml"),item("milk",150,"ml")),item("milk",300,"ml")));
        check("Yogurt spelling alias", match(recipe(item("yoghurt",100,"g")),item("yogurt",100,"g")));
        check("Bread slice alias", match(recipe(item("bread",2,"piece")),item("bread slices",2,"piece")));
        check("Repeated spaces normalize", IngredientRules.normalize("  peanut   butter  ").equals("peanut butter"));
        check("Different ingredients stay different", !match(recipe(item("rice",100,"g")),item("pasta",100,"g")));
        check("Floating point boundary tolerates roundoff", match(recipe(item("milk",0.3,"ml")),item("milk",0.1,"ml"),item("milk",0.2,"ml")));
        check("Small real shortage is rejected", !match(recipe(item("milk",250,"ml")),item("milk",249.999,"ml")));
        check("Blank name rejected", IngredientRules.validate(" ",2,"g") != null);
        check("Digits only name rejected", IngredientRules.validate("123",2,"g") != null);
        check("Zero quantity rejected", IngredientRules.validate("rice",0,"g") != null);
        check("Negative quantity rejected", IngredientRules.validate("rice",-1,"g") != null);
        check("NaN rejected", IngredientRules.validate("rice",Double.NaN,"g") != null);
        check("Infinity rejected", IngredientRules.validate("rice",Double.POSITIVE_INFINITY,"g") != null);
        check("Excessive quantity rejected", IngredientRules.validate("rice",1000001,"g") != null);
        check("Unknown unit rejected", IngredientRules.validate("rice",1,"bag") != null);
        check("Long name rejected", IngredientRules.validate(String.join("", Collections.nCopies(61,"a")),1,"g") != null);
        check("Valid entry accepted", IngredientRules.validate("Tomatoes",2,"piece") == null);
        System.out.println("\n" + passed + " checks passed.");
    }
}
