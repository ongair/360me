package com.app.chasebank.fragment;

import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.app.chasebank.CompanyAct;
import com.app.chasebank.R;
import com.app.chasebank.fragment.framework.DialogScreen;
import com.astuetz.PagerSlidingTabStrip;
import com.astuetz.PagerSlidingTabStrip.IconTabProvider;

public class LeadSubmittedFragment extends DialogScreen {
	
	public static LeadSubmittedFragment newInstance() {
		LeadSubmittedFragment f = new LeadSubmittedFragment();
		return f;
	}

	private View root;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (getDialog() != null) {
			getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		}

		root = inflater.inflate(R.layout.lead_summary_dialog, container, false);
		
		initUI();
		
		return root;
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
	    super.onActivityCreated(arg0);
	    getDialog().getWindow()
	    .getAttributes().windowAnimations = R.style.DialogAnimation;
	}
	
	private void initUI() {
		// TODO Inititalise the relevant views
		TextView desc = (TextView) root.findViewById(R.id.dialog_txt), 
				products = (TextView) root.findViewById(R.id.dialog_group_products),
				add_lead = (TextView) root.findViewById(R.id.dialog_submit_lead);
		
		getParent().setFontRegular(desc);
		getParent().setFontRegular(products);
		getParent().setFontRegular(add_lead);
		
		/**
		 * Onclick Listener
		 */
		OnClickListener notilist = new OnClickListener() {

			@Override
			public void onClick(View v) {
				//Launch the Lead activity for now
				switch (v.getId()) {
				case R.id.dialog_group_products:
					/**
					 * Chase points selected
					 */
					Intent intent = new Intent(getParent(), CompanyAct.class);
					startActivity(intent);
					break;
				case R.id.dialog_submit_lead:
					/**
					 * Submit Lead
					 */			
					Intent intents = new Intent(getParent(), CompanyAct.class);
					startActivity(intents);
					break;
				default:
					break;
				}
			}
		};
		
		add_lead.setOnClickListener(notilist);
		products.setOnClickListener(notilist);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onStart() {
		super.onStart();

		// change dialog width
		if (getDialog() != null) {

			int fullWidth = getDialog().getWindow().getAttributes().width;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
				Display display = getActivity().getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				fullWidth = size.x;
			} else {
				Display display = getActivity().getWindowManager().getDefaultDisplay();
				fullWidth = display.getWidth();
			}

			final int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
					.getDisplayMetrics());

			int w = fullWidth - padding;
			int h = getDialog().getWindow().getAttributes().height;

			getDialog().getWindow().setLayout(w, h);
		}
	}
}
