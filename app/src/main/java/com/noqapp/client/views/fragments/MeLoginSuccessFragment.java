package com.noqapp.client.views.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.noqapp.client.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MeLoginSuccessFragment extends Fragment {

    public MeLoginSuccessFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeLoginSuccesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeLoginSuccessFragment newInstance(String param1, String param2) {
        MeLoginSuccessFragment fragment = new MeLoginSuccessFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mesuccess, container, false);
        return view;
     }

}
