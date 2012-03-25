package org.synote.linkeddata.exception

import org.synote.exception.SynoteException;

class RDFGenerationException extends SynoteException {
	public RDFGenerationException(String message)
	{
		super(message)
	}
	
	public RDFGenerationException()
	{
		super()
	}
	
	public RDFGenerationException(String message, Throwable cause)
	{
		super(message, cause)
	}
	
	public RDFGenerationException(Throwable cause)
	{
		super(cause)
	}
}
