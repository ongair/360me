package com.app.chasebank.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.app.chasebank.entity.Lead;
import com.app.chasebank.entity.MyBranch;
import com.app.chasebank.entity.MyCompany;
import com.app.chasebank.entity.MyProduct;
import com.app.chasebank.fragment.framework.Act;

public class DownloadService extends IntentService {

	public static ArrayList<Object[]> companies = new ArrayList<Object[]>();
	private ArrayList<MyBranch> branchList = new ArrayList<MyBranch>();
	private ArrayList<MyProduct> productsList = new ArrayList<MyProduct>();	
	public static ArrayList<Lead> leads = new ArrayList<Lead>();

	private DatabaseHelper helper;

	private int result = Activity.RESULT_CANCELED;
	
	public static final int RESULT_COMPANIES = 120;   				// That the companies have completed processing and are now ready	
	public static final int RESULT_LEADS = 121;   	  				// That the Leads have completed processing and are now ready	
	public static final int RESULT_NETWORK_NOT_AVAILABLE = 122;   	// That the Leads have completed processing and are now ready	
	public static final int RESULT_CHECK_DB = 123;					// That leads should be retrieved from the local database instead
	
	public static final String RESULT = "result";
	public static final String NOTIFICATION = "com.app.chasebank.service.receiver";
	public static final String TAG = "DOWNLOADSERVICE";
	public static final String USER_ID = "user_id";

	public DownloadService() {
		super("DownloadService");
	}

	// will be called asynchronously by Android
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(TAG, "Service started...");	

		if(isNetworkAvailable()) {
			if(helper == null) helper = new DatabaseHelper(getBaseContext());
			
			//if(!helper.isDownloadable()) {		
			/**
			 * Load companies
			 */
			companies = CommonUtils.getCompanies();	

			if(companies != null) {
				// Process Company Information
				if(companies.size() > 0) {
					Log.i(TAG, "Companies size: "+ companies.size());			
					for (Object[] data: companies) {
						MyCompany company = (MyCompany) data[0];
						branchList.addAll((ArrayList<MyBranch>) data[1]);
						productsList.addAll((ArrayList<MyProduct>) data[2]);

						//Create or update Company
						//helper.createOrUpdateCompany(company);
						//Log.i(TAG+": COMPANY", company.toString());

						for (MyBranch myBranch : branchList) {
							//Log.i(TAG+": BRANCH", myBranch.toString());

							//Create Branch if it doesn't exist
							//helper.createOrUpdateBranch(myBranch.getId(), myBranch.getName(), company.getId());
							//Log.i(TAG+": BRANCH", myBranch.toString());
						}

						for(MyProduct product: productsList) {
							//Log.i(TAG+": PRODUCTS", product.getName());

							//Create or Update Product
							try {
								//helper.createOrUpdateProduct(product, company.getId());
							}catch(Exception ex){ex.printStackTrace();}
						}
					}

					/**
					 * Finished saving and updating the database, we can now send a broadcast
					 */
					result = RESULT_COMPANIES;
					publishResults(result);
				}			
			}
			
			/**
			 * Now the leads
			 */
			if (intent.getStringExtra(USER_ID) != null && !intent.getStringExtra(USER_ID).equalsIgnoreCase("")){
				leads = CommonUtils.getAllStatus(intent.getStringExtra(USER_ID), getBaseContext());
				
				if(leads.size() > 0) {
					/**
					 * We just send a Broadcast that the leads have been downloaded and updated successfully
					 */
					result = RESULT_LEADS;
					publishResults(result);
				}
				
			}else {
				// User ID Not found, we can't reload the leads
				Log.i(TAG+"LEADS", "USER ID NOT FOUND, LEADS NOT RELOADED");
			}
			
			helper.createApi();
			
			/**
			 * You could close yourself
			 */
			stopSelf();
			/*}else {			
			
			 *//**
			 * Tell it to check the db instead
			 *//*
				//List<MyCompany> comp = helper.getAllCompanies();
				result = RESULT_CHECK_DB;
				publishResults(result);

				//stopSelf();
			}*/
		} else {
			/**
			 * Notify that network is not available
			 */
			result = RESULT_NETWORK_NOT_AVAILABLE;
			publishResults(result);
		}
	}

	/**
	 * Publish or Broadcast this message to all the entire system
	 * @param result The message to send
	 */
	private void publishResults(int result) {
		Intent intent = new Intent(NOTIFICATION);
		intent.putExtra(RESULT, result);
		sendBroadcast(intent);
	}

	/**
	 * Checks if the Network access if available
	 * @return boolean TRUE if available, FALSE otherwise
	 */
	public boolean isNetworkAvailable() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		boolean isConnected = info != null &&
				info.isConnectedOrConnecting();
		return isConnected;
	}

}
