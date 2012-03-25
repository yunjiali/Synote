package org.synote.exception

class SynoteException extends RuntimeException 
{
	public SynoteException(String message)
	{
		super(message)
	}
	
	public SynoteException()
	{
		super()	
	}
	
	public SynoteException(String message, Throwable cause)
	{
		super(message, cause)	
	}
	
	public SynoteException(Throwable cause)
	{
		super(cause)	
	}
	
}
