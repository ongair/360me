package com.app.chasebank.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.app.chasebank.LeadSummaryActivity;
import com.app.chasebank.MainActivity;
import com.app.chasebank.R;
import com.app.chasebank.entity.MyBranch;
import com.app.chasebank.entity.MyCompany;
import com.app.chasebank.entity.MyProduct;
import com.app.chasebank.fragment.framework.DialogScreen;
import com.app.chasebank.util.CommonUtils;
import com.app.chasebank.util.DatabaseHelper;
import com.app.chasebank.util.DownloadService;

public class LoginAdditionalFragment extends DialogScreen {
	public static ArrayList<MyCompany> companyList = new ArrayList<MyCompany>();
	private DatabaseHelper helper = null;
	private ArrayList<String> comps = new ArrayList<String>();
	private ArrayList<String> compID = new ArrayList<String>();

	private View root;
	private EditText name;
	private EditText phone;
	private EditText depart;
	private TextView submit_btn;
	private AsyncTask<Void, Void, Void> mSetProfile;
	private AsyncTask<Void, Void, Void> mLoadCompanies;
	private Spinner company;
	private SpinnerAdapter adap;

	public static LoginAdditionalFragment newInstance() {
		LoginAdditionalFragment f = new LoginAdditionalFragment();
		return f;
	}

	/**
	 * Get the list of companies from the downloaded data
	 */
	public void getCompanies(){
		// Should only do this processing when the list of company Names and IDS is empty, otherwise dont process anything
		if(comps == null || comps.size() <= 0) { 
			comps.clear();
			compID.clear();

			/**
			 * Check if the download service has any data, else load from the db
			 */
			if(DownloadService.companies != null && DownloadService.companies.size() <= 0 ) {
				// Load the company's information from the database
				if(helper == null) helper = new DatabaseHelper(getParent());

				List<MyCompany> comp = helper.getAllCompanies();
				if(comp.size() > 0) {
					companyList.clear();
					companyList.addAll(comp);
				}else {
					/**
					 * If no list of companies in the database, just load a fresh copy from the server
					 */
					loadCompanies();
				}

			} else {
				for (Object[] data: DownloadService.companies) {
					MyCompany company = (MyCompany) data[0];				
					companyList.add(company);
				}
			}

			/**
			 * Get a map of Company Name and Company ID
			 */
			for (MyCompany company : companyList) {
				comps.add(company.getName());
				compID.add(company.getId());
			}
		}
	}

