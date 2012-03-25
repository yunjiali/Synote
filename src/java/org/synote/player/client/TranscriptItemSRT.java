package org.synote.player.client;
/*
 * Not used (may be used in other occasions
 */
public class TranscriptItemSRT {
	private static final long serialVersionUID = 1L;

	private int index;
	private int start;
	private int end;
	private String text;
	/*
	 * In fact, speaking is not included in srt, so this attribute is not used.
	 */
	private String speaker; 

	protected TranscriptItemSRT()
	{
		// This constructor is needed for GWT RPC.
	}

	public TranscriptItemSRT(int index,int start, int end, String text)
	{
		this.index = index;
		this.start = start;
		this.end = end;
		this.text = text;
		this.speaker = null;
	}
	public TranscriptItemSRT(int index,int start, int end, String text, String speaker)
	{
		this.index = index;
		this.start = start;
		this.end = end;
		this.text = text;
		this.speaker = speaker;
	}
	
	public int getIndex()
	{
		return index;
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
	
	public String getSpeaker()
	{
		return speaker;
	}
}

