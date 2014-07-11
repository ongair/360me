package com.app.chasebank.fragment.framework;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.app.chasebank.MainActivity;
import com.app.chasebank.R;
import com.app.chasebank.entity.LoginUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.PlusClient;
import com.infobip.push.PushNotification;
import com.infobip.push.PushNotificationBuilder;
import com.infobip.push.PushNotificationManager;
import com.infobip.push.RegistrationData;

public abstract class Act extends SherlockFragmentActivity implements PlusClient.OnAccessRevokedListener{
	protected static final int DIALOG_GET_GOOGLE_PLAY_SERVICES = 1;
	
	protected static final int REQUEST_CODE_SIGN_IN = 1;
	protected static final int REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES = 2;
	
	protected boolean mSignInStatus;
	
	protected PlusClient mPlusClient;
	
	public static final String TAG = "Chase360";
	
	/**
	 * The current active fragment
	 */
	private Screen currentScreen;	
	
	/**
	 * Level of the logged in user
	 */
	protected LoginUser loginUser = null;
	
	/**
	 * Source Sans Pro Black Font
	 */
	private String font_Black = "fonts/SOURCESANSPRO-BLACK.OTF";
	
	/**
	 * External Font declaration
	 */
	private Typeface tf_black, tf_bold, tf_extralight, tf_light, tf_regular, tf_semibold;
	
	/**
	 * Source Sans Pro Bold Font
	 */
	private String font_Bold = "fonts/SOURCESANSPRO-BOLD.OTF";
	
	/**
	 * Source Sans Pro Extra light Font
	 */
	private String font_ExtraLight = "fonts/SOURCESANSPRO-EXTRALIGHT.OTF";
	
	/**
	 * Source Sans Pro Light Font
	 */
	private String font_Light = "fonts/SOURCESANSPRO-LIGHT.OTF";
	
	/**
	 * Source Sans Pro Regular Font
	 */
	private String font_Regular = "fonts/SOURCESANSPRO-REGULAR.OTF";

	/**
	 * Source Sans Pro SemiBold Font
	 */
	private String font_SemiBold = "fonts/SOURCESANSPRO-SEMIBOLD.OTF";

	//Preferences
	public static final String PREFS_NAME = "MyChasePrefsFile"; 
	
	/**
	 * Push Notifications
	 */
	public PushNotificationManager manager;
	public static final String senderId = "922166502802";
	public static final String applicationId = "0a7535fbb603";
	public static final String applicationSecret = "6d0bc9408365";
	
	/**
	 * Request to be used for notification broadcasts
	 */
	public static final int REQUEST_UPDATE_NOTIFICATION = 400;
	
	// This is the text view to be used for notifications accross the system
	public static TextView notification_number = null;
	
	/**
	 * Register user to receive Push Notification..
	 */
	public void PushNotificationRegister() {
		/**
		 * Push Notification:
		 */
		manager = new PushNotificationManager(this);
		manager.setDebugModeEnabled(true); //To View log
		manager.initialize(senderId, applicationId, applicationSecret);

		/**
		 * Register for push Notifications
		 */
		manager.register();
		//manager.overrideDefaultMessageHandling(true);
	}
	
	/**
	 * Send user information, perhaps after user login is successful.
	 * @param emailAddress
	 */
	public void onSubscriptionClick(String userId) {
		//Perform registration
		RegistrationData registrationData = new RegistrationData();
		registrationData.setUserId(userId);
		manager.register(registrationData); //registrationData is optional
	}
	
	/**
	 * Build a notification out of the push notification we receive from InfoBip
	 * @param notification PushNotification received
	 * @return PushNotificationBuilder
	 */
	public PushNotificationBuilder buildNotification (PushNotification notification) {
		PushNotificationBuilder builder = new PushNotificationBuilder(getApplicationContext());
		builder.setLayoutId(R.layout.notification);
		builder.setImageId(R.id.image);
		builder.setTitleId(R.id.title);
		builder.setTextId(R.id.text);
		builder.setDateId(R.id.date);
		
		/**
		 * Set Content
		 */		
		builder.setImageDrawableId(R.drawable.ic_launcher);
		
		builder.setIconDrawableId(R.drawable.chase_128);
		builder.setTickerText(notification.getMessage());
		builder.setVibration(PushNotificationBuilder.ENABLED);
		builder.setVibrationPattern(new long[]{100, 200, 100, 300});
		builder.setLightsOnOffMS(2000, 1000);
		builder.setLightsColor(Color.WHITE);
		return builder;
	}
	
