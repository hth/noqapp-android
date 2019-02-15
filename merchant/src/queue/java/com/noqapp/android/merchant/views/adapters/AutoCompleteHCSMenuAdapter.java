package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.pojos.HCSMenuObject;

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

public class AutoCompleteHCSMenuAdapter extends ArrayAdapter<HCSMenuObject> implements Filterable {

    private final boolean isLocalUpdate = false;
    private ArrayList<HCSMenuObject> fullList;
    private ArrayList<HCSMenuObject> mOriginalValues;
    private ArrayFilter mFilter;
    private Context context;
    private SearchClick searchClick;
    private SearchByPos searchByPos;

    public AutoCompleteHCSMenuAdapter(Context context, List<HCSMenuObject> objects, SearchClick searchClick, SearchByPos searchByPos) {

        super(context, R.layout.layout_autocomplete, objects);
        fullList = (ArrayList<HCSMenuObject>) objects;
        mOriginalValues = new ArrayList<HCSMenuObject>(fullList);
        this.context = context;
        this.searchClick = searchClick;
        this.searchByPos = searchByPos;

    }

    public interface SearchClick {
        void searchClick(boolean isOpen, boolean isEdit, HCSMenuObject HCSMenuObject, int pos);
    }

    public interface SearchByPos {
        void searchByPos(HCSMenuObject HCSMenuObject);
    }


    @Override
    public int getCount() {
        return fullList.size();
    }

    @Override
    public HCSMenuObject getItem(int position) {
        return fullList.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_autocomplete, parent, false);
        }
        HCSMenuObject HCSMenuObject = fullList.get(position);

        TextView lblName = view.findViewById(R.id.lbl_name);
        if (lblName != null)
            lblName.setText(HCSMenuObject.getSortName());
        lblName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != searchClick && !isLocalUpdate) {
                    searchClick.searchClick(true, false, fullList.get(position), position);
                }
                if (null != searchByPos) {
                    searchByPos.searchByPos(fullList.get(position));
                }
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
        private Object lock;

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (lock) {
                    mOriginalValues = new ArrayList<HCSMenuObject>(fullList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    ArrayList<HCSMenuObject> list = new ArrayList<HCSMenuObject>(mOriginalValues);
                    results.values = list;
                    results.count = list.size();
                }
            } else {
                final String prefixString = prefix.toString().toLowerCase();

                ArrayList<HCSMenuObject> values = mOriginalValues;
                int count = values.size();

                ArrayList<HCSMenuObject> newValues = new ArrayList<HCSMenuObject>(count);

                for (int i = 0; i < count; i++) {
                    HCSMenuObject item = values.get(i);
                    if (item.getSortName().toLowerCase().contains(prefixString)) {
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
                fullList = (ArrayList<HCSMenuObject>) results.values;
            } else {
                fullList = new ArrayList<HCSMenuObject>();
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}