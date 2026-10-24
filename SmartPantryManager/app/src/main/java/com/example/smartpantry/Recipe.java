package com.example.smartpantry;

import java.util.ArrayList;
import java.util.List;
public class Recipe {
    public long id;
    public String name, steps;
    public List<PantryItem> ingredients = new ArrayList<>();
    public Recipe(long id, String name, String steps) {
        this.id = id; this.name = name; this.steps = steps;
    }
}
