package com.tobiassalem.creativeuser;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.tobiassalem.creativeuser.util.Settings;


/**
 * Class handling the Settings screen and it's functionality
 * 
 * @author Tobias
 *
 */
public class SettingsActivity extends AppActivity {
	
	// General info
	private TextView	userIdView;
	private TextView	userNameView;
	
	// User Name memory setting
	private CheckBox	userNameMemoryBox;
	
	// User session list size setting
	private TextView	sessionListSize;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		buildGUI();
		//updateData();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		verifyOnline();
		updateData();
	}
	
	private void buildGUI() {
		setContentView(R.layout.activity_settings);
		setTitle(R.string.Settings_title);
		
		// User Id
		userIdView = (TextView) findViewById(R.id.settingsUserid);
		//userIdView.setText(String.valueOf(user.getUserId()));
		
		// User name
		userNameView = (TextView) findViewById(R.id.settingsUserName);
		//userNameView.setText(user.getUserName());
		
		// User name memory checkbox
		userNameMemoryBox = buildCheckBox(R.id.userNameMemory, Settings.USERNAME_MEMORY);
		
		// Session list size option
		sessionListSize = (TextView) findViewById(R.id.sessionListSize);
		//sessionListSize.setText(String.valueOf(Settings.getInt(this, Settings.LIST_SIZE)) + " " + getString(R.string.items));
		((LinearLayout) findViewById(R.id.listSizeButton)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				showListSizeOptions();
			}
		});
		
		// Return button
		findViewById(R.id.returnButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				doReturn();
			}
		});
	}
	
	private void updateData() {
		debug("updateData, user: " + user + ", usedIdView: " + userIdView);
		//verifyOnline();
		userIdView.setText(String.valueOf(user.getUserId()));
		userNameView.setText(user.getUserName());
		userNameMemoryBox.setChecked(Settings.is(this, Settings.USERNAME_MEMORY));
		sessionListSize.setText(String.valueOf(Settings.getInt(this, Settings.LIST_SIZE)) + " " + getString(R.string.items));
	}
	
	/* ============================================= [OPTION DIALOGS] ================================================== */
	
	private void showListSizeOptions() {
		// Get popup time options from settings and build dialog
		//final List<String> options = Settings.getListSizeOptions();
		final List<Integer> options = Settings.getListSizeOptions();
		
		// Build dialog with SMS popup time options
		final AlertDialog.Builder message = new AlertDialog.Builder(this);
		message.setTitle(getResources().getString(R.string.Settings_listSize));
		
		ListAdapter adapter = buildListAdapterInt(options.toArray(new Integer[] {}));
		//message.setItems(options.toArray(new String[] {}), new DialogInterface.OnClickListener() {
		message.setAdapter(adapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				final Integer selected = options.get(which);
				Settings.set(getApplicationContext(), Settings.LIST_SIZE, selected);
				((TextView) findViewById(R.id.sessionListSize)).setText(String.valueOf(selected) + " "
					+ getString(R.string.items));
			}
		});
		final AlertDialog alert = message.create();
		alert.show();
	}
	
	private void doReturn() {
		switchTo(ListSessionsActivity.class);
		finish();
	}
}
