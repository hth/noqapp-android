package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.pojos.StoreCartItem;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.StoreMenuActivity;
import com.noqapp.android.merchant.views.adapters.MenuOrderAdapter;

import java.util.List;

public class FragmentDummyMenu extends Fragment {
    private View view;
    private List<StoreCartItem> childData;
    private RecyclerView listView;
    private MenuOrderAdapter.CartOrderUpdate cartOrderUpdate;
    private String currencySymbol;
    private StoreMenuActivity storeMenuActivity;
    public FragmentDummyMenu() {
        // Required empty public constructor
    }

    public FragmentDummyMenu(List<StoreCartItem> childData, StoreMenuActivity storeMenuActivity, MenuOrderAdapter.CartOrderUpdate cartOrderUpdate,String currencySymbol) {
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
        listView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        listView.setItemAnimator(new DefaultItemAnimator());
        MenuOrderAdapter menuAdapter = new MenuOrderAdapter(getActivity(), childData,storeMenuActivity, cartOrderUpdate,currencySymbol);
        listView.setAdapter(menuAdapter);
        return view;
    }

}