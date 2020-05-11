package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.noqapp.android.client.utils.AppUtils;

import java.util.List;

public class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private List<String> resultList;

    public GooglePlacesAutocompleteAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = AppUtils.autoCompleteWithOkHttp(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    if (resultList != null) {
                        filterResults.count = resultList.size();
                    } else {
                        filterResults.count = 0;
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }
}
