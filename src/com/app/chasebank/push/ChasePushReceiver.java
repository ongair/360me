package com.app.chasebank.push;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.infobip.push.AbstractPushReceiver;
import com.infobip.push.PushNotification;

public class ChasePushReceiver extends AbstractPushReceiver {
	
	private static final String TAG = "ChasePushReceiver";

	@Override
	public void onRegistered(Context context) {
		/**
		 * Registration successful
		 */
		Log.i(TAG, "onRegistered: ");
		Toast.makeText(context, "Successfully registered.", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onRegistrationRefreshed(Context context) {
		/**
		 * Registration Refresh successful
		 */
		Log.i(TAG, "onRegistrationRefreshed: ");
		//Toast.makeText(context, "Registration is refreshed.", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onNotificationReceived(PushNotification notification, Context context) { 
		/**
		 * Notification Successful
		 */
		Log.i(TAG, "onNotificationReceived: "+notification.getMessage());
		//Toast.makeText(context, "Received notification: " + notification.toString(), Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onNotificationOpened(PushNotification notification, Context context) {
		/**
		 * Notification Successful
		 */
		Log.i(TAG, "onNotificationOpened: "+notification.getMessage());
		//Toast.makeText(context, "Notification opened.", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onUnregistered(Context context) {
		/**
		 * Notification Successful
		 */
		Log.i(TAG, "onUnregistered: User has been unregistered ");
		//Toast.makeText(context, "Successfully unregistered.", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onError(int reason, Context context) {
		/**
		 * Notification Successful
		 */
		Log.i(TAG, "onError: An error occured while registering the user..");
		//Toast.makeText(context, "Error occurred: " + reason, Toast.LENGTH_SHORT).show();
	}
	
}
