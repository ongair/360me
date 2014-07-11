package com.app.chasebank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.app.chasebank.fragment.ChasePointsInfoFragment;
import com.app.chasebank.fragment.LoginInfoFragment;
import com.app.chasebank.fragment.SplashFragment;
import com.app.chasebank.fragment.framework.Act;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;

public class MainActivity extends Act {
	private String[] data= {"Splash", "Login Info", "Chase Points"};	
	private ViewPager mPager;
	private PageIndicator mIndicator;
	private View loginbtn;
	private boolean isOpen = false;
	
	public static final int LAUNCH_ACTIVITY = 84;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
		
		//View Pager
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(adapter);
				
		//View Page Indicator
		mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
						
		//Incase there is a message, we can show
		String message = getIntent().getStringExtra(LoginFragment.MESSAGE);
		if(message != null && !message.equals("")) {
			//message present
			toast(message);
		}		
	}

	class TabAdapter extends FragmentPagerAdapter implements IconPagerAdapter{
		public TabAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) { 
			switch (position) {
			case 0:
				/**
				 * We set the skiptext to visible before
				 */
				return new SplashFragment();
			case 1:
				/**
				 * We set the skiptext to invisible before
				 */
				return new LoginInfoFragment();
			case 2:
				/**
				 * We set the skiptext to invisible before
				 */
				return new ChasePointsInfoFragment();
			default:
				return null;
			}
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return data[position];
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public int getIconResId(int index) {			
			return 0;
		}
		
		@Override
		public Object instantiateItem(View container, int position) {
			Log.i(TAG, "Positionxx: "+position);
			return super.instantiateItem(container, position);
		}
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		super.onBackPressed();
	}
}
