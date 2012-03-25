package org.synote.player.client;

public class WebVTTCueData {
	private int index; //cue index
	private int start; //start time of the cue
	private int end; //end time of the cue
	private String cueText;
	private String cueSettings;
	
	protected WebVTTCueData()
	{
		// This constructor is needed for GWT RPC.
	}

	public WebVTTCueData(int index,int start, int end, String cueText)
	{
		this.index = index;
		this.start = start;
		this.end = end;
		this.cueText = cueText;
		replaceCharacters();
	}
	
	public WebVTTCueData(int index,int start, int end, String cueText, String cueSettings)
	{
		this.index = index;
		this.start = start;
		this.end = end;
		this.cueText = cueText;
		this.cueSettings = cueSettings;
		replaceCharacters();
	}
	
	public int getIndex()
	{
		return this.index;
	}
	
	public int getStart()
	{
		return this.start;
	}

	public int getEnd()
	{
		return this.end;
	}

	public String getCueText()
	{
		return this.cueText;
	}
	
	public String getCueSettings()
	{
		return this.cueSettings;
	}
	
	/*
	 * Replace & < > with 
	 */
	private String replaceCharacters()
	{
		return "";
	}
}
