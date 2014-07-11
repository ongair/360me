package com.app.chasebank;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

public class ProductAct extends Act {
	public static ArrayList<MyProduct> productsList = new ArrayList<MyProduct>();	
	private ViewPager mPager;
	public static  MyBranch selBranch;
	public static MyCompany selCompany;

	public static final String IMAGE_CACHE_DIR = "images";
	public static final String EXTRA_IMAGE = "extra_image";

	private ImageFetcher mImageFetcher;
	private DatabaseHelper helper;
	public static Bitmap company_icon;

	/**
	 * Get the Products for the given company
	 * 
	 * @param companyID Company ID
	 */
	private void getProduct(String companyID) {
		productsList.clear();
		if(helper == null) helper = new DatabaseHelper(getBaseContext());
		
		if(DownloadService.companies != null && DownloadService.companies.size() > 0) {
			for (Object[] data: DownloadService.companies) {
				MyCompany company = (MyCompany) data[0];
				
				if(company.getId().equalsIgnoreCase(companyID)) {
					productsList.addAll((ArrayList<MyProduct>) data[2]);
				}
			}
		}else {
			// Check if DB has any branches for this company
			List<MyProduct> products = helper.getAllProducts(companyID);
			productsList.addAll(products);
		}
		
		/**
		 * Log all the products
		 */
		for(MyProduct product: productsList) {
			Log.i(TAG+": PRODUCTS", product.toString());
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_company);
		
		selCompany = (MyCompany) getIntent().getExtras().getSerializable("company");
		company_icon = (Bitmap) getIntent().getExtras().getParcelable("company_icon");
		selBranch = (MyBranch) getIntent().getExtras().getSerializable("branch");
		
		if (selCompany != null) {
			getProduct(selCompany.getId());
		}
		
		init();		
		
		CompanyFragmentAdapter adapter = new CompanyFragmentAdapter(getSupportFragmentManager());

		//View Pager
		mPager = (ViewPager)findViewById(R.id.cpager);
		mPager.setAdapter(adapter);
		mPager.setOffscreenPageLimit(2);

		((TextView)findViewById(R.id.action_title)).setText("Choose Products");
		setFontSemiBold(((TextView)findViewById(R.id.action_title)));
		
		initUI();
	}

	private void init() {
		com.app.chasebank.bitmap.util.ImageCache.ImageCacheParams cacheParams = 
				new com.app.chasebank.bitmap.util.ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
		
		cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory
		
		// The ImageFetcher takes care of loading images into our ImageView children asynchronously
		mImageFetcher = new ImageFetcher(this, 256);
		mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
		mImageFetcher.setImageFadeIn(false);
	}

	private void initUI() {
		TextView  noti = (TextView) findViewById(R.id.lead_section_notification),
				add_lead = (TextView) findViewById(R.id.lead_section_add_lead),
				myleads = (TextView) findViewById(R.id.lead_section_my_leads);

		ImageView back = (ImageView) findViewById(R.id.action_image);
		
		// :Number of Notifications
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

	@Override
	public void onResume() {
		super.onResume();
		mImageFetcher.setExitTasksEarly(false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mImageFetcher.setExitTasksEarly(true);
		mImageFetcher.flushCache();
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
			return productsList.size();
		}

		@Override
		public Fragment getItem(int position) {
			if(position == productsList.size()-1) 
				position = 0; 
			if(position < productsList.size()) return CompanyFragment.newInstance(position);
			else return null;
		}

		@Override
		public void destroyItem(ViewGroup collection, int position, Object view) {
			//((ViewPager) collection).removeView((TextView) view);
		}

	}
	
	public static class CompanyFragment extends Screen {
		private View v;
		public final static String POSITION = "position";
		public int position = 0;

		public String mImageUrl;
		public com.app.chasebank.bitmap.util.RecyclingImageView logo;
		public ImageFetcher mImageFetcher;

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
		 * @return Fragment
		 */
		public static Fragment newInstance(int position) {
			CompanyFragment comp = new CompanyFragment();
			Bundle bun = new Bundle();
			bun.putInt(POSITION, position);
			comp.setArguments(bun);
			
			Log.i(TAG, "POSITION SCREEN:"+position);
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
			MyProduct product = productsList.get(position);
			Log.i(TAG, "POSITION SCREEN S:"+position);
			
			mImageUrl = product.getUrl();

			if(product != null) {
				logo = (com.app.chasebank.bitmap.util.RecyclingImageView) v.findViewById(R.id.list_company_logo);
				TextView desc = (TextView) v.findViewById(R.id.list_company_description);
				TextView name = (TextView) v.findViewById(R.id.list_company_product_name);
				Button selected = (Button) v.findViewById(R.id.swipe_btn_selected);
				
				//set the details in the views
				//logo.setImageDrawable(getResources().getDrawable(product.getLogo()));
				try {
					logo.setImageResource(R.drawable.products);
					//mImageFetcher.loadImage(mImageUrl, logo);
				}catch(Exception ex){ex.printStackTrace(); }

				name.setText(product.getName());
				desc.setText(product.getDescription());
				
				//Set Fonts
				getParent().setFontRegular(name);
				getParent().setFontRegular(desc);
				getParent().setFontRegular(selected);
				
				//onclick listener
				selected.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Bitmap bitmap = null;
						BitmapDrawable test = null;
						try {test = (BitmapDrawable) logo.getDrawable(); } catch(Exception ex){ex.printStackTrace(); }
						
						try {				
							if(test != null) bitmap = test.getBitmap();							
						}catch(Exception ex){ex.printStackTrace(); }
						
						Bundle bun = new Bundle();
						bun.putSerializable("product", productsList.get(position));
						bun.putSerializable("company", selCompany);
						bun.putSerializable("branch", selBranch);
						bun.putParcelable("product_icon", bitmap);
						bun.putParcelable("company_icon", company_icon);

						ContactsFragment conts = new ContactsFragment();
						conts.setArguments(bun);	                	
						getParent().switchScreen(conts);
					}
				});	
			}
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			
			// Use the parent activity to load the image asynchronously into the ImageView (so a single
			// cache can be used over all pages in the ViewPager
			mImageFetcher = ((ProductAct) getActivity()).getImageFetcher();
			mImageFetcher.setLoadingImage(R.drawable.products);
			mImageFetcher.loadImage(mImageUrl, logo);
			
		}
		
		@Override
		public void onDestroy() {
			super.onDestroy();
			if (logo != null) {
				// Cancel any pending image work
				com.app.chasebank.bitmap.util.ImageWorker.cancelWork(logo);
				logo.setImageDrawable(null);
			}
		}

	}

}
