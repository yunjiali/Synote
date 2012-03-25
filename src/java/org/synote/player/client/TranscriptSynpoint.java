package org.synote.player.client;

public class TranscriptSynpoint
{
	private static final long serialVersionUID = 1L;

	private String id;
	private int startIndex;
	private int endIndex;
	private int startTime;
	private int endTime;

	protected TranscriptSynpoint()
	{
		// This constructor is needed for GWT RPC.
	}

	public TranscriptSynpoint(int startIndex, int endIndex, int startTime, int endTime)
	{
		this(null, startIndex, endIndex, startTime, endTime);
	}

	public TranscriptSynpoint(String id, int startIndex, int endIndex, int startTime, int endTime)
	{
		this.id = id;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public String getId()
	{
		return id;
	}

	public int getStartIndex()
	{
		return startIndex;
	}

	public void setStartIndex(int startIndex)
	{
		this.startIndex = startIndex;
	}

	public int getEndIndex()
	{
		return endIndex;
	}

	public void setEndIndex(int endIndex)
	{
		this.endIndex = endIndex;
	}

	public int getStartTime()
	{
		return startTime;
	}

	public void setStartTime(int startTime)
	{
		this.startTime = startTime;
	}

	public int getEndTime()
	{
		return endTime;
	}

	public void setEndTime(int endTime)
	{
		this.endTime = endTime;
	}
}
