package com.app.chasebank;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.chasebank.bitmap.util.ImageFetcher;
import com.app.chasebank.entity.MyBranch;
import com.app.chasebank.entity.MyCompany;
import com.app.chasebank.entity.MyProduct;
import com.app.chasebank.fragment.ContactsFragment;
import com.app.chasebank.fragment.framework.Act;
import com.app.chasebank.fragment.framework.Screen;
import com.app.chasebank.util.DatabaseHelper;
import com.app.chasebank.util.DownloadService;

public class CompanyAct extends Act {
	public static ArrayList<MyCompany> companyList = new ArrayList<MyCompany>();
	private DatabaseHelper helper = null;
	
	private ViewPager mPager;
	private CompanyAdapter adapter;
	private ListView list;
	private ImageFetcher mImageFetcher;
	
	public static final String EXTRA_IMAGE = "extra_image";	
	
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
					
					if(DownloadService.companies != null && DownloadService.companies.size() > 0) {
						companyList.clear();
						for (Object[] data: DownloadService.companies) {
							MyCompany company = (MyCompany) data[0];
							
							companyList.add(company);
						}
					}
					
					// Refresh the adapter
					refreshAdapter();
				} else if (resultCode == DownloadService.RESULT_NETWORK_NOT_AVAILABLE) {
					/**
					 * Network not available
					 */
					toast("Sorry, Network is not available, please check your connection and try again.");
				} else if (resultCode == DownloadService.RESULT_CHECK_DB) {
					refreshAdapter();
				}
			}
		}
	};
	
	/**
	 * Get the list of companies from the downloaded data
	 */
	public void getCompanies(){
		companyList.clear();
		
		/**
		 * Check if the download service has any data, else load from the db
		 */
		if(DownloadService.companies == null || DownloadService.companies.size() <= 0) {
			// Load the company's information from the database
			if(helper == null) helper = new DatabaseHelper(getBaseContext());

			List<MyCompany> comp = helper.getAllCompanies();
			companyList.addAll(comp);

		}else {
			for (Object[] data: DownloadService.companies) {
				MyCompany company = (MyCompany) data[0];
				
				companyList.add(company);
			}
		}		
	}

	/**
	 * Check if the selected company has branches, so that we can just jump directly to products
	 * 
	 * @param companyID The Company Id
	 * @return boolean true if branches available, false otherwise
	 */
	private boolean hasBranches(String companyID) {
		ArrayList<MyBranch> branchList = new ArrayList<MyBranch>();
		if(helper == null) helper = new DatabaseHelper(getBaseContext());

		if(DownloadService.companies != null && DownloadService.companies.size() > 0) {
			for (Object[] data: DownloadService.companies) {
				MyCompany company = (MyCompany) data[0];

				if(company.getId().equalsIgnoreCase(companyID)) {
					branchList.addAll((ArrayList<MyBranch>) data[1]);

					if (branchList.size() > 0) return true;
					else return false;					
				}
			}

		}else {
			// Check if DB has any branches for this company
			List<MyBranch> branches = helper.getAllBranches(companyID);
			if(branches.size() > 0) return true;
			else return false;
		}

		return false;	
	}
	
	/**
	 * Check if the selected company has Products, so that we can just jump directly to Contacts
	 * 
	 * @param companyID The Company ID 
	 * @return boolean TRUE - has products, FALSE - otherwise
	 */
	private boolean hasProducts(String companyID) {
		ArrayList<MyProduct> productsList = new ArrayList<MyProduct>();
		if(helper == null) helper = new DatabaseHelper(getBaseContext());
		
		if(DownloadService.companies != null && DownloadService.companies.size() > 0) {
			for (Object[] data: DownloadService.companies) {
				MyCompany company = (MyCompany) data[0];
				
				if(company.getId().equalsIgnoreCase(companyID)) {
					productsList.addAll((ArrayList<MyProduct>) data[2]);
					
					if (productsList.size() > 0) return true;
					else return false;
				}
			}
		}else {
			// Check if DB has any branches for this company
			List<MyProduct> products = helper.getAllProducts(companyID);
			if(products.size() > 0) return true;
			else return false;
		}	
		
		return false;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_company);

		createCompanies();

		init();

		adapter = new CompanyAdapter();

		list = (ListView) findViewById(R.id.company_list);
		list.setAdapter(adapter);

		initUI();
	}

	private void init() {
		com.app.chasebank.bitmap.util.ImageCache.ImageCacheParams cacheParams = 
				new com.app.chasebank.bitmap.util.ImageCache.ImageCacheParams(this, ProductAct.IMAGE_CACHE_DIR);

		cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

		// The ImageFetcher takes care of loading images into our ImageView children asynchronously
		mImageFetcher = new ImageFetcher(this, 256);
		mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
		mImageFetcher.setImageFadeIn(false);
		mImageFetcher.setLoadingImage(R.drawable.products);

	}

	private void initUI() {
		TextView  noti = (TextView) findViewById(R.id.lead_section_notification),
				add_lead = (TextView) findViewById(R.id.lead_section_add_lead),
				myleads = (TextView) findViewById(R.id.lead_section_my_leads);

		ImageView back = (ImageView) findViewById(R.id.action_image);
		
		// :Notification For Number
		notification_number = (TextView) findViewById(R.id.lead_notification_number);
		notification_number.setVisibility(View.INVISIBLE);

		OnClickListener notilist = new OnClickListener() {

			@Override
			public void onClick(View v) {
				//Launch the Lead activity for now
				switch (v.getId()) {
				case R.id.lead_section_notification:

					break;
				case R.id.lead_section_my_leads:
					break;

				case R.id.action_image:
					finish();
					break;

				default:
					break;
				}
			}
		};

		add_lead.setEnabled(false);

		noti.setOnClickListener(notilist);
		back.setOnClickListener(notilist);
		add_lead.setOnClickListener(notilist);
		myleads.setOnClickListener(notilist);

		setFontRegular(add_lead);
		setFontRegular(myleads);
		setFontRegular(noti);
	}
	
	/**
	 * We create some dummy products here
	 */
	private void createCompanies() {
		/**
		 * Lets get the list of companies first, either from db or from the server
		 */
		getCompanies();

		for (MyCompany myCompany : companyList) {
			if(myCompany.getName().contains("Winton")) {
				myCompany.setLogo(R.drawable.icon_chasewinton_co_256);
			}
			else if(myCompany.getName().contains("Assurance")) {
				myCompany.setLogo(R.drawable.icon_chase_assurance);
			}
			else if(myCompany.getName().contains("Bank")) {
				myCompany.setLogo(R.drawable.icon_chasebank);
			}
			else if(myCompany.getName().contains("Rafiki")) {
				myCompany.setLogo(R.drawable.icon_rafiki);
			}
			else if(myCompany.getName().contains("Orchid")) {
				myCompany.setLogo(R.drawable.icon_orchid_capital);
			}
			else if(myCompany.getName().contains("LightHouse")) {
				myCompany.setLogo(R.drawable.icon_lighthouse);
			}
			else if(myCompany.getName().contains("Iman")) {
				myCompany.setLogo(R.drawable.icon_chase_iman);
			}
			else if(myCompany.getName().contains("GenCap")) {
				myCompany.setLogo(R.drawable.icon_gencap_trust);
			}
			else if(myCompany.getName().contains("Genghis")) {
				myCompany.setLogo(R.drawable.icon_genghis_capital);
			}
			else if(myCompany.getName().contains("Tulip")) {
				myCompany.setLogo(R.drawable.icon_tulip_healthcare);
			}
		}		
	}

	/**
	 * Refreshes the Adapter
	 */
	private void refreshAdapter() {	
		if(companyList.size() <= 0) {
			getCompanies();
		}
		
		for (MyCompany myCompany : companyList) {
			if(myCompany.getName().contains("Winton")) {
				myCompany.setLogo(R.drawable.icon_chasewinton_co_256);
			}
			else if(myCompany.getName().contains("Assurance")) {
				myCompany.setLogo(R.drawable.icon_chase_assurance);
			}
			else if(myCompany.getName().contains("Bank")) {
				myCompany.setLogo(R.drawable.icon_chasebank);
			}
			else if(myCompany.getName().contains("Rafiki")) {
				myCompany.setLogo(R.drawable.icon_rafiki);
			}
			else if(myCompany.getName().contains("Orchid")) {
				myCompany.setLogo(R.drawable.icon_orchid_capital);
			}
			else if(myCompany.getName().contains("LightHouse")) {
				myCompany.setLogo(R.drawable.icon_lighthouse);
			}
			else if(myCompany.getName().contains("Iman")) {
				myCompany.setLogo(R.drawable.icon_chase_iman);
			}
			else if(myCompany.getName().contains("GenCap")) {
				myCompany.setLogo(R.drawable.icon_gencap_trust);
			}
			else if(myCompany.getName().contains("Genghis")) {
				myCompany.setLogo(R.drawable.icon_genghis_capital);
			}
			else if(myCompany.getName().contains("Tulip")) {
				myCompany.setLogo(R.drawable.icon_tulip_healthcare);
			}
		}		

		adapter.notifyDataSetChanged();
	}

	@Override
	public void onResume() {
		super.onResume();
		mImageFetcher.setExitTasksEarly(false);

		registerReceiver(receiver, new IntentFilter(DownloadService.NOTIFICATION));
	}

	@Override
	protected void onPause() {
		super.onPause();
		mImageFetcher.setExitTasksEarly(true);
		mImageFetcher.flushCache();

		unregisterReceiver(receiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mImageFetcher.closeCache();
	}

	/**
	 * Called by the ViewPager child fragments to load images via the one ImageFetcher
	 */
	public ImageFetcher getImageFetcher() {
		return mImageFetcher;
	}

	/**
	 * Create an adapter to manage the horizontal swipings
	 * @author MATIVO-PC
	 */
	public class CompanyFragmentAdapter extends FragmentPagerAdapter {
		public CompanyFragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return companyList.size();
		}

		@Override
		public Fragment getItem(int position) {
			if(position == companyList.size()-1) 
				position = 0; 
			if(position < companyList.size()) return CompanyFragment.newInstance(position);
			else return null;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			super.destroyItem(container, position, object);
		}

	}

	public static class CompanyFragment extends Screen {

		private View v;
		private final static String POSITION = "position";
		private int position = 0;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			if(getArguments() != null) {
				this.position = getArguments().getInt(POSITION, 0);
			}
		}

		/**
		 * Create a new instance of CompanyFragment
		 * @param position
		 * @return
		 */
		public static Fragment newInstance(int position) {
			CompanyFragment comp = new CompanyFragment();
			Bundle bun = new Bundle();
			bun.putInt(POSITION, position);
			comp.setArguments(bun);
			return comp;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, 
				Bundle savedInstanceState) {
			v = getLayoutInflater(savedInstanceState).inflate(R.layout.x_list_company_holder, container, false);

			init();

			return v;
		}

		/**
		 * Initialise key variables and views
		 */
		private void init() {
			/**
			 * Get the company we are reffering to
			 */

		}
	}
	
	/**
	 * 	List adapter
	 * @author MATIVO-PC
	 *
	 */
	class CompanyAdapter extends BaseAdapter{
		private com.app.chasebank.bitmap.util.RecyclingImageView logo;
		private String mImageUrl;

		public CompanyAdapter() {
		}

		@Override
		public int getCount() {
			return companyList.size();
		}

		@Override
		public MyCompany getItem(int position) {
			return companyList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}
		
		@Override
		public View getView(final int position, View v, ViewGroup parent) {
			// Get company item from the arraylist at this position
			MyCompany company = getItem(position);

			// Inflate the view to be used as the holder
			v = getLayoutInflater().inflate(R.layout.list_product_holder_new, parent, false);

			// Set the content to be shown, fetched from the company instance acquired above.
			if(company != null) {
				logo = (com.app.chasebank.bitmap.util.RecyclingImageView) v.findViewById(R.id.list_company_logo);

				try {
					if(company.getLogo() <= 0) {
						mImageFetcher.loadImage(company.getImage_url(), logo);
					}else {
						logo.setImageDrawable(getResources().getDrawable(company.getLogo()));
					}

				}catch(Exception ex){
					ex.printStackTrace();
					mImageFetcher.loadImage(company.getImage_url(), logo);
				}

				// Onclick Listener
				v.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// Open Branches Fragment
						Bundle bun = new Bundle();

						// If no icon has been set for the company, just get the one downloaded instead
						if(companyList.get(position).getLogo() <= 0) {
							Bitmap bitmap = null;
							BitmapDrawable test = null;						
							try { test = (BitmapDrawable) logo.getDrawable(); } catch(Exception ex){ex.printStackTrace(); }						
							try { if(test != null) bitmap = test.getBitmap(); }catch(Exception ex){ex.printStackTrace(); }

							if(bitmap != null) {
								bun.putParcelable("company_icon", bitmap);
							}
						}

						bun.putSerializable("company", companyList.get(position));						
						Intent intent = null;

						if(hasBranches(companyList.get(position).getId())) {
							intent = new Intent(CompanyAct.this, Branch.class);

							intent.putExtras(bun);
							startActivity(intent);
						} else {
							if(hasProducts(companyList.get(position).getId())) {
								intent = new Intent(CompanyAct.this, ProductAct.class);
								bun.putSerializable("branch", null);

								intent.putExtras(bun);
								startActivity(intent);
							}else {
								// Lets redirect this quy to contacts
								bun.putSerializable("product", null);
								bun.putSerializable("branch", null);
								bun.putParcelable("product_icon", null);

								ContactsFragment conts = new ContactsFragment();
								conts.setArguments(bun);	                	
								switchScreen(conts);
							}
						}
					}
				});	

				return v;
			}else return null;
		}

	}

}
