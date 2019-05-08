package com.noqapp.android.client.views.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.noqapp.android.client.BuildConfig;
import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.AppUtilities;
import com.noqapp.android.client.utils.ImageUtils;
import com.noqapp.android.common.beans.JsonProfile;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileAdapter extends RecyclerView.Adapter {
    private final OnItemClickListener listener;
    private List<JsonProfile> jsonProfiles;
    private Context context;

    public ProfileAdapter(Context context, List<JsonProfile> jsonProfiles,
                          OnItemClickListener listener) {
        this.jsonProfiles = jsonProfiles;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rcv_item_user_profile, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder Vholder, final int position) {
        ViewHolder holder = (ViewHolder) Vholder;
        final JsonProfile jsonProfile = jsonProfiles.get(position);
        holder.tv_name.setText(jsonProfile.getName());
        loadProfilePic(holder.iv_profile, jsonProfile.getProfileImage());
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener)
                    listener.onProfileItemClick(position, jsonProfile);
            }
        });


    }

    private void loadProfilePic(ImageView iv_profile, String imageUrl) {
        Picasso.get().load(ImageUtils.getProfilePlaceholder()).into(iv_profile);
        try {
            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.get()
                        .load(AppUtilities.getImageUrls(BuildConfig.PROFILE_BUCKET, imageUrl))
                        .placeholder(ImageUtils.getProfilePlaceholder(context))
                        .error(ImageUtils.getProfileErrorPlaceholder(context))
                        .into(iv_profile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonProfiles.size();
    }


    public interface OnItemClickListener {
        void onProfileItemClick(int pos, JsonProfile jsonProfile);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private ImageView iv_profile;
        private CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tv_name = itemView.findViewById(R.id.tv_name);
            this.iv_profile = itemView.findViewById(R.id.iv_profile);
            this.cardview = itemView.findViewById(R.id.cardview);
        }
    }
}