package com.noqapp.android.merchant.views.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.noqapp.android.merchant.R;

public class ColorPaletteAdapter extends BaseAdapter {
    public final OnColorSelectedListener listener;
    final int[] colors;
    private int selectedPosition = -1;

    public ColorPaletteAdapter(OnColorSelectedListener listener, int[] colors) {
        this.listener = listener;
        this.colors = colors;
    }

    @Override
    public int getCount() {
        return colors.length;
    }

    @Override
    public Object getItem(int position) {
        return colors[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder(parent.getContext());
            convertView = holder.view;
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.colorPanelView.setBackgroundColor(colors[position]);
        holder.imageView.setImageResource(selectedPosition == position ? R.drawable.color_checked : 0);
        holder.imageView.setColorFilter(null);
        holder.colorPanelView.setOnClickListener(v -> {
            if (selectedPosition != position) {
                selectedPosition = position;
                notifyDataSetChanged();
            }
            listener.onColorSelected(colors[position]);
        });
        return convertView;
    }

    public interface OnColorSelectedListener {

        void onColorSelected(int color);
    }

    private final class ViewHolder {
        View view;
        ImageView colorPanelView;
        ImageView imageView;

        ViewHolder(Context context) {
            view = View.inflate(context, R.layout.color_item_circle, null);
            colorPanelView = view.findViewById(R.id.color_panel_view);
            imageView = view.findViewById(R.id.color_image_view);
            view.setTag(this);
        }

    }
}