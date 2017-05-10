package com.noqapp.client.views.activities;

/**
 * Created by chandra on 5/7/17.
 */


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.noqapp.client.R;
import com.noqapp.client.helper.ShowAlertInformation;
import com.noqapp.client.model.QueueModel;
import com.noqapp.client.model.ReviewModel;
import com.noqapp.client.presenter.ReviewPresenter;
import com.noqapp.client.presenter.beans.JsonResponse;
import com.noqapp.client.presenter.beans.JsonTokenAndQueue;
import com.noqapp.client.presenter.beans.body.ReviewRating;
import com.noqapp.client.utils.Formatter;
import com.noqapp.client.views.fragments.AfterJoinFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewActivity extends AppCompatActivity implements ReviewPresenter{
    private final String TAG = ReviewActivity.class.getSimpleName();

    @BindView(R.id.tv_store_name)
    protected TextView tv_store_name;
    @BindView(R.id.tv_queue_name)
    protected TextView tv_queue_name;
    @BindView(R.id.tv_address)
    protected TextView tv_address;
    @BindView(R.id.tv_mobile)
    protected TextView tv_mobile;
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
        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            JsonTokenAndQueue jtk =
                    (JsonTokenAndQueue) extras.getSerializable("object");
            tv_store_name.setText(jtk.getBusinessName());
            tv_queue_name.setText(jtk.getDisplayName());
            tv_address.setText(Formatter.getFormattedAddress(jtk.getStoreAddress()));
            tv_mobile.setText("Date of service : "+jtk.getCreateDate());

        } else {
            //Do nothing as of now
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
                if (ratingBar.getRating() == 0) {
                    Toast.makeText(ReviewActivity.this, "Please rate the service", Toast.LENGTH_LONG).show();
                } else {
                    if (LaunchActivity.getLaunchActivity().isOnline()) {
                        int selectedId = radioSave.getCheckedRadioButtonId();
                        RadioButton radioSexButton = (RadioButton) findViewById(selectedId);

                        Toast.makeText(ReviewActivity.this,
                                "Time saved :" + radioSexButton.getText() + "\n Your rating is :" + ratingBar.getRating(), Toast.LENGTH_SHORT).show();
                        ReviewRating rr = new ReviewRating();
                        rr.setHoursSaved("1");
                        rr.setRatingCount(String.valueOf(ratingBar.getRating()));
                        LaunchActivity.getLaunchActivity().progressDialog.show();
                        ReviewModel.reviewPresenter = ReviewActivity.this;
                        ReviewModel.review(LaunchActivity.getUdid(), rr);
                    } else {
                        ShowAlertInformation.showNetworkDialog(ReviewActivity.this);
                    }
                }

            }

        });
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Toast.makeText(this, "Please review the service , It is valuable to us.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void reviewResponse(JsonResponse jsonResponse) {
        if(null!=jsonResponse && jsonResponse.getResponse()==1){
            //success
            Log.v("Review response",jsonResponse.toString());
            Toast.makeText(this,"Thanks for feedback.",Toast.LENGTH_LONG).show();
            finish();
        }else{
            //fail
            Toast.makeText(this,"Failed to submit the review",Toast.LENGTH_LONG).show();
        }
        LaunchActivity.getLaunchActivity().dismissProgress();
    }

    @Override
    public void reviewError() {
        LaunchActivity.getLaunchActivity().dismissProgress();
    }
}
