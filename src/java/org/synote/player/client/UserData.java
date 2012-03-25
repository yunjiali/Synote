package org.synote.player.client;

public class UserData
{
	private static final long serialVersionUID = 1L;

	private String id;
	private String firstName;
	private String lastName;

	protected UserData()
	{
		// This constructor is needed for GWT RPC.
	}

	public UserData(String id, String firstName, String lastName)
	{
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getId()
	{
		return id;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public String getLastName()
	{
		return lastName;
	}
}
