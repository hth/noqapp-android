package com.noqapp.android.merchant.views.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.viewpager.widget.PagerAdapter;

import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.utils.AppUtils;
import com.noqapp.android.merchant.views.activities.WebViewActivity;
import com.noqapp.android.merchant.views.customviews.TouchImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by chandra on 3/26/18.
 */


public class SliderPagerAdapter extends PagerAdapter {
    private Activity activity;
    private ArrayList<String> image_arraylist;
    private boolean isDocument = false;
    private String recordReferenceId;

    public SliderPagerAdapter(Activity activity, ArrayList<String> image_arraylist) {
        this.activity = activity;
        this.image_arraylist = image_arraylist;
    }

    public SliderPagerAdapter(Activity activity, ArrayList<String> image_arraylist, boolean isDocument, String recordReferenceId) {
        this.activity = activity;
        this.image_arraylist = image_arraylist;
        this.isDocument = isDocument;
        this.recordReferenceId = recordReferenceId;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        if (isDocument) {
            view = layoutInflater.inflate(R.layout.layout_slider_document, container, false);
            final ProgressBar progress_bar = view.findViewById(R.id.progress_bar);
            TouchImageView im_slider = view.findViewById(R.id.im_slider);

            if (image_arraylist.get(position).endsWith(".pdf")) {
                Picasso.get()
                        .load(R.drawable.view_pdf)
                        .into(im_slider);
                im_slider.setOnClickListener(v -> {
                    if (image_arraylist.get(position).endsWith(".pdf")) {
                        Intent in = new Intent(activity, WebViewActivity.class);
                        in.putExtra("url", BuildConfig.AWSS3 + BuildConfig.MEDICAL_BUCKET + recordReferenceId + "/" + image_arraylist.get(position));
                        in.putExtra("title", "Pdf Document");
                        in.putExtra("isPdf", true);
                        activity.startActivity(in);
                    }
                });
            } else {
                progress_bar.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(BuildConfig.AWSS3 + BuildConfig.MEDICAL_BUCKET + recordReferenceId + "/" + image_arraylist.get(position))
                        .into(im_slider, new Callback() {
                            @Override
                            public void onSuccess() {
                                progress_bar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                progress_bar.setVisibility(View.GONE);
                            }
                        });
            }
        } else {
            view = layoutInflater.inflate(R.layout.layout_slider, container, false);
            ImageView im_slider = view.findViewById(R.id.im_slider);
            String url = image_arraylist.get(position).replace("40x40", "240x120");// added to check the image Quality
            Picasso.get()
                    .load(AppUtils.getImageUrls(BuildConfig.SERVICE_BUCKET, url))
                    // .placeholder(ImageUtils.getThumbPlaceholder(activity)) // optional
                    // .error(ImageUtils.getThumbErrorPlaceholder(activity))         // optional
                    .into(im_slider);
        }

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return image_arraylist.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
