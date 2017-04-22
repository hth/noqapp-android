package com.noqapp.merchant.views.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.noqapp.merchant.R;
import com.noqapp.merchant.model.ManageQueueModel;
import com.noqapp.merchant.presenter.beans.JsonTopic;
import com.noqapp.merchant.views.activities.LaunchActivity;
import com.noqapp.merchant.helper.ShowAlertInformation;

import java.util.List;

public class LoginFragment extends Fragment {

	private Button btn_login;
	private EditText edt_email,edt_pwd;
	public LoginFragment() {
		super();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle args) {

		View view = inflater.inflate(R.layout.frag_login, container, false);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		btn_login = (Button) view.findViewById(R.id.btn_login);
		edt_email = (EditText) view.findViewById(R.id.edt_email);
		edt_pwd = (EditText) view.findViewById(R.id.edt_pwd);

		btn_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String email = edt_email.getText().toString().trim();
				String pwd = edt_pwd.getText().toString().trim();
				edt_email.setError(null);
				edt_pwd.setError(null);
				if (TextUtils.isEmpty(email) ) {
					edt_email.setError("Please enter email id");
				} if (!TextUtils.isEmpty(email) && !isValidEmail(email)) {
					edt_email.setError("Please enter valid email");
				} else if (pwd.equals("")) {
					edt_pwd.setError("Please enter password");
				}else {
					if (LaunchActivity.getLaunchActivity().isOnline()) {

					//Login Process
						LaunchActivity.getLaunchActivity().setSharPreferancename("", "",
								email, true);
						LaunchActivity.getLaunchActivity().replaceFragmentWithoutBackStack(R.id.frame_layout, new MerchantListFragment());

//                        ManageQueueModel.getQueues("123213","b@r.com",
//                                "$2a$15$ed3VSsc5x367CNiwQ3fKsemHSZUr.D3EVjHVjZ2cBTySc/l7gwPua");
                    } else {
						ShowAlertInformation.showDialog(getActivity(), "Network error", getString(R.string.offline));
					}
				}

			}
		});
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		LaunchActivity.getLaunchActivity().setActionBarTitle("Login");

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
	}

	private boolean isValidEmail(CharSequence target) {
		if (TextUtils.isEmpty(target)) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
		}
	}
}
