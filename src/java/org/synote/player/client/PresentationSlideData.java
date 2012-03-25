package org.synote.player.client;

public class PresentationSlideData
{
	private static final long serialVersionUID = 1L;

	private String id;
	private Integer start;
	private Integer end;
	private String url;

	protected PresentationSlideData()
	{
		// This constructor is needed for GWT RPC.
	}

	public PresentationSlideData(String id, Integer start, String url)
	{
		this.id = id;
		this.start = start;
		this.end = null;
		this.url = url;
	}
	
	public PresentationSlideData(String id, Integer start,Integer end, String url)
	{
		this.id = id;
		this.start = start;
		this.end = end;
		this.url = url;
	}

	public String getId()
	{
		return id;
	}

	public Integer getStart()
	{
		return start;
	}
	
	public Integer getEnd()
	{
		return end;
	}

	public String getUrl()
	{
		return url;
	}
}
