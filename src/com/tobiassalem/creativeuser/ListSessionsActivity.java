package com.tobiassalem.creativeuser;

import java.util.List;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.tobiassalem.creativeuser.model.Session;
import com.tobiassalem.creativeuser.ui.SessionItemAdapter;
import com.tobiassalem.creativeuser.util.Settings;

/**
 * Activity lists historic sessions of authenticated user
 * 
 * @author Tobias
 *
 */
public class ListSessionsActivity extends AppActivity {
	
	// User session list
	private List<Session>	mSessionList;
	
	// UI references.
	private ListView		mListView;
	private TextView		mTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		buildUI();
		//updateData();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		verifyOnline();
		updateData();
	}
	
	private void buildUI() {
		// Build UI
		setContentView(R.layout.activity_list_sessions);
		//String title = getResources().getString(R.string.title_list_sessions, user.getUserName(), String.valueOf(Settings.getListSize(this)));
		mTextView = (TextView) findViewById(R.id.sessionListTitle);
		//mTextView.setText(title);
		
		mListView = (ListView) findViewById(R.id.sessionList);
		// Read user sessions from db and populate the list view
		// TODO: read sessions in AsyncTask
		//mSessionList = mDatabase.getUserSessions(user.getUserId(), Settings.getListSize(this));
		//Session[] sessionArray = mSessionList.toArray(new Session[mSessionList.size()]);		
		//debug("Got " + mSessionList.size() + " nr of sessions for user " + user.getUserId() + " - " + user.getUserName());
		
		// Use the SimpleCursorAdapter to show the elements in a ListView
		//mDatabase.getUserSessions(user.getUserId());
		//ArrayAdapter<Session> adapter = new ArrayAdapter<Session>(this, android.R.layout.simple_list_item_1, values);
		//SessionItemAdapter adapter = new SessionItemAdapter(this, R.layout.listitem, sessionArray);
		//mListView.setAdapter(adapter);
		
		findViewById(R.id.settingsButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				doSettings();
			}
		});
		
		findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				doLogout();
			}
		});
	}
	
	private void updateData() {
		// Setting for list size may have changed, update title and list
		String title = getResources().getString(R.string.title_list_sessions, user.getUserName(), String.valueOf(Settings.getListSize(this)));
		mTextView.setText(title);
		
		// Read user sessions from db and populate the list view
		// TODO: read sessions in AsyncTask
		mSessionList = mDatabase.getUserSessions(user.getUserId(), Settings.getListSize(this));
		Session[] sessionArray = mSessionList.toArray(new Session[mSessionList.size()]);
		debug("Got " + mSessionList.size() + " nr of sessions for user " + user.getUserId() + " - " + user.getUserName());
		
		SessionItemAdapter adapter = new SessionItemAdapter(this, R.layout.listitem, sessionArray);
		mListView.setAdapter(adapter);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_list_sessions, menu);
		return true;
	}
	
}
