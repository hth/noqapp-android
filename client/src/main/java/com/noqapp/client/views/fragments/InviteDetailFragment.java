package com.noqapp.client.views.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.noqapp.client.R;
import com.noqapp.client.views.activities.LaunchActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InviteDetailFragment extends NoQueueBaseFragment {

    @BindView(R.id.tv_title)
    protected TextView tv_title;
    @BindView(R.id.tv_details)
    protected TextView tv_details;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_invitedetail, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        if (null != bundle) {
            String title = bundle.getString("title", "");
            String details = bundle.getString("details", "");
            tv_title.setText(title);
            tv_details.setText(details);
            //added to check the content
            tv_title.setText("How it works?");
            tv_details.setText("Lorem Ipsum is simply dummy text of the printing and typesetting industry." +
                    " Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer" +
                    " took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries," +
                    " but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s " +
                    "with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software " +
                    "like Aldus PageMaker including versions of Lorem Ipsum.\n\n" +
                    "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text " +
                    "ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. " +
                    "It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. " +
                    "It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, " +
                    "and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.");
        } else {
            tv_title.setText("");
            tv_details.setText("");
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle("Invite Details");
        LaunchActivity.getLaunchActivity().enableDisableBack(true);
    }

}
