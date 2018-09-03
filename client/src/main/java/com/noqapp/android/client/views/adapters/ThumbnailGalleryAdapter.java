package com.noqapp.android.client.views.adapters;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.views.activities.SliderActivity;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by chandra on 3/26/18.
 */

public class ThumbnailGalleryAdapter extends RecyclerView.Adapter<ThumbnailGalleryAdapter.MyViewHolder> {
    private final int baseVisibleCount = 4;
    private ArrayList<String> imageUrls;
    private Context context;
    public ThumbnailGalleryAdapter(Context context, ArrayList<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public ThumbnailGalleryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.layout_image_thumb, parent, false);
        ThumbnailGalleryAdapter.MyViewHolder viewHolder = new ThumbnailGalleryAdapter.MyViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ThumbnailGalleryAdapter.MyViewHolder holder, int position) {
        Picasso.with(context)
                .load(AppUtilities.getImageUrls(BuildConfig.SERVICE_BUCKET, imageUrls.get(position)))
                .placeholder(ImageUtils.getThumbPlaceholder(context))
                .error(ImageUtils.getThumbErrorPlaceholder(context))
                .into(holder.iv_photo);
        if (position < 3 || imageUrls.size() == 4) {
            holder.tv_title.setVisibility(View.GONE);
        } else {
            holder.tv_title.setVisibility(View.VISIBLE);
            holder.tv_title.setText("+" + (imageUrls.size() - baseVisibleCount));
        }
    }

    @Override
    public int getItemCount() {
        return (imageUrls.size() > baseVisibleCount ? baseVisibleCount : imageUrls.size());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView iv_photo;
        public TextView tv_title;

        private MyViewHolder(View itemView) {
            super(itemView);
            iv_photo = itemView.findViewById(R.id.iv_photo);
            tv_title = itemView.findViewById(R.id.tv_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, SliderActivity.class);
            intent.putExtra("pos", getAdapterPosition());
            intent.putExtra("imageurls", imageUrls);
            context.startActivity(intent);
        }
    }


}