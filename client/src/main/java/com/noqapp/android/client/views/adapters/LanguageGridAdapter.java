package com.noqapp.android.client.views.adapters;

import com.noqapp.android.client.R;
import com.noqapp.android.client.presenter.beans.SurveyQuestion;
import com.noqapp.android.client.utils.KioskStringConstants;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LanguageGridAdapter extends RecyclerView.Adapter {
    private final OnItemClickListener listener;
    private List<Locale> localeList;
    private Map<Locale, List<SurveyQuestion>> questions;
    private Context context;

    public LanguageGridAdapter(
            Context context,
            List<Locale> localeList,
            Map<Locale, List<SurveyQuestion>> questions,
            OnItemClickListener listener
    ) {
        this.localeList = localeList;
        this.questions = questions;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rcv_language_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        final Locale locale = localeList.get(position);
        List<SurveyQuestion> questionTypeEnumMap = questions.get(locale);
        holder.tv_title.setText(KioskStringConstants.getLanguageLabel(locale.getLanguage()));
        holder.card_view.setOnClickListener((View v) -> {
            holder.tv_title.setBackground(ContextCompat.getDrawable(context, R.drawable.edit_orange));
            holder.tv_title.setTextColor(Color.BLACK);
            listener.onLanguageSelected(questionTypeEnumMap, locale);
        });
    }

    @Override
    public int getItemCount() {
        return localeList.size();
    }

    public interface OnItemClickListener {
        void onLanguageSelected(List<SurveyQuestion> item, Locale locale);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;
        private ImageView iv_main;
        private CardView card_view;

        public ViewHolder(View v) {
            super(v);
            tv_title = v.findViewById(R.id.tv_title);
            iv_main = v.findViewById(R.id.iv_main);
            card_view = v.findViewById(R.id.card_view);
        }
    }
}
