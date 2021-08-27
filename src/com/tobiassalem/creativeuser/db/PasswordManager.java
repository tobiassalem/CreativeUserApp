package com.tobiassalem.creativeuser.db;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.tobiassalem.creativeuser.model.User;

/**
 * Class manages our passwords and related logic.
 * 
 * @author Tobias
 */
public class PasswordManager {
	
	private static final String		DIGEST_ALGORITHM	= "SHA-256";
	private static final boolean	USE_HASH			= false;
	
	public byte[] hash(String password) throws NoSuchAlgorithmException {
		MessageDigest sha256 = MessageDigest.getInstance(DIGEST_ALGORITHM);
		byte[] passBytes = password.getBytes();
		byte[] passHash = sha256.digest(passBytes);
		return passHash;
	}
	
	public boolean isAuthenticated(User user, String password) {
		if (!USE_HASH) {
			return user.getUserPassword().equals(password);
		}
		
		byte[] hashedPassword;
		try {
			hashedPassword = hash(password);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		}
		return user.getUserPassword().equals(hashedPassword);
	}
}
