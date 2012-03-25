package org.synote.player.client;

public class MultimediaData extends AbstractData
{
	private String title;
	private String url;

	private boolean createTranscript = false;
	private boolean createPresentation = false;
	private boolean createSynmark = false;

	protected MultimediaData()
	{
		// This constructor is needed for GWT RPC.
	}

	public MultimediaData
		( String id
		, UserData owner
		, boolean edit
		, boolean delete
		, String title
		, String url
		, boolean createTranscript
		, boolean createPresentation
		, boolean createSynmark )
	{
		super(id, owner, edit, delete);

		this.title = title;
		this.url = url;

		this.createTranscript = createTranscript;
		this.createPresentation = createPresentation;
		this.createSynmark = createSynmark;
	}

	public String getTitle()
	{
		return title;
	}

	public String getUrl()
	{
		return url;
	}

	public boolean canCreateTranscript()
	{
		return createTranscript;
	}

	public boolean canCreatePresentation()
	{
		return createPresentation;
	}

	public boolean canCreateSynmark()
	{
		return createSynmark;
	}
}
