package org.synote.player.client;

public class SynmarkData extends AbstractData
{
	private int start;
	private Integer end;
	private String title;
	private String note;
	private String[] tags;
	private String next;

	protected SynmarkData()
	{
		// This constructor is needed for GWT RPC.
	}

	public SynmarkData(int start, Integer end, String title, String note, String[] tags, String next)
	{
		this(null, null, true, true, start, end, title, note, tags, next);
	}

	public SynmarkData
		( String id
		, UserData owner
		, boolean edit
		, boolean delete
		, int start
		, Integer end
		, String title
		, String note
		, String[] tags
		, String next)
	{
		super(id, owner, edit, delete);

		this.start = start;
		this.end = end;
		this.title = title;
		this.note = note;
		this.tags = tags;
		this.next = next;
	}

	public int getStart()
	{
		return start;
	}

	public Integer getEnd()
	{
		return end;
	}

	public String getTitle()
	{
		return title;
	}

	public String getNote()
	{
		return note;
	}

	public String[] getTags()
	{
		return tags;
	}

	public String getTag(int index)
	{
		return tags[index];
	}

	public String getNext()
	{
		return next;
	}

	public boolean isSimilar(SynmarkData synmark, int position)
	{
		if (synmark == null)
			return false;

		if (synmark == this)
			return true;

		return synmark.getStart() == getStart() && (synmark.getEnd() == null || synmark.getEnd() >= position);
	}
}
