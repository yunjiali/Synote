package org.synote.user

import org.springframework.security.*

class SynoteAPIAuthentication implements Authentication {
	
	GrantedAuthority[] authorities
	Object credentials
	Object details
	Object principal
	boolean authenticated
	
	public String getName()
	{
		return principal as String	
	}
}
