package com.app.chasebank.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.chasebank.LoginFragment;
import com.app.chasebank.MainActivity;
import com.app.chasebank.R;
import com.app.chasebank.fragment.framework.Screen;

public final class LoginInfoFragment extends Screen {
	
	private View v;
	private TextView loginText;
	private TextView mSignInButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		v = getLayoutInflater(savedInstanceState).inflate(R.layout.tour_login_signup, container, false);
		
		init();
		return v;
	}

	private void init() {
		//Login Information to display
		loginText = (TextView) v.findViewById(R.id.app_loginsp_text);
		
		mSignInButton = (TextView) v.findViewById(R.id.login_button);
		mSignInButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/**
				 * Transition to login Activity
				 */
				Intent intent = new Intent(getParent(), LoginFragment.class);
				startActivityForResult(intent, MainActivity.LAUNCH_ACTIVITY);
			}
		});

		//The descriptive text view that we dont need to retain their reference
		getParent().setFontRegular((TextView) v.findViewById(R.id.app_loginsp_text));
		getParent().setFontRegular((TextView) v.findViewById(R.id.app_loginsp_title));
		
		//Set Fonts
		getParent().setFontRegular(loginText);
		getParent().setFontSemiBold(mSignInButton);		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}
