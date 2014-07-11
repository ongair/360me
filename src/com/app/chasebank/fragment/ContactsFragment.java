package com.app.chasebank.fragment;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Photo;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AlphabetIndexer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.app.chasebank.BuildConfig;
import com.app.chasebank.R;
import com.app.chasebank.contactslist.util.ImageLoader;
import com.app.chasebank.contactslist.util.Utils;
import com.app.chasebank.entity.MyBranch;
import com.app.chasebank.entity.MyCompany;
import com.app.chasebank.entity.MyContact;
import com.app.chasebank.entity.MyProduct;
import com.app.chasebank.fragment.framework.Screen;

public class ContactsFragment extends Screen implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	private static final String TAG = "ContactsListFragment";
	private static final String STATE_PREVIOUSLY_SELECTED_KEY = "com.example.android.contactslist.ui.SELECTED_ITEM";

	private ContactsAdapter mAdapter; 
	private com.app.chasebank.contactslist.util.ImageLoader mImageLoader; 
	private ListView listContacts;
	private View v;
	private EditText searchText;

	private String mSearchTerm; 
	private int mPreviouslySelectedSearchItem = 0;

	private ArrayList<MyContact> contactList = new ArrayList<MyContact>();
	public static MyBranch selBranch;
	private MyCompany selCompany;
	private MyProduct selProduct;	
	private Bitmap product_bitmap = null;
	private Bitmap company_icon = null;	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		selCompany = (MyCompany) getArguments().getSerializable("company");		
		selProduct = (MyProduct) getArguments().getSerializable("product");		
		selBranch = (MyBranch) getArguments().getSerializable("branch");	

		if(getArguments().getParcelable("product_icon") != null)
			product_bitmap = (Bitmap)getArguments().getParcelable("product_icon");	

		if(getArguments().getParcelable("company_icon") != null)
			company_icon = (Bitmap)getArguments().getParcelable("company_icon");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {
		v = getLayoutInflater(savedInstanceState).inflate(R.layout.activity_contacts, container, false);

		init();

		// Create the main contacts adapter
		mAdapter = new ContactsAdapter(getActivity());

		if (savedInstanceState != null) {
			// If we're restoring state after this fragment was recreated then retrieve previous search term and previously selected search
			// result.
			mSearchTerm = savedInstanceState.getString(SearchManager.QUERY);
			mPreviouslySelectedSearchItem = savedInstanceState.getInt(STATE_PREVIOUSLY_SELECTED_KEY, 0);
		}

		/*
		 * An ImageLoader object loads and resizes an image in the background and binds it to the
		 * QuickContactBadge in each item layout of the ListView. ImageLoader implements memory
		 * caching for each image, which substantially improves refreshes of the ListView as the
		 * user scrolls through it.
		 *
		 * To learn more about downloading images asynchronously and caching the results, read the
		 * Android training class Displaying Bitmaps Efficiently.
		 *
		 * http://developer.android.com/training/displaying-bitmaps/
		 */
		mImageLoader = new ImageLoader(getActivity(), getListPreferredItemHeight()) {
			@Override
			protected Bitmap processBitmap(Object data) {
				// This gets called in a background thread and passed the data from
				// ImageLoader.loadImage().
				return loadContactPhotoThumbnail((String) data, getImageSize());
			}
		};

		// Set a placeholder loading image for the image loader
		mImageLoader.setLoadingImage(R.drawable.contact_holder);

		// Add a cache to the image loader
		mImageLoader.addImageCache(getActivity().getSupportFragmentManager(), 0.1f);
		return v;
	}

	public void setSearchQuery(String query) {
		mSearchTerm = query;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Set up ListView, assign adapter and set some listeners. The adapter was previously
		// created in onCreate().
		listContacts.setAdapter(mAdapter);

		listContacts.setOnItemClickListener(this);

		listContacts.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView absListView, int scrollState) {
				// Pause image loader to ensure smoother scrolling when flinging
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
					mImageLoader.setPauseWork(true);
				} else {
					mImageLoader.setPauseWork(false);
				}
			}

			@Override
			public void onScroll(AbsListView absListView, int i, int i1, int i2) {}
		});

		// If there's a previously selected search item from a saved state then don't bother
		// initializing the loader as it will be restarted later when the query is populated into
		// the action bar search view (see onQueryTextChange() in onCreateOptionsMenu()).
		if (mPreviouslySelectedSearchItem == 0) {
			// Initialize the loader, and create a loader identified by ContactsQuery.QUERY_ID
			getLoaderManager().initLoader(ContactsQuery.QUERY_ID, null, this);
		}
	}

	private void init() {
		//Login views
		searchText = (EditText) v.findViewById(R.id.contact_search);	
		listContacts = (ListView) v.findViewById(R.id.contact_list);
		//listContacts.setOnItemClickListener(this);
		listContacts.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		//Set Fonts
		getParent().setFontRegular(searchText);
		//Add Filter
		searchText.addTextChangedListener(filterTextWatcher);

		getParent().setFontSemiBold((TextView) v.findViewById(R.id.action_title));

		TextView  noti = (TextView) v.findViewById(R.id.lead_section_notification),
				add_lead = (TextView) v.findViewById(R.id.lead_section_add_lead),
				myleads = (TextView) v.findViewById(R.id.lead_section_my_leads);

		ImageView back = (ImageView) v.findViewById(R.id.action_image);

		// :Number of Notifications
		getParent().notification_number = (TextView) v.findViewById(R.id.lead_notification_number);
		getParent().notification_number.setVisibility(View.INVISIBLE);
		
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
		
		getParent().setFontRegular(add_lead);
		getParent().setFontRegular(myleads);
		getParent().setFontRegular(noti);
	}

	@Override
	public void onPause() {
		super.onPause();

		// In the case onPause() is called during a fling the image loader is
		// un-paused to let any remaining background work complete.
		mImageLoader.setPauseWork(false);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		try {
			// Gets the Cursor object currently bound to the ListView
			final Cursor cursor = mAdapter.getCursor();

			// Moves to the Cursor row corresponding to the ListView item that was clicked
			cursor.moveToPosition(position);

			Contacts.getLookupUri(cursor.getLong(ContactsQuery.ID), cursor.getString(ContactsQuery.LOOKUP_KEY));
			String photoUri = cursor.getString(ContactsQuery.PHOTO_THUMBNAIL_DATA);

			// From the view we get the clicked users details
			MyContact contact = null;
			Bitmap bitmap = null;

			if(v != null) {
				TextView name = (TextView) v.findViewById(R.id.contact_name);
				TextView phone = (TextView) v.findViewById(R.id.contact_phone);	
				TextView email = (TextView) v.findViewById(R.id.contact_email);			
				QuickContactBadge icon = (QuickContactBadge) v.findViewById(android.R.id.icon);

				String name_ = name.getText().toString(), phone_ = phone.getText().toString(), email_ = email.getText().toString();

				contact = new MyContact(name_, phone_);
				ArrayList<String> ems = new ArrayList<String>();
				ems.add(email_);
				contact.setEmail(ems);

				BitmapDrawable test = null;
				try {test = (BitmapDrawable) icon.getDrawable(); }catch(Exception ex){ex.printStackTrace(); }

				try {				
					if(test != null) bitmap = test.getBitmap();
					else {
						bitmap = null;
						bitmap = mImageLoader.getBitmapImage(photoUri);
					}

					Bundle bun = new Bundle();
					bun.putSerializable("product", selProduct);
					bun.putSerializable("company", selCompany);
					bun.putSerializable("branch", selBranch);
					bun.putSerializable("contact", contact);
					bun.putParcelable("contactPhoto", bitmap);
					bun.putParcelable("product_icon", product_bitmap);
					bun.putParcelable("company_icon", company_icon);

					AddLeadFragment addl = new AddLeadFragment();
					addl.setArguments(bun);

					getParent().switchScreen(ContactsFragment.this, addl);
				}catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}catch(Exception ex){ex.printStackTrace();}

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
		if (!TextUtils.isEmpty(mSearchTerm)) {
			// Saves the current search string
			outState.putString(SearchManager.QUERY, mSearchTerm);

			// Saves the currently selected contact
			outState.putInt(STATE_PREVIOUSLY_SELECTED_KEY, listContacts.getCheckedItemPosition());
		}
	}

	private TextWatcher filterTextWatcher = new TextWatcher() {

		public void afterTextChanged(Editable s) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

			Log.i("Searching...", s.toString());
			String newFilter = !TextUtils.isEmpty(s.toString()) ? s.toString() : null;

			// Don't do anything if the filter is empty
			if (mSearchTerm == null && newFilter == null) {
				return;
			}

			// Don't do anything if the new filter is the same as the current filter
			if (mSearchTerm != null && mSearchTerm.equals(newFilter)) {
				return;
			}

			// Updates current filter to new filter
			mSearchTerm = newFilter;

			// Restarts the loader. This triggers onCreateLoader(), which builds the
			// necessary content Uri from mSearchTerm.
			getLoaderManager().restartLoader(ContactsQuery.QUERY_ID, null, ContactsFragment.this);
		}

	};

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		// If this is the loader for finding contacts in the Contacts Provider
		// (the only one supported)
		if (id == ContactsQuery.QUERY_ID) {
			Uri contentUri;

			// There are two types of searches, one which displays all contacts and
			// one which filters contacts by a search query. If mSearchTerm is set
			// then a search query has been entered and the latter should be used.

			if (mSearchTerm == null) {
				// Since there's no search string, use the content URI that searches the entire
				// Contacts table
				contentUri = ContactsQuery.CONTENT_URI;
			} else {
				// Since there's a search string, use the special content Uri that searches the
				// Contacts table. The URI consists of a base Uri and the search string.
				contentUri =
						Uri.withAppendedPath(ContactsQuery.FILTER_URI, Uri.encode(mSearchTerm));
			}

			// Returns a new CursorLoader for querying the Contacts table. No arguments are used
			// for the selection clause. The search string is either encoded onto the content URI,
			// or no contacts search string is used. The other search criteria are constants. See
			// the ContactsQuery interface.
			return new CursorLoader(getActivity(),
					contentUri,
					ContactsQuery.PROJECTION,
					ContactsQuery.SELECTION,
					null,
					ContactsQuery.SORT_ORDER);
		}

		Log.e(TAG, "onCreateLoader - incorrect ID provided (" + id + ")");
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// This swaps the new cursor into the adapter.
		if (loader.getId() == ContactsQuery.QUERY_ID) {
			mAdapter.swapCursor(data);			
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (loader.getId() == ContactsQuery.QUERY_ID) {
			// When the loader is being reset, clear the cursor from the adapter. This allows the
			// cursor resources to be freed.
			mAdapter.swapCursor(null);
		}
	}

	/**
	 * Gets the preferred height for each item in the ListView, in pixels, after accounting for
	 * screen density. ImageLoader uses this value to resize thumbnail images to match the ListView
	 * item height.
	 *
	 * @return The preferred height in pixels, based on the current theme.
	 */
	private int getListPreferredItemHeight() {
		return (int) getImageHeight();
	}

	/**
	 * This method converts dp unit to equivalent pixels, depending on device density. 
	 * 
	 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on device density
	 */
	public float getImageHeight(){
		Resources resources = getParent().getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = 48 * (metrics.densityDpi / 160f);
		return px;
	}

	/**
	 * Decodes and scales a contact's image from a file pointed to by a Uri in the contact's data,
	 * and returns the result as a Bitmap. The column that contains the Uri varies according to the
	 * platform version.
	 *
	 * @param photoData For platforms prior to Android 3.0, provide the Contact._ID column value.
	 *                  For Android 3.0 and later, provide the Contact.PHOTO_THUMBNAIL_URI value.
	 * @param imageSize The desired target width and height of the output image in pixels.
	 * @return A Bitmap containing the contact's image, resized to fit the provided image size. If
	 * no thumbnail exists, returns null.
	 */
	private Bitmap loadContactPhotoThumbnail(String photoData, int imageSize) {

		// Ensures the Fragment is still added to an activity. As this method is called in a
		// background thread, there's the possibility the Fragment is no longer attached and
		// added to an activity. If so, no need to spend resources loading the contact photo.
		if (!isAdded() || getActivity() == null) {
			return null;
		}

		// Instantiates an AssetFileDescriptor. Given a content Uri pointing to an image file, the
		// ContentResolver can return an AssetFileDescriptor for the file.
		AssetFileDescriptor afd = null;

		// This "try" block catches an Exception if the file descriptor returned from the Contacts
		// Provider doesn't point to an existing file.
		try {
			Uri thumbUri;
			// If Android 3.0 or later, converts the Uri passed as a string to a Uri object.
			if (Utils.hasHoneycomb()) {
				thumbUri = Uri.parse(photoData);
			} else {
				// For versions prior to Android 3.0, appends the string argument to the content
				// Uri for the Contacts table.
				final Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_URI, photoData);

				// Appends the content Uri for the Contacts.Photo table to the previously
				// constructed contact Uri to yield a content URI for the thumbnail image
				thumbUri = Uri.withAppendedPath(contactUri, Photo.CONTENT_DIRECTORY);
			}
			// Retrieves a file descriptor from the Contacts Provider. To learn more about this
			// feature, read the reference documentation for
			// ContentResolver#openAssetFileDescriptor.
			afd = getActivity().getContentResolver().openAssetFileDescriptor(thumbUri, "r");

			// Gets a FileDescriptor from the AssetFileDescriptor. A BitmapFactory object can
			// decode the contents of a file pointed to by a FileDescriptor into a Bitmap.
			FileDescriptor fileDescriptor = afd.getFileDescriptor();

			if (fileDescriptor != null) {
				// Decodes a Bitmap from the image pointed to by the FileDescriptor, and scales it
				// to the specified width and height
				return ImageLoader.decodeSampledBitmapFromDescriptor(
						fileDescriptor, imageSize, imageSize);
			}
		} catch (FileNotFoundException e) {
			// If the file pointed to by the thumbnail URI doesn't exist, or the file can't be
			// opened in "read" mode, ContentResolver.openAssetFileDescriptor throws a
			// FileNotFoundException.
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Contact photo thumbnail not found for contact " + photoData
						+ ": " + e.toString());
			}
		} finally {
			// If an AssetFileDescriptor was returned, try to close it
			if (afd != null) {
				try {
					afd.close();
				} catch (IOException e) {
					// Closing a file descriptor might cause an IOException if the file is
					// already closed. Nothing extra is needed to handle this.
				}
			}
		}

		// If the decoding failed, returns null
		return null;
	}

	/**
	 * This is a subclass of CursorAdapter that supports binding Cursor columns to a view layout.
	 * If those items are part of search results, the search string is marked by highlighting the
	 * query text. An {@link AlphabetIndexer} is used to allow quicker navigation up and down the
	 * ListView.
	 */
	private class ContactsAdapter extends CursorAdapter implements SectionIndexer {
		private LayoutInflater mInflater; // Stores the layout inflater
		private AlphabetIndexer mAlphabetIndexer; // Stores the AlphabetIndexer instance
		private TextAppearanceSpan highlightTextSpan; // Stores the highlight text appearance style

		/**
		 * Instantiates a new Contacts Adapter.
		 * @param context A context that has access to the app's layout.
		 */
		public ContactsAdapter(Context context) {
			super(context, null, 0);

			// Stores inflater for use later
			mInflater = LayoutInflater.from(context);

			final String alphabet = context.getString(R.string.alphabet);

			mAlphabetIndexer = new AlphabetIndexer(null, ContactsQuery.SORT_KEY, alphabet);

			// Defines a span for highlighting the part of a display name that matches the search string
			highlightTextSpan = new TextAppearanceSpan(getActivity(), R.style.searchTextHiglight);
		}

		/**
		 * Identifies the start of the search string in the display name column of a Cursor row.
		 * E.g. If displayName was "Adam" and search query (mSearchTerm) was "da" this would
		 * return 1.
		 *
		 * @param displayName The contact display name.
		 * @return The starting position of the search string in the display name, 0-based. The
		 * method returns -1 if the string is not found in the display name, or if the search
		 * string is empty or null.
		 */
		private int indexOfSearchQuery(String displayName) {
			if (!TextUtils.isEmpty(mSearchTerm)) {
				return displayName.toLowerCase(Locale.getDefault()).indexOf(
						mSearchTerm.toLowerCase(Locale.getDefault()));
			}
			return -1;
		}

		/**
		 * Overrides newView() to inflate the list item views.
		 */
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
			// Inflates the list item layout.
			final View itemLayout = mInflater.inflate(R.layout.contact_list_holder, viewGroup, false);

			// Creates a new ViewHolder in which to store handles to each view resource. This
			// allows bindView() to retrieve stored references instead of calling findViewById for
			// each instance of the layout.
			final ViewHolder holder = new ViewHolder();
			holder.name = (TextView) itemLayout.findViewById(R.id.contact_name);
			holder.phone = (TextView) itemLayout.findViewById(R.id.contact_phone);		
			holder.email = (TextView)itemLayout.findViewById(R.id.contact_email);
			holder.icon = (QuickContactBadge) itemLayout.findViewById(android.R.id.icon);

			holder.watch = (ImageView) itemLayout.findViewById(R.id.contact_icon_watch);
			holder.flag = (ImageView) itemLayout.findViewById(R.id.contact_icon_flag);
			holder.more = (ImageView) itemLayout.findViewById(R.id.contact_icon_more);

			//Set the application font
			getParent().setFontSemiBold(holder.name);
			getParent().setFontRegular(holder.phone);

			// Disable two first icons
			holder.flag.setVisibility(View.INVISIBLE);
			holder.watch.setVisibility(View.INVISIBLE);

			// Stores the resourceHolder instance in itemLayout. This makes resourceHolder
			// available to bindView and other methods that receive a handle to the item view.
			itemLayout.setTag(holder);

			// Returns the item layout view
			return itemLayout;
		}

		/**
		 * Binds data from the Cursor to the provided view.
		 */
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// Gets handles to individual view resources
			final ViewHolder holder = (ViewHolder) view.getTag();
			MyContact contact = new MyContact();

			// For Android 3.0 and later, gets the thumbnail image Uri from the current Cursor row.
			// For platforms earlier than 3.0, this isn't necessary, because the thumbnail is
			// generated from the other fields in the row.
			final String photoUri = cursor.getString(ContactsQuery.PHOTO_THUMBNAIL_DATA);

			final String displayName = cursor.getString(ContactsQuery.DISPLAY_NAME);
			cursor.getString(ContactsQuery.ID);

			String phoneNumber = null;
			boolean isPhoneNumFound = false;

			try {
				Cursor phones = getParent().getContentResolver().query(CommonDataKinds.Phone.CONTENT_URI, null, CommonDataKinds.Phone.CONTACT_ID +" = "+ cursor.getString(ContactsQuery.ID), null, null); 
				if (phones != null && phones.moveToNext()) { 
					phoneNumber = phones.getString(phones.getColumnIndex(CommonDataKinds.Phone.NUMBER));                 
					isPhoneNumFound = true;
				} 
				phones.close();
			}catch(Exception ex) {ex.printStackTrace(); isPhoneNumFound = false; }

			// Get email and type
			String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));	        
			Cursor emailCur = getParent().getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, 
					ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
			ArrayList<String> emailArray = new ArrayList<String>();

			while (emailCur.moveToNext()) {
				// This would allow you get several email addresses if the email addresses were stored in an array
				String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
				Log.i(TAG, email);
				emailArray.add(email);
			}
			emailCur.close();

			contact.setEmail(emailArray);
			if(emailArray != null && emailArray.size() > 0 && !emailArray.get(0).equals("")) {
				holder.email.setText(emailArray.get(0));
			}

			//only continue when phone number is available
			if(isPhoneNumFound) {
				holder.phone.setText(phoneNumber);
				contact.setPhone(phoneNumber);

				final int startIndex = indexOfSearchQuery(displayName);

				if (startIndex == -1) {
					// If the user didn't do a search, or the search string didn't match a display
					// name, show the display name without highlighting
					holder.name.setText(displayName);
					contact.setName(displayName);

				} else {
					// If the search string matched the display name, applies a SpannableString to
					// highlight the search string with the displayed display name

					// Wraps the display name in the SpannableString
					final SpannableString highlightedName = new SpannableString(displayName);

					// Sets the span to start at the starting point of the match and end at "length"
					// characters beyond the starting point
					highlightedName.setSpan(highlightTextSpan, startIndex, startIndex + mSearchTerm.length(), 0);

					// Binds the SpannableString to the display name View object
					holder.name.setText(highlightedName);
					contact.setName(highlightedName.toString());
				}

				// Processes the QuickContactBadge. A QuickContactBadge first appears as a contact's
				// thumbnail image with styling that indicates it can be touched for additional
				// information. When the user clicks the image, the badge expands into a dialog box
				// containing the contact's details and icons for the built-in apps that can handle
				// each detail type.

				// Generates the contact lookup Uri
				final Uri contactUri = Contacts.getLookupUri(cursor.getLong(ContactsQuery.ID), cursor.getString(ContactsQuery.LOOKUP_KEY));

				// Binds the contact's lookup Uri to the QuickContactBadge
				holder.icon.assignContactUri(contactUri);

				// Loads the thumbnail image pointed to by photoUri into the QuickContactBadge in a
				// background worker thread
				mImageLoader.loadImage(photoUri, holder.icon);
			}

			contactList.add(contact);
			contact = null;
		}

		/**
		 * Overrides swapCursor to move the new Cursor into the AlphabetIndex as well as the
		 * CursorAdapter.
		 */
		@Override
		public Cursor swapCursor(Cursor newCursor) {
			// Update the AlphabetIndexer with new cursor as well
			mAlphabetIndexer.setCursor(newCursor);
			return super.swapCursor(newCursor);
		}

		/**
		 * An override of getCount that simplifies accessing the Cursor. If the Cursor is null,
		 * getCount returns zero. As a result, no test for Cursor == null is needed.
		 */
		@Override
		public int getCount() {
			if (getCursor() == null) {
				return 0;
			}
			return super.getCount();
		}

		/**
		 * Defines the SectionIndexer.getSections() interface.
		 */
		@Override
		public Object[] getSections() {
			return mAlphabetIndexer.getSections();
		}

		/**
		 * Defines the SectionIndexer.getPositionForSection() interface.
		 */
		@Override
		public int getPositionForSection(int i) {
			if (getCursor() == null) {
				return 0;
			}
			return mAlphabetIndexer.getPositionForSection(i);
		}

		/**
		 * Defines the SectionIndexer.getSectionForPosition() interface.
		 */
		@Override
		public int getSectionForPosition(int i) {
			if (getCursor() == null) {
				return 0;
			}
			return mAlphabetIndexer.getSectionForPosition(i);
		}

		/**
		 * A class that defines fields for each resource ID in the list item layout. This allows
		 * ContactsAdapter.newView() to store the IDs once, when it inflates the layout, instead of
		 * calling findViewById in each iteration of bindView.
		 */
		private class ViewHolder {
			TextView email;
			TextView name;
			TextView phone;

			ImageView flag;
			ImageView watch;
			ImageView more;

			QuickContactBadge icon;
		}
	}

	/**
	 * This interface must be implemented by any activity that loads this fragment. When an
	 * interaction occurs, such as touching an item from the ListView, these callbacks will
	 * be invoked to communicate the event back to the activity.
	 */
	public interface OnContactsInteractionListener {
		/**
		 * Called when a contact is selected from the ListView.
		 * @param contactUri The contact Uri.
		 */
		public void onContactSelected(Uri contactUri);

		/**
		 * Called when the ListView selection is cleared like when
		 * a contact search is taking place or is finishing.
		 */
		public void onSelectionCleared();
	}

	/**
	 * This interface defines constants for the Cursor and CursorLoader, based on constants defined
	 * in the {@link android.provider.ContactsContract.Contacts} class.
	 */
	public interface ContactsQuery {

		// An identifier for the loader
		final static int QUERY_ID = 1;

		// A content URI for the Contacts table
		final static Uri CONTENT_URI = Contacts.CONTENT_URI;

		// The search/filter query Uri
		final static Uri FILTER_URI = Contacts.CONTENT_FILTER_URI;

		// The selection clause for the CursorLoader query. The search criteria defined here
		// restrict results to contacts that have a display name and are linked to visible groups.
		// Notice that the search on the string provided by the user is implemented by appending
		// the search string to CONTENT_FILTER_URI.
		@SuppressLint("InlinedApi")
		final static String SELECTION = (Utils.hasHoneycomb() ? Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME) + "<>''" + " AND " + Contacts.IN_VISIBLE_GROUP + "=1";

		// The desired sort order for the returned Cursor. In Android 3.0 and later, the primary
		// sort key allows for localization. In earlier versions. use the display name as the sort key.
		@SuppressLint("InlinedApi")
		final static String SORT_ORDER = Utils.hasHoneycomb() ? Contacts.SORT_KEY_PRIMARY : Contacts.DISPLAY_NAME;

		// The projection for the CursorLoader query. This is a list of columns that the Contacts
		// Provider should return in the Cursor.
		@SuppressLint("InlinedApi")
		final static String[] PROJECTION = {

			// The contact's row id
			Contacts._ID,

			// A pointer to the contact that is guaranteed to be more permanent than _ID. Given
			// a contact's current _ID value and LOOKUP_KEY, the Contacts Provider can generate
			// a "permanent" contact URI.
			Contacts.LOOKUP_KEY,

			// In platform version 3.0 and later, the Contacts table contains
			// DISPLAY_NAME_PRIMARY, which either contains the contact's displayable name or
			// some other useful identifier such as an email address. This column isn't
			// available in earlier versions of Android, so you must use Contacts.DISPLAY_NAME
			// instead.
			Utils.hasHoneycomb() ? Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME,

					// In Android 3.0 and later, the thumbnail image is pointed to by
					// PHOTO_THUMBNAIL_URI. In earlier versions, there is no direct pointer; instead,
					// you generate the pointer from the contact's ID value and constants defined in
					// android.provider.ContactsContract.Contacts.
					Utils.hasHoneycomb() ? Contacts.PHOTO_THUMBNAIL_URI : Contacts._ID,
							// The sort order column for the returned Cursor, used by the AlphabetIndexer
							SORT_ORDER,
		};

		// The query column numbers which map to each value in the projection
		final static int ID = 0;
		final static int LOOKUP_KEY = 1;
		final static int DISPLAY_NAME = 2;
		final static int PHOTO_THUMBNAIL_DATA = 3;
		final static int SORT_KEY = 4;
	}
}