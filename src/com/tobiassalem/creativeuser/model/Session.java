package com.tobiassalem.creativeuser.model;


/**
 * Entity class for a session. Part of our data model.
 * 
 * @author Tobias
 *
 */
public class Session {
	
	private int		sessionId;
	private int		sessionUserId;
	private long	timestamp;
	private boolean	authenticated;
	
	/* ====================================== [MEMBER PROPERTIES] ================================================== */
	
	public int getSessionId() {
		return sessionId;
	}
	
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
	
	public int getSessionUserId() {
		return sessionUserId;
	}
	
	public void setSessionUser(int sessionUserId) {
		this.sessionUserId = sessionUserId;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public boolean isAuthenticated() {
		return authenticated;
	}
	
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}
	
	/* ====================================== [DERIVED PROPERTIES] ================================================== */
	
	//	public String getFormattedTimestamp(final Context context) {
	//		// Convert the timestamp to a readable datestring
	//		//String dateString = DateUtils.formatDateTime(getApplicationContext(), callDate, DateUtils.FORMAT_24HOUR);
	//		String dateString = DateUtils.getRelativeDateTimeString(context, timestamp, DateUtils.MINUTE_IN_MILLIS, DateUtils.DAY_IN_MILLIS,
	//			DateUtils.FORMAT_ABBREV_ALL).toString();
	//		return dateString;
	//	}
	
	@Override
	public String toString() {
		return "User session: " + timestamp;
	}
	
	
}
