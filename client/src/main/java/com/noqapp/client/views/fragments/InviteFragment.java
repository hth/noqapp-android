package com.noqapp.client.views.fragments;

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

import com.noqapp.client.R;
import com.noqapp.client.views.activities.LaunchActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chandra on 4/9/17.
 */

public class InviteFragment extends NoQueueBaseFragment {


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
    private final String TAG = InviteFragment.class.getSimpleName();

    public InviteFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_invite, container, false);
        ButterKnife.bind(this, view);
        tv_how_it_works.setPaintFlags(tv_how_it_works.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv_copy.setPaintFlags(tv_copy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String title = bundle.getString("title", "Hardcoded Title");
            String details = bundle.getString("details", "Hardcoded Details....");
            String invite_code = bundle.getString("invite_code", "");

            if (invite_code.isEmpty()) {
                btn_send_invite.setEnabled(false);
            }
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

        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, selectedText);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "No app to share invitation", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.tv_copy)
    public void copyText() {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", selectedText);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getActivity(), "copied", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.tv_how_it_works)
    public void howItWorks() {
        Bundle b = new Bundle();
        b.putString("title", tv_title.getText().toString());
        b.putString("details", tv_details.getText().toString());
        InviteDetailFragment indf = new InviteDetailFragment();
        indf.setArguments(b);
        replaceFragmentWithBackStack(getActivity(), R.id.frame_layout, indf, TAG, LaunchActivity.tabMe);
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity.getLaunchActivity().setActionBarTitle("Invite");
        LaunchActivity.getLaunchActivity().enableDisableBack(true);
    }
}
