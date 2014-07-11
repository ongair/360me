package com.app.chasebank.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.app.chasebank.CompanyAct;
import com.app.chasebank.LeadActivity;
import com.app.chasebank.LeadSummaryActivity;
import com.app.chasebank.R;
import com.app.chasebank.entity.MyBranch;
import com.app.chasebank.entity.MyCompany;
import com.app.chasebank.entity.MyContact;
import com.app.chasebank.entity.MyProduct;
import com.app.chasebank.fragment.framework.Screen;

public final class AddLeadFragment extends Screen {

	public static ArrayList<MyContact> contactList = new ArrayList<MyContact>();
	private MyCompany selCompany;
	private MyProduct selProduct;
	private MyContact selContact;
	
	private View v;
	private ListView listContacts;
	private ContactListAdapter adapter;
	private EditText detailsText;
	private Button addleadBtn;
	private EditText emailText;
	
	private Bitmap bitmap = null;
	private Bitmap product_bitmap = null;
	private Bitmap company_icon;	
	
	public static MyBranch selBranch;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			selCompany = (MyCompany) getArguments().getSerializable("company");
			selProduct = (MyProduct) getArguments().getSerializable("product");
			selContact = (MyContact) getArguments().getSerializable("contact");
			selBranch = (MyBranch) getArguments().getSerializable("branch");

			if(getArguments().getParcelable("contactPhoto") != null)
				bitmap = (Bitmap)getArguments().getParcelable("contactPhoto");

			if(getArguments().getParcelable("product_icon") != null)
				product_bitmap = (Bitmap)getArguments().getParcelable("product_icon");

			if(getArguments().getParcelable("company_icon") != null)
				company_icon = (Bitmap)getArguments().getParcelable("company_icon");

		}catch(Exception ex){ex.printStackTrace();}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		v = getLayoutInflater(savedInstanceState).inflate(R.layout.activity_add_lead, container, false);
		init();
		return v;
	}
	
	private void init() {
		//Login views
		detailsText = (EditText)v.findViewById(R.id.adddet_additional_details);
		emailText = (EditText)v.findViewById(R.id.addet_emailaddress);
		addleadBtn = (Button)v.findViewById(R.id.add_lead_btn);
		listContacts = (ListView) v.findViewById(R.id.adddet_list);

		TextView  noti = (TextView) v.findViewById(R.id.lead_section_notification),
				add_lead = (TextView) v.findViewById(R.id.lead_section_add_lead),
				myleads = (TextView) v.findViewById(R.id.lead_section_my_leads);

		// :Number of Notifications
		getParent().notification_number = (TextView) v.findViewById(R.id.lead_notification_number);
		getParent().notification_number.setVisibility(View.INVISIBLE);


		//Prefill the emailText with the contact email from the contact itself
		if(selContact.getEmail() != null && selContact.getEmail().size() > 0 && !selContact.getEmail().get(0).equalsIgnoreCase("")) {
			emailText.setText(selContact.getEmail().get(0));
		}

		OnClickListener notilist = new OnClickListener() {

			@Override
			public void onClick(View v) {
				//Launch the Lead activity for now
				switch (v.getId()) {
				case R.id.lead_section_notification:

					break;
				case R.id.lead_section_my_leads:
					break;

				default:
					break;
				}
			}
		};

		add_lead.setEnabled(false);

		noti.setOnClickListener(notilist);
		add_lead.setOnClickListener(notilist);
		myleads.setOnClickListener(notilist);

		getParent().setFontRegular(add_lead);
		getParent().setFontRegular(myleads);
		getParent().setFontRegular(noti);

		addleadBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try{
					//Open Lead Activity
					Intent intent = new Intent(getActivity(), LeadSummaryActivity.class);
					//Create a bundle and put everything inside
					Bundle bun = new Bundle();
					bun.putSerializable("company", selCompany);
					bun.putSerializable("product", selProduct);
					bun.putSerializable("contact", selContact);
					bun.putSerializable("branch", selBranch);
					bun.putParcelable("contactPhoto", bitmap);

					intent.putExtra("details", detailsText.getText().toString());
					intent.putExtra("email", emailText.getText().toString());					
					intent.putExtras(bun);
					
					emailText.setText("");
					startActivity(intent);
				}catch(Exception ex) {
					ex.printStackTrace();

					Intent intent = new Intent(getActivity(), LeadSummaryActivity.class);
					startActivity(intent);
				}
			}
		});

		adapter = new ContactListAdapter();
		//set list adapter
		listContacts.setAdapter(adapter);

		//Set Fonts
		getParent().setFontRegular(detailsText);
		getParent().setFontRegular(emailText);
		getParent().setFontRegular(addleadBtn);

	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_logout:
			/**
			 * Attempt to logout of google +
			 */
			getParent().attemptLogout();
			break;

		default:
			break;
		}
		return true;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}	
	
	class ContactListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public Object getItem(int position) {
			return contactList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			v = LayoutInflater.from(getActivity()).inflate(R.layout.list_holder_add_lead, parent, false);

			//contact details
			ImageView photo = (ImageView) v.findViewById(R.id.contact_photo);
			TextView name = (TextView) v.findViewById(R.id.contact_name);
			final TextView phone = (TextView) v.findViewById(R.id.contact_phone);

			//Set Fonts
			getParent().setFontSemiBold(name);
			getParent().setFontRegular(phone);			

			//At this point we can enable or disable views we want
			phone.setVisibility(View.GONE);

			switch (position) {
			case 0:
				//company
				if(selCompany.getLogo() <= 0) {
					if(company_icon != null) {
						photo.setImageBitmap(company_icon);
					}else {
						photo.setImageURI(Uri.parse(selCompany.getImage_url()));
					}

				}else {
					photo.setImageDrawable(getResources().getDrawable(selCompany.getLogo()));
				}

				name.setText(selCompany.getName());
				phone.setText(selCompany.getDescription());				
				break;
			case 1:
				if(selProduct == null) {
					return null;
				}else {
					//product
					if(product_bitmap != null) photo.setImageBitmap(product_bitmap);
					name.setText(selProduct.getName());
					phone.setText(selProduct.getDescription());
				}
				break;
			case 2:
				//contact 
				if(bitmap !=null) photo.setImageBitmap(bitmap);
				name.setText(selContact.getName());
				phone.setText(selContact.getPhone());
				phone.setVisibility(View.VISIBLE);

				break;
			default:
				break;
			}			

			return v;
		}

	}
	
}
