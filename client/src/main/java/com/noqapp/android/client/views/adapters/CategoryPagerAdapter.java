package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.JsonCategory;

import java.util.List;

public class CategoryPagerAdapter extends PagerAdapter {

    private String[] colorCodes = new String[]{"#F08a5d"
            , "#B83b5e"
            , "#Aa96da"
            , "#Ff9999"
            , "#6a2c70"
            , "#3fc1c9"
            , "#3F72AF"
            , "#9896F1"
            , "#15B7B9"
            , "#52616B"
            , "#1FAB89"
            , "#3FC1C9"
            , "#3498DB"
            , "#E41655"

    };
    public interface CategoryPagerClick {
        public void pageClicked(int position);
    }
    private List<JsonCategory> categories;
    private Context context;
    private CategoryPagerClick categoryPagerClick;
    public CategoryPagerAdapter(Context context, List<JsonCategory> categories,CategoryPagerClick pagerClick) {
        this.categories = categories;
        this.context = context;
        this.categoryPagerClick = pagerClick;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.category_pager_item, container, false);
        container.addView(view);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        TextView tv_detail = (TextView) view.findViewById(R.id.tv_detail);
        tv_title.setText(categories.get(position).getCategoryName());
       // tv_detail.setText(mData.get(position).getText());
        final CardView cardView = (CardView) view.findViewById(R.id.cardView);
        cardView.setCardBackgroundColor(Color.parseColor(colorCodes[position % colorCodes.length]));
        cardView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                categoryPagerClick.pageClicked(position);
            }
        });

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
