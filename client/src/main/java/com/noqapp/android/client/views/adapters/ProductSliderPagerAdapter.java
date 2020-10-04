package com.noqapp.android.client.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.common.beans.store.JsonStoreProduct;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductSliderPagerAdapter extends PagerAdapter {
    private Activity activity;
    private List<JsonStoreProduct> products_arraylist;

    public ProductSliderPagerAdapter(Activity activity, List<JsonStoreProduct> products_arraylist) {
        this.activity = activity;
        this.products_arraylist = products_arraylist;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.product_layout_slider, container, false);
        JsonStoreProduct jsonStoreProduct = products_arraylist.get(position);
        ImageView im_slider = view.findViewById(R.id.im_slider);
        TextView tv_product_name = view.findViewById(R.id.tv_product_name);
        TextView tv_product_details = view.findViewById(R.id.tv_product_details);
        Picasso.get()
                .load(AppUtils.getImageUrls(BuildConfig.PRODUCT_BUCKET, jsonStoreProduct.getProductImage()))
                .placeholder(ImageUtils.getThumbPlaceholder(activity)) // optional
                .error(ImageUtils.getThumbErrorPlaceholder(activity))         // optional
                .into(im_slider);
        tv_product_name.setText(jsonStoreProduct.getProductName());
        tv_product_details.setText(jsonStoreProduct.getProductInfo());

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return products_arraylist.size();
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
