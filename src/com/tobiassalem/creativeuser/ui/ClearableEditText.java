package com.tobiassalem.creativeuser.ui;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/**
 * Utility class for an EditText with a clear button (typically an X) at the end of the line.
 * The purpose of this clear button is to quickly be able to clear the input field without having to press backspace multiple times.
 * 
 * @author tobias
 *
 */
public class ClearableEditText extends EditText {
	// NOTE: X_SIZE will be to large on LD screens. Much better to use intrinsic size on native drawable - which will be adapted to the device!
	public static final int	X_WIDTH			= 30;
	public static final int	X_HEIGHT		= 30;
	public String			defaultValue	= "";
	final Drawable			imgX			= getResources().getDrawable(android.R.drawable.presence_offline);	// X image
																												
																												
	public ClearableEditText(Context context) {
		super(context);
		
		init();
	}
	
	public ClearableEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		init();
	}
	
	public ClearableEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init();
	}
	
	
	void init() {
		
		// Set bounds of our X button (left, top, right, bottom)
		imgX.setBounds(0, 0, imgX.getIntrinsicWidth(), imgX.getIntrinsicHeight());
		//imgX.setBounds(0, 0, X_WIDTH, X_HEIGHT);
		
		// Set bounds of our X button
		//imgX.setBounds(0, 0, imgX.getIntrinsicWidth(), imgX.getIntrinsicHeight());
		
		// There may be initial text in the field, so we may need to display the button
		manageClearButton();
		
		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				ClearableEditText et = ClearableEditText.this;
				
				// Is there an X showing?
				if (et.getCompoundDrawables()[2] == null) {
					return false;
				}
				// Only do this for up touches
				if (event.getAction() != MotionEvent.ACTION_UP) {
					return false;
				}
				// Is touch on our clear button?
				if (event.getX() > et.getWidth() - et.getPaddingRight() - imgX.getIntrinsicWidth()) {
					et.setText("");
					ClearableEditText.this.removeClearButton();
				}
				return false;
			}
		});
		
		addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				ClearableEditText.this.manageClearButton();
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
		});
	}
	
	void manageClearButton() {
		if (getText().toString().equals("")) {
			removeClearButton();
		} else {
			addClearButton();
		}
	}
	
	void addClearButton() {
		setCompoundDrawables(getCompoundDrawables()[0],
			getCompoundDrawables()[1],
			imgX,
			getCompoundDrawables()[3]);
	}
	
	void removeClearButton() {
		setCompoundDrawables(getCompoundDrawables()[0],
			getCompoundDrawables()[1],
			null,
			getCompoundDrawables()[3]);
	}
	
}
