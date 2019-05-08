package com.noqapp.android.client.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.views.activities.StoreMenuActivity;
import com.noqapp.android.client.views.adapters.MenuAdapter;
import com.noqapp.android.common.beans.ChildData;

import java.util.List;

import androidx.fragment.app.Fragment;


public class FragmentDummy extends Fragment {
    private View view;
    private List<ChildData> childData;
    private ListView listView;
    private StoreMenuActivity storeMenuActivity;
    private MenuAdapter.CartOrderUpdate cartOrderUpdate;
    private String currencySymbol;

    public FragmentDummy() {
        // Required empty public constructor
    }

    public FragmentDummy(List<ChildData> childData, StoreMenuActivity storeMenuActivity, MenuAdapter.CartOrderUpdate cartOrderUpdate,String currencySymbol) {
        this.childData = childData;
        this.storeMenuActivity = storeMenuActivity;
        this.cartOrderUpdate = cartOrderUpdate;
        this.currencySymbol = currencySymbol;
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
        MenuAdapter menuAdapter = new MenuAdapter(getActivity(), childData, storeMenuActivity, cartOrderUpdate,currencySymbol);
        listView.setAdapter(menuAdapter);
        return view;
    }

}