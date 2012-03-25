package org.synote.player.client;

public class WebVTTData extends AbstractData{
	
	private WebVTTCueData[] cues;

	private String fileHeader;
	
	protected WebVTTData()
	{
		// This constructor is needed for GWT RPC.
	}

	public WebVTTData(String id, UserData owner, boolean edit, boolean delete, WebVTTCueData[] cues, String fileHeader)
	{
		super(id, owner, edit, delete);

		this.cues = cues;
		this.fileHeader = fileHeader;
	}

	public WebVTTCueData[] getCues()
	{
		return cues;
	}

	public WebVTTCueData getCue(int index)
	{
		return cues[index];
	}

	public void setCues(WebVTTCueData[] cues)
	{
		this.cues = cues;
	}

	public void setCue(WebVTTCueData cue, int index)
	{
		cues[index] = cue;
	}
	
	public String getfileHeader()
	{
		return this.fileHeader;
	}
}
