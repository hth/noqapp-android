package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.model.types.QuestionTypeEnum;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LanguageGridAdapter extends RecyclerView.Adapter {

    private final OnItemClickListener listener;
    private List<Locale> localeList;
    private Map<Locale, Map<String, QuestionTypeEnum>> questions;
    private Context context;

    public LanguageGridAdapter(Context context, List<Locale> localeList,
                               Map<Locale, Map<String, QuestionTypeEnum>> questions, OnItemClickListener listener) {
        this.localeList = localeList;
        this.questions = questions;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        final Locale local = localeList.get(position);
        Map<String, QuestionTypeEnum> questionTypeEnumMap = questions.get(local);
        holder.tv_title.setText(local.getDisplayLanguage());
        holder.iv_main.setBackgroundResource(R.drawable.bg_language);
        holder.card_view.setOnClickListener((View v) -> {
            listener.onLanguageSelected(questionTypeEnumMap);
        });
    }

    @Override
    public int getItemCount() {
        return localeList.size();
    }

    public interface OnItemClickListener {
        void onLanguageSelected(Map<String, QuestionTypeEnum> item);
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
