package org.trsfrm.security;

import java.util.Map;

import lombok.Data;

@Data
public class AuthenticationResponse {

	private String token;
	private String username;
	public AuthenticationResponse(Map<String,String> map){
		this.token = map.get("token");
		this.username = map.get("username");
	}
}
