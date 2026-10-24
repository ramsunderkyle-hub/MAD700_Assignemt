package com.example.smartpantry;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class PantryAdapter extends BaseAdapter {
    public interface Actions { void edit(PantryItem item); void delete(PantryItem item); }
    private final Context context;
    private final List<PantryItem> items;
    private final Actions actions;
    public PantryAdapter(Context context, List<PantryItem> items, Actions actions) {
        this.context = context; this.items = items; this.actions = actions;
    }
    public int getCount() { return items.size(); }
    public PantryItem getItem(int position) { return items.get(position); }
    public long getItemId(int position) { return getItem(position).id; }
    @Override public View getView(int position, View view, ViewGroup parent) {
        if (view == null) view = LayoutInflater.from(context).inflate(R.layout.row_pantry, parent, false);
        PantryItem item = getItem(position);
        ((TextView)view.findViewById(R.id.itemName)).setText(item.name);
        ((TextView)view.findViewById(R.id.itemAmount)).setText(IngredientRules.amount(item.quantity) + " " + item.unit);
        TextView expiry = view.findViewById(R.id.itemExpiry);
        expiry.setTextColor(Color.DKGRAY); // Reset recycled rows before applying their current state.
        String label = "No expiry date";
        if (item.expiry != null && !item.expiry.isEmpty()) {
            long days = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(item.expiry));
            label = "Expires " + item.expiry;
            if (days < 0) { label += " - expired (not used)"; expiry.setTextColor(Color.rgb(160,30,30)); }
            else if (days <= 3 && context.getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("expiry_alerts", true)) {
                label += " - expiring soon"; expiry.setTextColor(Color.rgb(130,80,0));
            }
        }
        expiry.setText(label);
        view.findViewById(R.id.editItem).setOnClickListener(v -> actions.edit(item));
        view.findViewById(R.id.deleteItem).setOnClickListener(v -> actions.delete(item));
        return view;
    }
}
