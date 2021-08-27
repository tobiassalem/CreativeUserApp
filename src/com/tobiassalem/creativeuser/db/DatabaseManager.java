package com.tobiassalem.creativeuser.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tobiassalem.creativeuser.model.Session;
import com.tobiassalem.creativeuser.model.User;
import com.tobiassalem.creativeuser.util.Settings;

/**
 * Class manages the communication with our database.
 * 
 * @author Tobias
 *
 */
public class DatabaseManager extends SQLiteOpenHelper {
	
	// Database Version
	private static final int	DATABASE_VERSION	= 1;
	// Database Name
	private static final String	DATABASE_NAME		= "creativeUserDB";
	
	// Database constants
	private static final int	DB_TRUE				= 1;
	
	// Table name - users
	private static final String	TABLE_USERS			= "users";
	
	private static String		USER_ID				= "user_id";
	private static String		USER_NAME			= "user_name";
	private static String		USER_PWD			= "user_pwd";
	private static String		PHONE				= "user_phone";
	private static String		ALL_COLUMNS_USER	= USER_ID + ", " + USER_NAME + ", " + USER_PWD + ", " + PHONE;
	
	// Table name - sessions
	private static final String	TABLE_SESSIONS		= "sessions";
	private static String		SESSION_ID			= "session_id";
	private static String		SESSION_USER_ID		= "session_user_id";
	private static String		SESSION_TIMESTAMP	= "timestamp";
	private static String		SESSION_AUTH		= "authenticated";
	
	
	public DatabaseManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	/**
	 * onCreate() is called by the framework, if the database does not exists.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		final String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USERS + "("
			+ USER_ID + " INTEGER PRIMARY KEY,"
			+ USER_NAME + " TEXT, "
			+ USER_PWD + " TEXT, "
			+ PHONE + " TEXT"
			+ ")";
		db.execSQL(CREATE_USER_TABLE);
		
		final String CREATE_SESSIONS_TABLE =
			"CREATE TABLE " + TABLE_SESSIONS + "("
				+ SESSION_ID + " INTEGER PRIMARY KEY,"
				+ SESSION_USER_ID + " INTEGER, "
				+ SESSION_TIMESTAMP + " INTEGER, "
				+ SESSION_AUTH + " INTEGER,"
				+ "FOREIGN KEY(" + SESSION_USER_ID + ") references " + TABLE_USERS + "(" + USER_ID + ") "
				+ ")";
		db.execSQL(CREATE_SESSIONS_TABLE);
		//db.close();
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO: add any needed data migration on upgrades
		// Drop any obsolete tables on upgrade
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
		// Create tables for our new database version
		onCreate(db);
	}
	
	/* ============================================== [USER LOGIC] ============================================================== */
	