	/**
	 * Inialises the fonts if they have not been set up
	 */
	public void setupFonts() {
		if(tf_black == null) tf_black= Typeface.createFromAsset(getAssets(), font_Black); 
		if(tf_bold == null) tf_bold= Typeface.createFromAsset(getAssets(), font_Bold); 
		if(tf_extralight == null) tf_extralight= Typeface.createFromAsset(getAssets(), font_ExtraLight); 
		if(tf_light == null) tf_light= Typeface.createFromAsset(getAssets(), font_Light); 
		if(tf_regular == null) tf_regular= Typeface.createFromAsset(getAssets(), font_Regular); 
		if(tf_semibold == null) tf_semibold= Typeface.createFromAsset(getAssets(), font_SemiBold); 		
	}

	/**
	 * Applies a source sans pro Black font to the view
	 * @param v Text View
	 */
	public void setFontBlack(TextView v) {
		setupFonts();
		v.setTypeface(tf_black);
	}

	/**
	 * Applies a source sans pro Black font to the view
	 * @param v Button
	 */
	public void setFontBlack(Button v) {
		setupFonts();
		v.setTypeface(tf_black);
	}

	/**
	 * Applies a source sans pro Black font to the view
	 * @param v Edit Text
	 */
	public void setFontBlack(EditText v) {
		setupFonts();
		v.setTypeface(tf_black);
	}

	/**
	 * Applies a soource sans pro Bold Font
	 * @param v Text View
	 */
	public void setFontBold(TextView v) {
		setupFonts();
		v.setTypeface(tf_bold);
	}

	/**
	 * Applies a source sans pro Bold font to the view
	 * @param v Button
	 */
	public void setFontBold(Button v) {
		setupFonts();
		v.setTypeface(tf_bold);
	}

	/**
	 * Applies a source sans pro Bold font to the view
	 * @param v Edit Text
	 */
	public void setFontBold(EditText v) {
		setupFonts();
		v.setTypeface(tf_bold);
	}

	/**
	 * Applies a source sans pro Extra Light font to the view
	 * @param v TextView
	 */
	public void setFontExtraLight(TextView v) {
		setupFonts();
		v.setTypeface(tf_extralight);
	}

	/**
	 * Applies a source sans pro Extra Light font to the view
	 * @param v Button
	 */
	public void setFontExtraLight(Button v) {
		setupFonts();
		v.setTypeface(tf_extralight);
	}

	/**
	 * Applies a source sans pro Extra Light font to the view
	 * @param v Edit Text
	 */
	public void setFontExtraLight(EditText v) {
		setupFonts();
		v.setTypeface(tf_extralight);
	}

	public void setFontLight(TextView v) {
		setupFonts();
		v.setTypeface(tf_light);
	}

	/**
	 * Applies a source sans pro Light font to the view
	 * @param v Button
	 */
	public void setFontLight(Button v) {
		setupFonts();
		v.setTypeface(tf_light);
	}

	/**
	 * Applies a source sans pro Light font to the view
	 * @param v Edit Text
	 */
	public void setFontLight(EditText v) {
		setupFonts();
		v.setTypeface(tf_light);
	}

	/**
	 * Applies a source sans pro Regualar Font
	 * @param v Text View
	 */
	public void setFontRegular(TextView v) {
		setupFonts();
		v.setTypeface(tf_regular);
	}

	/**
	 * Applies a source sans pro Regular font to the view
	 * @param v Button
	 */
	public void setFontRegular(Button v) {
		setupFonts();
		v.setTypeface(tf_regular);
	}

	/**
	 * Applies a source sans pro Regualr Font
	 * @param v Edit Text
	 */
	public void setFontRegular(EditText v) {
		setupFonts();
		v.setTypeface(tf_regular);
	}

	/**
	 * Applies a source sans pro Semi Bold font
	 * @param v Text View 
	 */
	public void setFontSemiBold(TextView v) {
		setupFonts();
		v.setTypeface(tf_semibold);
	}

	/**
	 * Applies a source sans pro Semi Boldfont
	 * @param v Button
	 */
	public void setFontSemiBold(Button v) {
		setupFonts();
		v.setTypeface(tf_semibold);
	}

	/**
	 * Applies a source sans pro Semi Bold font
	 * @param v Edit Text
	 */
	public void setFontSemiBold(EditText v) {
		setupFonts();
		v.setTypeface(tf_semibold);
	}

