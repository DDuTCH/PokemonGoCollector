package com.michielvanderlee.pokemongo.collector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.UniformReservoir;
import com.michielvanderlee.pokemongo.collector.dto.ApiResult;
import com.michielvanderlee.pokemongo.collector.dto.Pokemon;

public class Collector
{
	// ****************************************************************************************
	// Constructors
	// ****************************************************************************************

	// ****************************************************************************************
	// Methods
	// ****************************************************************************************

	public void collect()
	{
		logger.info( "Starting collection" );
		ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool( 2 );
		scheduledExecutor.scheduleWithFixedDelay( new ScanRunner(), 0, 1, TimeUnit.SECONDS );
		scheduledExecutor.scheduleWithFixedDelay( new CacheRefreshRunner(), 0, 5, TimeUnit.SECONDS );

	}

	private void updatePos()
	{
		int maxHorizontal = 14;
		if( verticalPos < getMaxVerticalPos( horizontalPos ) )
		{
			verticalPos++;
		}
		else
		{
			verticalPos = getMinVerticalPos( horizontalPos );
			if( horizontalPos < maxHorizontal )
			{
				horizontalPos++;
			}
			else
			{
				horizontalPos = 0;
			}
		}
	}

	private int getMinVerticalPos( int horizontalPos )
	{
		if( horizontalPos < 7 || horizontalPos == 14 )
		{
			return 0;
		}
		else
		{
			return -1;
		}
	}

	private int getMaxVerticalPos( int horizontalPos )
	{
		int max = 14;
		if( horizontalPos < 6 )
		{
			return max;
		}
		else if( horizontalPos < 8 )
		{
			return max + 5 - horizontalPos;
		}
		else
		{
			return max + 3 - horizontalPos;
		}
	}
	
	private String formatMilliseconds( long millis )
	{
		long tmpSec = millis/1000;
		long minutes = tmpSec / 60;
		long seconds = tmpSec % 60;
		
		return minutes + ":" + seconds;
	}
	
	/**
	 * Generates a list of all coordinates that will be scanned. You can enter
	 * this list on: https://www.mapcustomizer.com/# to see them overlayed on a
	 * map.
	 * 
	 * @return
	 */
	public static String getAllCollectableCoordinates()
	{
		Set<String> coordinates = new HashSet<String>();
		Collector collector = new Collector();

		boolean notExistent = true;
		do
		{
			double latitude = Coordinates.PROSPECT_SMYTHE.getLatitude() + (collector.verticalPos * LATITUDE_OFFSET_VERTICAL) - (collector.horizontalPos * LATITUDE_OFFSET_HORIZONTAL);
			double longitude = Coordinates.PROSPECT_SMYTHE.getLongitude() + (collector.verticalPos * LONGITUDE_OFFSET_VERTICAL) + (collector.horizontalPos * LONGITUDE_OFFSET_HORIZONTAL);

			collector.updatePos();

			notExistent = coordinates.add( latitude + "," + longitude );
		} while( notExistent );

		return String.join( "\n", coordinates );
	}

	// ****************************************************************************************
	// getters and setters.
	// ****************************************************************************************

	// ****************************************************************************************
	// Properties
	// ****************************************************************************************
	private static final String			SCAN_METRICS				= MetricRegistry.name( Collector.class, "scan", "time" );
	private static final double			LATITUDE_OFFSET_VERTICAL	= 0.00167978;
	private static final double			LONGITUDE_OFFSET_VERTICAL	= 0.00140068;
	private static final double			LATITUDE_OFFSET_HORIZONTAL	= 0.00124;
	private static final double			LONGITUDE_OFFSET_HORIZONTAL	= 0.00294;

	private static final Logger			logger						= LoggerFactory.getLogger( Collector.class );
	private static final MetricRegistry	metrics						= new MetricRegistry();
	private static final Scanner		scanner						= Scanner.getInstance();

	static {
		metrics.register( SCAN_METRICS, new Histogram( new UniformReservoir() ) );
	}
	
	// ****************************************************************************************
	// State
	// ****************************************************************************************
	private int							verticalPos					= 0;
	private int							horizontalPos				= 0;
	private long						resetTime					= 0L;



	class ScanRunner implements Runnable
	{
		@Override
		public void run()
		{
			long startTime = System.currentTimeMillis();
			try
			{
				if( verticalPos == 0 && horizontalPos == 0 )
				{
					if( resetTime != 0 )
					{
						logger.debug( "It's been {} minutes since my last reset", formatMilliseconds( System.currentTimeMillis() - resetTime) );
					}
					resetTime = startTime;
				}
				
				double scanLatitude = Coordinates.PROSPECT_SMYTHE.getLatitude() + (verticalPos * LATITUDE_OFFSET_VERTICAL) - (horizontalPos * LATITUDE_OFFSET_HORIZONTAL);
				double scanLongitude = Coordinates.PROSPECT_SMYTHE.getLongitude() + (verticalPos * LONGITUDE_OFFSET_VERTICAL) + (horizontalPos * LONGITUDE_OFFSET_HORIZONTAL);
				
				logger.debug( "Scanning: {},{}", scanLatitude, scanLongitude );
				ApiResult scanResults = scanner.executeScanClean( scanLatitude, scanLongitude, 60, TimeUnit.SECONDS );
				long endTime = System.currentTimeMillis();
				long scanTime= endTime - startTime;
				metrics.histogram( SCAN_METRICS ).update( scanTime );
				logger.debug( "Scan took: {} seconds", scanTime/1000 );
				
				Persistor persistor = Persistor.getInstance();
				persistor.persistPokemon( scanResults.getPokemon() );

				updatePos();
			}
			catch( Exception e )
			{
				logger.error( "Unexpected error occurred while collecting scan data.", e );
			}

		}
	}

	class CacheRefreshRunner implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				ArrayList<Pokemon> pokemonList = scanner.executeCacheRefresh( Coordinates.DUNDONALD_REGENT_BEAVERBROOK.getLatitude(), Coordinates.DUNDONALD_REGENT_BEAVERBROOK.getLongitude() );

				Persistor persistor = Persistor.getInstance();
				persistor.persistPokemon( pokemonList );
			}
			catch( Exception e )
			{
				logger.error( "Unexpected error occurred while collecting cache data.", e );
			}
		}
	}
}
