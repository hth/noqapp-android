package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.noqapp.android.common.beans.ChildData;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.adapters.MenuAdapter;

import java.util.List;

public class FragmentDummy extends Fragment {
    private View view;
    private List<ChildData> childData;
    private ListView listView;
    private MenuAdapter.MenuItemUpdate menuItemUpdate;

    public FragmentDummy() {
        // Required empty public constructor
    }

    public FragmentDummy(List<ChildData> childData,MenuAdapter.MenuItemUpdate menuItemUpdate) {
        this.childData = childData;
        this.menuItemUpdate = menuItemUpdate;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            if ((ViewGroup) view.getParent() != null)
                ((ViewGroup) view.getParent()).removeView(view);
            return view;
        }
        view = inflater.inflate(R.layout.fragment_dummy, container, false);
        listView = view.findViewById(R.id.listView);
        MenuAdapter menuAdapter = new MenuAdapter(getActivity(), childData, menuItemUpdate);
        listView.setAdapter(menuAdapter);
        return view;
    }

}