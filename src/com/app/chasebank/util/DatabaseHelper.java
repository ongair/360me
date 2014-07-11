package com.app.chasebank.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.app.chasebank.entity.Lead;
import com.app.chasebank.entity.MyBranch;
import com.app.chasebank.entity.MyCompany;
import com.app.chasebank.entity.MyProduct;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	// Logcat tag
	private static final String LOG = DatabaseHelper.class.getName();
	private Context context;

	// Database Version
	private static final int DATABASE_VERSION = 3;

	// Database Name
	private static final String DATABASE_NAME = "chase_DB";

	// Table Names
	private static final String TABLE_LEADS = "leads";
	private static final String TABLE_COMPANY = "company";
	private static final String TABLE_BRANCH = "branches";
	private static final String TABLE_PRODUCT = "products";
	private static final String TABLE_API = "api_access_checkin";

	// Common column names
	private static final String KEY_ID = "id";
	private static final String KEY_CREATED_AT = "created_at";
	private static final String KEY_NAME = "name";

	// Leads Table - column names
	private static final String KEY_LEAD_COMPANY = "company";
	private static final String KEY_LEAD_BRANCH = "branch";
	private static final String KEY_LEAD_PRODUCT = "product";
	private static final String KEY_LEAD_NAME = "contact_name";
	private static final String KEY_LEAD_PHONE = "contact_phone";
	private static final String KEY_LEAD_EMAIL = "contact_email";
	private static final String KEY_LEAD_COMMENTS = "comments";
	private static final String KEY_LEAD_STATUS = "status";

	// Company Table - column names
	private static final String KEY_COMPANY_DESCRIPTION = "description";
	private static final String KEY_COMPANY_ICON = "image_url";

	// Branch Table - column names
	private static final String KEY_BRANCH_COMPANY = "branch";

	// Api Access Table - column names
	private static final String KEY_API_DATE = "date";

	// Product Table - column names
	private static final String KEY_PRODUCT_DESCRIPTION = "description";
	private static final String KEY_PRODUCT_ICON = "image_url";
	private static final String KEY_PRODUCT_COMPANY = "company";

	// Leads table create statement
	private static final String CREATE_TABLE_LEADS = "CREATE TABLE "
			+ TABLE_LEADS + "(" + 
			KEY_ID + " integer primary key," + 
			KEY_LEAD_COMPANY + " TEXT," + 
			KEY_LEAD_BRANCH + " TEXT," + 
			KEY_LEAD_PRODUCT + " TEXT," + 
			KEY_LEAD_NAME + " TEXT," + 
			KEY_LEAD_PHONE + " TEXT," + 
			KEY_LEAD_EMAIL + " TEXT," + 
			KEY_LEAD_COMMENTS + " TEXT," + 
			KEY_LEAD_STATUS + " TEXT," + 
			KEY_CREATED_AT + " DATETIME" + ")";

	// Company table create statement
	private static final String CREATE_TABLE_COMPANY = "CREATE TABLE "
			+ TABLE_COMPANY + "(" + 
			KEY_ID + " integer primary key," + 
			KEY_NAME + " TEXT," + 
			KEY_COMPANY_DESCRIPTION + " TEXT," + 
			KEY_COMPANY_ICON + " TEXT," + 
			KEY_CREATED_AT + " DATETIME" + ")";

	// Product table create statement
	private static final String CREATE_TABLE_PRODUCT = "CREATE TABLE "
			+ TABLE_PRODUCT + "(" + 
			KEY_ID + " integer primary key," + 
			KEY_NAME + " TEXT," + 
			KEY_PRODUCT_DESCRIPTION + " TEXT," + 
			KEY_PRODUCT_ICON + " TEXT," + 
			KEY_PRODUCT_COMPANY + " TEXT," + 
			KEY_CREATED_AT + " DATETIME" + ")";

	// Branch table create statement
	private static final String CREATE_TABLE_BRANCH = "CREATE TABLE "
			+ TABLE_BRANCH + "(" + 
			KEY_ID + " integer primary key," + 
			KEY_NAME + " TEXT," + 
			KEY_BRANCH_COMPANY + " TEXT," + 
			KEY_CREATED_AT + " DATETIME" + ")";

	// Api table create statement
	private static final String CREATE_TABLE_API = "CREATE TABLE "
			+ TABLE_API + "(" + 
			KEY_ID + " integer primary key autoincrement," + 
			KEY_API_DATE + " DATETIME" + ")";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_LEADS);
		db.execSQL(CREATE_TABLE_COMPANY);
		db.execSQL(CREATE_TABLE_BRANCH);
		db.execSQL(CREATE_TABLE_PRODUCT);
		db.execSQL(CREATE_TABLE_API);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEADS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPANY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BRANCH);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_API);

		// create new tables
		onCreate(db);

	}

	/**
	 * Insert a new lead record
	 * 
	 * @param lead A sales object containing all the details we need
	 * @return The id of the new record
	 */
	public long createLead(Lead lead) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		if(!lead.getId().equalsIgnoreCase(""))
			values.put(KEY_ID, lead.getId());
		values.put(KEY_LEAD_COMPANY, lead.getCompany());
		values.put(KEY_LEAD_BRANCH, lead.getBranch());
		values.put(KEY_LEAD_PRODUCT, lead.getProduct());
		values.put(KEY_LEAD_NAME, lead.getName());
		values.put(KEY_LEAD_PHONE, lead.getPhone());
		values.put(KEY_LEAD_EMAIL, lead.getEmail());
		values.put(KEY_LEAD_COMMENTS, lead.getDetails());
		values.put(KEY_LEAD_STATUS, lead.getStatus());
		values.put(KEY_CREATED_AT, getDateTime());
		long id = db.insert(TABLE_LEADS, null, values);

		//close db connection
		db.close();
		return id;
	}

	/**
	 * Create a New Company
	 * @param company Instance of <MyCompany>
	 * @return long id of the new record
	 */
	public long createCompany(MyCompany company) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ID, company.getId());
		values.put(KEY_NAME, company.getName());
		values.put(KEY_COMPANY_DESCRIPTION, company.getDescription());
		values.put(KEY_COMPANY_ICON, company.getImage_url());
		values.put(KEY_CREATED_AT, getDateTime());
		long id = db.insert(TABLE_COMPANY, null, values);

		//close db connection
		db.close();
		return id;
	}

	/**
	 * Create a New Product
	 * @param product Instance of <MyProduct>
	 * @param company Company that the product belongs
	 * @return long Id of the new record
	 */
	public long createProduct(MyProduct product, String company) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ID, product.getId());
		values.put(KEY_NAME, product.getName());
		values.put(KEY_PRODUCT_DESCRIPTION, product.getDescription());
		values.put(KEY_PRODUCT_ICON, product.getUrl());
		values.put(KEY_PRODUCT_COMPANY, company);
		values.put(KEY_CREATED_AT, getDateTime());
		long id = db.insert(TABLE_PRODUCT, null, values);

		//close db connection
		db.close();
		return id;
	}

	/**
	 * Create a New Branch
	 * @param branchid Id of the Branch
	 * @param name Name of the Branch
	 * @param company Company that the branch belongs
	 * @return long id of the new record
	 */
	public long createBranch(String branchid, String name, String company) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ID, branchid);
		values.put(KEY_NAME, name);
		values.put(KEY_BRANCH_COMPANY, company);
		values.put(KEY_CREATED_AT, getDateTime());
		long id = db.insert(TABLE_BRANCH, null, values);

		//close db connection
		db.close();
		return id;
	}

	/**
	 * Create a new Api
	 * @return long id of the new record
	 */
	public long createApi() {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_API_DATE, getDateTime());
		long id = db.insert(TABLE_API, null, values);

		//close db connection
		db.close();
		return id;
	}

	/**
	 * Check if the difference between the last record and now's date is more than 1 hour. 
	 * @return boolean
	 */
	public boolean isDownloadable(){
		String selectQuery = "SELECT  * FROM " + TABLE_API +" ORDER BY "+ KEY_API_DATE + " DESC ";
		//Log.e(LOG, selectQuery);

		boolean isDownloadable = true;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cusorsales = db.rawQuery(selectQuery, null);

		String date = "";
		if(cusorsales.moveToNext()) {
			date = cusorsales.getString((cusorsales.getColumnIndex(KEY_API_DATE)));
			//Log.e(LOG, date);

			int hours = getDateDifference(getDateFromString(date));
			if(hours >= 1) isDownloadable = true;
			else isDownloadable = false;
		}else {
			isDownloadable = true;
		}

		cusorsales.close();
		db.close();

		return isDownloadable;
	}

	/**
	 * Update the sales record
	 * 
	 * @param statud Status of the lead
	 * @param leadId The lead unique id
	 * @param name The name of the contact
	 * @return boolean, true if updated, false otherwise
	 */
	public boolean updateLead(String status, String leadId, String name) {
		boolean isUpdated = false;
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_LEAD_STATUS, status);
		values.put(KEY_LEAD_NAME, name);

		int rows_affected = db.update(TABLE_LEADS, values, KEY_ID + "=?", new String[] { leadId });
		Log.i("DATABASE_HELPER", rows_affected+"");

		if (rows_affected > 0)
			isUpdated =  true;
		else
			isUpdated =  false;

		//close the db connection
		db.close();
		return isUpdated;
	}
	
	/**
	 * Update the sales record
	 * 
	 * @param statud Status of the lead
	 * @param leadId The lead unique id
	 * @return boolean, true if updated, false otherwise
	 */
	public boolean updateLead(String status, String leadId) {
		boolean isUpdated = false;
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_LEAD_STATUS, status);

		int rows_affected = db.update(TABLE_LEADS, values, KEY_ID + "=?", new String[] { leadId });
		Log.i("DATABASE_HELPER", rows_affected+"");

		if (rows_affected > 0)
			isUpdated =  true;
		else
			isUpdated =  false;

		//close the db connection
		db.close();
		return isUpdated;
	}

	/**
	 * Checks if the lead exists in the db, and saves it if its not available
	 * 
	 * @param lead Lead to check or save
	 * @param lead_status The lead status
	 * @return boolean, if operation has been successful.
	 */
	public void createOrUpdateLead(Lead lead, String status) {
		String selectQuery = "SELECT  * FROM " + TABLE_LEADS +" WHERE "+ KEY_ID +"='"+lead.getId()+"'";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cusorsales = db.rawQuery(selectQuery, null);
		
		// looping through all rows and adding to list
		if(cusorsales.moveToNext()) {
			updateLead(status, lead.getId(), lead.getName());
		}else {
			createLead(lead);
			
			//Update the lead and status
			updateLead(status, lead.getId());
		}
	}

	/**
	 * Update a product's record
	 * 
	 * @param name Name of the product
	 * @param description Description of the product
	 * @param image Image Icon of the product
	 * @param id Product ID
	 * @return boolean Whether the product has been updated
	 */
	public boolean updateProduct(String name, String description, String image, String id) {
		boolean isUpdated = false;
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name);
		values.put(KEY_PRODUCT_DESCRIPTION, description);
		values.put(KEY_PRODUCT_ICON, image);

		int rows_affected = db.update(TABLE_PRODUCT, values, KEY_ID + "=?", new String[] { id });
		Log.i("DATABASE_HELPER", rows_affected+"");

		if (rows_affected > 0)
			isUpdated =  true;
		else
			isUpdated =  false;

		//close the db connection
		db.close();
		return isUpdated;
	}

	/**
	 * Update a company's record
	 * 
	 * @param description Company Description
	 * @param image Company Image Icon
	 * @param id Company Id
	 * @return boolean Whether the company has been updated
	 */
	public boolean updateCompany(String description, String image, String id) {
		boolean isUpdated = false;
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_COMPANY_DESCRIPTION, description);
		values.put(KEY_COMPANY_ICON, image);

		int rows_affected = db.update(TABLE_COMPANY, values, KEY_ID + "=?", new String[] { id });
		//Log.i("DATABASE_HELPER", rows_affected+"");

		if (rows_affected > 0)
			isUpdated =  true;
		else
			isUpdated =  false;

		//close the db connection
		db.close();
		return isUpdated;
	}

	/**
	 * Check if the record exists, then do an update or create a new Company record
	 * @param company Instance of MyCompany
	 */
	public void createOrUpdateCompany(MyCompany company) {
		// Check if this record exists kwanza, then we can either create a record or update the existing record
		String selectQuery = "SELECT  * FROM " + TABLE_COMPANY +" WHERE "+ KEY_ID +"='"+company.getId()+"'";

		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cusorsales = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if(cusorsales.moveToNext()) {
			updateCompany(company.getDescription(), company.getImage_url(), company.getId());
		}else {
			createCompany(company);
		}
	}

	/**
	 * Checks if a branch is available in the db, if not the branch is created
	 * 
	 * @param branchid Branch ID
	 * @param branchName Branch Name
	 * @param company Company Linked with the branch
	 */
	public void createOrUpdateBranch(String branchid, String branchName, String company) {
		// Check if this record exists kwanza, then we can either create a record or update the existing record
		String selectQuery = "SELECT  * FROM " + TABLE_BRANCH +" WHERE "+ KEY_ID +"='"+branchid+"'";

		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cusorsales = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if(cusorsales.moveToNext()) {			
		}else {
			createBranch(branchid, branchName, company);
		}
	}

	/**
	 * Check if the record exists, then do an update or create a new Company record
	 * @param product Instance of MyProduct
	 * @param company Company that this product belongs
	 */
	public void createOrUpdateProduct(MyProduct product, String company) {
		// Check if this record exists, then either create or update
		String selectQuery = "SELECT  * FROM " + TABLE_PRODUCT +" WHERE "+ KEY_ID +"='"+product.getId()+"'";

		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cusorsales = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if(cusorsales.moveToNext()) {
			Log.i(LOG, "createOrUpdateProduct: PRODUCT =>:"+product.getId());
			updateProduct(product.getName(), product.getDescription(), product.getUrl(), product.getId());
		}else {
			createProduct(product, company);
		}
	}

	/**
	 * Delete a record from the Sales Table
	 * @param saleId The lead id, pass '1' to delete all the records
	 * @return boolean, true if deleted, false otherwise
	 */
	public boolean deleteLead(String leadId) {
		boolean isDeleted = false;
		SQLiteDatabase db = this.getWritableDatabase();

		int rowsAffected = db.delete(TABLE_LEADS, KEY_ID + "=?", new String[] { leadId });
		if (rowsAffected > 0)
			isDeleted =  true;
		else
			isDeleted =  false;

		//Close the db connection
		db.close();
		return isDeleted;
	}

	/**
	 * getting all Sales
	 * @return List<Lead>
	 */
	public List<Lead> getAllLeads() {
		List<Lead> all_leads = new ArrayList<Lead>();
		String selectQuery = "SELECT  * FROM " + TABLE_LEADS +" ORDER BY "+ KEY_ID + " DESC ";

		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cusorsales = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cusorsales.moveToFirst()) {
			do {
				Lead t = new Lead();
				t.setId(cusorsales.getInt((cusorsales.getColumnIndex(KEY_ID)))+"");
				t.setCompany(cusorsales.getString(cusorsales.getColumnIndex(KEY_LEAD_COMPANY)));
				t.setBranch(cusorsales.getString(cusorsales.getColumnIndex(KEY_LEAD_BRANCH)));
				t.setProduct(cusorsales.getString(cusorsales.getColumnIndex(KEY_LEAD_PRODUCT)));
				t.setName(cusorsales.getString(cusorsales.getColumnIndex(KEY_LEAD_NAME)));
				t.setPhone(cusorsales.getString(cusorsales.getColumnIndex(KEY_LEAD_PHONE)));
				t.setEmail(cusorsales.getString(cusorsales.getColumnIndex(KEY_LEAD_EMAIL)));
				t.setDetails(cusorsales.getString(cusorsales.getColumnIndex(KEY_LEAD_COMMENTS)));
				t.setStatus(cusorsales.getString(cusorsales.getColumnIndex(KEY_LEAD_STATUS)));

				// adding to tags list
				all_leads.add(t);
			} while (cusorsales.moveToNext());
		}

		cusorsales.close();
		db.close();
		return all_leads;
	}

	/**
	 * Get List of all the companies
	 * @return List<MyCompany>
	 */
	public List<MyCompany> getAllCompanies() {
		List<MyCompany> all_company = new ArrayList<MyCompany>();
		String selectQuery = "SELECT  * FROM " + TABLE_COMPANY;

		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cusorsales = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cusorsales.moveToFirst()) {
			do {
				MyCompany t = new MyCompany();
				t.setId(cusorsales.getInt((cusorsales.getColumnIndex(KEY_ID)))+"");
				t.setName(cusorsales.getString(cusorsales.getColumnIndex(KEY_NAME)));
				t.setDescription(cusorsales.getString(cusorsales.getColumnIndex(KEY_COMPANY_DESCRIPTION)));
				t.setImage_url(cusorsales.getString(cusorsales.getColumnIndex(KEY_COMPANY_ICON)));

				// adding to tags list
				all_company.add(t);
				Log.i(LOG, t.toString());
			} while (cusorsales.moveToNext());
		}

		cusorsales.close();
		db.close();
		return all_company;

	}

	/**
	 * Get All Products of a certain company
	 * @param company
	 * @return List<MyProduct>
	 */
	public List<MyProduct> getAllProducts(String company) {
		List<MyProduct> all_products = new ArrayList<MyProduct>();
		String selectQuery = "SELECT  * FROM " + TABLE_PRODUCT + " WHERE "+KEY_PRODUCT_COMPANY +"='"+company+"'";

		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cusorsales = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cusorsales.moveToFirst()) {
			do {
				MyProduct t = new MyProduct();
				t.setId(cusorsales.getInt((cusorsales.getColumnIndex(KEY_ID)))+"");
				t.setName(cusorsales.getString(cusorsales.getColumnIndex(KEY_NAME)));
				t.setDescription(cusorsales.getString(cusorsales.getColumnIndex(KEY_PRODUCT_DESCRIPTION)));
				t.setUrl(cusorsales.getString(cusorsales.getColumnIndex(KEY_PRODUCT_ICON)));

				// adding to tags list
				all_products.add(t);
				Log.i(LOG, t.toString());				
			} while (cusorsales.moveToNext());
		}

		cusorsales.close();
		db.close();
		return all_products;

	}

	/**
	 * Get All Branches of a certain company
	 * @param company
	 * @return List<MyBranch>
	 */
	public List<MyBranch> getAllBranches(String company) {
		List<MyBranch> all_leads = new ArrayList<MyBranch>();
		String selectQuery = "SELECT  * FROM " + TABLE_BRANCH + " WHERE "+KEY_BRANCH_COMPANY +"='"+ company +"'";

		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cusorsales = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cusorsales.moveToFirst()) {
			do {
				MyBranch t = new MyBranch();
				t.setId(cusorsales.getInt((cusorsales.getColumnIndex(KEY_ID)))+"");
				t.setName(cusorsales.getString(cusorsales.getColumnIndex(KEY_NAME)));
				t.setDescription(cusorsales.getString(cusorsales.getColumnIndex(KEY_BRANCH_COMPANY)));
				t.setCompany(cusorsales.getString(cusorsales.getColumnIndex(KEY_BRANCH_COMPANY)));

				// adding to tags list
				all_leads.add(t);
			} while (cusorsales.moveToNext());
		}

		cusorsales.close();
		db.close();
		return all_leads;

	}

	/**
	 * Lookup for one lead with the given id, then return its instance
	 * 
	 * @param leadId The Leads Id
	 * @return Lead
	 */
	public Lead getLead(String leadId) {		
		String selectQuery = "SELECT  * FROM " + TABLE_LEADS +" WHERE "+ KEY_ID +"='"+leadId+"'";

		//Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cusorsales = db.rawQuery(selectQuery, null);
		Lead t = new Lead();

		// looping through all rows and adding to list
		if (cusorsales.moveToFirst()) {
			if(cusorsales.moveToNext()) {
				t.setId(cusorsales.getInt((cusorsales.getColumnIndex(KEY_ID)))+"");
				t.setCompany(cusorsales.getString(cusorsales.getColumnIndex(KEY_LEAD_COMPANY)));
				t.setBranch(cusorsales.getString(cusorsales.getColumnIndex(KEY_LEAD_BRANCH)));
				t.setProduct(cusorsales.getString(cusorsales.getColumnIndex(KEY_LEAD_PRODUCT)));
				t.setName(cusorsales.getString(cusorsales.getColumnIndex(KEY_LEAD_NAME)));
				t.setPhone(cusorsales.getString(cusorsales.getColumnIndex(KEY_LEAD_PHONE)));
				t.setEmail(cusorsales.getString(cusorsales.getColumnIndex(KEY_LEAD_EMAIL)));
				t.setDetails(cusorsales.getString(cusorsales.getColumnIndex(KEY_LEAD_COMMENTS)));
				t.setStatus(cusorsales.getString(cusorsales.getColumnIndex(KEY_LEAD_STATUS)));		

				Log.i("DATABASE_HELPER", t.toString());
			}
		}
		cusorsales.close();
		db.close();
		return t;
	}

	/**
	 * get datetime
	 * */
	private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}

	/**
	 * Converts a string to date
	 * @param dateString String date
	 * @return Date
	 */
	private Date getDateFromString(String dateString) {
		try {
			Date dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dateString);
			return dateFormat;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get the difference in hours between the two dates
	 * @param from Date in database
	 * @return int Number of hours between the two dates
	 */
	private int getDateDifference(Date from) {
		long secs = ((new Date().getTime()) - (from.getTime())) / 1000;
		int hours = (int) (secs / 3600);		
		return hours;
	}
}
