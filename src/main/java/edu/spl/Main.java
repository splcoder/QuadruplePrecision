package edu.spl;

import java.util.stream.Stream;

public class Main {

	public static void main( String[] args ){
		if( args.length > 0 ){
			System.out.println( args[0] );
		}
		R r = new R( 3.141512345678910111213 );
		System.out.println( r );
		r = R.random();
		r = R.M_PI;
		R t = r.add( 0.15 );
		double d = r.doubleValue();
		long lo = r.longValue();
		System.out.println( "r: " + r + ", t: " + t );
		System.out.println( "Double: " + d + " = " + Math.PI );
		System.out.println( "Long  : " + lo );
		System.out.println( "=? " + R.areEquals( r, t ) + ", compare: " + r.compareTo( t ) );
		System.out.println( R.round( r ).toString( 5 ) );
		System.out.println( R.round( 3.141516 ).toString( 5 ) );
		R[] out = R.sinCos( 1 );
		System.out.println( "sinCos(1): " + out[0] + ", " + out[1] );

		Stream<R> stream = Stream.of( R.valueOf( 3 ), R.valueOf( 5 ), R.valueOf( 6 ), R.valueOf( -1 ), R.valueOf( 20 ), R.valueOf( 11 ) );
		//System.out.println( R.sum( stream ) );
		System.out.println( "Stream product:   " + R.product( stream ) );
		System.out.println( "Stream product 2: " + R.product( R.valueOf( 3 ), R.valueOf( 5 ), R.valueOf( 6 ) ) );
		System.out.println( "Stream product 3: " + R.product( 3, 5, 6 ) );

		// stream has already been operated upon or closed
		Stream<R> stream2 = Stream.of( R.valueOf( 3 ), R.valueOf( 5 ), R.valueOf( 6 ), R.valueOf( -1 ), R.valueOf( 20 ), R.valueOf( 11 ) );
		R[] min_max = R.minMax( stream2 );
		System.out.println( "Stream minMax: " + min_max[0] + ", " + min_max[1] );

		R[] mean_sd = R.meanSD( 1, 2, 3, 4, 5 );
		System.out.println( "Stream mean: " + mean_sd[0] + ", sd: " + mean_sd[1] );

		Stream<R> stream3 = Stream.of( R.valueOf( 1 ), R.valueOf( 2 ), R.valueOf( 3 ), R.valueOf( 4 ), R.valueOf( 5 ) );
		R[] mean_sd2 = R.meanSD( stream3 );
		System.out.println( "Stream mean: " + mean_sd2[0] + ", sd: " + mean_sd2[1] );

		out = R.modf( R.M_PI );
		System.out.println( "Parts of PI: " + out[0] + ", " + out[1] );

		System.out.println( "7.5 % 2.1 = " + R.fmod( 7.5, 2.1 ) + " = " + (7.5%2.1) );

		System.out.println( "max: " + R.max( R.valueOf( 3 ), R.valueOf( 5 ), R.valueOf( 6 ) ) );
		System.out.println( "min: " + R.min( R.valueOf( 3 ), R.valueOf( 5 ), R.valueOf( 6 ) ) );

		double[] lst = new double[]{ 3, 5, 7, 6 };
		R[] result = R.meanSD( lst );
		System.out.println( "Mean-SD: " + result[0] + ", " + result[1] );

		System.out.println( "ENDED......................................................................................" );

		Stream<Double> doubleStream = Stream.of(1.5, 2.3, 4.6, 0.8, 3.2 );
		Double[] maxMinValues = doubleStream.reduce( new Double[]{ Double.MIN_VALUE, Double.MAX_VALUE }
				, (acc, val) -> new Double[]{ Math.max( acc[0], val ), Math.min( acc[1], val ) }
				, (result1, result2) -> new Double[]{ Math.max( result1[0], result2[0] ), Math.min( result1[1], result2[1] ) }
		);
		System.out.println("Valor máximo: " + maxMinValues[0] + ", Valor mínimo: " + maxMinValues[1] );

		System.out.println( "ENDED......................................................................................" );

		R rand = R.random();
		System.out.println( "RANDOM: " + rand );

		System.out.println( "ENDED......................................................................................" );

		byte[] byte_arr = R.toBytes( R.M_PI );
		for( int i = 0; i < byte_arr.length; i++ ) System.out.print( byte_arr[ i ] + "," );
		System.out.println();
		R from = R.fromBytes( byte_arr );
		System.out.println( "FROM: " + from );
		System.out.println( "ENDED......................................................................................" );

		R toSum = R.M_PI;
		long end, start = System.currentTimeMillis();
		for( int i = 0; i < 1_000_000; i++ ){
			toSum = toSum.addPRU( R.M_E );
		}
		end = System.currentTimeMillis();
		System.out.println( "addPRU -> toSum: " + toSum + ", time: " + (end - start) );

		toSum = R.M_PI;
		start = System.currentTimeMillis();
		for( int i = 0; i < 1_000_000; i++ ){
			toSum = toSum.add( R.M_E );
		}
		end = System.currentTimeMillis();
		System.out.println( "add    -> toSum: " + toSum + ", time: " + (end - start) );

		System.out.println( "ENDED......................................................................................" );
	}
}