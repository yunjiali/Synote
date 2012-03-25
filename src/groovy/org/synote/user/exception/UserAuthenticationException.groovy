package org.synote.user.exception

import org.synote.exception.SynoteException

class UserAuthenticationException extends SynoteException {
	
	public UserAuthenticationException(String message)
	{
		super(message)
	}
	
	public UserAuthenticationException(String message, Throwable cause)
	{
		super(message, cause)
	}
}