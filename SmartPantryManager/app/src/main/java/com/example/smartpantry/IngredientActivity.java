package com.example.smartpantry;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.time.LocalDate;
import java.util.Arrays;

public class IngredientActivity extends BaseActivity {
    private EditText name, quantity;
    private Spinner unit;
    private Button expiryButton;
    private String expiry = "";
    private long itemId;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_ingredient); setNavigation();
        name = findViewById(R.id.nameInput); quantity = findViewById(R.id.quantityInput);
        unit = findViewById(R.id.unitInput); expiryButton = findViewById(R.id.pickExpiry);
        ArrayAdapter<String> units = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, IngredientRules.UNITS);
        units.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); unit.setAdapter(units);
        itemId = getIntent().getLongExtra("item_id", -1);
        if (itemId != -1) {
            ((TextView)findViewById(R.id.formTitle)).setText("Edit ingredient");
            PantryItem item = database.getItem(itemId);
            if (item == null) { Toast.makeText(this,"Item no longer exists",Toast.LENGTH_SHORT).show(); finish(); return; }
            if (state == null) {
                name.setText(item.name); quantity.setText(IngredientRules.amount(item.quantity));
                unit.setSelection(Arrays.asList(IngredientRules.UNITS).indexOf(item.unit));
                expiry = item.expiry == null ? "" : item.expiry;
            }
        }
        if (state != null) expiry = state.getString("expiry", "");
        showExpiry();
        expiryButton.setOnClickListener(v -> {
            LocalDate date = expiry.isEmpty() ? LocalDate.now() : LocalDate.parse(expiry);
            new DatePickerDialog(this, (picker, year, month, day) -> {
                expiry = LocalDate.of(year, month + 1, day).toString(); showExpiry();
            }, date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth()).show();
        });
        findViewById(R.id.clearExpiry).setOnClickListener(v -> { expiry = ""; showExpiry(); });
        findViewById(R.id.saveIngredient).setOnClickListener(v -> save());
        findViewById(R.id.cancelIngredient).setOnClickListener(v -> finish());
    }
    private void showExpiry() { expiryButton.setText(expiry.isEmpty() ? "Choose expiry date" : expiry); }
    private void save() {
        String enteredName = name.getText().toString().trim();
        double enteredQuantity;
        try { enteredQuantity = Double.parseDouble(quantity.getText().toString().trim()); }
        catch (NumberFormatException e) { showError("Enter a valid quantity above 0."); return; }
        String selectedUnit = unit.getSelectedItem().toString();
        String error = IngredientRules.validate(enteredName, enteredQuantity, selectedUnit);
        if (error != null) { showError(error); return; }
        try {
            long result = database.save(new PantryItem(itemId, enteredName, enteredQuantity, selectedUnit, expiry));
            if (result == -1) { showError("Item no longer exists. Return to the pantry."); return; }
            Toast.makeText(this, itemId == -1 ? "Ingredient added" : "Ingredient updated", Toast.LENGTH_SHORT).show(); finish();
        } catch (android.database.SQLException e) { showError("Could not save the ingredient. Please try again."); }
    }
    private void showError(String error) { ((TextView)findViewById(R.id.formError)).setText(error); }
    @Override protected void onSaveInstanceState(Bundle out) { out.putString("expiry", expiry); super.onSaveInstanceState(out); }
}
