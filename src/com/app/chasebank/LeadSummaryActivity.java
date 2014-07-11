package com.app.chasebank;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.app.chasebank.entity.Lead;
import com.app.chasebank.entity.MyBranch;
import com.app.chasebank.entity.MyCompany;
import com.app.chasebank.entity.MyContact;
import com.app.chasebank.entity.MyProduct;
import com.app.chasebank.fragment.LeadSubmittedFragment;
import com.app.chasebank.fragment.SummaryFragment;
import com.app.chasebank.fragment.framework.Act;
import com.app.chasebank.util.CommonUtils;
import com.app.chasebank.util.DatabaseHelper;
import com.app.chasebank.util.DownloadService;
import com.app.chasebank.util.LeadsLoadedListener;
import com.astuetz.PagerSlidingTabStrip;
import com.viewpagerindicator.IconPagerAdapter;

public class LeadSummaryActivity extends Act {

	private TabAdapter adapter;
	private ViewPager mPager;
	private PagerSlidingTabStrip tabs;
	private TextView tab_leads, tab_closed, tab_pending, tab_stalled;	

	private MyCompany selCompany = null;
	private MyProduct selProduct = null;
	private MyContact selContact = null;
	private String details = null;
	private Bitmap bitmap = null;
	private MyBranch selBranch = null;
	private String email = null;

	private AsyncTask<Void, Void, Void> mSubmitLead;
	private AsyncTask<Void, Void, Void> mLoadStatus;
	public LeadsLoadedListener listener;
	private DatabaseHelper helper;

	/*
	 * New Leads that have not been assigned
	 */
	public static ArrayList<Lead> leadsList = new ArrayList<Lead>();
	/*
	 * Leads that have been closed
	 */
	public static ArrayList<Lead> closedList = new ArrayList<Lead>();
	/*
	 * Leads that are pending
	 */
	public static ArrayList<Lead> pendingList = new ArrayList<Lead>();
	/*
	 * Leads that have stalled
	 */
	public static ArrayList<Lead> stalledList = new ArrayList<Lead>();
	/*
	 * All the leads 
	 */
	public static ArrayList<Lead> mLeads = new ArrayList<Lead>();

	private AsyncTask<Void, Void, Void> mLoadCompanies;

	public static ArrayList<Object[]> companies;

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			
			if (bundle != null) {	    	  
				int resultCode = bundle.getInt(DownloadService.RESULT);
				
				if (resultCode == DownloadService.RESULT_LEADS) {
					/**
					 * Leads update complete
					 */
					
					// Update the ui
					if(DownloadService.leads != null && DownloadService.leads.size() > 0) {
						loadAllStatus();
						
						adapter.notifyDataSetChanged();
					}
				} else if (resultCode == DownloadService.RESULT_NETWORK_NOT_AVAILABLE) {
					/**
					 * Network not available
					 */
					toast("Sorry, Network is not available, please check your connection and try again.");
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_summary_lead);

		helper = new DatabaseHelper(this);

		try {
			if(getIntent().getExtras().getSerializable("company") != null)
				selCompany = (MyCompany) getIntent().getExtras().getSerializable("company");

			if(getIntent().getExtras().getSerializable("product") != null)
				selProduct = (MyProduct) getIntent().getExtras().getSerializable("product");

			if(getIntent().getExtras().getSerializable("contact") != null)
				selContact = (MyContact) getIntent().getExtras().getSerializable("contact");

			if(getIntent().getExtras().getSerializable("branch") != null)
				selBranch = (MyBranch) getIntent().getExtras().getSerializable("branch");

			if(getIntent().getExtras().getString("details") != null)
				details = getIntent().getExtras().getString("details");

			if(getIntent().getExtras().getString("email") != null)
				email = getIntent().getExtras().getString("email");

			if(getIntent().getExtras().getParcelable("contactPhoto") != null)
				bitmap = getIntent().getExtras().getParcelable("contactPhoto");

			if (selCompany != null && selProduct != null && selContact != null) {
				/**
				 * Submit data
				 */
				String branchId, productId;
				if(selBranch == null) branchId = "null";
				else branchId = selBranch.getId();

				if(selProduct == null) productId = "null";
				else productId = selProduct.getId();

				if(isNetworkAvailable()) {
					Lead lead = new Lead(null, selCompany.getId(), branchId, productId, selContact.getName(), selContact.getPhone(), email, details);
					submitLead(lead);
				}else {
					/**
					 * Network not available
					 */
					toast("Sorry, Network is not available, please check your connection and try again.");
				}				
			}
		}catch(Exception ex) {
			//ex.printStackTrace();
		}

		initViews();

		initList();

		loadAllStatus();

		initialiseAdapter();

	}

	/**
	 * Here we initialise the adapter, then attach it to the mPager. We also reset the tabs
	 */
	public void initialiseAdapter() {
		adapter = new TabAdapter(getSupportFragmentManager());

		mPager = (ViewPager)findViewById(R.id.pager);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setVisibility(View.GONE);

		mPager.setAdapter(adapter);				

		tabs.setViewPager(mPager);

		init();
	}

