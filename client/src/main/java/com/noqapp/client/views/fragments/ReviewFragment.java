package com.noqapp.client.views.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.client.R;
import com.noqapp.client.views.activities.LaunchActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewFragment extends NoQueueBaseFragment {

    @BindView(R.id.btn_submit)
    protected Button btn_submit;
    @BindView(R.id.ratingBar)
    protected RatingBar ratingBar;
    @BindView(R.id.radioSave)
    protected RadioGroup radioSave;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.fragment_review, container, false);
        ButterKnife.bind(this, view);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
               // Toast.makeText(getActivity(), String.valueOf(rating), Toast.LENGTH_LONG).show();


            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                int selectedId = radioSave.getCheckedRadioButtonId();
                RadioButton radioSexButton = (RadioButton) view.findViewById(selectedId);

                Toast.makeText(getActivity(),
                        "Time saved :" + radioSexButton.getText() + "\n Your rating is :" + ratingBar.getRating(), Toast.LENGTH_SHORT).show();

            }

        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle("Review");
        LaunchActivity.getLaunchActivity().enableDisableBack(false);
    }

}
