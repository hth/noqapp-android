package com.noqapp.android.merchant.views.adapters;

/**
 * Created by chandra on 2/6/18.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.JsonTopic;

import java.util.ArrayList;
import java.util.List;

public class AutocompleteAdapter extends ArrayAdapter {
    private List<JsonTopic> dataList;
    private int itemLayout;
    private ListFilter listFilter = new ListFilter();
    private List<JsonTopic> dataListAllItems;

    public AutocompleteAdapter(Context context, int resource, List<JsonTopic> storeDataLst) {
        super(context, resource, storeDataLst);
        dataList = storeDataLst;
        itemLayout = resource;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public String getItem(int position) {
        return dataList.get(position).getDisplayName();
    }

    public String getQRCode(int position) {
        return dataList.get(position).getCodeQR();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        }

        TextView strName = (TextView) view.findViewById(R.id.tv_name);
        strName.setText(getItem(position));
        return view;
    }


    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public class ListFilter extends Filter {
        private Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (dataListAllItems == null) {
                synchronized (lock) {
                    dataListAllItems = new ArrayList<>(dataList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = dataListAllItems;
                    results.count = dataListAllItems.size();
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();
                ArrayList<JsonTopic> matchValues = new ArrayList<>();
                for (JsonTopic dataItem : dataListAllItems) {
                    if (dataItem.getDisplayName().toLowerCase().contains(searchStrLowerCase)) {
                        matchValues.add(dataItem);
                    }
                }

                results.values = matchValues;
                results.count = matchValues.size();

//                Answers.getInstance().logSearch(new SearchEvent()
//                        .putQuery(prefix.toString()));
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                dataList = (ArrayList<JsonTopic>) results.values;
            } else {
                dataList = null;
            }

            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