	/**
	 * Initialise views
	 */
	private void initViews() {
		TextView noti = (TextView) findViewById(R.id.lead_section_notification),
				add_lead = (TextView) findViewById(R.id.lead_section_add_lead),
				myleads = (TextView) findViewById(R.id.lead_section_my_leads);
		
		// :Number of Notifications
		notification_number = (TextView) findViewById(R.id.lead_notification_number);
		notification_number.setVisibility(View.INVISIBLE);
		
		//Custom Tabs
		tab_leads = (TextView) findViewById(R.id.tab_lead);
		tab_closed = (TextView) findViewById(R.id.tab_closed);
		tab_pending = (TextView) findViewById(R.id.tab_pending);
		tab_stalled = (TextView) findViewById(R.id.tab_stalled);
		
		//Click Listener for Notifications
		OnClickListener notilist = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.lead_section_notification:
					//Notification-DISABLED
					//Launch the Lead activity for now
					//startActivity(new Intent(getBaseContext(), LeadActivity.class));
					break;
				case R.id.lead_section_add_lead:
					//Open / Start Lead cycle, Open Companies
					Intent intent = new Intent(LeadSummaryActivity.this, CompanyAct.class);
					startActivity(intent);

					break;
				case R.id.lead_section_my_leads:
					startActivity(new Intent(LeadSummaryActivity.this, MyLeadSummaryActivity.class));
					break;

				default:
					break;
				}
			}
		};

		//Click Listener for the Tabs
		OnClickListener tabsListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.tab_lead:
					mPager.setCurrentItem(0);
					break;
				case R.id.tab_closed:
					mPager.setCurrentItem(1);					
					break;
				case R.id.tab_pending:
					mPager.setCurrentItem(2);
					break;
				case R.id.tab_stalled:
					mPager.setCurrentItem(3);
					break;
				default:
					break;
				}				
			}
		};

		//Set the listeners for the notifications
		noti.setOnClickListener(notilist);
		add_lead.setOnClickListener(notilist);
		myleads.setOnClickListener(notilist);

		//Set the listeners for the tabs
		tab_leads.setOnClickListener(tabsListener);
		tab_closed.setOnClickListener(tabsListener);
		tab_pending.setOnClickListener(tabsListener);
		tab_stalled.setOnClickListener(tabsListener);

		//Set Fonts
		setFontRegular(noti);
		setFontRegular(add_lead);
		setFontRegular(myleads);

		setFontRegular(tab_closed);
		setFontRegular(tab_leads);
		setFontRegular(tab_pending);
		setFontRegular(tab_stalled);

		setFontRegular((TextView)  findViewById(R.id.text_points_title));
		setFontSemiBold((TextView)  findViewById(R.id.action_title));
		setFontRegular((Button)  findViewById(R.id.btn_redeem));
	}

	/**
	 * Initialise Tabs
	 */
	private void initList() {
		initLeadsTab();
		initClosedTab();
		initPendingTab();
		initStalledTab();
	}

	/**
	 * Initialise views
	 */
	private void init() {
		//Listener for the viewPage 
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				switch (position) {
				case 0:
					tabsUnselected();
					isTabLeadsSelected(true);
					break;
				case 1:
					tabsUnselected();
					isTabClosedSelected(true);
					break;
				case 2:
					tabsUnselected();
					isTabPendingSelected(true);
					break;
				case 3:
					tabsUnselected();
					isTabStalledSelected(true);
					break;

				default:
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	/**
	 * Set the number of leads so far
	 */
	private void initLeadsTab() {
		tab_leads.setText(leadsList.size()+" Leads");
	}

	/**
	 * Set the number of closed tabs so far
	 */
	private void initClosedTab() {
		tab_closed.setText(closedList.size()+" Closed");
	}

	/**
	 * Set the number of leads pending 
	 */
	private void initPendingTab() {
		tab_pending.setText(pendingList.size()+" Pending");
	}

	/**
	 * Set the number of Leads Stalled
	 */
	private void initStalledTab() {
		tab_stalled.setText(stalledList.size()+" Stalled");
	}

	/**
	 * Unselect all the tabs.
	 */
	private void tabsUnselected() {
		tab_leads.setBackgroundResource(R.drawable.act_tab_leads);
		tab_closed.setBackgroundResource(R.drawable.act_tab_closed);
		tab_pending.setBackgroundResource(R.drawable.act_tab_pending);
		tab_stalled.setBackgroundResource(R.drawable.act_tab_stalled);
	}

	/**
	 * Select or deselect tab Leads
	 * 
	 * @param isSelected If the tab is selected or deselected
	 */
	private void isTabLeadsSelected(boolean isSelected) {
		//TODO change the background of the text View as desired
		if (isSelected) {
			tab_leads.setBackgroundResource(R.drawable.tab_leads);
		} else {
			tab_leads.setBackgroundResource(R.drawable.act_tab_leads);
		}
	}

	/**
	 * Select or deselect tab closed
	 * 
	 * @param isSelected If the tab is selected or deselected
	 */
	private void isTabClosedSelected(boolean isSelected) {
		if (isSelected) {
			tab_closed.setBackgroundResource(R.drawable.tab_closed);
		} else {
			tab_closed.setBackgroundResource(R.drawable.act_tab_closed);
		}
	}

	/**
	 * Select or deselect tab pending
	 * 
	 * @param isSelected If the tab is selected or deselected
	 */
	private void isTabPendingSelected(boolean isSelected) {
		if (isSelected) {
			tab_pending.setBackgroundResource(R.drawable.tab_pending);
		} else {
			tab_pending.setBackgroundResource(R.drawable.act_tab_pending);
		}
	}

	/**
	 * Select or deselect tab stalled
	 * 
	 * @param isSelected If the tab is selected or deselected
	 */
	private void isTabStalledSelected(boolean isSelected) {
		if (isSelected) {
			tab_stalled.setBackgroundResource(R.drawable.tab_stalled);
		} else {
			tab_stalled.setBackgroundResource(R.drawable.act_tab_stalled);
		}
	}
	
	/**
	 * Load leads
	 */
	private void loadAllStatus() {		
		if(helper == null) helper = new DatabaseHelper(getBaseContext());

		List<Lead> leads = helper.getAllLeads();

		if(leads.size() > 0) {
			mLeads.clear();
			mLeads.addAll(leads);
			
			leads.clear();
			/**
			 * We clear all the lists first before we proceed
			 */
			leadsList.clear();
			pendingList.clear();
			closedList.clear();
			stalledList.clear();
			
			// Sample all the leads into the different statuses that we have
			for (Lead lead : mLeads) {
				if(lead.getStatus().equalsIgnoreCase("new")) {
					leadsList.add(lead);
				}else if(lead.getStatus().equalsIgnoreCase("pending")) {
					pendingList.add(lead);
				}else if(lead.getStatus().equalsIgnoreCase("Verified")) {
					closedList.add(lead);
				}else if(lead.getStatus().equalsIgnoreCase("stalled")) {
					stalledList.add(lead);
				}
			}

			//Refresh the lead tabs
			initList();
		}else Log.i(TAG, "LEAD FROM DB: NOT FOUND");		
	}
		
	@Override
	protected void onResume() {
		super.onResume();

		registerReceiver(receiver, new IntentFilter(DownloadService.NOTIFICATION));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}	  
	
	/**
	 * Submits a new lead to the server
	 * @param lead
	 */
	private void submitLead(final Lead lead) {
		/**
		 * Show the progress bar
		 */
		setSupportProgressBarIndeterminateVisibility(true);

		mSubmitLead = new AsyncTask<Void, Void, Void>() {
			private boolean isLeadSubmitted;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();				
			}


			@Override
			protected Void doInBackground(Void... params) {
				try {
					isLeadSubmitted = CommonUtils.submitLead(lead, helper, getUserId());					
				} catch (Exception e) {
					e.printStackTrace();
				}				
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {				
				if(isLeadSubmitted) {

					//Add the contact to the leads
					leadsList.add(lead);

					//Adjust number of items in the leads textview
					initLeadsTab();

					//Re-Initialise the segments again
					adapter.notifyDataSetChanged();

					Log.i(TAG, selCompany.getName()+", "+selProduct.getName()+", "+selContact.getName());

					/**
					 * Show the submitted dialog
					 */
					try {
						LeadSubmittedFragment dialog = new LeadSubmittedFragment();
						dialog.show(getSupportFragmentManager(), "LeadSubmittedFragment");
					}catch(Exception ex) {ex.printStackTrace(); }

				}else {
					toast("Ooops, Could not upload new Lead. We will try it for you later");

				}
				mSubmitLead = null;

			}
		};
		mSubmitLead.execute(null, null, null);

	}
	
	/**
	 * A tab adapter used to create and initialise the tabs and view pager
	 * @author MATIVO-PC	 *
	 */
	class TabAdapter extends FragmentPagerAdapter implements IconPagerAdapter{
		private String[] data = {"3 Leads", "2 Closed", "7 Pending", "1 Stalled"};

		public TabAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			SummaryFragment sums = new SummaryFragment();
			Bundle bun = new Bundle();

			switch (position) {
			case 0:
				bun.putInt(SummaryFragment.LEADTYPE, SummaryFragment.SUBMITTED);
				break;
			case 1:
				bun.putInt(SummaryFragment.LEADTYPE, SummaryFragment.CLOSED);
				break;
			case 2:
				bun.putInt(SummaryFragment.LEADTYPE, SummaryFragment.PENDING);
				break;
			case 3:
				bun.putInt(SummaryFragment.LEADTYPE, SummaryFragment.STALLED);
				break;
			default:
				bun.putInt(SummaryFragment.LEADTYPE, SummaryFragment.SUBMITTED);
				break;
			}

			sums.setArguments(bun);
			return sums;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return data[position];
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public int getIconResId(int index) {
			return 0;
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();

		}

	}
	
	@Override
	public void onBackPressed() {
		// Close this activity. Open the main acvitity
		attemptLogout();
		//Finish this activity
		finish();
	}
}
