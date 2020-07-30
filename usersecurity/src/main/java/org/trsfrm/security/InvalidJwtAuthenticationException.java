package org.trsfrm.security;

public class InvalidJwtAuthenticationException extends Exception {
	public InvalidJwtAuthenticationException(String message){
		super(message);
	}
	
}
