package org.synote.api.exception



import org.springframework.security.authentication.BadCredentialsException

class SynoteAPIException extends BadCredentialsException 
{
	private int statusCode
	
	/**
     * Constructs a <code>SynoteAPIException</code> with the specified
     * message.
     *
     * @param msg the detail message
     */
    public SynoteAPIException(String msg, int stat) {
		super(msg)
		statusCode = stat
    }

    public SynoteAPIException(String msg, Object extraInformation,int stat) {
		super(msg, extraInformation)
		statusCode = stat
    }

    /**
     * Constructs a <code>SynoteAPIException</code> with the specified
     * message and root cause.
     *
     * @param msg the detail message
     * @param t root cause
     */
    public SynoteAPIException(String msg, Throwable t,int stat) {
		super(msg, t)
		statusCode = stat
    }
	
	public int getStatusCode()
	{
		return this.statusCode	
	}
}
