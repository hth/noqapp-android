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
import com.noqapp.android.client.presenter.beans.JsonQueue;
import com.noqapp.android.client.utils.Formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.noqapp.android.client.utils.AppUtilities.getSystemHourMinutes;

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
        void pageClicked(int position);
    }

    private List<JsonCategory> categories;
    private Map<String, ArrayList<JsonQueue>> queueMap;
    private Context context;
    private CategoryPagerClick categoryPagerClick;

    public CategoryPagerAdapter(
            Context context,
            List<JsonCategory> categories,
            Map<String, ArrayList<JsonQueue>> queueMap,
            CategoryPagerClick pagerClick) {
        this.categories = categories;
        this.queueMap = queueMap;
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
        JsonCategory jsonCategory = categories.get(position);
        List<JsonQueue> jsonQueues = queueMap.get(jsonCategory.getBizCategoryId());
        JsonQueue jsonQueue = null;
        if (!jsonQueues.isEmpty()) {
            jsonQueue = jsonQueues.get(0);
        }
        int systemHourMinutes = getSystemHourMinutes();

        View view = LayoutInflater
                .from(container.getContext())
                .inflate(R.layout.category_pager_item, container, false);

        container.addView(view);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        TextView tv_detail = (TextView) view.findViewById(R.id.tv_detail);
        tv_title.setText(jsonCategory.getCategoryName());
        if (jsonQueue.isDayClosed()) {
            //Fetch for tomorrow when closed
            tv_detail.setText(jsonQueue.getDisplayName() + " is closed today.");
        } else if(jsonQueue.getStartHour() < systemHourMinutes) {
            tv_detail.setText(jsonQueue.getDisplayName() + " is open and can service you now. Click to join the queue.");
        } else {
            tv_detail.setText(jsonQueue.getDisplayName() + " can service you at " + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getStartHour()) + ".");
        }
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
