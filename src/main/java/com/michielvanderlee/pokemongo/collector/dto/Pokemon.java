package com.michielvanderlee.pokemongo.collector.dto;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@SuppressWarnings( "serial" )
@Entity
@NamedQueries( {
		@NamedQuery( name = Pokemon.FIND_ALL, query = "SELECT p FROM Pokemon p" ),
		@NamedQuery( name = Pokemon.FIND_BY_ID, query = "SELECT p FROM Pokemon p WHERE p.id = :id" ),
		@NamedQuery( name = Pokemon.FIND_BY_NAME, query = "SELECT p FROM Pokemon p WHERE p.pokemon_id = :name" ),
		@NamedQuery( name = Pokemon.FIND_BY_ENCOUNTER_ID, query = "SELECT p FROM Pokemon p WHERE p.encounter_id = :encounter_id" ),
		@NamedQuery( name = Pokemon.COUNT_BY_NAME, query = "SELECT COUNT(p) FROM Pokemon p WHERE p.pokemon_id = :name" )		
} )
public class Pokemon implements Serializable
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
	public long getId()
	{
		return id;
	}
	public void setId( long id )
	{
		this.id = id;
	}
	
	public String getPokemon_id()
	{
		return pokemon_id;
	}
	public void setPokemon_id( String pokemon_id )
	{
		this.pokemon_id = pokemon_id;
	}

	public BigInteger getEncounter_id()
	{
		return encounter_id;
	}
	public void setEncounter_id( BigInteger encounter_id )
	{
		this.encounter_id = encounter_id;
	}

	public Long getZone()
	{
		return zone;
	}
	public void setZone( Long zone )
	{
		this.zone = zone;
	}

	public String getSpawn_point_id()
	{
		return spawn_point_id;
	}
	public void setSpawn_point_id( String spawn_point_id )
	{
		this.spawn_point_id = spawn_point_id;
	}

	public Long getExpiration_timestamp()
	{
		return expiration_timestamp;
	}
	public void setExpiration_timestamp( Long expiration_timestamp )
	{
		this.expiration_timestamp = expiration_timestamp;
	}

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

	// @formatter:on
	// ****************************************************************************************
	// Properties
	// ****************************************************************************************
	public static final String FIND_ALL = "Pokemon.findAll"; 
	public static final String FIND_BY_ID = "Pokemon.findByID"; 
	public static final String FIND_BY_NAME = "Pokemon.findByName"; 
	public static final String FIND_BY_ENCOUNTER_ID = "Pokemon.findByEncounterID"; 
	public static final String COUNT_BY_NAME = "Pokemon.countByName"; 
	
	@Id
	@GeneratedValue( strategy = GenerationType.AUTO )
	private long		id;
	private String		pokemon_id;
	@Column( columnDefinition="numeric" )
	private BigInteger	encounter_id;
	private Long		zone;

	private String		spawn_point_id;
	private Long		expiration_timestamp;
	private double		latitude;
	private double		longitude;
}
