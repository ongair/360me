package com.app.chasebank;

import java.io.InputStream;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.chasebank.auth.MomentUtil;
import com.app.chasebank.fragment.LoginAdditionalFragment;
import com.app.chasebank.fragment.framework.Act;
import com.app.chasebank.util.CommonUtils;
import com.app.chasebank.util.DownloadService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.plus.PlusClient;

public final class LoginFragment extends Act implements OnClickListener, PlusClient.ConnectionCallbacks, PlusClient.OnConnectionFailedListener {

	private TextView mSignInButton;
	private ConnectionResult mConnectionResult;
	private AsyncTask<Void, Void, Void> mAuthenticate;
	protected ProgressDialog pDialog;
	private String userEmail;

	public final static String MESSAGE = "message";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		mPlusClient = new PlusClient.Builder(this, this, this)
		.setActions(MomentUtil.ACTIONS)
		.build();

		mSignInStatus = false;
		init();
	}

	private void init() {
		//Login views
		mSignInButton = (TextView) findViewById(R.id.login_button);
		mSignInButton.setOnClickListener(this);
		
		//The descriptive text view that we dont need to retain their reference
		setFontRegular((TextView) findViewById(R.id.textView1));
		setFontRegular((TextView) findViewById(R.id.act_login_info));
		setFontSemiBold(mSignInButton);
	}

	/**
	 * This method checks for the level of the logged in user in the organisation
	 * @param username The current user
	 */
	private void authenticate(final String username) {
		/**
		 * Show the progress bar
		 */
		setSupportProgressBarIndeterminateVisibility(true);

		mAuthenticate = new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				// Showing progress dialog
				pDialog = new ProgressDialog(LoginFragment.this);
				pDialog.setMessage("Please wait while we log you in...");
				pDialog.setCancelable(true);
				pDialog.setOnCancelListener(new OnCancelListener() {					

					@Override
					public void onCancel(DialogInterface dialog) {
						//Attempt logout first
						attemptLogout();

						// The dialog has been cancelled, move to home screen
						Intent intent = new Intent(LoginFragment.this, MainActivity.class);
						intent.putExtra(MESSAGE, "Login Cancelled..");
						startActivity(intent);
					}
				});
				pDialog.show();
			}

			@Override
			protected Void doInBackground(Void... params) {
				try {
					/**
					 * Request for authentication, return the users info
					 */
					loginUser = CommonUtils.authenticate(username);

				} catch (Exception e) {
					e.printStackTrace();
				}				
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if (pDialog.isShowing())
					pDialog.dismiss();
				if(loginUser.isLoginStatus()) {					
					Intent intent = new Intent(LoginFragment.this, DownloadService.class);
					intent.putExtra(DownloadService.USER_ID, loginUser.getUserId());
					startService(intent);

					setUser(loginUser.getUserId(), loginUser.getUserRole());

					/**
					 * Push Notification
					 */
					if(!loginUser.getUserId().equalsIgnoreCase("")) {
						PushNotificationRegister();
						onSubscriptionClick(loginUser.getUserEmail());
					}

					//Login successful
					if(loginUser.isSetup()) {
						//Open Summary page 2
						startActivityForResult(new Intent(LoginFragment.this, LeadSummaryActivity.class), MainActivity.LAUNCH_ACTIVITY);
					}else {
						if(isFirstLogin()){
							/**
							 * Open The dialog->LoginAdditionalFragment->LoggedInFragment
							 */
							//switchScreen(new LoginAdditionalFragment());
							LoginAdditionalFragment dialog = new LoginAdditionalFragment();
							dialog.show(getSupportFragmentManager(), "LoginAdditionalFragment");												
						}
						else 
						{
							//Open Summary page 2
							startActivityForResult(new Intent(LoginFragment.this, LeadSummaryActivity.class), MainActivity.LAUNCH_ACTIVITY);
						}
					}
				}else {
					//Log out the current user from g+
					attemptLogout();
					toast("Sorry, This email is invalid, please try again.");
				}
				mAuthenticate = null;

			}
		};
		mAuthenticate.execute(null, null, null);

	}

	@Override
	public void onStart() {
		super.onStart();
		mPlusClient.connect();
	}

	@Override
	public void onStop() {
		mPlusClient.disconnect();
		super.onStop();
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.login_button:
			int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
			if (available != ConnectionResult.SUCCESS) {
				showDialog(DIALOG_GET_GOOGLE_PLAY_SERVICES);
				return;
			}
			try {
				try {
					mConnectionResult.startResolutionForResult(this, REQUEST_CODE_SIGN_IN);
				} catch (IntentSender.SendIntentException e) {
					// Fetch a new result to start.
					mPlusClient.connect();
				}
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id != DIALOG_GET_GOOGLE_PLAY_SERVICES) {
			return super.onCreateDialog(id);
		}

		int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (available == ConnectionResult.SUCCESS) {
			return null;
		}
		if (GooglePlayServicesUtil.isUserRecoverableError(available)) {
			return GooglePlayServicesUtil.getErrorDialog(
					available, this, REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES);
		}
		return new AlertDialog.Builder(this)
		.setMessage(R.string.plus_generic_error)
		.setCancelable(true)
		.create();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_SIGN_IN
				|| requestCode == REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES) {
			if (resultCode == RESULT_OK && !mPlusClient.isConnected()
					&& !mPlusClient.isConnecting()) {
				// This time, connect should succeed.
				mPlusClient.connect();
			}
		}else if(requestCode==MainActivity.LAUNCH_ACTIVITY){
			finish();
		}		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		String currentPersonName = mPlusClient.getCurrentPerson() != null
				? mPlusClient.getCurrentPerson().getDisplayName()
						: "unknown";

				userEmail = mPlusClient.getAccountName();
				//toast(getString(R.string.signed_in_status, currentPersonName)+", Email: "+userEmail);
				updateButtons(true /* isSignedIn */);
	}

	/**
	 * Background Async task to load user profile picture from url
	 * */
	private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public LoadProfileImage(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}

	@Override
	public void onDisconnected() {
		toast(R.string.loading_status);
		mPlusClient.connect();
		updateButtons(false /* isSignedIn */);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		mConnectionResult = result;
		updateButtons(false);
	}

	private void updateButtons(boolean isSignedIn) {
		if (isSignedIn) {
			//mSignInButton.setVisibility(View.INVISIBLE);
			/**
			 * Proceed
			 */
			authenticate(userEmail);
		} 
	}


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		super.onBackPressed();
	}

}
