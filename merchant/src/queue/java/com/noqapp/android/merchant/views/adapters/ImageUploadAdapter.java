package com.noqapp.android.merchant.views.adapters;

import com.noqapp.android.merchant.BuildConfig;
import com.noqapp.android.merchant.R;

import com.squareup.picasso.Picasso;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;


public class ImageUploadAdapter extends RecyclerView.Adapter<ImageUploadAdapter.MyViewHolder> {
    private final OnItemClickListener listener;
    private List<String> imageUrls;
    private Context context;
    private String recordReferenceId;

    public ImageUploadAdapter(List<String> data, Context context, String recordReferenceId, OnItemClickListener listener) {
        this.imageUrls = data;
        this.listener = listener;
        this.context = context;
        this.recordReferenceId = recordReferenceId;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rcv_item_upload_pic, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.imageDeleteClick(imageUrls.get(listPosition));
                }
            }
        });
        holder.iv_inlarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.imageEnlargeClick(imageUrls.get(listPosition));
                }
            }
        });

        if (!TextUtils.isEmpty(imageUrls.get(listPosition))) {
            Picasso.get()
                    .load(BuildConfig.AWSS3 + BuildConfig.MEDICAL_BUCKET + recordReferenceId + "/" + imageUrls.get(listPosition))
                    .into(holder.iv_thumb);
        } else {
            Picasso.get().load(R.drawable.profile_red).into(holder.iv_thumb);
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public interface OnItemClickListener {
        void imageEnlargeClick(String imageUrl);

        void imageDeleteClick(String imageUrl);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_thumb;
        private ImageView iv_delete;
        private ImageView iv_inlarge;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.iv_thumb = itemView.findViewById(R.id.iv_thumb);
            this.iv_delete = itemView.findViewById(R.id.iv_delete);
            this.iv_inlarge = itemView.findViewById(R.id.iv_inlarge);
        }
    }

}
