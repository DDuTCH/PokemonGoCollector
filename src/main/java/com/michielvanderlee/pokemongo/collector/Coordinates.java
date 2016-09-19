package com.michielvanderlee.pokemongo.collector;

public enum Coordinates
{	
	PROSPECT_SMYTHE( 45.9425392, -66.6696788 ),
	DUNDONALD_REGENT_BEAVERBROOK( 45.952880252803446, -66.64581298828126 );
	
	//****************************************************************************************
	// Constructors
	//****************************************************************************************
	private Coordinates( double latitude, double longitude )
	{
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	//****************************************************************************************
	// Methods
	//****************************************************************************************

	//****************************************************************************************
	// getters and setters.
	//****************************************************************************************
	public double getLatitude()
	{
		return latitude;
	}
	public void setLatitude( double latitude )
	{
		this.latitude = latitude;
	}
	
	public double getLongitude()
	{
		return longitude;
	}
	public void setLongitude( double longitude )
	{
		this.longitude = longitude;
	}
	
	//****************************************************************************************
	// Properties
	//****************************************************************************************
	private double		latitude;
	private double		longitude;
}
