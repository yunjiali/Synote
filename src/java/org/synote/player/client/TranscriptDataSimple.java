package org.synote.player.client;
/*
 * Deprecated
 */
public class TranscriptDataSimple extends AbstractData
{
	private TranscriptItemData[] items;

	protected TranscriptDataSimple()
	{
		// This constructor is needed for GWT RPC.
	}

	public TranscriptDataSimple(String id, UserData owner, boolean edit, boolean delete, TranscriptItemData[] items)
	{
		super(id, owner, edit, delete);

		this.items = items;
	}

	public TranscriptItemData[] getItems()
	{
		return items;
	}

	public TranscriptItemData getItem(int index)
	{
		return items[index];
	}

	public void setTranscriptItemData(TranscriptItemData[] items)
	{
		this.items = items;
	}

	public void setTranscriptItemData(TranscriptItemData item, int index)
	{
		items[index] = item;
	}
}