	/**
	 * Show a toast message to the screen
	 */
	public void toast(String message) {
		//Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		
		 LayoutInflater inflater = getLayoutInflater();
         // Inflate the Layout
         View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.custom_toast_layout));

         TextView text = (TextView) layout.findViewById(R.id.toast_text);
         // Set the Text to show in TextView
         text.setText(message);
         setFontRegular(text);

         Toast toast = new Toast(getApplicationContext());
         toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
         toast.setDuration(Toast.LENGTH_LONG);
         toast.setView(layout);
         toast.show();
	}

	/**
	 * Show toast message using int resource
	 */
	public void toast(int id) {
		toast(getResources().getString(id));
	}
	
	
	
	/**
	 * Switch screens
	 * 
	 * @param current Current Fragment
	 * @param newScreen Fragment To launch
	 * @param addToStack whether to add the current fragment to the stack
	 */
	public void switchScreen(Screen current, Screen newScreen, boolean addToStack) {
		try {
			FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
			if (addToStack) trans.addToBackStack(null);
			newScreen.setHasOptionsMenu(true);
			trans.replace(R.id.frame_main, newScreen);
			currentScreen = current;
			trans.commit();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Switch screens from a Fragment Activity
	 * 
	 * @param newScreen Fragment To launch
	 */
	public void switchScreen(Screen newScreen) {
		try {
			FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
			newScreen.setHasOptionsMenu(true);
			trans.replace(R.id.frame_main, newScreen);
			trans.commit();
		}catch(Exception ex) {ex.printStackTrace(); }
	}

	/**
	 * Get the current Screen
	 */
	public Screen getCurrentScreen() {
		return currentScreen;
	}

	/**
	 * Get the current screen name
	 */
	public String getCurrentScreenName() {
		return currentScreen.getClass().getSimpleName();
	}

	/**
	 * Switch screens (put the current screen on the stack by default)
	 * 
	 * @param current The current fragment
	 * @param newScreen The Fragment to launch
	 */

	public void switchScreen(Screen current, Screen newScreen) {
		switchScreen(current, newScreen, true);
	}

	/**
	 * Attempt to logout from the mPlusClient 
	 */
	public void attemptLogout() {
		try {
			if (mPlusClient.isConnected()) {
				mPlusClient.clearDefaultAccount();
				mPlusClient.disconnect();
				mPlusClient.connect();

				/**
				 * Close all the open fragments and go to the MainActivity
				 */
				startActivity(new Intent(this, MainActivity.class));
			}
		}catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Attempt to revoke access from the mPlusClient 
	 */
	public void attemptRevokeAccess() {
		if (mPlusClient.isConnected()) {
			mPlusClient.revokeAccessAndDisconnect(this);
		}
	}

	@Override
	public void onAccessRevoked(ConnectionResult status) {
		if (status.isSuccess()) {
			toast(R.string.revoke_access_status);
		} else {
			toast(R.string.revoke_access_error_status);
			mPlusClient.disconnect();
		}
		mPlusClient.connect();
	}

	/**
	 * Check if this is the first login that has been made
	 * @return boolean
	 */
	protected boolean isFirstLogin() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); 
		boolean firstRun = settings.getBoolean("firstLoginSetUp", true); 
		
		if (firstRun) {	        
			return true;
		} else {
			return false;
		}
	}

	/**
	 * First login been set up, set first setup preference to true
	 */
	public void setFirstLogin() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); 
		SharedPreferences.Editor editor = settings.edit(); 
		editor.putBoolean("firstLoginSetUp", false);
		editor.commit(); 
	}
	
	/**
	 * Set the Users Details 
	 * 
	 * @param user_id The User's ID
	 * @param user_role The User's Role
	 */
	public void setUser(String user_id, String user_role) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); 
		SharedPreferences.Editor editor = settings.edit(); 
		editor.putString("USER_ID", user_id);
		editor.putString("USER_ROLE", user_role);
		editor.commit(); 
	}
	
	/**
	 * Get Logged in user's id
	 * @return String
	 */
	public String getUserId() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); 
		return settings.getString("USER_ID", "");
	}
	
	/**
	 * Get Logged in user's Role
	 * @return String
	 */
	public String getUserRole() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); 
		return settings.getString("USER_ROLE", "");
	}
	
	/**
	 * Check if this is the first login that has been made
	 * @return boolean
	 */
	protected boolean isFirstRun() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); 
		boolean firstRun = settings.getBoolean("firstRunSetUp", true); 
		
		if (firstRun) {	        
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * First login been set up, set first setup preference to true
	 */
	protected void setFirstRun() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0); 
		SharedPreferences.Editor editor = settings.edit(); 
		editor.putBoolean("firstRunSetUp", false); 
		editor.commit(); 
	}
	
	/**
	 * Set the tour text to enabled or disabled. 
	 * @param isVisible 
	 */
	public void setVisible(boolean isVisible) {
		if(isVisible) {
			findViewById(R.id.swipe_left_for_tour).setVisibility(View.VISIBLE);
		}else {
			findViewById(R.id.swipe_left_for_tour).setVisibility(View.INVISIBLE);
		}		
	}
	
	/**
	 * Checks if the device is online
	 * @return boolean
	 */
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
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
	
	/**
	 * Update the number of notifications if any
	 */
	public void updateNotification(String num) {
		if(num != null && !num.equals("")) {
			int nums = Integer.parseInt(num);
			if(nums > 0) {
				
			}else {
				// Set to gone
				if (notification_number.isShown()) notification_number.setVisibility(View.GONE);
			}
		}else {
			// Set to gone
			if (notification_number.isShown()) notification_number.setVisibility(View.GONE);
		}
	}
	
}