	/**
	 * Load companies information
	 */
	private void loadCompanies() {		
		mLoadCompanies = new AsyncTask<Void, Void, Void>() {
			ArrayList<MyCompany> comp; 

			@Override
			protected void onPreExecute() {
				super.onPreExecute();				
			}

			@Override
			protected Void doInBackground(Void... params) {
				try {
					comp = CommonUtils.getAllCompanies();					
				} catch (Exception e) {
					e.printStackTrace();
				}				
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {				
				if(comp != null && comp.size() > 0) {
					comps.clear();
					compID.clear();

					companyList.clear();
					companyList.addAll(comp);

					for (MyCompany company : companyList) {
						comps.add(company.getName());
						compID.add(company.getId());
					}

					/**
					 * Notify the spinner adapter that company data has changed, so that it can lay itself again
					 */
					adap = new ArrayAdapter<String>(getActivity(), R.layout.company_spinner_item, comps);
					company.setAdapter(adap);
					company.setSelection(0);
				}				
				mLoadCompanies = null;
			}			
		};

		mLoadCompanies.execute(null, null, null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (getDialog() != null) {
			getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		}

		root = inflater.inflate(R.layout.login_more_details_dialog, container, false);
		
		if(companyList == null || companyList.size() <= 0) {
			/**
			 * Reload the list of companies
			 */
			getCompanies();
		}

		initUI();

		return root;
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		getDialog().getWindow()
		.getAttributes().windowAnimations = R.style.DialogAnimation;

		getCompanies();
	}

	public void initUI() {
		name = (EditText) root.findViewById(R.id.user_name); 
		phone = (EditText) root.findViewById(R.id.user_phone);
		depart = (EditText) root.findViewById(R.id.user_department);
		submit_btn = (TextView) root.findViewById(R.id.user_submit_lead);

		company = (Spinner) root.findViewById(R.id.user_company);
		adap = new ArrayAdapter<String>(getActivity(), R.layout.company_spinner_item, comps);
		company.setAdapter(adap);

		//select index 0
		if(comps.size() > 0) company.setSelection(0,  true);		

		getParent().setFontRegular(name);
		getParent().setFontRegular(depart);
		getParent().setFontRegular(submit_btn);

		/**
		 * Onclick Listener
		 */
		OnClickListener notilist = new OnClickListener() {

			@Override
			public void onClick(View v) {
				/**
				 * Submit the content to the server, then close this dialog
				 */
				if(name.getEditableText().toString().equalsIgnoreCase("") || phone.getEditableText().toString().equalsIgnoreCase("") || depart.getEditableText().toString().equalsIgnoreCase("") ) {
					//When name is null
					if(name.getEditableText().toString().equalsIgnoreCase(""))
						name.setError("name required!");

					//When Phonenumber is null
					if(phone.getEditableText().toString().equalsIgnoreCase(""))
						phone.setError("phone number required!");

					//When department is null
					if(depart.getEditableText().toString().equalsIgnoreCase(""))
						depart.setError("Department required!");
				}else {
					int positionCompany = company.getSelectedItemPosition();
					String idcompany = compID.get(positionCompany);
					String nameStr = name.getEditableText().toString();
					String phoneStr = phone.getEditableText().toString();
					String departStr = depart.getEditableText().toString();
					
					// Clear everything 
					name.setText("");
					phone.setText("");
					depart.setText("");
					
					setProfile(getParent().getUserId(), 
							nameStr, 
							"", 
							idcompany, 
							phoneStr, 
							departStr);
				}
			}
		};

		submit_btn.setOnClickListener(notilist);

		/**
		 * When cancelled:Go back to splashscreen, then logout and remove the first login.
		 */
		this.onCancel(new DialogInterface() {
			
			@Override
			public void dismiss() {
				// TODO Auto-generated method stub
				//getParent().toast("Dialog dismissed..!");
			}
			
			@Override
			public void cancel() {
				// TODO Auto-generated method stub
				//getParent().toast("Dialog cancelled..!");
				//Logout
				getParent().attemptLogout();
			}
		});
	}

	/**
	 * Set the current user's profile
	 * 
	 * @param id User's ID 
	 * @param first_name User's First Name
	 * @param last_name User's Last Name
	 * @param company User's Affiliated Company
	 * @param phone_number User's Phone number
	 * @param department User's Department
	 */
	private void setProfile(final String id, final String first_name, final String last_name, final String company, final String phone_number, final String department) {		
		mSetProfile = new AsyncTask<Void, Void, Void>() {
			boolean profileSet; 

			@Override
			protected void onPreExecute() {
				super.onPreExecute();				
			}

			@Override
			protected Void doInBackground(Void... params) {
				try {
					profileSet = CommonUtils.setProfile(id, first_name, company, phone_number, department);					
				} catch (Exception e) {
					e.printStackTrace();
					profileSet = false;
				}				
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {				
				if(profileSet) {
					/**
					 * Everything has been successful, please continue. 
					 * Open LoggedInFragment
					 */

					getParent().setFirstLogin();

					//dismiss this dialog
					LoginAdditionalFragment.this.dismiss();

					// Open LoggedInFragment
					getParent().switchScreen(new LoggedInFragment());
				}else {
					/**
					 * Could not set the user's profile, lets proceed and set the profile later
					 */

					getParent().toast("Sorry, could not set your profile, please try again later..");

					//dismiss this dialog
					LoginAdditionalFragment.this.dismiss();

					// Open LoggedInFragment
					getParent().switchScreen(new LoggedInFragment());
				}

				mSetProfile = null;
			}			
		};

		mSetProfile.execute(null, null, null);
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
