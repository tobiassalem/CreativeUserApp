package com.tobiassalem.creativeuser.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tobiassalem.creativeuser.R;
import com.tobiassalem.creativeuser.model.Session;
import com.tobiassalem.creativeuser.util.StringUtil;

public class SessionItemAdapter extends ArrayAdapter<Session> {
	
	public SessionItemAdapter(final Context context, final int resource, final Session[] objects) {
		super(context, resource, objects);
	}
	
	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			final LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.listitem, null);
		}
		final TextView title = (TextView) v.findViewById(R.id.listItemTitle);
		final TextView text = (TextView) v.findViewById(R.id.listItemText);
		try {
			final Session item = getItem(position);
			
			if (item != null) {
				title.setText(String.valueOf("User id " + item.getSessionUserId()));
				text.setText(StringUtil.getFormattedTimestamp(getContext(), item.getTimestamp()));
			}
		} catch (final Exception e) {
			Log.e(SessionItemAdapter.class.getSimpleName(), "Exception while reading session item: " + e.getMessage());
			title.setText("");
			text.setText(getContext().getString(R.string.list_no_records));
			text.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		}
		return v;
	}
}
