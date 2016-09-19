package com.michielvanderlee.pokemongo.collector;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.michielvanderlee.pokemongo.collector.dto.Pokemon;

public class Persistor
{
	// ****************************************************************************************
	// Constructors
	// ****************************************************************************************
	private Persistor()
	{

	}

	// ****************************************************************************************
	// Methods
	// ****************************************************************************************
	public synchronized void persistPokemon( List<Pokemon> pokemons )
	{
		try
		{
			em.getTransaction().begin();
			for( Pokemon pokemon : pokemons )
			{
				if( !isPokemonIgnored( pokemon ) && !isPokemonPersisted( pokemon ) )
				{
					em.persist( pokemon );
				}
			}
			em.getTransaction().commit();
		}
		catch( Exception e )
		{
			logger.error( "Unexpected error occurred while persisting Pokemon", e );
			em.getTransaction().rollback();
		}
	}

	public boolean isPokemonPersisted( Pokemon pokemon )
	{
		TypedQuery<Pokemon> query = em.createNamedQuery( Pokemon.FIND_BY_ENCOUNTER_ID, Pokemon.class );
		query.setParameter( "encounter_id", pokemon.getEncounter_id() );

		return !query.getResultList().isEmpty();
	}

	public boolean isPokemonIgnored( Pokemon pokemon )
	{
		return ignorePokemons.contains( pokemon.getPokemon_id() );
	}

	private void init()
	{
		ignorePokemons = new HashSet<String>();
		try
		{
			ignorePokemons.addAll( FileUtils.readLines( new File( IGNORE_POKEMON_FILE ), Charset.defaultCharset() ) );

		}
		catch( IOException e )
		{
			logger.error( "Unexpected error occurred while loading " + IGNORE_POKEMON_FILE, e );
		}

		try
		{
			factory = Persistence.createEntityManagerFactory( "openjpa_postgresql" );
			em = factory.createEntityManager();
		}
		catch( Exception e )
		{
			logger.error( "Unexpected error occurred while connecting to psql", e );
			close();
		}
	}

	public void close()
	{
		if( em != null )
		{
			em.close();
		}
		if( factory != null )
		{
			factory.close();
		}
	}

	public static Persistor getInstance()
	{
		if( instance == null )
		{
			instance = new Persistor();
			instance.init();
		}

		return instance;
	}

	// ****************************************************************************************
	// getters and setters.
	// ****************************************************************************************

	// ****************************************************************************************
	// Properties
	// ****************************************************************************************
	private static final String		IGNORE_POKEMON_FILE	= "ignore_pokemon.csv";

	private static final Logger		logger				= LoggerFactory.getLogger( Persistor.class );
	private static Persistor		instance			= null;

	private Set<String>				ignorePokemons		= null;
	private EntityManagerFactory	factory				= null;
	private EntityManager			em					= null;
}
