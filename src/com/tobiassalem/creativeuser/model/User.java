package com.tobiassalem.creativeuser.model;

/**
 * Entity class for a user. Part of our data model.
 * 
 * @author Tobias
 *
 */
public class User {
	
	private static final String	ANONYMOUS	= "";
	
	private int					userId;
	private String				userName;			// normally an email-address
	private String				userPassword;		// hashed, i.e. not clear text
	private String				phone;
	
	public User(int userId, String userName, String userPassword, String phone) {
		this.userId = userId;
		this.userName = userName;
		this.userPassword = userPassword;
		this.phone = phone;
	}
	
	public User(String userName, String userPassword, String phone) {
		this(-1, userName, userPassword, phone);
	}
	
	public User(String userName, String userPassword) {
		this(userName, userPassword, ANONYMOUS);
	}
	
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getUserPassword() {
		return userPassword;
	}
	
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Override
	public String toString() {
		return "User id: " + userId + ", userName: " + userName + ", userPwd: " + userPassword + ", phone: " + phone;
	}
}
