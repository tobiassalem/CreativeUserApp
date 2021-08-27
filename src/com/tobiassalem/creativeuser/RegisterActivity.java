package com.tobiassalem.creativeuser;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tobiassalem.creativeuser.model.User;

public class RegisterActivity extends AppActivity {
	
	/**
	 * Keep track of the register task to ensure we can cancel it if requested.
	 */
	private UserRegisterTask	mAuthTask	= null;
	
	// Values for user data at the time of the register.
	private String				mEmail;
	private String				mPassword;
	private String				mPasswordConfirm;
	private String				mPhone;
	
	// UI references.
	private EditText			mEmailView;
	private EditText			mPasswordView;
	private EditText			mPasswordConfirmView;
	private EditText			mPhoneView;
	private View				mRegisterFormView;
	private View				mRegisterStatusView;
	private TextView			mRegisterStatusMessageView;
	
	/* ======================================== [LIFECYCLE METHODS] ======================================================= */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Build UI
		setContentView(R.layout.activity_register);
		
		// Set up the register form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);
		
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordConfirmView = (EditText) findViewById(R.id.passwordConfirm);
		mPhoneView = (EditText) findViewById(R.id.phone);
		
		//		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
		//			@Override
		//			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
		//				if (id == R.id.login || id == EditorInfo.IME_NULL) {
		//					doLogin();
		//					return true;
		//				}
		//				return false;
		//			}
		//		});
		
		mRegisterFormView = findViewById(R.id.register_form);
		mRegisterStatusView = findViewById(R.id.register_status);
		mRegisterStatusMessageView = (TextView) findViewById(R.id.register_status_message);
		
		findViewById(R.id.registerButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				doRegister();
			}
		});
		
		findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				doCancel();
			}
		});
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_register, menu);
		return true;
	}
	
	/* ======================================== [LOGIC METHODS] ======================================================= */
	
	private void doRegister() {
		// TODO Store user in database, forward to list session view
		
		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);
		
		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mPasswordConfirm = mPasswordConfirmView.getText().toString();
		mPhone = mPhoneView.getText().toString();
		
		boolean cancel = false;
		View focusView = null;
		
		// TODO: Check for unique  user
		
		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < MIN_PASSWORD_LENGTH) {
			mPasswordView.setError(getString(R.string.error_password_invalid));
			focusView = mPasswordView;
			cancel = true;
		} else if (!mPassword.equals(mPasswordConfirm)) {
			mPasswordConfirmView.setError(getString(R.string.error_password_confirm));
			focusView = mPasswordConfirmView;
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
		} else if (mDatabase.userExists(mEmail)) {
			mEmailView.setError(getString(R.string.error_email_exists));
			focusView = mEmailView;
			cancel = true;
		}
		
		if (cancel) {
			// There was an error; don't attempt to register and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			debug("Validation ok, email: " + mEmail + ", pwd: " + mPassword + ", pwdConfirm: " + mPasswordConfirm + ", phone: " + mPhone);
			
			User user = new User(mEmail, mPassword, mPhone);
			mRegisterStatusMessageView.setText(R.string.progress_registering);
			showProgress(true);
			mAuthTask = new UserRegisterTask();
			mAuthTask.execute(user);
		}
		
		
	}
	
	private void doCancel() {
		switchTo(LoginActivity.class);
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
			
			mRegisterStatusView.setVisibility(View.VISIBLE);
			mRegisterStatusView.animate()
				.setDuration(shortAnimTime)
				.alpha(show ? 1 : 0)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						mRegisterStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
					}
				});
			
			mRegisterFormView.setVisibility(View.VISIBLE);
			mRegisterFormView.animate()
				.setDuration(shortAnimTime)
				.alpha(show ? 0 : 1)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
					}
				});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mRegisterStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserRegisterTask extends AsyncTask<User, Void, Boolean> {
		@Override
		protected Boolean doInBackground(User... params) {
			// Confirm we have the User parameter
			if (params == null || params.length < 1) {
				return false;
			}
			
			// Register user in the database, return true if ok
			User user = params[0];
			int userId = mDatabase.addUser(user);
			if (userId > 0) {
				user.setUserId(userId);
				return true;
			} else {
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);
			
			if (success) {
				debug("Register and login successful for " + mEmail);
				postLogin(mEmail);
				finish();
			} else {
				debug("Register failed for " + mEmail);
				showPopup("Register failed - please review your credentials");
				mPasswordView.setError(getString(R.string.error_password_incorrect));
				mPasswordView.requestFocus();
			}
		}
		
		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
