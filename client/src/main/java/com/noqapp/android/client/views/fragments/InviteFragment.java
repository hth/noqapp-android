package com.noqapp.android.client.views.fragments;

import com.noqapp.android.client.R;
import com.noqapp.android.client.utils.Constants;
import com.noqapp.android.client.utils.ShowAlertInformation;
import com.noqapp.android.client.views.activities.LaunchActivity;
import com.noqapp.android.client.views.activities.WebViewActivity;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.InviteEvent;

import org.apache.commons.lang3.StringUtils;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chandra on 4/9/17.
 */
public class InviteFragment extends NoQueueBaseFragment {
    private final String TAG = InviteFragment.class.getSimpleName();

    @BindView(R.id.tv_how_it_works)
    protected TextView tv_how_it_works;

    @BindView(R.id.tv_copy)
    protected TextView tv_copy;

    @BindView(R.id.tv_title)
    protected TextView tv_title;

    @BindView(R.id.tv_details)
    protected TextView tv_details;

    @BindView(R.id.tv_invite_code)
    protected TextView tv_invite_code;

    @BindView(R.id.btn_send_invite)
    protected Button btn_send_invite;

    private String selectedText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_invite, container, false);
        ButterKnife.bind(this, view);
        tv_how_it_works.setPaintFlags(tv_how_it_works.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv_copy.setPaintFlags(tv_copy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Bundle bundle = getArguments();
        if (null != bundle) {
            String title = bundle.getString("title", "Invite Friends");
            String details = bundle.getString("details", "As a user of NoQApp, you can invite your friends and family.");
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

    @OnClick(R.id.btn_send_invite)
    public void sendInvitation() {
        if (StringUtils.isBlank(selectedText)) {
            ShowAlertInformation.showThemeDialog(getActivity(), getString(R.string.alert), getString(R.string.empty_invite_code));
        } else {
            try {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, selectedText);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

                Answers.getInstance().logInvite(new InviteEvent()
                        .putMethod("Invite")
                        .putCustomAttribute("Code", selectedText));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getActivity(), getString(R.string.app_missing), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.tv_copy)
    public void copyText() {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", selectedText);
        clipboard.setPrimaryClip(clip);
        if (selectedText.equals(""))
            Toast.makeText(getActivity(), "Nothing to copy", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getActivity(), "copied", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.tv_how_it_works)
    public void howItWorks() {
        if (LaunchActivity.getLaunchActivity().isOnline()) {
            Intent in = new Intent(getActivity(), WebViewActivity.class);
            in.putExtra("url", Constants.URL_HOW_IT_WORKS);
            getActivity().startActivity(in);
        } else {
            ShowAlertInformation.showNetworkDialog(getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // LaunchActivity.getLaunchActivity().setActionBarTitle(getString(R.string.screen_invite));
        // LaunchActivity.getLaunchActivity().enableDisableBack(true);
    }
}
