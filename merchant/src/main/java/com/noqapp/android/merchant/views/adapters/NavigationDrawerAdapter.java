package com.noqapp.android.merchant.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.presenter.beans.NavigationBean;

public class NavigationDrawerAdapter extends ArrayAdapter<NavigationBean> {

    private Context mContext;
    private int layoutResourceId;
    private NavigationBean data[];

    public NavigationDrawerAdapter(Context mContext, int layoutResourceId, NavigationBean[] data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View listItem = inflater.inflate(layoutResourceId, parent, false);
        ImageView iv_icon =  listItem.findViewById(R.id.iv_icon);
        TextView tv_title =  listItem.findViewById(R.id.tv_title);
        NavigationBean navigationBean = data[position];
        iv_icon.setImageResource(navigationBean.getIcon());
        tv_title.setText(navigationBean.getTitle());
        return listItem;
    }
}