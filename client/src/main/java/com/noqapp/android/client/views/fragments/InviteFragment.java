package com.noqapp.android.client.views.fragments;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.AnalyticsEvents;
import com.noqapp.android.client.utils.IBConstant;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.WebViewActivity;
import com.noqapp.android.common.customviews.CustomToast;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by chandra on 4/9/17.
 */
public class InviteFragment extends NoQueueBaseFragment implements View.OnClickListener{
    private String selectedText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_invite, container, false);

        TextView tv_how_it_works = view.findViewById(R.id.tv_how_it_works);
        TextView tv_copy = view.findViewById(R.id.tv_copy);
        TextView tv_title = view.findViewById(R.id.tv_title);
        TextView tv_details = view.findViewById(R.id.tv_details);
        TextView tv_invite_code = view.findViewById(R.id.tv_invite_code);
        TextView btn_send_invite = view.findViewById(R.id.btn_send_invite);
        btn_send_invite.setOnClickListener(this);
        tv_how_it_works.setPaintFlags(tv_how_it_works.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv_copy.setPaintFlags(tv_copy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv_copy.setOnClickListener(this);
        tv_how_it_works.setOnClickListener(this);
        Bundle bundle = getArguments();
        if (null != bundle) {
            String title = bundle.getString("title", "Invite Friends");
            String details = bundle.getString("details", "As a user of NoQueue, you can invite your friends and family.");
            String invite_code = bundle.getString("invite_code", "");

            tv_title.setText(title);
            tv_details.setText(details);
            tv_invite_code.setText(invite_code);
        } else {
            tv_title.setText("Title is here");
            tv_details.setText("Details are .....");
            tv_invite_code.setText("");
        }
        selectedText = tv_invite_code.getText().toString();
        return view;
    }


    private void sendInvitation() {
        if (StringUtils.isBlank(selectedText)) {
            ShowAlertInformation.showThemeDialog(getActivity(), getString(R.string.alert), getString(R.string.empty_invite_code));
        } else {
            try {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, selectedText);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

                Bundle params = new Bundle();
                params.putString("Code", selectedText);
                LaunchActivity.getLaunchActivity().getFireBaseAnalytics().logEvent(AnalyticsEvents.EVENT_INVITE, params);
            } catch (ActivityNotFoundException e) {
                new CustomToast().showToast(getActivity(), getString(R.string.app_missing));
            }
        }
    }

    private void copyText() {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", selectedText);
        clipboard.setPrimaryClip(clip);
        if (selectedText.equals("")) {
            new CustomToast().showToast(getActivity(), "Nothing to copy");
        } else {
            new CustomToast().showToast(getActivity(), "Copied");
        }
    }

    private void howItWorks() {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            Intent in = new Intent(getActivity(), WebViewActivity.class);
            in.putExtra(IBConstant.KEY_URL, Constants.URL_HOW_IT_WORKS);
            getActivity().startActivity(in);
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tv_how_it_works:
                howItWorks();
                break;
            case R.id.tv_copy:
                copyText();
                break;
            case R.id.btn_send_invite:
                sendInvitation();
                break;

        }
    }
}
