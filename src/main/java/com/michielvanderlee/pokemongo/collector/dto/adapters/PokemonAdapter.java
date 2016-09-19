package com.michielvanderlee.pokemongo.collector.dto.adapters;

import java.io.IOException;
import java.math.BigInteger;
import java.time.OffsetDateTime;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.michielvanderlee.pokemongo.collector.dto.Pokemon;

public class PokemonAdapter extends TypeAdapter<Pokemon>
{
	//****************************************************************************************
	// Constructors
	//****************************************************************************************

	//****************************************************************************************
	// Methods
	//****************************************************************************************
	@Override
	public void write( JsonWriter out, Pokemon value ) throws IOException
	{
		// TODO Auto-generated method stub
	}

	@Override
	public Pokemon read( JsonReader in ) throws IOException
	{
		final Pokemon pokemon = new Pokemon();
		in.beginObject();
	    while (in.hasNext()) {
	    	switch (in.nextName()) {
	    		case "pokemon_id":
	    			pokemon.setPokemon_id( in.nextString() );
	    			break;
	    		case "encounter_id":
	    			pokemon.setEncounter_id( new BigInteger( in.nextString() ) );
	    			break;
	    		case "spawn_id":
	    			pokemon.setSpawn_point_id( in.nextString() );
	    			break;
	    		case "expireAt":
	    			String expireAt = in.nextString();
	    			OffsetDateTime odt = OffsetDateTime.parse(expireAt);
	    			pokemon.setExpiration_timestamp( odt.toEpochSecond() );
	    			break;
	    		case "lnglat":
	    			in.beginObject();
	    		    while (in.hasNext()) {
	    		    	switch (in.nextName()) {
	    		    		case "type":
	    		    			// read type value but we don't need it.
	    		    			in.nextString();
	    		    			break;
	    		    		case "coordinates":
	    		    			in.beginArray();
	    		    			double lng = in.nextDouble();
	    		    			double lat = in.nextDouble();
	    		    			in.endArray();
	    		    			pokemon.setLongitude( lng );
	    		    			pokemon.setLatitude( lat );
	    		    			break;
	    		    	}
	    		    }
	    		    in.endObject();
	    			break;
	    	}
	    }
	    in.endObject();
		
		return pokemon;
	}
	
	//****************************************************************************************
	// getters and setters.
	//****************************************************************************************

	//****************************************************************************************
	// Properties
	//****************************************************************************************
}
