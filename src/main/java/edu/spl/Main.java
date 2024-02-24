package edu.spl;

public class Main {

	public static void main( String[] args ){
		if( args.length > 0 ){
			System.out.println( args[0] );
		}
		R r = new R( 3.141512345678910111213 );
		r = R.random();
		r.print();
		System.out.println( r );
		System.out.println( "ENDED...................." );
	}
}