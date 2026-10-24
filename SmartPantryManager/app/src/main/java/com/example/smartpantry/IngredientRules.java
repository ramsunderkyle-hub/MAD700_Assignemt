package com.example.smartpantry;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// One set of rules is used for both database names and recipe matching.
public class IngredientRules {
    public static final String[] UNITS = {"piece", "g", "kg", "ml", "l", "tsp", "tbsp", "cup"};
    private static final Map<String, String> ALIASES = new HashMap<>();
    static {
        String[][] groups = {
            {"tomato", "tomatoes"}, {"potato", "potatoes"},
            {"egg", "eggs"}, {"onion", "onions"}, {"carrot", "carrots"},
            {"banana", "bananas"}, {"apple", "apples"}, {"pepper", "peppers", "bell pepper", "bell peppers"},
            {"bread", "bread slice", "bread slices", "slice of bread", "slices of bread"},
            {"yoghurt", "yogurt"}, {"oat", "oats"}, {"bean", "beans"},
            {"garlic", "garlic clove", "garlic cloves"},
            {"pea", "peas"}, {"cucumber", "cucumbers"}
        };
        for (String[] group : groups) for (String alias : group) ALIASES.put(alias, group[0]);
    }
    public static String normalize(String name) {
        if (name == null) return "";
        String clean = name.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        return ALIASES.getOrDefault(clean, clean);
    }
    public static String dimension(String unit) {
        switch (unit) {
            case "g": case "kg": return "mass";
            case "ml": case "l": case "tsp": case "tbsp": case "cup": return "volume";
            case "piece": return "count";
            default: throw new IllegalArgumentException("Unsupported unit: " + unit);
        }
    }
    public static double factor(String unit) {
        switch (unit) {
            case "kg": case "l": return 1000;
            case "tsp": return 5;
            case "tbsp": return 15;
            case "cup": return 250;
            default: dimension(unit); return 1;
        }
    }
    public static boolean expired(PantryItem item, LocalDate today) {
        return item.expiry != null && !item.expiry.isEmpty()
                && LocalDate.parse(item.expiry).isBefore(today);
    }
    public static String validate(String name, double quantity, String unit) {
        if (normalize(name).isEmpty() || !name.matches(".*[\\p{L}].*")) return "Enter an ingredient name.";
        if (name.trim().length() > 60) return "Name must be 60 characters or less.";
        if (!Double.isFinite(quantity) || quantity <= 0 || quantity > 1000000)
            return "Enter a quantity above 0 and no more than 1000000.";
        try { dimension(unit); } catch (IllegalArgumentException e) { return e.getMessage(); }
        return null;
    }
    public static String amount(double quantity) {
        return java.math.BigDecimal.valueOf(quantity).stripTrailingZeros().toPlainString();
    }
}