	// Adding new user
	public int addUser(User user) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(USER_NAME, user.getUserName());
		values.put(USER_PWD, user.getUserPassword());
		values.put(PHONE, user.getPhone());
		// Inserting Row
		int id = (int) db.insert(TABLE_USERS, null, values);
		db.close(); // Closing database connection
		return id;
	}
	
	// Edit an existing user
	public int editUser(User user, String position) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(USER_NAME, user.getUserName());
		values.put(USER_PWD, user.getUserPassword());
		values.put(PHONE, user.getPhone());
		// updating row
		return db.update(TABLE_USERS, values, USER_ID + " = ?",
			new String[] { String.valueOf(position) });
	}
	
	public User getUser(final int id) {
		User user = null;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_USERS,
			new String[] { USER_ID, USER_NAME, USER_PWD, PHONE },
			USER_ID + "=?",
			new String[] { String.valueOf(id) },
			null, null, null, null);
		if (cursor.moveToFirst()) {
			user = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
		}
		
		cursor.close();
		db.close();
		return user;
	}
	
	public User getUser(final String userName) {
		// ISSUE: sometimes user is not found, even though it exists! REALLY WEIRD! Sometimes it IS found correctly. 
		// Pattern: seems to be if you give incorrect pwd first, THEN it finds the user, NOT if you give correct pwd on first attempt. 
		// However this is not always the case, is there some cache timeout?
		User user = null;
		
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT " + ALL_COLUMNS_USER + " FROM " + TABLE_USERS + " WHERE " + USER_NAME + " = ?";
		Cursor cursor = db.rawQuery(selectQuery, new String[] { userName });
		
		if (cursor.moveToFirst()) {
			user = cursorToUser(cursor);
			user = new User(cursor.getInt(cursor.getColumnIndex(USER_ID)), cursor.getString(cursor.getColumnIndex(USER_NAME)), cursor.getString(cursor
				.getColumnIndex(USER_PWD)), cursor.getString(cursor.getColumnIndex(PHONE)));
			debug("Found user: " + user + " given userName " + userName + ", cursor.count: " + cursor.getCount());
		} else {
			error("Did NOT find user given userName " + userName + ", cursor.count: " + cursor.getCount());
		}
		
		cursor.close();
		db.close();
		return user;
	}
	
	public boolean userExists(final String userName) {
		return getUser(userName) != null;
	}
	
	// List all users
	public String[] getAllUsers() {
		ArrayList<String> usersList = new ArrayList<String>();
		String selectQuery = "SELECT " + ALL_COLUMNS_USER + " FROM " + TABLE_USERS;
		try {
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);
			debug("found " + cursor.getCount() + " nr of users, columnCount: " + cursor.getColumnCount());
			
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					String user = cursor.getInt(0) + ", " + cursor.getString(1) + ", " + cursor.getString(2) + ", " + cursor.getString(3);
					usersList.add(user);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
			
			String[] users = new String[usersList.size()];
			return (usersList.toArray(users));
		} catch (Exception e) {
			error("Exception when reading users from DB: " + e.getMessage());
			return null;
		}
	}
	
	/* ============================================== [SESSION LOGIC] ============================================================== */
	
	public List<Session> getUserSessions(final int userId, final int limit) {
		List<Session> result = new ArrayList<Session>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_SESSIONS,
			new String[] { SESSION_ID, SESSION_USER_ID, SESSION_TIMESTAMP, SESSION_AUTH },
			SESSION_USER_ID + "=?",
			new String[] { String.valueOf(userId) },
			null, null, SESSION_TIMESTAMP + " DESC", String.valueOf(limit)); // groupBy, having, orderBy, limit
		
		if (cursor.moveToFirst()) {
			do {
				Session s = new Session();
				s.setSessionId(cursor.getInt(cursor.getColumnIndexOrThrow(SESSION_ID)));
				s.setSessionUser(cursor.getInt(cursor.getColumnIndexOrThrow(SESSION_USER_ID)));
				s.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(SESSION_TIMESTAMP)));
				int sessionAuth = cursor.getInt(cursor.getColumnIndexOrThrow(SESSION_AUTH));
				s.setAuthenticated(sessionAuth == DB_TRUE);
				
				result.add(s);
			} while (cursor.moveToNext());
		}
		
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		
		return result;
	}
	
	public int logSession(final User user) {
		long timestamp = System.currentTimeMillis();
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(SESSION_USER_ID, user.getUserId());
		values.put(SESSION_TIMESTAMP, timestamp);
		values.put(SESSION_AUTH, DB_TRUE);
		// Insert row and close database connection
		int id = (int) db.insert(TABLE_SESSIONS, null, values);
		db.close();
		return id;
	}
	
	/* ============================================== [UTIL METHODS] ============================================================== */
	
	private User cursorToUser(final Cursor cursor) {
		User user = new User(cursor.getInt(cursor.getColumnIndex(USER_ID)), cursor.getString(cursor.getColumnIndex(USER_NAME)), cursor.getString(cursor
			.getColumnIndex(USER_PWD)), cursor.getString(cursor.getColumnIndex(PHONE)));
		return user;
	}
	
	private void debug(String message) {
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
	
	private void error(String message) {
		if (!Settings.isDebugMode()) {
			return;
		}
		Log.e(this.getClass().getSimpleName(), message);
	}
	
}
