package com.noqapp.merchant.views.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.noqapp.merchant.R;
import com.noqapp.merchant.helper.ShowAlertInformation;
import com.noqapp.merchant.model.LoginModel;
import com.noqapp.merchant.model.MerchantProfileModel;
import com.noqapp.merchant.presenter.beans.JsonMerchant;
import com.noqapp.merchant.views.activities.LaunchActivity;
import com.noqapp.merchant.views.interfaces.LoginPresenter;
import com.noqapp.merchant.views.interfaces.MerchantPresenter;

import org.apache.commons.lang3.StringUtils;

public class LoginFragment extends Fragment implements LoginPresenter,MerchantPresenter{

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
		LoginModel.loginPresenter=this;
		MerchantProfileModel.merchantPresenter=this;
		btn_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideKeyBoard();
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
						LaunchActivity.getLaunchActivity().progressDialog.show();
						LoginModel.login(email, pwd);
                    } else {
						ShowAlertInformation.showNetworkDialog(getActivity());
					}
				}

			}
		});
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		LaunchActivity.getLaunchActivity();
		LaunchActivity.getLaunchActivity().setActionBarTitle("");
		LaunchActivity.getLaunchActivity().toolbar.setVisibility(View.GONE);
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


	@Override
	public void loginResponse(String email, String outh) {
		if(StringUtils.isNotBlank(email) && StringUtils.isNotBlank(outh)) {
			LaunchActivity.getLaunchActivity().setSharPreferancename("", "",
					email, outh, true);
			MerchantProfileModel.fetch(email, outh);
		}else{
			LaunchActivity.getLaunchActivity().dismissProgress();
			Toast.makeText(getActivity(),"Invalid Credential",Toast.LENGTH_LONG).show();

		}
	}

	@Override
	public void loginError() {
		LaunchActivity.getLaunchActivity().dismissProgress();
	}

	@Override
	public void merchantResponse(JsonMerchant jsonMerchant) {
		if(null!=jsonMerchant){
			LaunchActivity.getLaunchActivity().setUserName(jsonMerchant.getJsonProfile().getName());
			MerchantListFragment mlf =new MerchantListFragment();
			Bundle b =new Bundle();
			b.putSerializable("jsonMerchant",jsonMerchant);
			mlf.setArguments(b);
			LaunchActivity.getLaunchActivity().replaceFragmentWithoutBackStack(R.id.frame_layout, mlf);
		}
		LaunchActivity.getLaunchActivity().dismissProgress();
	}

	@Override
	public void merchantError() {
		LaunchActivity.getLaunchActivity().dismissProgress();
	}


	private void hideKeyBoard(){
		View view = getActivity().getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
}
