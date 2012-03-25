package org.synote.player.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/*
 * Deprecated
 */
public class TranscriptData extends AbstractData
{
	private String text;
	private List<TranscriptSynpoint> synpoints;

	protected TranscriptData()
	{
		// This constructor is needed for GWT RPC.
	}

	public TranscriptData(String text)
	{
		this(null, null, true, true, text, null);
	}

	public TranscriptData(String id, UserData owner, boolean edit, boolean delete, String text, List<TranscriptSynpoint> synpoints)
	{
		super(id, owner, edit, delete);

		this.text = text;
		this.synpoints = (synpoints != null) ? synpoints : new ArrayList<TranscriptSynpoint>();
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public List<TranscriptSynpoint> getSynpoints()
	{
		return synpoints;
	}

	public TranscriptSynpoint getSynpoint(String id)
	{
		for (Iterator<TranscriptSynpoint> iterator = synpoints.iterator(); iterator.hasNext(); )
		{
			TranscriptSynpoint synpoint = iterator.next();
			if (synpoint.getId() != null && synpoint.getId().equals(id))
				return synpoint;
		}

		return null;
	}

	public TranscriptSynpoint getSynpoint(int index)
	{
		return synpoints.get(index);
	}

	public boolean addSynpoint(TranscriptSynpoint synpoint)
	{
		if (getSynpoint(synpoint.getId()) != null)
			return false;

		synpoints.add(synpoint);

		return true;
	}

	public boolean removeSynpoint(TranscriptSynpoint synpoint)
	{
		return synpoints.remove(synpoint);
	}
}
