package com.app.chasebank;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.chasebank.entity.Lead;
import com.app.chasebank.fragment.LeadSubmittedFragment;
import com.app.chasebank.fragment.framework.Act;
import com.app.chasebank.util.CommonUtils;
import com.app.chasebank.util.DatabaseHelper;

public class LeadActivity extends Act {
	private String name, phone, email;
	private Bitmap bitmap;
	private Lead lead;
	private AsyncTask<Void, Void, Void> mChangeLeadStatus;
	
	private DatabaseHelper helper;
	private Button close_lead;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lead_new);
		
		helper = new DatabaseHelper(this);
		
		if(getIntent().getExtras().getString("NAME") != null)
			name = getIntent().getExtras().getString("NAME");

		if(getIntent().getExtras().getString("PHONE") != null)
			phone = getIntent().getExtras().getString("PHONE");

		if(getIntent().getExtras().getString("EMAIL") != null)
			email = getIntent().getExtras().getString("EMAIL");

		if(getIntent().getExtras().getSerializable("LEAD") != null)
			lead = (Lead) getIntent().getExtras().getSerializable("LEAD");

		if(getIntent().getExtras().getParcelable("PHOTO") != null)
			bitmap = getIntent().getExtras().getParcelable("PHOTO");

		initUI();
	}

	/**
	 * Initialise the UI Components
	 */
	private void initUI() {
		TextView call = (TextView) findViewById(R.id.lead_call_now),
				rem = (TextView) findViewById(R.id.lead_reminder),
				notes = (TextView) findViewById(R.id.lead_notes),
				noti = (TextView) findViewById(R.id.lead_section_notification),
				add_lead = (TextView) findViewById(R.id.lead_section_add_lead),
				myleads = (TextView) findViewById(R.id.lead_section_my_leads);
		
		close_lead = (Button)findViewById(R.id.btn_close_lead);		
		ImageView back = (ImageView) findViewById(R.id.action_back);
		/**
		 * If the lead is closed, we will just show 'LEAD CLOSED'
		 * And make the button unclickable
		 */
		if(lead.getStatus().equalsIgnoreCase("Verified")) {
			close_lead.setText("LEAD CLOSED");	
			setFontSemiBold(close_lead);
			close_lead.setClickable(false);
		}
		
		/**
		 * Disable Notes, and reminder
		 */
		rem.setEnabled(false);
		notes.setEnabled(false);
		
		((TextView) findViewById(R.id.action_title)).setText(name);
		((TextView) findViewById(R.id.contact_phone)).setText(phone);
		
		if(email != null && !email.equals("")) ((TextView) findViewById(R.id.contact_email)).setText(email);
		else ((TextView) findViewById(R.id.contact_phone)).setVisibility(View.GONE);
		
		ImageView photov = (ImageView) findViewById(R.id.contacts_photo);
		
		// Notification For Number
		notification_number = (TextView) findViewById(R.id.lead_notification_number);
		notification_number.setVisibility(View.INVISIBLE);
		
		if(bitmap != null){
			photov.setImageBitmap(bitmap);
		}

		OnClickListener notilist = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Launch the Lead activity for now

				switch (v.getId()) {
				case R.id.lead_section_notification:
					//Notification-DISABLED
					
					break;					
				case R.id.lead_section_add_lead:
					//Open / Start Lead cycle, Open Companies
					finish();
					
					/**
					 * Start the company Act
					 */
					Intent intent = new Intent(LeadActivity.this, CompanyAct.class);
					startActivity(intent);
					
					break;
				case R.id.lead_call_now:
					//toast("Notification clicked..!");
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:"+phone));
					startActivity(callIntent);
					
					break;
				case R.id.lead_reminder:
					//toast("Notification clicked..!");
					
					break;
				case R.id.lead_notes:
					//toast("Notification clicked..!");
					
					break;

				case R.id.btn_close_lead:
					/**
					 * We can now close the lead
					 */
					changeLeadStatus();
					break;
				case R.id.action_image:
					finish();
					break;

				default:
					break;
				}
			}
		};

		myleads.setEnabled(false);

		noti.setOnClickListener(notilist);
		add_lead.setOnClickListener(notilist);
		myleads.setOnClickListener(notilist);
		back.setOnClickListener(notilist);
		close_lead.setOnClickListener(notilist);

		//Set the listeners
		call.setOnClickListener(notilist);
		rem.setOnClickListener(notilist);
		notes.setOnClickListener(notilist);

		//Set Fonts
		setFontRegular(call);
		setFontRegular(rem);
		setFontRegular(notes);
		setFontRegular(noti);
		setFontRegular(add_lead);
		setFontRegular(myleads);		
		setFontRegular(close_lead);

		setFontRegular((TextView)  findViewById(R.id.contact_email));
		setFontRegular((TextView)  findViewById(R.id.contact_phone));

		setFontRegular((TextView)  findViewById(R.id.lead_text_1));
	}

	/**
	 * Change the status of the lead
	 */
	private void changeLeadStatus() {
		/**
		 * Show the progress bar
		 */
		//setSupportProgressBarIndeterminateVisibility(true);
		if(lead != null) { 
			mChangeLeadStatus = new AsyncTask<Void, Void, Void>() {
				private boolean isLeadStatusChanged;
				
				@Override
				protected void onPreExecute() {
					super.onPreExecute();				
				}
				
				@Override
				protected Void doInBackground(Void... params) {
					try {
						isLeadStatusChanged = CommonUtils.changeLeadStatus(getUserId(), lead.getId(), "close", helper);					
					} catch (Exception e) {
						e.printStackTrace();
					}				
					return null;
				}
				
				@Override
				protected void onPostExecute(Void result) {				
					if(isLeadStatusChanged) {
						toast("Lead has been closed..");
						
						close_lead.setText("LEAD CLOSED");	
						setFontSemiBold(close_lead);
						close_lead.setClickable(false);
						
					}else {
						toast("Ooops, Lead closure failed, Please try again Later.");
					}
					mChangeLeadStatus = null;
					
				}
			};
			mChangeLeadStatus.execute(null, null, null);
		}
	}

}
