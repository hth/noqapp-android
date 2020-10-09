package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtils;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.client.views.activities.SliderActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chandra on 3/26/18.
 */
public class AccreditionAdapter extends RecyclerView.Adapter {
    private final int baseVisibleCount = 4;
    private List<String> imageUrls;
    private Context context;

    public AccreditionAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.layout_image_accred, parent, false);
        return new AccreditionAdapter.MyViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder Vholder, int position) {
        MyViewHolder holder = (MyViewHolder) Vholder;
        Picasso.get()
            .load(AppUtils.getImageUrls(BuildConfig.ACCREDITATION_BUCKET, imageUrls.get(position)))
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
            intent.putExtra("imageurls", (ArrayList<String>) imageUrls);
            intent.putExtra("isDocument", false);
            intent.putExtra("recordReferenceId", "");
            intent.putExtra("bucket", BuildConfig.ACCREDITATION_BUCKET);
            context.startActivity(intent);
        }
    }
}