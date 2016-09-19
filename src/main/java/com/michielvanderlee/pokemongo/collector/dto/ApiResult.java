package com.michielvanderlee.pokemongo.collector.dto;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings( "serial" )
public class ApiResult implements Serializable
{
	// ****************************************************************************************
	// Constructors
	// ****************************************************************************************

	// ****************************************************************************************
	// Methods
	// ****************************************************************************************

	// ****************************************************************************************
	// getters and setters.
	// ****************************************************************************************
	// @formatter:off
	public String getError()
	{
		return error;
	}
	public void setError( String error )
	{
		this.error = error;
	}

	public List<Pokemon> getPokemon()
	{
		return pokemon;
	}
	public void setPokemon( List<Pokemon> pokemon )
	{
		this.pokemon = pokemon;
	}

	// @formatter:on
	// ****************************************************************************************
	// Properties
	// ****************************************************************************************
	private String			error;
	@SerializedName( "result" )
	private List<Pokemon>	pokemon;
}
