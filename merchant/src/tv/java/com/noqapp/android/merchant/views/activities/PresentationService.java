package com.noqapp.android.merchant.views.activities;

import com.noqapp.android.common.utils.Formatter;
import com.noqapp.android.merchant.R;

import com.google.android.gms.cast.CastPresentation;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PresentationService extends CastRemoteDisplayLocalService {
  private DetailPresentation castPresentation;
  private TvObject tvObject;

  @Override
  public void onCreatePresentation(Display display) {
    dismissPresentation();
    castPresentation = new DetailPresentation(this, display);

    try {
      castPresentation.show();
    } catch (WindowManager.InvalidDisplayException ex) {
      dismissPresentation();
    }
  }

  @Override
  public void onDismissPresentation() {
    dismissPresentation();
    tvObject = null;
  }

  private void dismissPresentation() {
    if (castPresentation != null) {
      castPresentation.dismiss();
      castPresentation = null;
    }
  }

  public void setTvObject(TvObject ad) {
    tvObject = ad;
    if (castPresentation != null) {
      castPresentation.updateDetail(ad);
    }
  }

  public class DetailPresentation extends CastPresentation {
    public ImageView image,iv_banner,iv_banner1;
    private TextView title,tv_timing;
    public LinearLayout ll_list;
    public Context context;
    public DetailPresentation(Context context, Display display) {
      super(context, display);
      this.context =context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.presentation_detail);
      image = findViewById(R.id.ad_image);
      iv_banner = findViewById(R.id.iv_banner);
      iv_banner1 = findViewById(R.id.iv_banner1);
      title = findViewById( R.id.ad_title);
      tv_timing = findViewById( R.id.tv_timing);
      ll_list = findViewById( R.id.ll_list);
      updateDetail(tvObject);
    }

    public void updateDetail(TvObject tvObject) {
      Picasso.with(getContext()).load("http://www.ssdhospital.in/wp-content/uploads/2016/12/dr-deepak-vaswani.jpg").into(image);
     // Picasso.with(getContext()).load("http://businessplaces.in/wp-content/uploads/2017/07/ssdhospital-logo-2.jpg").into(iv_banner);
     // Picasso.with(getContext()).load("https://steamuserimages-a.akamaihd.net/ugc/824566056082911413/D6CF5FF8C8E7C3C693E70B02C55CD2CB0E87D740/").into(iv_banner1);
      title.setText(tvObject.getJsonTopic().getDisplayName());
      tv_timing.setText("Timing: " + Formatter.convertMilitaryTo12HourFormat(tvObject.getJsonTopic().getHour().getStartHour())
              + " - " + Formatter.convertMilitaryTo12HourFormat(tvObject.getJsonTopic().getHour().getEndHour()));
      ll_list.removeAllViews();
      LayoutInflater inflater = LayoutInflater.from(context);
      if(null != tvObject.getJsonQueuedPersonList())
      for (int i = 0; i < tvObject.getJsonQueuedPersonList().size(); i++) {
        View customView = inflater.inflate(R.layout.lay_text, null, false);
        TextView textView = customView.findViewById(R.id.tv_name);
        TextView tv_mobile = customView.findViewById(R.id.tv_mobile);
        textView.setText("( "+(i+1)+") "+tvObject.getJsonQueuedPersonList().get(i).getCustomerName());
        String phoneNo = tvObject.getJsonQueuedPersonList().get(i).getCustomerPhone();
        if (null != phoneNo && phoneNo.length() >= 10) {
          String number = phoneNo.substring(0, 4) + "XXXXXX" + phoneNo.substring(phoneNo.length() - 3, phoneNo.length() - 1);
          tv_mobile.setText(number);
        }else{
          tv_mobile.setText("");
        }
        ll_list.addView(customView);
      }

    }

    @Override
    public void onDetachedFromWindow() {
      super.onDetachedFromWindow();
    }
  }
}