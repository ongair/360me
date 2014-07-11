package com.app.chasebank.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.chasebank.CompanyAct;
import com.app.chasebank.LeadSummaryActivity;
import com.app.chasebank.R;
import com.app.chasebank.fragment.framework.Screen;

public final class LoggedInFragment extends Screen implements OnClickListener{
	
	private View v;
	private TextView chaseProducts, submitLead;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		v = getLayoutInflater(savedInstanceState).inflate(R.layout.act_logged_in, container, false);
		
		init();
		return v;
	}
	
	private void init() {
		//Initialise Buttons
		chaseProducts = (TextView) v.findViewById(R.id.act_log_chase_products);
		submitLead = (TextView) v.findViewById(R.id.act_log_submit_lead);
		
		//Set Fonts
		getParent().setFontRegular((TextView) v.findViewById(R.id.act_log_chase_products));
		getParent().setFontRegular((TextView) v.findViewById(R.id.act_log_submit_lead));
		getParent().setFontRegular((TextView) v.findViewById(R.id.act_log_title));
		getParent().setFontRegular((TextView) v.findViewById(R.id.act_log_txt));
		
		//Onclick Listeners for the two button above
		chaseProducts.setOnClickListener(this);
		submitLead.setOnClickListener(this);
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.act_log_chase_products:
			/**
			 * Chase points selected
			 */
			//getParent().switchScreen(LoggedInFragment.this, new CompanyFragment());
			Intent intent = new Intent(getParent(), CompanyAct.class);
			startActivity(intent);
			break;

		case R.id.act_log_submit_lead:
			/**
			 * Submit Lead
			 */			
			//getParent().switchScreen(new ContactsFragment());
			Intent intents = new Intent(getParent(), CompanyAct.class);
			startActivity(intents);
			break;
		default:
			break;
		}
		
	}
}
