package com.tobiassalem.creativeuser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.tobiassalem.creativeuser.db.DatabaseManager;
import com.tobiassalem.creativeuser.model.User;
import com.tobiassalem.creativeuser.util.Settings;


/**
 * Class managing all common data and logic for our application activities.
 * 
 * @author tobias
 *
 */
public abstract class AppActivity extends Activity {
	
	/**
	 * The default email to populate the email field with.
	 */
	protected static final String	EXTRA_EMAIL			= "com.example.android.authenticatordemo.extra.EMAIL";
	protected static final int		MIN_PASSWORD_LENGTH	= 4;
	
	protected static User			user				= null;
	protected DatabaseManager		mDatabase			= null;
	
	/* ======================================== [LIFECYCLE METHODS] ======================================================= */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initDB();
		Settings.init(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mDatabase != null) {
			debug("Closing database");
			mDatabase.close();
		}
	}
	
	/* ======================================== [DATABASE METHODS] ======================================================= */
	
	protected void initDB() {
		// Setup and read the database
		if (mDatabase == null) {
			mDatabase = new DatabaseManager(getApplicationContext());
			debugList("getAllUsers", mDatabase.getAllUsers());
		}
	}
	
	/* ======================================== [LOGIC METHODS] ======================================================= */
	
	protected boolean isOnline() {
		return (user != null && user.getUserId() > 0);
	}
	
	protected void verifyOnline() {
		// Verify user is online, if not redirect to login view
		// TODO: research and implement best practice for this
		if (!isOnline()) {
			debug("User not online, forwarding to login view!");
			showPopup(R.string.error_online_required);
			switchTo(LoginActivity.class);
			finish();
		} else {
			debug("User online: " + user.getUserId() + " - " + user.getUserName() + ". Reading sessions...");
		}
	}
	
	protected void doSettings() {
		switchTo(SettingsActivity.class);
	}
	
	protected void doLogout() {
		showPopup(R.string.progress_signing_out);
		user = null;
		switchTo(LoginActivity.class);
		finish();
	}
	
	protected void postLogin(final String userName) {
		showPopup("Login successful for " + userName + "- forwarding to your session view");
		user = mDatabase.getUser(userName);
		mDatabase.logSession(user);
		if (Settings.is(this, Settings.USERNAME_MEMORY)) {
			Settings.set(this, Settings.USERNAME_LAST, userName);
		}
		switchTo(ListSessionsActivity.class);
		finish();
	}
	
	/* ======================================== [UTILITY METHODS] ======================================================= */
	
	protected void switchTo(final Class<?> clazz) {
		final Intent intent = new Intent(this, clazz);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}
	
	protected void switchTo(Intent intent) {
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}
	
	protected void showPopup(final String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	protected void showPopup(final int resourceId) {
		String message = this.getString(resourceId);
		showPopup(message);
	}
	
	/**
	 * Builds a list adapter to be used for all our lists where you select values from a range of options.
	 * 
	 * @param listOptions
	 * @return
	 */
	protected ListAdapter buildListAdapter(String[] listOptions) {
		// If we ever make the layout file more complex, send the id of the textview as a parameter.
		ListAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.listitem_simple, listOptions);
		return adapter;
	}
	
	protected ListAdapter buildListAdapterInt(Integer[] listOptions) {
		// If we ever make the layout file more complex, send the id of the textview as a parameter.
		ListAdapter adapter = new ArrayAdapter<Integer>(getApplicationContext(), R.layout.listitem_simple, listOptions);
		return adapter;
	}
	
	/**
	 * Builds a checkbox with the given resourceId, and which toggles the given key in our Settings
	 * @param resourceId
	 * @param KEY
	 * @return
	 */
	protected CheckBox buildCheckBox(final int resourceId, final String KEY) {
		final CheckBox box = (CheckBox) findViewById(resourceId);
		Object o = Settings.is(this, KEY);
		debug("Setting " + KEY + " is " + Settings.is(this, KEY) + " of type String: " + (o instanceof String));
		
		box.setChecked(Settings.is(this, KEY));
		box.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(final View v) {
				final boolean toggledValue = !Settings.is(getApplicationContext(), KEY);
				Settings.set(getApplicationContext(), KEY, toggledValue);
				box.setChecked(toggledValue);
			}
		});
		return box;
	}
	
	/* ======================================== [DEBUG METHODS] ======================================================= */
	
	protected void debug(String message) {
		if (!Settings.isDebugMode()) {
			return;
		}
		Log.d(this.getClass().getSimpleName(), message);
	}
	
	protected void debugList(String message, String[] list) {
		if (!Settings.isDebugMode()) {
			return;
		}
		debug(message + ": list of size " + list.length);
		for (int i = 0; i < list.length; i++) {
			debug("object " + i + ": " + list[i]);
		}
	}
}
