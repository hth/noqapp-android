package com.noqapp.android.client.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.noqapp.android.client.R;
import com.noqapp.android.client.views.activities.StoreMenuActivity;
import com.noqapp.android.client.views.adapters.StoreMenuAdapter;
import com.noqapp.android.common.pojos.StoreCartItem;

import java.util.List;


public class FragmentDummy extends Fragment {
    private View view;
    private List<StoreCartItem> childData;
    private ListView listView;
    private StoreMenuActivity storeMenuActivity;
    private StoreMenuAdapter.CartOrderUpdate cartOrderUpdate;
    private String currencySymbol;

    public FragmentDummy() {
        // Required empty public constructor
    }

    public FragmentDummy(List<StoreCartItem> childData, StoreMenuActivity storeMenuActivity, StoreMenuAdapter.CartOrderUpdate cartOrderUpdate, String currencySymbol) {
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
        StoreMenuAdapter menuAdapter = new StoreMenuAdapter(getActivity(), childData, storeMenuActivity, cartOrderUpdate,currencySymbol);
        listView.setAdapter(menuAdapter);
        return view;
    }

}