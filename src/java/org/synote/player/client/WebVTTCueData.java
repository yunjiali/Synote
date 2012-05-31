package org.synote.player.client;

public class WebVTTCueData {
	private String id;
	private int index; //cue index
	private int start; //start time of the cue
	private int end; //end time of the cue
	private String cueText;
	private String cueSettings;
	private String thumbnail;
	
	protected WebVTTCueData()
	{
		
	}

	public WebVTTCueData(String id, int index,int start, int end, String cueText,String thumbnail)
	{
		this.id = id;
		this.index = index;
		this.start = start;
		this.end = end;
		this.cueText = cueText;
		this.thumbnail = thumbnail;
		replaceCharacters();
	}
	
	public WebVTTCueData(String id,int index,int start, int end, String cueText, String cueSettings, String thumbnail)
	{
		this.id = id;
		this.index = index;
		this.start = start;
		this.end = end;
		this.cueText = cueText;
		this.cueSettings = cueSettings;
		this.thumbnail = thumbnail;
		replaceCharacters();
	}
	
	public String getId()
	{
		return id;
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
	
	public String getThumbnail()
	{
		return this.thumbnail;
	}
	
	/*
	 * Replace & < > with 
	 */
	private String replaceCharacters()
	{
		return "";
	}
}
