package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonMasterLab;

import java.util.ArrayList;
import java.util.List;

public class TestListAutoComplete extends ArrayAdapter<JsonMasterLab> implements Filterable {

    private ArrayList<JsonMasterLab> fullList;
    private ArrayList<JsonMasterLab> mOriginalValues;
    private ArrayFilter mFilter;
    private Context context;

    public TestListAutoComplete(Context context, List<JsonMasterLab> objects) {
        super(context, R.layout.layout_autocomplete, objects);
        fullList = (ArrayList<JsonMasterLab>) objects;
        mOriginalValues = new ArrayList<>(fullList);
        this.context = context;
    }

    @Override
    public int getCount() {
        return fullList.size();
    }

    @Override
    public JsonMasterLab getItem(int position) {
        return fullList.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_autocomplete, parent, false);
        }
        JsonMasterLab JsonMasterLab = fullList.get(position);

        TextView lblName = view.findViewById(R.id.lbl_name);
        if (lblName != null)
            lblName.setText(JsonMasterLab.getProductShortName());

        return view;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }


    private class ArrayFilter extends Filter {
        private Object lock;

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (lock) {
                    mOriginalValues = new ArrayList<JsonMasterLab>(fullList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    ArrayList<JsonMasterLab> list = new ArrayList<JsonMasterLab>(mOriginalValues);
                    results.values = list;
                    results.count = list.size();
                }
            } else {
                final String prefixString = prefix.toString().toLowerCase();

                ArrayList<JsonMasterLab> values = mOriginalValues;
                int count = values.size();

                ArrayList<JsonMasterLab> newValues = new ArrayList<JsonMasterLab>(count);

                for (int i = 0; i < count; i++) {
                    JsonMasterLab item = values.get(i);
                    if (item.getProductShortName().toLowerCase().contains(prefixString)) {
                        newValues.add(item);
                    }

                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                fullList = (ArrayList<JsonMasterLab>) results.values;
            } else {
                fullList = new ArrayList<JsonMasterLab>();
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}