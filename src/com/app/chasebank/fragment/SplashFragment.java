package com.app.chasebank.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.chasebank.R;
import com.app.chasebank.fragment.framework.Screen;

public final class SplashFragment extends Screen {

	private View v;
	private TextView skiptext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		v = getLayoutInflater(savedInstanceState).inflate(R.layout.tour_splash_screen, container, false);
		
//		skiptext = (TextView)v.findViewById(R.id.swipe_left);
		
		//getParent().setFontRegular(skiptext);
		
		init();
		
		return v;
	}
	
	/**
	 * Initialise views and set listeners
	 */
	private void init() {
		
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}
