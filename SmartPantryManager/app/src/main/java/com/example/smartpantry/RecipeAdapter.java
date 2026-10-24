package com.example.smartpantry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class RecipeAdapter extends BaseAdapter {
    private final Context context;
    private final List<Recipe> recipes;
    public RecipeAdapter(Context context, List<Recipe> recipes) { this.context = context; this.recipes = recipes; }
    public int getCount() { return recipes.size(); }
    public Recipe getItem(int position) { return recipes.get(position); }
    public long getItemId(int position) { return getItem(position).id; }
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) view = LayoutInflater.from(context).inflate(R.layout.row_recipe, parent, false);
        Recipe recipe = getItem(position);
        ((TextView)view.findViewById(R.id.recipeName)).setText(recipe.name);
        ((TextView)view.findViewById(R.id.recipeInfo)).setText(recipe.ingredients.size() + " ingredients - tap for method");
        return view;
    }
}
