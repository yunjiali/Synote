package org.synote.player.client;

/*
 * Deprecated
 */
public class TranscriptItemData
{
	private static final long serialVersionUID = 1L;

	private int start;
	private int end;
	private String text;

	protected TranscriptItemData()
	{
		// This constructor is needed for GWT RPC.
	}

	public TranscriptItemData(int start, int end, String text)
	{
		this.start = start;
		this.end = end;
		this.text = text;
	}

	public int getStart()
	{
		return start;
	}

	public int getEnd()
	{
		return end;
	}

	public String getText()
	{
		return text;
	}
}
