package com.noqapp.android.client.views.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AnalyticsEvents;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.views.activities.AppInitialize;
import com.noqapp.android.client.views.pojos.LanguageInfo;
import com.noqapp.android.common.customviews.CustomToast;

import java.util.List;

public class LanguageAdapter extends RecyclerView.Adapter {
    private final OnItemClickListener listener;
    private List<LanguageInfo> languageInfos;
    private Activity context;
    private boolean isIndian;

    public LanguageAdapter(Activity context, List<LanguageInfo> languageInfos,
                           OnItemClickListener listener, boolean isIndian) {
        this.languageInfos = languageInfos;
        this.context = context;
        this.listener = listener;
        this.isIndian = isIndian;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.language_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder Vholder, final int position) {
        ViewHolder holder = (ViewHolder) Vholder;
        final LanguageInfo languageInfo = languageInfos.get(position);
        holder.tv_language_title.setText(languageInfo.getLanguageTitle());
        holder.rb_language.setChecked(languageInfo.isLanguageSelected());
        //AppUtils.loadProfilePic(holder.iv_country_flag, languageInfo.getLanguageDrawable(), context);
        holder.iv_country_flag.setBackground(context.getDrawable(languageInfo.getLanguageDrawable()));
        holder.cardview.setOnClickListener((View v) -> {
            resetSelection();
            languageInfos.get(position).setLanguageSelected(true);
            holder.rb_language.setChecked(true);
            AppUtils.changeLanguage(languageInfo.getLanguageCode());
            if (AppUtils.isRelease()) {
                Bundle params = new Bundle();
                params.putString("Language", languageInfo.getLanguageTitle());
                AppInitialize.getFireBaseAnalytics().logEvent(AnalyticsEvents.EVENT_CHANGE_LANGUAGE, params);
            }
            notifyDataSetChanged();
            new CustomToast().showToast(context, " Language changed successfully");
            context.finish();
            if(null != listener){
                listener.onLanguageSelected(languageInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return languageInfos.size();
    }


    public interface OnItemClickListener {
        void onLanguageSelected(LanguageInfo languageInfo);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_language_title;
        private ImageView iv_country_flag;
        private AppCompatRadioButton rb_language;
        private CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tv_language_title = itemView.findViewById(R.id.tv_language_title);
            this.iv_country_flag = itemView.findViewById(R.id.iv_country_flag);
            this.rb_language = itemView.findViewById(R.id.rb_language);
            this.cardview = itemView.findViewById(R.id.cardview);
            this.iv_country_flag.setVisibility(isIndian ? View.GONE : View.VISIBLE);
        }
    }

    private void resetSelection(){
        for (int i = 0; i < languageInfos.size(); i++) {
            languageInfos.get(i).setLanguageSelected(false);
        }
    }
}