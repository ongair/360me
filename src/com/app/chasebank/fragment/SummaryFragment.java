package com.app.chasebank.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.chasebank.LeadActivity;
import com.app.chasebank.LeadSummaryActivity;
import com.app.chasebank.R;
import com.app.chasebank.entity.ChaseContact;
import com.app.chasebank.entity.Lead;
import com.app.chasebank.fragment.framework.Screen;
import com.app.chasebank.util.LeadsLoadedListener;

public final class SummaryFragment extends Screen implements LeadsLoadedListener{
	/**
	 * Types of leads
	 */
	public static final int SUBMITTED = 201401; 
	public static final int CLOSED = 201402;
	public static final int PENDING = 201403;
	public static final int STALLED = 201404;
	public static final String LEADTYPE = "leadtype";
	private int LEAD_TYPE;
	
	private View v;
	private ListView listContacts;
	private ArrayList<Lead> contactList = new ArrayList<Lead>();
	private ContactListAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		v = getLayoutInflater(savedInstanceState).inflate(R.layout.fragment_summary, container, false);

		//get the lead type, then set the details according to the type selected
		try {
			LEAD_TYPE = getArguments().getInt(LEADTYPE, SUBMITTED);
		}catch(Exception ex) {ex.printStackTrace(); LEAD_TYPE = SUBMITTED;}

		init();
		return v;
	}

	/**
	 * Initialise views and implement default funcctionalities
	 */
	private void init() {
		listContacts = (ListView) v.findViewById(R.id.summary_list);		
		adapter = new ContactListAdapter();

		//create some dummy contacts here
		switch (LEAD_TYPE) {
		case SUBMITTED:
			contactList.clear();
			contactList.addAll(LeadSummaryActivity.leadsList);
			break;
			
		case CLOSED:
			contactList.clear();
			contactList.addAll(LeadSummaryActivity.closedList);			
			break;
			
		case PENDING:
			contactList.clear();
			contactList.addAll(LeadSummaryActivity.pendingList);			
			break;
			
		case STALLED:
			contactList.clear();
			contactList.addAll(LeadSummaryActivity.stalledList);			
			break;
			
		default:
			break;
		}
		
		//set list adapter
		listContacts.setAdapter(adapter);		
	}

	/**
	 * Refresh our list view
	 */
	private void refreshListView() {
		switch (LEAD_TYPE) {
		case SUBMITTED:
			contactList.clear();
			contactList.addAll(LeadSummaryActivity.leadsList);
			break;
		case CLOSED:
			contactList.clear();
			contactList.addAll(LeadSummaryActivity.closedList);			
			break;
		case PENDING:
			contactList.clear();
			contactList.addAll(LeadSummaryActivity.pendingList);			
			break;
		case STALLED:
			contactList.clear();
			contactList.addAll(LeadSummaryActivity.stalledList);			
			break;
		default:
			break;
		}

		//notify adapter that our underlying data has changed		
		if(adapter != null) {
			adapter.notifyDataSetChanged();
		}else {
			/**
			 * We can re-initialise the adapter.
			 */
			init();

			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	class ContactListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return contactList.size();
		}

		@Override
		public Lead getItem(int position) {
			return contactList.get(position);
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
			v = LayoutInflater.from(getActivity()).inflate(R.layout.list_holder_contacts, parent, false);
			//contact details
			ImageView photo = (ImageView) v.findViewById(R.id.contact_photo);
			TextView name = (TextView) v.findViewById(R.id.contact_name);
			TextView phone = (TextView) v.findViewById(R.id.contact_phone);
			//shortcuts
			ImageView flag = (ImageView) v.findViewById(R.id.contact_icon_flag);
			ImageView watch = (ImageView) v.findViewById(R.id.contact_icon_watch);
			ImageView more = (ImageView) v.findViewById(R.id.contact_icon_more);

			//set the details in the views
			name.setText(getItem(position).getName());
			phone.setText(getItem(position).getPhone());
			//if(getItem(position).getBitmap() != null) photo.setImageBitmap(getItem(position).getBitmap());

			//Set Fonts
			getParent().setFontSemiBold(name);
			getParent().setFontRegular(phone);

			//At this point we can enable or disable views we want
			flag.setVisibility(View.GONE);
			watch.setVisibility(View.GONE);
			more.setVisibility(View.GONE);

			// Onclick Listener
			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String name = getItem(position).getName(),
							phone = getItem(position).getPhone(),
							email = getItem(position).getEmail();

					Bundle bun = new Bundle();
					bun.putParcelable("PHOTO", null);
					bun.putString("NAME", name);
					bun.putString("PHONE", phone);
					bun.putString("EMAIL", email);

					//We try to pass the lead
					bun.putSerializable("LEAD", getItem(position));

					Intent intent = new Intent(getActivity(), LeadActivity.class);
					intent.putExtras(bun);
					startActivity(intent);
				}
			});
			return v;
		}

	}

	@Override
	public void onLeadsLoadingComplete() {
		/**
		 * Loading complete, reload your listview
		 */
		Log.i(TAG, "Refreshing Summary Leads Fragement: onLeadsLoadingComplete()");
		refreshListView();
	}
}
