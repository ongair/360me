package com.app.chasebank.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.chasebank.R;
import com.app.chasebank.fragment.framework.Screen;

public final class ChasePointsInfoFragment extends Screen {

	private View v;
	private TextView chasePointsText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		v = getLayoutInflater(savedInstanceState).inflate(R.layout.tour_chase_points, container, false);
		
		init();
		return v;
	}
	
	/**
	 * Initialise views
	 */
	private void init() {
		//Chase Points Information to display
		chasePointsText = (TextView) v.findViewById(R.id.app_chasepoints_text);
		
		//Set Fonts
		getParent().setFontRegular(chasePointsText); 
		getParent().setFontSemiBold((TextView) v.findViewById(R.id.app_chasepoints_title));		
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}
