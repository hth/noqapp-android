package com.noqapp.android.client.views.adapters;

import com.noqapp.android.client.presenter.beans.JsonUserAddress;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SpinAdapter extends ArrayAdapter<JsonUserAddress> {
    private List<JsonUserAddress> values = new ArrayList<>();

    public SpinAdapter(Context context, int textViewResourceId,
                       List<JsonUserAddress> values) {
        super(context, textViewResourceId, values);
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public JsonUserAddress getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setTextSize(18.0f);
        label.setPadding(5, 5, 5, 5);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(values.get(position).getAddress());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setTextSize(18.0f);
        label.setPadding(5, 5, 5, 5);
        label.setText(values.get(position).getAddress());
        return label;
    }
}