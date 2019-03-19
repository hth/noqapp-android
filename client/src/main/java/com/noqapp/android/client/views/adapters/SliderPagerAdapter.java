package com.noqapp.android.client.views.adapters;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.views.activities.WebViewActivity;
import com.noqapp.android.client.views.customviews.TouchImageView;

import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.viewpager.widget.PagerAdapter;

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
            TouchImageView im_slider = view.findViewById(R.id.im_slider);

            if (image_arraylist.get(position).endsWith(".pdf")) {
                Picasso.with(activity.getApplicationContext())
                        .load(R.drawable.pdf_thumb)
                        .into(im_slider);
                im_slider.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (image_arraylist.get(position).endsWith(".pdf")) {
                            Intent in = new Intent(activity, WebViewActivity.class);
                            in.putExtra("url", BuildConfig.AWSS3 + BuildConfig.MEDICAL_BUCKET + recordReferenceId + "/" + image_arraylist.get(position));
                            in.putExtra("title", "Pdf Document");
                            in.putExtra("isPdf", true);
                            activity.startActivity(in);
                        }
                    }
                });
            } else {
                Picasso.with(activity.getApplicationContext())
                        .load(BuildConfig.AWSS3 + BuildConfig.MEDICAL_BUCKET + recordReferenceId + "/" + image_arraylist.get(position))
                        .into(im_slider);
            }
        } else {
            view = layoutInflater.inflate(R.layout.layout_slider, container, false);
            ImageView im_slider = view.findViewById(R.id.im_slider);
            String url = image_arraylist.get(position).replace("40x40", "240x120");// added to check the image Quality
            Picasso.with(activity.getApplicationContext())
                    .load(AppUtilities.getImageUrls(BuildConfig.SERVICE_BUCKET, url))
                    .placeholder(ImageUtils.getThumbPlaceholder(activity)) // optional
                    .error(ImageUtils.getThumbErrorPlaceholder(activity))         // optional
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
