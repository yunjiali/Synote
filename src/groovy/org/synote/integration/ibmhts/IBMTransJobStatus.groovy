package org.synote.integration.ibmhts

import org.synote.integration.ibmhts.exception.IBMTransJobException

enum IBMTransJobStatus 
{
	/*
	 * PROCESSING and DONE can be found in IBM server response
	 * FAILED means for any reason, the job cannot be processed
	 */
	PROCESSING(1), DONE(10), FAILED(20)

	IBMTransJobStatus(int value)
	{
		this.value=value	
	}
	
	private final int value
	
	public int value()
	{
		return value	
	}
	
	static IBMTransJobStatus valueOfString(String value)
	{
		if(value == null)
			return null

		switch (value.toLowerCase())
		{
			//TODO: We need to add submitted and remove uploaded later
			case "in process": return PROCESSING
			case "done": return DONE
			case "failed": return FAILED
			default: throw new IBMTransJobException("Status:"+value+" is not a valid status")
		}
	}
	
	static String valueOfInt(int value)
	{
		if(value == null)
			return null
		switch (value)
		{
			//TODO: We need to add submitted and remove uploaded later
			case PROCESSING.value(): return "in process"
			case DONE.value(): return "done"
			case FAILED.value(): return "failed"
			default: throw new IBMTransJobException("Status:"+value+" is not a valid status")
		}
	}
}
