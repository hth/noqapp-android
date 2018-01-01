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
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.Formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.noqapp.android.client.utils.AppUtilities.getSystemHourMinutes;

public class CategoryPagerAdapter extends PagerAdapter {

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

        View view = LayoutInflater
                .from(container.getContext())
                .inflate(R.layout.category_pager_item, container, false);

        container.addView(view);
        TextView tv_title = view.findViewById(R.id.tv_title);
        TextView tv_detail = view.findViewById(R.id.tv_detail);
        tv_title.setText(jsonCategory.getCategoryName());
        tv_detail.setText(getAdditionalCardText(jsonQueue));
        final CardView cardView = (CardView) view.findViewById(R.id.cardView);
        cardView.setCardBackgroundColor(Color.parseColor(Constants.colorCodes[position % Constants.colorCodes.length]));
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

    private String getAdditionalCardText(JsonQueue jsonQueue) {
        String additionalText;
        if (jsonQueue.isDayClosed()) {
            //Fetch for tomorrow when closed
            additionalText = jsonQueue.getDisplayName() + " is closed today.";
        } else if(jsonQueue.getStartHour() < getSystemHourMinutes()) {
            //Based on location let them know in how much time they will reach or suggest the next queue.
            additionalText = jsonQueue.getDisplayName()
                    + " is open & can service you now. Click to join the queue.";
        } else {
            if(jsonQueue.getTokenAvailableFrom() < getSystemHourMinutes()) {
                additionalText = jsonQueue.getDisplayName()
                        + " opens at "
                        + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getStartHour())
                        + ". Join queue now to save time.";
            } else {
                additionalText = jsonQueue.getDisplayName()
                        + " can service you at "
                        + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getStartHour())
                        + ". You can join this queue at "
                        + Formatter.convertMilitaryTo12HourFormat(jsonQueue.getTokenAvailableFrom());
            }
        }

        return additionalText;
    }
}
