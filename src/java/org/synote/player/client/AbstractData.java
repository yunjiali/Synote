package org.synote.player.client;

public class AbstractData
{
	private static final long serialVersionUID = 1L;

	private String id;
	private UserData owner;
	private boolean edit;
	private boolean delete;
	private String thumbnail;

	protected AbstractData()
	{
		// This constructor is needed for GWT RPC.
	}

	public AbstractData(String id, UserData owner, boolean edit, boolean delete)
	{
		this.id = id;
		this.owner = owner;
		this.edit = edit;
		this.delete = delete;
		this.thumbnail = null;
	}

	public AbstractData(String id, UserData owner, boolean edit, boolean delete,String thumbnail)
	{
		this.id = id;
		this.owner = owner;
		this.edit = edit;
		this.delete = delete;
		this.thumbnail = thumbnail;
	}
	
	public String getId()
	{
		return id;
	}

	public UserData getOwner()
	{
		return owner;
	}

	public String getThumbnail()
	{
		return thumbnail;
	}
	
	public boolean canEdit()
	{
		return edit;
	}

	public boolean canDelete()
	{
		return delete;
	}
}
