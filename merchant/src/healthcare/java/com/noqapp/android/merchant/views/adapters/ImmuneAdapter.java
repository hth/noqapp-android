package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.merchant.R;
import com.noqapp.android.merchant.views.pojos.ImmuneObjList;

import java.util.List;

public class ImmuneAdapter extends RecyclerView.Adapter {

    private final OnItemClickListener listener;
    private List<ImmuneObjList> categories;
    private Context context;
    private int selected_pos = -1;

    public ImmuneAdapter(Context context, List<ImmuneObjList> categories,
                         OnItemClickListener listener) {
        this.categories = categories;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.rcv_immune, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder Vholder, final int position) {
        ViewHolder holder = (ViewHolder) Vholder;
        holder.tv_menu_header.setOnClickListener((View v) -> {
            //listener.onCategoryItemClick(position, jsonCategory);
        });
        holder.tv_menu_header.setText(categories.get(position).getHeaderTitle());
        holder.ll_header.removeAllViews();
        for (int i = 0; i < categories.get(position).getImmuneObjs().size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.rcv_immune_item, null, false);
            TextView tv_immune_title = view.findViewById(R.id.tv_immune_title);
            TextView tv_immune_date = view.findViewById(R.id.tv_immune_date);
            CardView card_view = view.findViewById(R.id.card_view);
            tv_immune_title.setText(categories.get(position).getImmuneObjs().get(i).getImmuneTitle());
            tv_immune_date.setText(categories.get(position).getImmuneObjs().get(i).getImmuneDate());
            if(categories.get(position).getImmuneObjs().get(i).isImmunationDone()){
                card_view.setCardBackgroundColor(Color.parseColor("#008080"));
            }else{
                card_view.setCardBackgroundColor(ContextCompat.getColor(context, R.color.pressed_color));
            }
            holder.ll_child.addView(view);
        }

        if (selected_pos == position) {
//            holder.ll_header.setBackgroundColor(ContextCompat.getColor(context, R.color.theme_color_dark));
//            holder.tv_menu_header.setTextColor(ContextCompat.getColor(context, R.color.theme_color_dark));
            holder.ll_header.setBackgroundColor(Color.BLUE);
            holder.tv_menu_header.setTextColor(Color.WHITE);
        } else {
            holder.ll_header.setBackgroundColor(Color.WHITE);
            holder.tv_menu_header.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

//    public CategoryHeaderAdapter setSelected_pos(int selected_pos) {
//        this.selected_pos = selected_pos;
//        return this;
//    }
//
    public interface OnItemClickListener {
       // void onCategoryItemClick(int pos, JsonCategory jsonCategory);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_menu_header;
        private LinearLayout ll_header;
        private LinearLayout ll_child;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tv_menu_header = itemView.findViewById(R.id.tv_menu_header);
            this.ll_header = itemView.findViewById(R.id.ll_header);
            this.ll_child = itemView.findViewById(R.id.ll_child);
        }
    }

}