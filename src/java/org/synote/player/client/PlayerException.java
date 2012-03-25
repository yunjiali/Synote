package org.synote.player.client;

public class PlayerException extends Exception
{
	//The apistaus code of the error
	int statusCode = 99;

	public PlayerException(int statusCode, String message)
	{
		super(message);
		this.statusCode = statusCode;
	}
	
	public PlayerException(int statusCode,String message, Throwable cause)
	{
		super(message, cause);
		this.statusCode = statusCode;
	}
	
	public PlayerException(int statusCode,Throwable cause)
	{
		super(cause);
		this.statusCode = statusCode;
	}
	
	public PlayerException(String message)
	{
		super(message);
	}
	
	public PlayerException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public PlayerException(Throwable cause)
	{
		super(cause);
	}
	
	public int getStatusCode()
	{
		return this.statusCode;
	}
}
