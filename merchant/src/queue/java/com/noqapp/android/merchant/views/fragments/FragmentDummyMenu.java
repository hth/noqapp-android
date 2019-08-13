package com.noqapp.android.merchant.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.common.pojos.StoreCartItem;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.activities.StoreMenuActivity;
import com.noqapp.android.merchant.views.adapters.StoreMenuGridAdapter;

import java.util.List;

public class FragmentDummyMenu extends Fragment {
    private View view;
    private List<StoreCartItem> childData;
    private RecyclerView rcv_product;
    private StoreMenuGridAdapter.CartOrderUpdate cartOrderUpdate;
    private StoreMenuActivity storeMenuActivity;

    public FragmentDummyMenu() {
        // Required empty public constructor
    }

    public FragmentDummyMenu(List<StoreCartItem> childData, StoreMenuActivity storeMenuActivity, StoreMenuGridAdapter.CartOrderUpdate cartOrderUpdate) {
        this.childData = childData;
        this.storeMenuActivity = storeMenuActivity;
        this.cartOrderUpdate = cartOrderUpdate;
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
        view = inflater.inflate(R.layout.fragment_dummy_menu, container, false);
        rcv_product = view.findViewById(R.id.rcv_product);
        rcv_product.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rcv_product.setItemAnimator(new DefaultItemAnimator());
        StoreMenuGridAdapter menuAdapter = new StoreMenuGridAdapter( childData,storeMenuActivity, cartOrderUpdate);
        rcv_product.setAdapter(menuAdapter);
        return view;
    }

}