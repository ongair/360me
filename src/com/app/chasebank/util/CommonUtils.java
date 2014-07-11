package com.app.chasebank.util;
/**
 * @author Gregory Murimi
 */
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.app.chasebank.entity.Category;
import com.app.chasebank.entity.Lead;
import com.app.chasebank.entity.LoginUser;
import com.app.chasebank.entity.MyBranch;
import com.app.chasebank.entity.MyCompany;
import com.app.chasebank.entity.MyProduct;

public class CommonUtils {
	public static final String SERVER_URL = "http://chase360.herokuapp.com/"; 

	/**
	 * Registration States
	 */
	public static final int REGISTERED = 1;
	public static final int NOT_REGISTERED = 2;
	public static final int USERNAME_NOT_FOUND = 3;

	private static final String TAG = "CommonUtils";

	/**
	 * Issue a POST request to the server and returns a message from the server
	 *
	 * @param endpoint POST address.
	 * @param params request parameters.
	 * @return String data from the server
	 *
	 * @throws IOException propagated from POST.
	 */
	private static String postAndGetData(String endpoint) {
		HttpEntity resEntity;    	
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(endpoint);

			HttpResponse response = client.execute(post);
			resEntity = response.getEntity();
			final String response_str = EntityUtils.toString(resEntity);

			if (resEntity != null) {
				return response_str;
			}
		} catch (Exception ex) {
			Log.e("Debug", "error: " + ex.getMessage(), ex);
		}
		return "";
	}

	/**
	 * Get the role of the currently logged in user in the organisation
	 * 
	 * @param email The users email address
	 * @return The role of the user, or an error perhaps
	 */
	public static LoginUser authenticate(String email) {
		HttpEntity resEntity;    
		String  serverUrl = SERVER_URL + "verify.json";
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(serverUrl);

			MultipartEntity entity = new MultipartEntity();

			entity.addPart("email", new StringBody(email)); 
			entity.addPart("verify", new StringBody("verify")); 

			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			resEntity = response.getEntity();

			final String response_str = EntityUtils.toString(resEntity).trim();
			Log.i(TAG, "LOGIN RESPONSE: "+ response_str);

			/**
			 * Parse the response here, json format
			 */
			JSONObject verifyJson = new JSONObject(response_str);	
			String success = verifyJson.getString("status");
			if(success.equalsIgnoreCase("success")) {

				//User logged or verified
				String user_id = verifyJson.getString("user_id");
				String user_email = verifyJson.getString("user_email");
				String user_role = verifyJson.getString("user_role");
				String message = verifyJson.getString("message");
				String setup = verifyJson.getString("setup");

				Log.i(TAG, success+": "+user_id+", "+user_email+", "+user_role+", "+message+", setup=>"+setup);

				return new LoginUser(user_id, user_email, user_role, true, setup);
			}else if(success.equalsIgnoreCase("unprocessable_entity")) {
				//login failed, so logout the user from g+
				String message = verifyJson.getString("message");
				Log.i(TAG, success+": "+message);
				return new LoginUser(false);
			}
		} catch (Exception ex) {
			Log.e("Debug", "error: " + ex.getMessage(), ex);
			return new LoginUser(false);
		}
		return new LoginUser(false);
	}

	/**
	 * Query and return a companys data
	 * @param companyId The company Id
	 * @return array of companies details
	 */
	public static Object[] getCompanyDetails(String companyId) {
		HttpEntity resEntity;    
		String  serverUrl = SERVER_URL + "companies/"+companyId+".json?show=show";
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet post = new HttpGet(serverUrl);

			HttpResponse response = client.execute(post);
			resEntity = response.getEntity();

			final String response_str = EntityUtils.toString(resEntity).trim();

			/**
			 * Parse the response here, json format
			 */
			JSONObject companyJson = new JSONObject(response_str);	
			String companyName = companyJson.getString("company_name");
			String status = companyJson.getString("status");

			ArrayList<MyProduct> products = new ArrayList<MyProduct>();
			ArrayList<MyBranch> branchs = new ArrayList<MyBranch>();
			ArrayList<Category> categorys = new ArrayList<Category>();

			if(status.equalsIgnoreCase("success")) {
				//company details available
				JSONArray category = companyJson.getJSONArray("categories");
				JSONArray product = companyJson.getJSONArray("products");
				JSONArray branch = companyJson.getJSONArray("branches");

				// looping through All Categories
				for (int i = 0; i < category.length(); i++) {
					JSONObject c = category.getJSONObject(i);

					String id = c.getString("id"),
							name = c.getString("name"),
							company_id = c.getString("company_id");

					categorys.add(new Category(id, name, company_id));
				}

				// looping through All Products
				for (int i = 0; i < product.length(); i++) {
					JSONObject c = product.getJSONObject(i);

					String id = c.getString("id"),
							name = c.getString("name"),
							category_id = c.getString("category_id"),
							description = c.getString("description"),
							image_url = c.getString("image_url");

					products.add(new MyProduct(id, name, description, image_url));
				}

				// looping through All Branches
				for (int i = 0; i < branch.length(); i++) {
					JSONObject c = branch.getJSONObject(i);

					String id = c.getString("id"),
							name = c.getString("name");
					//Add to the list
					branchs.add(new MyBranch(id, name, companyId));
				}

				Object[] data = {true, companyName, branchs, products, categorys};
				return data;
			}else {
				Object[] data = {false};
				return data;
			}			
		} catch (Exception ex) {
			Log.e("Debug", "error: " + ex.getMessage(), ex);
			Object[] data = {false};
			return data;
		}
	}

	/**
	 * Gets all the company details, including branches and products
	 * @return ArrayList<Object[]>
	 */
	public static ArrayList<Object[]> getCompanies() {
		//---------------------------------------------------------
		String TAG_ID = "id", 
				TAG_NAME = "name",
				TAG_DESC = "description",
				TAG_IMAGE = "image_url";
		//---------------------------------------------------------

		HttpEntity resEntity;    
		String  serverUrl = SERVER_URL + "companies.json";
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet post = new HttpGet(serverUrl);

			HttpResponse response = client.execute(post);
			resEntity = response.getEntity();

			final String response_str = EntityUtils.toString(resEntity).trim();

			ArrayList<Object[]> all = new ArrayList<Object[]>();

			JSONArray jsonStr = new JSONArray(response_str);
			for (int i = 0; i < jsonStr.length(); i++) {
				JSONObject c = jsonStr.getJSONObject(i);

				String idc = c.getString(TAG_ID),
						namec = c.getString(TAG_NAME),
						descriptionc = c.getString(TAG_DESC),
						image_urlc = c.getString(TAG_IMAGE);

				JSONArray product = c.getJSONArray("products");
				JSONArray branch = c.getJSONArray("branches");

				ArrayList<MyProduct> products = new ArrayList<MyProduct>();
				ArrayList<MyBranch> branchs = new ArrayList<MyBranch>();

				// looping through All Products
				for (int x = 0; x < product.length(); x++) {
					JSONObject cp = product.getJSONObject(x);

					String pid = cp.getString("id"),
							pname = cp.getString("name"),
							pdescription = cp.getString("description"),
							pimage_url = cp.getString("image_url");

					products.add(new MyProduct(pid, pname, pdescription, pimage_url));
				}

				// looping through All Branches
				for (int ib = 0; ib < branch.length(); ib++) {
					JSONObject cb = branch.getJSONObject(ib);

					String idb = cb.getString("id"),
							nameb = cb.getString("name");
					//Add to the list
					branchs.add(new MyBranch(idb, nameb, namec));
				}

				Object[] data = {new MyCompany(idc, namec, descriptionc, image_urlc), branchs, products};
				all.add(data);
			}
			return all;

		} catch (Exception ex) {
			Log.e("Debug", "error: " + ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * Query and return an array of companies.
	 * @return ArrayList<MyCompany>
	 */
	public static ArrayList<MyCompany> getAllCompanies() {
		// ------------------------
		String TAG_ID = "id", 
				TAG_NAME = "name",
				TAG_DESC = "description",
				TAG_IMAGE = "image_url";
		// ------------------------
		HttpEntity resEntity;    
		String  serverUrl = SERVER_URL + "companies.json";

		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet post = new HttpGet(serverUrl);

			HttpResponse response = client.execute(post);
			resEntity = response.getEntity();

			final String response_str = EntityUtils.toString(resEntity).trim();

			/**
			 * Parse the response here, json format
			 */
			ArrayList<MyCompany> companies = new ArrayList<MyCompany>();

			JSONArray companyArray = new JSONArray(response_str);
			for (int i = 0; i < companyArray.length(); i++) {
				JSONObject c = companyArray.getJSONObject(i);

				String id = c.getString(TAG_ID),
						name = c.getString(TAG_NAME),
						description = c.getString(TAG_DESC),
						image_url = c.getString(TAG_IMAGE);
				companies.add(new MyCompany(id, name, description, image_url));
			}

			return companies;
		} catch (Exception ex) {
			Log.e("Debug", "error: " + ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * Submit a new lead
	 * 
	 * @param company The companies details we are submitting 
	 * @param branch The company Branch
	 * @param product The company product
	 * @param contact The contact linked with the lead
	 * @param details More comments about the lead
	 * @param emailaddress Email address of the person
	 * @return boolean
	 * 	  
	 * @return boolean 
	 */
	public static boolean submitLead(Lead lead, DatabaseHelper helper, String user_id) {
		HttpEntity resEntity;    
		String  serverUrl = SERVER_URL + "leads.json";

		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(serverUrl);

			MultipartEntity entity = new MultipartEntity();

			entity.addPart("branch_id", new StringBody(lead.getBranch())); 
			entity.addPart("product_id", new StringBody(lead.getProduct())); 
			entity.addPart("name", new StringBody(lead.getName())); 
			entity.addPart("phone_number", new StringBody(lead.getPhone())); 
			entity.addPart("email", new StringBody(lead.getEmail())); 
			entity.addPart("submitted_by_id", new StringBody(user_id));
			entity.addPart("message", new StringBody(lead.getDetails()));			
			post.setEntity(entity);

			HttpResponse response = client.execute(post);
			resEntity = response.getEntity();

			final String response_str = EntityUtils.toString(resEntity).trim();

			JSONObject verifyJson = new JSONObject(response_str);	
			String id = verifyJson.getString("id");
			if(id != null && !id.equalsIgnoreCase("")) {				
				/**
				 * Request successful and data submitted successfully, save in local db
				 */
				lead.setStatus("new");
				//Should get this id from the server, first time its uploaded.
				lead.setId(id);
				helper.createLead(lead);
				return true;
			}else {
				/**
				 * Request failed
				 */
				lead.setStatus("failed");
				helper.createLead(lead);
				return false;
			}			
		} catch (Exception ex) {
			Log.e("Debug", "error: " + ex.getMessage(), ex);
		}
		return false;
	}

	/**
	 * Load all the leads from the server. Combine returned data with local data
	 * 
	 * @param user_id The current users id
	 * @param context Context
	 * 
	 * @return ArrayList<Lead>
	 */
	public static ArrayList<Lead> getAllStatus(String user_id, Context context) {
		DatabaseHelper helper = new DatabaseHelper(context);
		HttpEntity resEntity;    
		String  serverUrl = SERVER_URL + "leads.json?submitted_by_id="+user_id;

		//-----------------------------------------
		String TAG_ID = "id";
		String TAG_PRODUCT = "product_id";
		String TAG_BRANCH = "branch_id";
		String TAG_SUBMITTED_BY = "submitted_by_id";
		String TAG_STATUS = "status";
		String TAG_NAME = "name";
		String TAG_EMAIL = "email";
		String TAG_PHONE = "phone_number";
		String TAG_ASSIGNED_TO = "assigned_to_id";
		String TAG_CREATED_AT = "created_at";
		String TAG_VERIFIED = "verified";
		String TAG_VALUE = "value";
		
		/**
		 {
	        "*id": 1,
	        "*name": "John",
	        "*email": "test@domain.com",
	        "*phone_number": "254705866564",
	        "*submitted_by_id": 11,
	        "*status": "Verified",
	        "assigned_to_id": 21,
	        "created_at": "2014-06-18T13:23:06.000Z",
	        "updated_at": "2014-06-18T16:37:46.000Z",
	        "*branch_id": 51,
	        "*product_id": 221,
	        "verified": true,
	        "value": 1000000,
	        "verified_by_id": null
	    }
		 */
		//-----------------------------------------
		
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet post = new HttpGet(serverUrl);

			HttpResponse response = client.execute(post);
			resEntity = response.getEntity();

			final String response_str = EntityUtils.toString(resEntity).trim();

			/**
			 * Parse the response here, json format
			 */
			ArrayList<Lead> leads = new ArrayList<Lead>();

			JSONArray leadsArray = new JSONArray(response_str);
			for (int i = 0; i < leadsArray.length(); i++) {
				JSONObject c = leadsArray.getJSONObject(i);
				
				String id = c.getString(TAG_ID),
						product = c.getString(TAG_PRODUCT),
						branch = c.getString(TAG_BRANCH),
						submitted_by = c.getString(TAG_SUBMITTED_BY),
						status = c.getString(TAG_STATUS),
						email = c.getString(TAG_EMAIL),
						name = c.getString(TAG_NAME),
						phone = c.getString(TAG_PHONE);
				
				Lead lead = new Lead(id, null, branch, product, name, phone, email, null);
				lead.setStatus(status);

				if(id != null && status != null && !id.equals("") && !status.equals("")) 
					leads.add(updateAndGetLead(id, status, helper, lead));
			}

			return leads;
		} catch (Exception ex) {
			Log.e("Debug", "error: " + ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * Update a leads status and return The Lead
	 * 
	 * @param leadId The LeadId
	 * @param lead_status The Lead Status
	 * @param helper The DatabaseHelper instance
	 * 
	 * @return Lead
	 */
	public static Lead updateAndGetLead(String leadId, String lead_status, DatabaseHelper helper, Lead lead) {
		helper.updateLead(lead_status, leadId);
		helper.createOrUpdateLead(lead, lead_status);
		Lead leads = helper.getLead(leadId);
		return lead;		
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
	 * @return boolean
	 */
	public static boolean setProfile(String id, String first_name, String company, String phone_number, String department) {
		HttpEntity resEntity;    
		String  serverUrl = SERVER_URL + "users/"+id+".json";
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(serverUrl);

			MultipartEntity entity = new MultipartEntity();

			entity.addPart("name", new StringBody(first_name)); 
			entity.addPart("company", new StringBody(company)); 
			entity.addPart("phone_number", new StringBody(phone_number));
			entity.addPart("department", new StringBody(department)); 
			entity.addPart("id", new StringBody(id)); 

			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			resEntity = response.getEntity();

			final String response_str = EntityUtils.toString(resEntity).trim();

			/**
			 * Parse the response here, json format
			 */
			JSONObject verifyJson = new JSONObject(response_str);	
			String xsuccess = verifyJson.getString("status");
			String xid = verifyJson.getString("id");

			if(xsuccess.equalsIgnoreCase("success")) return true; 
			else return false;

		} catch (Exception ex) {
			Log.e("Debug", "error: " + ex.getMessage(), ex);
			return false;
		}
	}

	/**
	 * Change the lead status of a Lead
	 * 
	 * @param id The user ID 
	 * @param lead_id The lead ID
	 * @param status The new status you want for the lead
	 * @return boolean If the change was successful
	 */
	public static boolean changeLeadStatus(String id, String lead_id, String status, DatabaseHelper helper) {
		HttpEntity resEntity;    
		String  serverUrl = SERVER_URL + "leads/"+lead_id+".json";
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(serverUrl);

			MultipartEntity entity = new MultipartEntity();

			//entity.addPart("userid", new StringBody(id)); 
			entity.addPart("id", new StringBody(lead_id)); 
			//entity.addPart("status", new StringBody(status));
			
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			resEntity = response.getEntity();
			
			final String response_str = EntityUtils.toString(resEntity).trim();
			Log.i(TAG+":"+lead_id, response_str);
			
			/**
			 * Parse the response here, json format
			 */
			JSONObject verifyJson = new JSONObject(response_str);	
			//String xsuccess = verifyJson.getString("status");
			
			try {
				String xid = verifyJson.getString("id");
				if(xid.equalsIgnoreCase(lead_id)) {
					/**
					 * We also update the db on the specific lead. 
					 */
					try {
						boolean dbSaved = helper.updateLead(status, lead_id);
						if(dbSaved) Log.i(TAG, "changeLeadStatus: lead Status Updated.");
						else Log.i(TAG, "changeLeadStatus: lead Status Update failed.");
					}catch(Exception ex) {ex.printStackTrace(); }
					
					return true; 
				}
				else return false;
			}catch(Exception ex) {ex.printStackTrace(); return false;}

		} catch (Exception ex) {
			Log.e("Debug", "error: " + ex.getMessage(), ex);
			return false;
		}
	}

}
