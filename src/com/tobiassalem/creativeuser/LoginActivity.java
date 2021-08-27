package com.tobiassalem.creativeuser;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.tobiassalem.creativeuser.db.PasswordManager;
import com.tobiassalem.creativeuser.model.User;
import com.tobiassalem.creativeuser.util.Settings;

/**
 * Activity displays a login view to the user, offering registration as well.
 * @author Tobias
 *
 */
public class LoginActivity extends AppActivity {
	
	public static enum LoginResult {
		OK, INVALID_PASSWORD, INVALID_USER
	};
	
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	@SuppressWarnings("unused")
	private static final String[]	DUMMY_CREDENTIALS	= new String[] {
														"foo@example.com:hello",
														"bar@example.com:world",
														"frodo@shire.org:hobbit42",
														"sam@shire.org:gardener11",
														"gandalf@shire.org:wizard66"
														};
	
	
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask			mAuthTask			= null;
	
	// Values for email and password at the time of the login attempt.
	private String					mEmail;
	private String					mPassword;
	
	// UI references.
	private AutoCompleteTextView	mEmailView;
	private EditText				mPasswordView;
	private View					mLoginFormView;
	private View					mLoginStatusView;
	private TextView				mLoginStatusMessageView;
	
	/* ======================================== [LIFECYCLE METHODS] ======================================================= */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Build UI
		setContentView(R.layout.activity_login);
		
		// Set up the login form.
		// Read last user name if setting is activated
		if (Settings.is(this, Settings.USERNAME_MEMORY)) {
			mEmail = Settings.getLastUsername(this);
		}
		//mEmailView = (EditText) findViewById(R.id.email);
		mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
		// NOTE: build the adapter in an AsyncTask to avoid loading times when getAutoCompleteNames are read from database.
		// Set the adapter in the callback method back on the UI thread.
		AsyncTask<AutoCompleteTextView, Void, ArrayAdapter<String>> async = new AsyncTask<AutoCompleteTextView, Void, ArrayAdapter<String>>() {
			
			@Override
			protected ArrayAdapter<String> doInBackground(final AutoCompleteTextView... params) {
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(LoginActivity.this, R.layout.listitem_simple, getAutoCompleteNames());
				return adapter;
			}
			
			@Override
			protected void onPostExecute(ArrayAdapter<String> adapter) {
				mEmailView.setAdapter(adapter);
			}
		};
		async.execute(mEmailView);
		
		mEmailView.setText(mEmail);
		
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					doLogin();
					return true;
				}
				return false;
			}
		});
		
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		
		findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				doLogin();
			}
		});
		
		findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				doRegister();
			}
		});
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}
	
	/* ======================================== [LOGIC METHODS] ======================================================= */
	
	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	private void doLogin() {
		if (mAuthTask != null) {
			return;
		}
		
		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);
		
		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		
		boolean cancel = false;
		View focusView = null;
		
		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < MIN_PASSWORD_LENGTH) {
			mPasswordView.setError(getString(R.string.error_password_invalid));
			focusView = mPasswordView;
			cancel = true;
		}
		
		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_email_invalid));
			focusView = mEmailView;
			cancel = true;
		}
		
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute(mEmail, mPassword);
		}
	}
	
	private void doRegister() {
		// Send user name to the register view so we do not have to repeat it
		Intent i = new Intent(this, RegisterActivity.class);
		i.putExtra(EXTRA_EMAIL, mEmailView.getText().toString());
		switchTo(i);
		finish();
	}
	
	private String[] getAutoCompleteNames() {
		String[] autoCompleteNames = { "frodo@shire.org", "sam@shire.org", "gandalf@shire.org", "saruman@isegard.org", "sauron@mordor.org" };
		
		// TODO: implement reading of user names from database, e.g. once a day
		// Optimize loading times by caching autocomplete data in Settings
		
		return autoCompleteNames;
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
			
			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate()
				.setDuration(shortAnimTime)
				.alpha(show ? 1 : 0)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
					}
				});
			
			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate()
				.setDuration(shortAnimTime)
				.alpha(show ? 0 : 1)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
					}
				});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	/**
	 * Represents an asynchronous login task used to authenticate 
	 * the user against the database.
	 */
	public class UserLoginTask extends AsyncTask<String, Void, LoginResult> {
		@Override
		protected LoginResult doInBackground(String... params) {
			// NOTE: return result code dependent on login result state.
			String userName = params[0];
			String userPwd = params[1];
			User user = mDatabase.getUser(userName);
			debug("Looking up " + userName + ". User from database: " + user);
			
			if (user == null || user.getUserId() < 0) {
				debug("Could not find user with userName " + userName);
				debugList("getAllUsers when user not found - ", mDatabase.getAllUsers());
				return LoginResult.INVALID_USER;
			} else {
				debug("Found user with userName: " + userName + ", userId: " + user.getUserId());
				PasswordManager pwdManager = new PasswordManager();
				boolean isAuth = pwdManager.isAuthenticated(user, userPwd);
				debug("User " + userName + " authenticated: " + isAuth);
				return isAuth ? LoginResult.OK : LoginResult.INVALID_PASSWORD;
			}
		}
		
		@Override
		protected void onPostExecute(final LoginResult loginResult) {
			mAuthTask = null;
			showProgress(false);
			
			switch (loginResult) {
				case OK:
					debug("Login successful for " + mEmail);
					postLogin(mEmail);
					break;
				case INVALID_PASSWORD:
					debug("Login has invalid password for " + mEmail);
					mPasswordView.setError(getString(R.string.error_password_incorrect));
					mPasswordView.requestFocus();
					break;
				case INVALID_USER:
					debug("Login has invalid user " + mEmail);
					mEmailView.setError(getString(R.string.error_user_invalid));
					mEmailView.requestFocus();
					break;
			}
		}
		
		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
