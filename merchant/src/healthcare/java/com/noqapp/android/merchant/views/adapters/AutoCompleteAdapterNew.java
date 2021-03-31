package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.pojos.DataObj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteAdapterNew extends ArrayAdapter<DataObj> implements Filterable {
    private final boolean isLocalUpdate = false;
    private ArrayList<DataObj> fullList;
    private ArrayList<DataObj> mOriginalValues;
    private ArrayFilter mFilter;
    private Context context;
    private SearchClick searchClick;
    private SearchByPos searchByPos;

    public AutoCompleteAdapterNew(Context context, int resource, List<DataObj> objects, SearchClick searchClick, SearchByPos searchByPos) {
        super(context, resource, objects);
        fullList = (ArrayList<DataObj>) objects;
        mOriginalValues = new ArrayList<>(fullList);
        this.context = context;
        this.searchClick = searchClick;
        this.searchByPos = searchByPos;
    }

    public interface SearchClick {
        void searchClick(boolean isOpen, boolean isEdit, DataObj dataObj, int pos);
    }

    public interface SearchByPos {
        void searchByPos(DataObj dataObj);
    }

    @Override
    public int getCount() {
        return fullList.size();
    }

    @Override
    public DataObj getItem(int position) {
        return fullList.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_autocomplete, parent, false);
        }
        DataObj dataObj = fullList.get(position);

        TextView lblName = view.findViewById(R.id.lbl_name);
        if (lblName != null)
            lblName.setText(dataObj.getShortName());
        lblName.setOnClickListener(v -> {
            if (null != searchClick && !isLocalUpdate) {
                searchClick.searchClick(true, false, fullList.get(position), position);
            }
            if (null != searchByPos) {
                searchByPos.searchByPos(fullList.get(position));
            }
        });

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
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<>(fullList);
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<DataObj> list = new ArrayList<>(mOriginalValues);
                results.values = list;
                results.count = list.size();
            } else {
                final String prefixString = prefix.toString().toLowerCase();

                ArrayList<DataObj> values = mOriginalValues;
                int count = values.size();

                ArrayList<DataObj> newValues = new ArrayList<>(count);

                for (int i = 0; i < count; i++) {
                    DataObj item = values.get(i);
                    if (item.getShortName().toLowerCase().contains(prefixString)) {
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
                fullList = (ArrayList<DataObj>) results.values;
            } else {
                fullList = new ArrayList<>();
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}