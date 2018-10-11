package com.noqapp.android.client.views.adapters;

import com.noqapp.android.client.R;
import com.noqapp.android.common.beans.NavigationBean;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class NavigationDrawerAdapter extends BaseAdapter {


    private Context mContext;
    private ArrayList<NavigationBean> data;

    public ArrayList<NavigationBean> getData() {
        return data;
    }



    public NavigationDrawerAdapter(Context mContext, ArrayList<NavigationBean> data) {
        this.mContext = mContext;
        this.data = data;
    }

    public int getCount() {
        return this.data.size();
    }

    public Object getItem(int n) {
        return null;
    }

    public long getItemId(int n) {
        return 0;
    }

    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View listItem = inflater.inflate(R.layout.listitem_navigation_drawer, parent, false);
        ImageView iv_icon = listItem.findViewById(R.id.iv_icon);
        TextView tv_title = listItem.findViewById(R.id.tv_title);
        NavigationBean navigationBean = data.get(position);
        iv_icon.setImageResource(navigationBean.getIcon());
        tv_title.setText(navigationBean.getTitle());
        return listItem;
    }

}