package com.noqapp.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.client.R;
import com.noqapp.client.model.ReviewModel;
import com.noqapp.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.client.presenter.beans.body.ReviewRating;
import com.noqapp.client.utils.UserUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewActivity extends AppCompatActivity {

    @BindView(R.id.btn_submit)
    protected Button btn_submit;
    @BindView(R.id.ratingBar)
    protected RatingBar ratingBar;
    @BindView(R.id.radioSave)
    protected RadioGroup radioSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);
//        tv_toolbar_title.setText("Invite Details");
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//        }
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//
//            }
        //       });
        Bundle extras = getIntent().getExtras();
        if (null != extras) {


            JsonTokenAndQueue jtk =
                    (JsonTokenAndQueue) extras.getSerializable("object");


        } else {

        }

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
                RadioButton radioSexButton = (RadioButton) findViewById(selectedId);

                Toast.makeText(ReviewActivity.this,
                        "Time saved :" + radioSexButton.getText() + "\n Your rating is :" + ratingBar.getRating(), Toast.LENGTH_SHORT).show();
                ReviewRating rr =new ReviewRating();
                rr.setHoursSaved("1");
                rr.setRatingCount("4");

                ReviewModel.review(UserUtils.getDeviceId(),rr);

            }

        });
    }


}
