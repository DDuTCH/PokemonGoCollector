package com.michielvanderlee.pokemongo.collector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.TimeLimitExceededException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.logging.LoggingFeature.Verbosity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.michielvanderlee.pokemongo.collector.dto.ApiResult;
import com.michielvanderlee.pokemongo.collector.dto.Pokemon;
import com.michielvanderlee.pokemongo.collector.dto.adapters.PokemonAdapter;

public class Scanner
{
	// ****************************************************************************************
	// Constructors
	// ****************************************************************************************
	private Scanner()
	{
		System.setProperty( "sun.net.http.allowRestrictedHeaders", "true" );
		Logger logger = Logger.getLogger( getClass().getName() );
		Feature feature = new LoggingFeature( logger, Level.INFO, Verbosity.PAYLOAD_TEXT, null );
		client = ClientBuilder.newBuilder()
				//.register( feature )
				.build();
	}

	// ****************************************************************************************
	// Methods
	// ****************************************************************************************
	/**
	 * Executes a scan on the provided coordinates. Will return the first result
	 * it receives.
	 * 
	 * @param latitude
	 * @param longtitude
	 * @return
	 * @throws Exception
	 *             When the response code was not 2##
	 */
	public ApiResult executeScan( double latitude, double longtitude ) throws Exception
	{
		WebTarget resource = client
				.target( API_WEB_URL )
				.queryParam( "key", "allow-all" )
				.queryParam( "ts", 0 )
				.queryParam( "lat", latitude )
				.queryParam( "lng", longtitude );
		Builder request = resource.request();

		Response response = request
				.header( "Origin", "https://fastpokemap.se" )
				.buildGet()
				.invoke();

		if( response.getStatusInfo().getFamily() == Family.SUCCESSFUL )
		{
			String strResult = response.readEntity( String.class );

			ApiResult result = new Gson().fromJson( strResult, ApiResult.class );
			return result;
		}
		else
		{
			throw new Exception( "ERROR! " + response.getStatus() );// + ": " + response.readEntity( String.class ) );
		}
	}

	/**
	 * Executes a scan on the provided coordinates. If the result json has
	 * {'error': 'overload'} it will keep trying until the timeout is reached.
	 * 
	 * @param latitude
	 * @param longtitude
	 * @param timeout
	 *            Timeout in ms
	 * @return
	 * @throws Exception
	 *             When the response code was not 2##, when the error was not
	 *             'overload', or when the timeout was reached.
	 */
	public ApiResult executeScanClean( double latitude, double longtitude, long timeout ) throws Exception
	{
		long startTime = System.currentTimeMillis();
		while( (System.currentTimeMillis() - startTime) < timeout )
		{
			ApiResult result = executeScan( latitude, longtitude );
			if( StringUtils.isBlank( result.getError() ) )
			{
				return result;
			}
			else if( !result.getError().equals( "overload" ) )
			{
				throw new Exception( "Unexpected error: " + result.getError() );
			}
			else
			{
				Thread.sleep( 1000 );
			}
		}

		throw new TimeLimitExceededException( "No scan returned in " + timeout + " milliseconds." );
	}

	/**
	 * Executes a scan on the provided coordinates. If the result json has
	 * {'error': 'overload'} it will keep trying until the timeout is reached.
	 * 
	 * @param latitude
	 * @param longtitude
	 * @param timeout
	 * @param timeUnit
	 *            TimeUnit of the timeout variable.
	 * @return
	 * @throws Exception
	 *             When the response code was not 2##, when the error was not
	 *             'overload', or when the timeout was reached.
	 */
	public ApiResult executeScanClean( double latitude, double longtitude, long timeout, TimeUnit timeUnit ) throws Exception
	{
		return executeScanClean( latitude, longtitude, timeUnit.toMillis( timeout ) );
	}

	public ArrayList<Pokemon> executeCacheRefresh( double latitude, double longtitude ) throws Exception
	{
		WebTarget resource = client
				.target( CACHE_WEB_URL )
				.queryParam( "key", "allow-all" )
				.queryParam( "ts", 0 )
				.queryParam( "compute", getPublicIP() )
				.queryParam( "lat", latitude )
				.queryParam( "lng", longtitude );
		Builder request = resource.request();

		Response response = request
				.header( "Origin", "https://fastpokemap.se" )
				.buildGet()
				.invoke();

		if( response.getStatusInfo().getFamily() == Family.SUCCESSFUL )
		{
			String strResult = response.readEntity( String.class );

			Gson gson = new GsonBuilder()
					.registerTypeAdapter( Pokemon.class, new PokemonAdapter() )
					.create();
			ArrayList<Pokemon> result = gson.fromJson( strResult, new TypeToken<ArrayList<Pokemon>>() {
			}.getType() );

			return result;
		}
		else
		{
			throw new Exception( "ERROR! " + response.getStatus() );// + ": " + response.readEntity( String.class ) );
		}
	}

	public static Scanner getInstance()
	{
		if( instance == null )
		{
			instance = new Scanner();
		}

		return instance;
	}

	private static String getPublicIP() throws IOException
	{
		if( StringUtils.isBlank( publicIP ) )
		{
			URL url_name = new URL( IP_URL );
			BufferedReader sc = new BufferedReader( new InputStreamReader( url_name.openStream() ) );
			publicIP = sc.readLine().trim();
		}

		return publicIP;
	}

	// ****************************************************************************************
	// getters and setters.
	// ****************************************************************************************

	// ****************************************************************************************
	// Properties
	// ****************************************************************************************
	private static final String	IP_URL			= "http://bot.whatismyipaddress.com";
	private static final String	API_WEB_URL		= "https://api.fastpokemap.se/";
	private static final String	CACHE_WEB_URL	= "https://cache.fastpokemap.se/";

	private static Scanner		instance;
	private static String		publicIP;

	private Client				client;
}
