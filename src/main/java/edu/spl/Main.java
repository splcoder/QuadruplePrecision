package edu.spl;

public class Main {

	public static void main( String[] args ){
		if( args.length > 0 ){
			System.out.println( args[0] );
		}
		R r = new R( 3.141512345678910111213 );
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
		System.out.println( "ENDED...................." );
	}
}