package org.synote.player.client;

public class PresentationData extends AbstractData
{
	private PresentationSlideData[] slides;

	protected PresentationData()
	{
		// This constructor is needed for GWT RPC.
	}

	public PresentationData(String id, UserData owner, boolean edit, boolean delete, PresentationSlideData[] slides)
	{
		super(id, owner, edit, delete);

		this.slides = slides;
	}

	public PresentationSlideData[] getSlides()
	{
		return slides;
	}

	public PresentationSlideData getSlide(int index)
	{
		return slides[index];
	}
}
