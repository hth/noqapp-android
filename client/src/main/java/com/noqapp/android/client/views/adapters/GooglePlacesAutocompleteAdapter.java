package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.noqapp.android.client.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private List<String> resultList = new ArrayList<>();

    public GooglePlacesAutocompleteAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public int getCount() {
        return null == resultList ? 0 : resultList.size();
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
                synchronized (filterResults) {
                    if (constraint != null) {
                        // Clear and Retrieve the autocomplete results.
                        resultList = AppUtils.autoCompleteWithOkHttp(constraint.toString());
                        if(null == resultList){
                            resultList = new ArrayList<>();
                        }

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults filterResults) {
                if (filterResults != null && filterResults.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }
}
