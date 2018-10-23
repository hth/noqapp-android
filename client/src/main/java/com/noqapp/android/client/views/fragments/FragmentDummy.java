package com.noqapp.android.client.views.fragments;

import com.noqapp.android.client.R;
import com.noqapp.android.client.views.activities.StoreMenuActivity;
import com.noqapp.android.client.views.adapters.MenuAdapter;
import com.noqapp.android.common.beans.ChildData;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;


public class FragmentDummy extends Fragment {
    private View view;
    private List<ChildData> childData;
    private ListView listView;
    private StoreMenuActivity storeMenuActivity;
    private MenuAdapter.CartOrderUpdate cartOrderUpdate;

    public FragmentDummy() {
        // Required empty public constructor
    }

    public FragmentDummy(List<ChildData> childData, StoreMenuActivity storeMenuActivity, MenuAdapter.CartOrderUpdate cartOrderUpdate) {
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
        view = inflater.inflate(R.layout.fragment_dummy, container, false);
        listView = view.findViewById(R.id.listView);
        MenuAdapter menuAdapter = new MenuAdapter(getActivity(), childData, storeMenuActivity, cartOrderUpdate);
        listView.setAdapter(menuAdapter);
        return view;
    }

}