package com.tobiassalem.creativeuser.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;


/**
 * Class manages all Settings of the application
 * 
 * @author tobias
 *
 */
public class Settings {
	// Debug mode flag - TODO: do not forget to set debugMode to false before releasing.
	private static final boolean	DEBUG_MODE				= false;
	
	// Settings persistence identifier - update if any relevant keys change
	private static final String		IDENTIFIER				= "CreativeUserApp.settings.1.0";
	
	// Basics
	private static final String		TAG						= Settings.class.getSimpleName();
	private static final String		SPLITTER				= ",";
	private static String			DEFAULTS_LOADED			= "DEFAULTS_LOADED";
	private static boolean			initialized				= false;
	
	// Setting for list size
	public static final String		LIST_SIZE				= "LIST_SIZE";
	public static final int			DEFAULT_LIST_SIZE		= 5;
	
	// Setting for user name memory on login view
	public static final String		USERNAME_MEMORY			= "USERNAME_MEMORY";
	public static final String		USERNAME_LAST			= "USERNAME_LAST";
	public static final boolean		DEFAULT_USERNAME_MEMORY	= true;
	
	private static List<Integer>	listSizeOptions			= new ArrayList<Integer>();
	
	static {
		listSizeOptions.add(2);
		listSizeOptions.add(5);
		listSizeOptions.add(10);
		listSizeOptions.add(15);
		listSizeOptions.add(20);
		listSizeOptions.add(30);
		listSizeOptions.add(40);
		listSizeOptions.add(50);
	}
	
	public static void init(Context context) {
		if (initialized) {
			return;
		}
		
		loadDefaults(context);
		initialized = true;
	}
	
	public static boolean isDebugMode() {
		return DEBUG_MODE;
	}
	
	/**
	 * Loads the default values of the application. It's important that this is only done once.
	 * Any user settings should of course be persisted over multiple sessions.
	 * Thus this is NOT the same as initializing the Settings data maps (which is done once per session).
	 * 
	 * @param context
	 */
	private static void loadDefaults(Context context) {
		if (is(context, DEFAULTS_LOADED)) {
			//Log.d(TAG, "Defaults already loaded, return.");
			return;
		}
		// === [Default values] ===
		//Settings.set(context, Settings.ROAMING, true);
		Settings.set(context, Settings.LIST_SIZE, DEFAULT_LIST_SIZE);
		Settings.set(context, Settings.USERNAME_MEMORY, DEFAULT_USERNAME_MEMORY);
		
		// Persist that we have loaded defaults (i.e. do not overwrite persisted user settings on a new session)
		Settings.set(context, DEFAULTS_LOADED, true);
	}
	
	public static String getString(final Context context, final int key) {
		return context.getResources().getString(key);
	}
	
	public static String[] getStringArray(final Context context, final String key) {
		String values = get(context, key);
		return values.split(SPLITTER);
	}
	
	public static boolean contains(final Context context, final String key) {
		return Settings.getSettings(context).contains(key);
	}
	
	public static String get(final Context context, final String key) {
		return Settings.getSettings(context).getString(key, "");
	}
	
	public static int getInt(final Context context, final String key) {
		return Settings.getSettings(context).getInt(key, 0);
	}
	
	public static Long getLong(final Context context, final String key) {
		return Settings.getSettings(context).getLong(key, 0);
	}
	
	public static boolean is(final Context context, final String key) {
		return Settings.getSettings(context).getBoolean(key, false);
	}
	
	public static void set(final Context context, final String key, final boolean value) {
		final Editor editor = Settings.getSettings(context).edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public static void set(final Context context, final String key, final String value) {
		final Editor editor = Settings.getSettings(context).edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	
	public static void set(final Context context, final String key, final String[] values) {
		final Editor editor = Settings.getSettings(context).edit();
		StringBuffer valuesMerged = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			valuesMerged.append(values[i]);
			// Don't add splitter after last value
			if (i < values.length - 1) {
				valuesMerged.append(SPLITTER);
			}
		}
		editor.putString(key, valuesMerged.toString());
		editor.commit();
	}
	
	public static void set(final Context context, final String key, final int value) {
		final Editor editor = Settings.getSettings(context).edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	public static void set(final Context context, final String key, final long value) {
		final Editor editor = Settings.getSettings(context).edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	public static void clear(final Context context) {
		Editor editor = getSettings(context).edit();
		editor.clear();
		editor.commit();
		initialized = false;
	}
	
	/* ====================================== [UTILITY METHODS ] ================================================= */
	
	public static String getLastUsername(final Context context) {
		return Settings.get(context, Settings.USERNAME_LAST);
	}
	
	public static int getListSize(final Context context) {
		return Settings.getInt(context, Settings.LIST_SIZE);
	}
	
	public static List<Integer> getListSizeOptions() {
		return listSizeOptions;
	}
	
	/* ====================================== [TIMESTAMP LOGIC] ================================================ */
	
	public static void setTimestamp(final Context context, final String TIMESTAMP_KEY) {
		set(context, TIMESTAMP_KEY, System.currentTimeMillis());
	}
	
	/**
	 * Checks if at least TIME has passed since the TIMESTAMP_KEY was last stored in Settings.
	 * All timestamps are naturally stored as longs - typically by calling System.currentTimeMillis();
	 * 
	 * @param context
	 * @param TIMESTAMP_KEY - the timestamp key to check against
	 * @param TIME - the threshold time
	 * @return true if atleast TIME has passed, false otherwise
	 */
	public static boolean timePassedSinceTimestamp(final Context context, final String TIMESTAMP_KEY, final long TIME) {
		final long now = System.currentTimeMillis();
		long timestamp = 0;
		try {
			timestamp = Settings.getLong(context, TIMESTAMP_KEY);
		} catch (final Exception e) {
			Log.e(TAG, "Exception while reading timestamp " + TIMESTAMP_KEY + ": " + e.getMessage());
		}
		return now - timestamp > TIME;
	}
	
	public static void resetTimestamp(final Context context, final String TIMESTAMP_KEY) {
		final long resetValue = 0;
		set(context, TIMESTAMP_KEY, resetValue);
	}
	
	public static boolean timestampExists(final Context context, final String TIMESTAMP_KEY) {
		SharedPreferences settings = getSettings(context);
		long value = settings.getLong(TIMESTAMP_KEY, -1);
		return value != -1;
	}
	
	
	/* ====================================== [PRIVATE HELP METHODS ] ================================================ */
	
	private static SharedPreferences getSettings(final Context context) {
		return context.getSharedPreferences(Settings.IDENTIFIER, Context.MODE_PRIVATE);
	}
}
