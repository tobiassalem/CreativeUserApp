package com.tobiassalem.creativeuser.util;

import android.content.Context;
import android.text.format.DateUtils;

/**
 * Class has String utility methods.
 * 
 * @author Tobias
 *
 */
public class StringUtil {
	
	public static String getFormattedTimestamp(final Context context, final long timestamp) {
		// Convert the timestamp to a readable datestring
		//String dateString = DateUtils.formatDateTime(getApplicationContext(), callDate, DateUtils.FORMAT_24HOUR);
		String dateString = DateUtils.getRelativeDateTimeString(context, timestamp, DateUtils.MINUTE_IN_MILLIS, DateUtils.DAY_IN_MILLIS,
			DateUtils.FORMAT_ABBREV_ALL).toString();
		return dateString;
	}
}
