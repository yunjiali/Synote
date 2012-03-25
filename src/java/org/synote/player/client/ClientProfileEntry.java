package org.synote.player.client;

public class ClientProfileEntry
{
	private static final long serialVersionUID = 1L;

	private static int globalVersion = 1;

	private int version;

	private String resourceId;
	private String name;
	private String value;

	protected ClientProfileEntry()
	{
		// This constructor is needed for GWT RPC.
	}

	public ClientProfileEntry(String resourceId, String name, String value)
	{
		this.version = globalVersion++;

		this.resourceId = resourceId;
		this.name = name;
		this.value = value;
	}

	public int getVersion()
	{
		return version;
	}

	public boolean isStored()
	{
		return version == 0;
	}

	public void setStored()
	{
		version = 0;
	}

	public String getResourceId()
	{
		return resourceId;
	}

	public String getName()
	{
		return name;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.version = globalVersion++;
		this.value = value;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		if (resourceId != null)
		{
			builder.append(resourceId);
			builder.append(", ");
		}

		builder.append(name);
		builder.append(" = ");
		if (value != null)
		{
			builder.append(">");
			builder.append(value);
			builder.append("<");
		}
		else
			builder.append("NULL");

		if (version != 0)
		{
			builder.append(", ");
			builder.append(version);
		}

		return builder.toString();
	}

	public static String toString(String resourceId, String name, String value)
	{
		StringBuilder builder = new StringBuilder();

		if (resourceId != null)
		{
			builder.append(resourceId);
			builder.append(", ");
		}

		builder.append(name);
		builder.append(" = ");
		if (value != null)
		{
			builder.append(">");
			builder.append(value);
			builder.append("<");
		}
		else
			builder.append("NULL");

		return builder.toString();
	}
}
