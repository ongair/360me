package com.app.chasebank;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.app.chasebank.fragment.SummaryFragment;
import com.app.chasebank.fragment.framework.Act;
import com.app.chasebank.util.DatabaseHelper;
import com.app.chasebank.util.DownloadService;
import com.astuetz.PagerSlidingTabStrip;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class ROActivity extends Act {
	
	private TabAdapter adapter;
	private ViewPager mPager;
	private PagerSlidingTabStrip tabs;
	private TextView tab_leads, tab_closed, tab_pending, tab_stalled;
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();

			if (bundle != null) {	    	  
				int resultCode = bundle.getInt(DownloadService.RESULT);

				if (resultCode == DownloadService.RESULT_COMPANIES) {
					/**
					 * Companies update complete
					 */
					//toast("Companies successfully updated..");

				} else if (resultCode == DownloadService.RESULT_LEADS) {
					/**
					 * Leads update complete
					 */
					//toast("Leads successfully updated..");

				} else if (resultCode == DownloadService.RESULT_NETWORK_NOT_AVAILABLE) {
					/**
					 * Network not available
					 */
					toast("Sorry, Network is not available, please check your connection and try again.");
				}
			}
		}
	};
	
	private DatabaseHelper helper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myleads);
		
		adapter = new TabAdapter(getSupportFragmentManager());
		
		mPager = (ViewPager)findViewById(R.id.pager);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setVisibility(View.GONE);
		
		mPager.setAdapter(adapter);				
		
		tabs.setViewPager(mPager);
		
		init();
		
		initLeadsTab();
		initClosedTab();
		initPendingTab();
		initStalledTab();
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
	 * Initialise the layout views
	 */
	private void init() {
		TextView noti = (TextView) findViewById(R.id.lead_section_notification),
				add_lead = (TextView) findViewById(R.id.lead_section_add_lead),
				myleads = (TextView) findViewById(R.id.lead_section_my_leads);
		
		//Custom Tabs
		tab_leads = (TextView) findViewById(R.id.tab_lead);
		tab_closed = (TextView) findViewById(R.id.tab_closed);
		tab_pending = (TextView) findViewById(R.id.tab_pending);
		tab_stalled = (TextView) findViewById(R.id.tab_stalled);
		
		// :Number of Notifications
		notification_number = (TextView) findViewById(R.id.lead_notification_number);
		notification_number.setVisibility(View.INVISIBLE);
		
		myleads.setEnabled(false);

		//Click Listener for Notifications
		OnClickListener notilist = new OnClickListener() {

			@Override
			public void onClick(View v) {
				//Launch the Lead activity for now
				switch (v.getId()) {
				case R.id.lead_section_notification:
					//Notification-DISABLED
					startActivity(new Intent(ROActivity.this, LeadActivity.class));
					break;
					
				case R.id.lead_section_add_lead:
					//Open / Start Lead cycle, Open Companies
					finish();
					
					Intent intent = new Intent(ROActivity.this, CompanyAct.class);
					startActivity(intent);
					break;
				case R.id.lead_section_my_leads:
					//My leads - DISABLED
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
		tab_leads.setText(LeadSummaryActivity.leadsList.size()+" Leads");
	}

	/**
	 * Set the number of closed tabs so far
	 */
	private void initClosedTab() {
		tab_closed.setText(LeadSummaryActivity.closedList.size()+" Closed");
	}

	/**
	 * Set the number of leads pending 
	 */
	private void initPendingTab() {
		tab_pending.setText(LeadSummaryActivity.pendingList.size()+" Pending");
	}

	/**
	 * Set the number of Leads Stalled
	 */
	private void initStalledTab() {
		tab_stalled.setText(LeadSummaryActivity.stalledList.size()+" Stalled");
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
	 * Load leads
	 */
	private void loadStatus() {		
		if(helper == null) helper = new DatabaseHelper(getBaseContext());
		List<Lead> leads = helper.getAllLeads();
		
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
			SummaryFragment sum = new SummaryFragment();
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

			sum.setArguments(bun);
			return sum;
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
	}

}
