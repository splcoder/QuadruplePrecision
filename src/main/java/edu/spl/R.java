package edu.spl;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Real numbers using quadruple precision
 *
 * https://en.wikipedia.org/wiki/Quadruple-precision_floating-point_format
 * It will use JNI for managing the libquadmath (the GCC quad-precision math library)
 * 		https://gcc.gnu.org/onlinedocs/libquadmath/
 */
public class R extends Number implements Comparable<R>, Serializable {

	static {
		//System.loadLibrary( "quadp" );
		System.load("C:\\Users\\Sergio\\Desktop\\Cursos Certificacion\\Curso Java\\Projects\\QuadruplePrecision\\build\\libs\\quadp\\shared\\quadp.dll");
	}
	private static native long[] initWithDouble( double value );
	private static native long[] initWithLong( long value );
	private static native long[] initWithString( String value );
	private static native String toStr( long low, long high, int precision );
	private static native long toLong( long low, long high );
	private static native double toDouble( long low, long high );

	// Constants -------------------------------------------------------------------------------------------------------
	public static final int BITS		= 128;
	public static final int BYTES		= 16;
	public static final R ZERO			= new R( 0 );
	public static final R ONE			= new R( 1 );
	private static native long[] initConstant( int cte );
	public static final R NAN			= new R( -6, true );
	public static final R INF_P			= new R( -5, true );
	public static final R INF_N			= new R( -4, true );
	public static final R MAX			= new R( -3, true );
	public static final R MIN			= new R( -2, true );
	public static final R PRECISION		= new R( -1, true );

	// Math constants --------------------------------------------------------------------------------------------------
	public static final R M_E			= new R( 0, true );
	public static final R M_1_LN2		= new R( 1, true );
	public static final R M_1_LN10		= new R( 2, true );
	public static final R M_LN2			= new R( 3, true );
	public static final R M_LN10		= new R( 4, true );
	public static final R M_PI			= new R( 5, true );
	public static final R M_PI_2		= new R( 6, true );
	public static final R M_PI_4		= new R( 7, true );
	public static final R M_1_PI		= new R( 8, true );
	public static final R M_2_PI		= new R( 9, true );
	public static final R M_2_SQRTPI	= new R( 10, true );
	public static final R M_SQRT2		= new R( 11, true );
	public static final R M_1_SQRT2		= new R( 12, true );

	// Data ------------------------------------------------------------------------------------------------------------
	private final long low, high;
	private static final Random random = new Random( System.currentTimeMillis() );	// transient
	private R( long low, long high ){
		this.low	= low;
		this.high	= high;
	}
	private R( int cte, boolean init ){
		long[] v	= initConstant( cte );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		this.low	= v[0];
		this.high	= v[1];
	}
	public R(){		// Set this = 0
		this.low	= 0;
		this.high	= 0;
	}
	public R( double value ){
		long[] v	= initWithDouble( value );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		this.low	= v[ 0 ];
		this.high	= v[ 1 ];
	}
	public R( long value ){
		long[] v	= initWithLong( value );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		this.low	= v[ 0 ];
		this.high	= v[ 1 ];
	}
	public R( String value ){
		long[] v	= initWithString( value );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		this.low	= v[ 0 ];
		this.high	= v[ 1 ];
	}

	public static R valueOf( R value ){ return new R( value.low, value.high ); }	// Copy
	public static R valueOf( double value ){ return new R( value ); }
	public static R valueOf( long value ){ return new R( value ); }
	public static R valueOf( String value ){ return new R( value ); }
	public static R random(){ return new R( random.nextLong(), random.nextLong() ); }

	@Override
	public int intValue(){ return (int)toLong( low, high ); }

	@Override
	public long longValue(){ return toLong( low, high ); }

	@Override
	public float floatValue(){ return (float)toDouble( low, high ); }

	@Override
	public double doubleValue(){ return toDouble( low, high ); }

	@Override
	public String toString(){ return toStr( low, high, 0 ).trim(); }	// 0 = 33 digits ('all')
	public String toString( int prec ){ return toStr( low, high, prec ).trim(); }

	@Override
	public boolean equals( Object o ){
		if( ! (o instanceof R) )	return false;
		if( this == o )	return true;
		R r = (R)o;
		return low == r.low && high == r.high;		// Check exact
	}

	private static native boolean areEquals( long lLow, long lHigh, long rLow, long rHigh );
	public static boolean areEquals( R l, R r ){ return areEquals( l.low, l.high, r.low, r.high ); }	// Check by precision

	private static native int compare( long lLow, long lHigh, long rLow, long rHigh );
	@Override
	public int compareTo( R o ){
		if( o == null )	return 1;
		return compare( this.low, this.high, o.low, o.high );
	}

	@Override
	public int hashCode(){
		int hash = 7;
		hash = 31 * hash + Long.hashCode( low );
		hash = 31 * hash + Long.hashCode( high );
		return hash;
	}

	// Save to file / Load from file -----------------------------------------------------------------------------------

	// TODO toBytes, fromBytes <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	public static boolean save( List<R> c, String filename ){
		try {
			FileOutputStream fileOut = new FileOutputStream( filename );
			ObjectOutputStream out = new ObjectOutputStream( fileOut );
			out.writeObject( c );
			out.close();
			fileOut.close();
		} catch ( IOException ioe ){
			ioe.printStackTrace();
			return false;
		}
		return true;
	}
	public static List<R> load( String filename ){
		List<R> c = null;
		try {
			FileInputStream fileIn = new FileInputStream( filename );
			ObjectInputStream in = new ObjectInputStream( fileIn );
			c = (List<R>) in.readObject();
			in.close();
			fileIn.close();
		} catch( IOException ioe ){
			ioe.printStackTrace();
		} catch( ClassNotFoundException cnfe ){
			cnfe.printStackTrace();
		}
		return c == null ? new ArrayList<>() : c;
	}

	// Basic functions -------------------------------------------------------------------------------------------------
	public boolean isNan(){ return this.equals( R.NAN ); }
	public boolean isInf(){ return this.equals( R.INF_P ) || this.equals( R.INF_N ); }
	public boolean isInfP(){ return this.equals( R.INF_P ); }
	public boolean isInfN(){ return this.equals( R.INF_N ); }
	public boolean isFin(){ return ! (this.equals( R.NAN ) || this.equals( R.INF_P ) || this.equals( R.INF_N )); }
	public R neg(){ return this.mul( -1 ); }
	public R sqr(){ return this.mul( this ); }

	// Fast access for basic operations (+ - * /) ----------------------------------------------------------------------
	private static native long[] operation( long lLow, long lHigh, long rLow, long rHigh, int ope );
	private static native long[] operation2( long lLow, long lHigh, double right, int ope );
	public R add( R r ){
		long[] v = operation( this.low, this.high, r.low, r.high, 0 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public R add( double r ){
		long[] v = operation2( this.low, this.high, r, 0 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public R sub( R r ){
		long[] v = operation( this.low, this.high, r.low, r.high, 1 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public R sub( double r ){
		long[] v = operation2( this.low, this.high, r, 1 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public R mul( R r ){
		long[] v = operation( this.low, this.high, r.low, r.high, 2 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public R mul( double r ){
		long[] v = operation2( this.low, this.high, r, 2 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public R div( R r ){
		long[] v = operation( this.low, this.high, r.low, r.high, 3 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public R div( double r ){
		long[] v = operation2( this.low, this.high, r, 3 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}

	// Addition, Subtraction, Multiplication, Division -----------------------------------------------------------------

	private static native long[] operation3( double left, long rLow, long rHigh, int ope );
	private static native long[] operation4( double left, double right, int ope );

	public static R add( R l, R r ){
		long[] v = operation( l.low, l.high, r.low, r.high, 0 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R add( R l, double r ){
		long[] v = operation2( l.low, l.high, r, 0 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R add( double l, R r ){
		long[] v = operation3( l, r.low, r.high, 0 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R add( double l, double r ){
		long[] v = operation4( l, r, 0 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R sub( R l, R r ){
		long[] v = operation( l.low, l.high, r.low, r.high, 1 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R sub( R l, double r ){
		long[] v = operation2( l.low, l.high, r, 1 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R sub( double l, R r ){
		long[] v = operation3( l, r.low, r.high, 1 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R sub( double l, double r ){
		long[] v = operation4( l, r, 1 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R mul( R l, R r ){
		long[] v = operation( l.low, l.high, r.low, r.high, 2 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R mul( R l, double r ){
		long[] v = operation2( l.low, l.high, r, 2 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R mul( double l, R r ){
		long[] v = operation3( l, r.low, r.high, 2 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R mul( double l, double r ){
		long[] v = operation4( l, r, 2 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R div( R l, R r ){
		long[] v = operation( l.low, l.high, r.low, r.high, 3 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R div( R l, double r ){
		long[] v = operation2( l.low, l.high, r, 3 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R div( double l, R r ){
		long[] v = operation3( l, r.low, r.high, 3 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R div( double l, double r ){
		long[] v = operation4( l, r, 3 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}

	// Rounding functions ----------------------------------------------------------------------------------------------
	private static native long[] operation5( long low, long high, int ope );
	private static native long[] operation6( double value, int ope );

	public static R abs( R r ){
		long[] v = operation5( r.low, r.high, 0 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R abs( double r ){
		long[] v = operation6( r, 0 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R floor( R r ){
		long[] v = operation5( r.low, r.high, 1 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R floor( double r ){
		long[] v = operation6( r, 1 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R ceil( R r ){
		long[] v = operation5( r.low, r.high, 2 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R ceil( double r ){
		long[] v = operation6( r, 2 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R trunc( R r ){
		long[] v = operation5( r.low, r.high, 3 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R trunc( double r ){
		long[] v = operation6( r, 3 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R round( R r ){
		long[] v = operation5( r.low, r.high, 4 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R round( double r ){
		long[] v = operation6( r, 4 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	// Power and Root functions ----------------------------------------------------------------------------------------
	public static R hypot( R l, R r ){
		long[] v = operation( l.low, l.high, r.low, r.high, 4 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R hypot( R l, double r ){
		long[] v = operation2( l.low, l.high, r, 4 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R hypot( double l, R r ){
		long[] v = operation3( l, r.low, r.high, 4 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R hypot( double l, double r ){
		long[] v = operation4( l, r, 4 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R sqrt( R r ){
		long[] v = operation5( r.low, r.high, 5 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R sqrt( double r ){
		long[] v = operation6( r, 5 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R cbrt( R r ){
		long[] v = operation5( r.low, r.high, 6 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R cbrt( double r ){
		long[] v = operation6( r, 6 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R pow( R l, R r ){
		long[] v = operation( l.low, l.high, r.low, r.high, 5 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R pow( R l, double r ){
		long[] v = operation2( l.low, l.high, r, 5 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R pow( double l, R r ){
		long[] v = operation3( l, r.low, r.high, 5 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R pow( double l, double r ){
		long[] v = operation4( l, r, 5 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	// Logarithm and Exponential functions -----------------------------------------------------------------------------
	public static R exp( R r ){
		long[] v = operation5( r.low, r.high, 7 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R exp( double r ){
		long[] v = operation6( r, 7 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R exp2( R r ){		// 2^x
		long[] v = operation5( r.low, r.high, 8 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R exp2( double r ){	// 2^x
		long[] v = operation6( r, 8 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	// For small magnitude values of x, expm1(x) may be more accurate than exp(x)-1.
	public static R expm1( R r ){
		long[] v = operation5( r.low, r.high, 9 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R expm1( double r ){
		long[] v = operation6( r, 9 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R expW( R r ){
		long[] v = operation5( r.low, r.high, 10 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R expW( double r ){
		long[] v = operation6( r, 10 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R ln( R r ){
		long[] v = operation5( r.low, r.high, 11 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R ln( double r ){
		long[] v = operation6( r, 11 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	// For small magnitude values of x, ln1p(x) may be more accurate than ln(1+x)
	public static R ln1p( R r ){
		long[] v = operation5( r.low, r.high, 12 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R ln1p( double r ){
		long[] v = operation6( r, 12 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R log2( R r ){
		long[] v = operation5( r.low, r.high, 13 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R log2( double r ){
		long[] v = operation6( r, 13 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R log10( R r ){
		long[] v = operation5( r.low, r.high, 14 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R log10( double r ){
		long[] v = operation6( r, 14 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	// Log in a base = log(l)/log(base)
	public static R logn( R l, R base ){
		long[] v = operation( l.low, l.high, base.low, base.high, 6 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R logn( R l, double base ){
		long[] v = operation2( l.low, l.high, base, 6 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R logn( double l, R base ){
		long[] v = operation3( l, base.low, base.high, 6 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R logn( double l, double base ){
		long[] v = operation4( l, base, 6 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	// Trigonometric functions -----------------------------------------------------------------------------------------
	public static R sin( R r ){
		long[] v = operation5( r.low, r.high, 15 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R sin( double r ){
		long[] v = operation6( r, 15 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R cos( R r ){
		long[] v = operation5( r.low, r.high, 16 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R cos( double r ){
		long[] v = operation6( r, 16 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R tan( R r ){
		long[] v = operation5( r.low, r.high, 17 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R tan( double r ){
		long[] v = operation6( r, 17 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	private static native long[] operation7( long low, long high, int ope );	// Returns an array of 2 R values
	private static native long[] operation8( double value, int ope );			// Returns an array of 2 R values

	public static R[] sinCos( R r ){
		long[] v = operation7( r.low, r.high, 0 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R[]{ new R( v[0], v[1] ), new R( v[2], v[3] ) };
	}
	public static R[] sinCos( double r ){
		long[] v = operation8( r, 0 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R[]{ new R( v[0], v[1] ), new R( v[2], v[3] ) };
	}
	public static R asin( R r ){
		long[] v = operation5( r.low, r.high, 18 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R asin( double r ){
		long[] v = operation6( r, 18 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R acos( R r ){
		long[] v = operation5( r.low, r.high, 19 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R acos( double r ){
		long[] v = operation6( r, 19 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R atan( R r ){
		long[] v = operation5( r.low, r.high, 20 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R atan( double r ){
		long[] v = operation6( r, 20 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R atan2( R l, R r ){
		long[] v = operation( l.low, l.high, r.low, r.high, 7 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R atan2( R l, double r ){
		long[] v = operation2( l.low, l.high, r, 7 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R atan2( double l, R r ){
		long[] v = operation3( l, r.low, r.high, 7 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R atan2( double l, double r ){
		long[] v = operation4( l, r, 7 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	// Hyperbolic functions --------------------------------------------------------------------------------------------
	public static R sinh( R r ){
		long[] v = operation5( r.low, r.high, 21 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R sinh( double r ){
		long[] v = operation6( r, 21 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R cosh( R r ){
		long[] v = operation5( r.low, r.high, 22 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R cosh( double r ){
		long[] v = operation6( r, 22 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R tanh( R r ){
		long[] v = operation5( r.low, r.high, 23 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R tanh( double r ){
		long[] v = operation6( r, 23 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R asinh( R r ){
		long[] v = operation5( r.low, r.high, 24 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R asinh( double r ){
		long[] v = operation6( r, 24 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R acosh( R r ){
		long[] v = operation5( r.low, r.high, 25 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R acosh( double r ){
		long[] v = operation6( r, 25 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R atanh( R r ){
		long[] v = operation5( r.low, r.high, 26 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R atanh( double r ){
		long[] v = operation6( r, 26 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	// Mod, Max and Min functions ---------------------------------------------------------------------------------------------------
	public static R max( R l, R r ){
		long[] v = operation( l.low, l.high, r.low, r.high, 8 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R max( R l, double r ){
		long[] v = operation2( l.low, l.high, r, 8 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R max( double l, R r ){
		long[] v = operation3( l, r.low, r.high, 8 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R max( double l, double r ){
		long[] v = operation4( l, r, 8 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R min( R l, R r ){
		long[] v = operation( l.low, l.high, r.low, r.high, 9 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R min( R l, double r ){
		long[] v = operation2( l.low, l.high, r, 9 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R min( double l, R r ){
		long[] v = operation3( l, r.low, r.high, 9 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R min( double l, double r ){
		long[] v = operation4( l, r, 9 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R fmod( R l, R r ){
		long[] v = operation( l.low, l.high, r.low, r.high, 10 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R fmod( R l, double r ){
		long[] v = operation2( l.low, l.high, r, 10 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R fmod( double l, R r ){
		long[] v = operation3( l, r.low, r.high, 10 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R fmod( double l, double r ){
		long[] v = operation4( l, r, 10 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R rem( R l, R r ){	// Remainder: https://stackoverflow.com/questions/25734144/difference-between-c-functions-remainder-and-fmod
		long[] v = operation( l.low, l.high, r.low, r.high, 11 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R rem( R l, double r ){
		long[] v = operation2( l.low, l.high, r, 11 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R rem( double l, R r ){
		long[] v = operation3( l, r.low, r.high, 11 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R rem( double l, double r ){
		long[] v = operation4( l, r, 11 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R[] modf( R r ){	// decompose the floating-point number: [fractpart, intpart]
		long[] v = operation7( r.low, r.high, 1 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R[]{ new R( v[0], v[1] ), new R( v[2], v[3] ) };
	}
	public static R[] modf( double r ){
		long[] v = operation8( r, 1 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R[]{ new R( v[0], v[1] ), new R( v[2], v[3] ) };
	}
	// Bessel functions ------------------------------------------------------------------------------------------------
	public static R j0( R r ){	// Bessel function of the first kind, first order
		long[] v = operation5( r.low, r.high, 27 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R j0( double r ){
		long[] v = operation6( r, 27 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R j1( R r ){	// Bessel function of the first kind, second order
		long[] v = operation5( r.low, r.high, 28 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R j1( double r ){
		long[] v = operation6( r, 28 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	private static native long[] operation9( int order, long low, long high, int ope );
	private static native long[] operation10( int order, double value, int ope );
	public static R jn( int order, R r ){	// Bessel function of the first kind, n-th order
		long[] v = operation9( order, r.low, r.high, 0 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R jn( int order, double r ){
		long[] v = operation10( order, r, 0 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R y0( R r ){	// Bessel function of the second kind, first order
		long[] v = operation5( r.low, r.high, 29 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R y0( double r ){
		long[] v = operation6( r, 29 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R y1( R r ){	// Bessel function of the second kind, second order
		long[] v = operation5( r.low, r.high, 30 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R y1( double r ){
		long[] v = operation6( r, 30 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R yn( int order, R r ){	// Bessel function of the second kind, n-th order
		long[] v = operation9( order, r.low, r.high, 1 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R yn( int order, double r ){
		long[] v = operation10( order, r, 1 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	// Other functions -------------------------------------------------------------------------------------------------
	public static R erf( R r ){		// Error function
		long[] v = operation5( r.low, r.high, 31 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R erf( double r ){
		long[] v = operation6( r, 31 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R erfc( R r ){		// Complementary Error function
		long[] v = operation5( r.low, r.high, 32 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R erfc( double r ){
		long[] v = operation6( r, 32 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R lgamma( R r ){		// logarithmic gamma function
		long[] v = operation5( r.low, r.high, 33 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R lgamma( double r ){
		long[] v = operation6( r, 33 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R tgamma( R r ){		// true gamma function
		long[] v = operation5( r.low, r.high, 34 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R tgamma( double r ){
		long[] v = operation6( r, 34 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	// Extra functions -------------------------------------------------------------------------------------------------
	public static R sum( R... list ){
		R res = ZERO;
		for( int i = 0; i < list.length; i++ )	res = res.add( list[ i ] );
		return res;
	}
	public static R sum( double... list ){
		R res = ZERO;
		for( int i = 0; i < list.length; i++ )	res = res.add( list[ i ] );
		return res;
	}
	public static R sum( Collection<R> col ){
		R res = ZERO;
		if( ! col.isEmpty() ){
			for( R r : col )	res = res.add( r );
		}
		return res;
	}
	public static R sum( Stream<R> stream ){ return stream.reduce( ZERO, (a, b) -> a.add( b ) ); }
	public static R product( R... list ){
		R res = ONE;
		for( int i = 0; i < list.length; i++ )	res = res.mul( list[ i ] );
		return res;
	}
	public static R product( double... list ){
		R res = ONE;
		for( int i = 0; i < list.length; i++ )	res = res.mul( list[ i ] );
		return res;
	}
	public static R product( Collection<R> col ){
		R res = ONE;
		if( ! col.isEmpty() ){
			for( R r : col )	res = res.mul( r );
		}
		return res;
	}
	public static R product( Stream<R> stream ){ return stream.reduce( ONE, (a, b) -> a.mul( b ) ); }
	public static R max( R... list ){
		if( list.length == 0 )	return R.INF_N;
		R res = list[0];
		for( int i = 1; i < list.length; i++ )	res = R.max( res, list[ i ] );
		return res;
	}
	public static R max( double... list ){
		if( list.length == 0 )	return R.INF_N;
		R res = new R( list[0] );
		for( int i = 1; i < list.length; i++ )	res = R.max( res, list[ i ] );
		return res;
	}
	public static R max( Collection<R> col ){
		if( col.isEmpty() )	return R.INF_N;
		R res = R.INF_N;
		for( R r : col )	res = R.max( res, r );
		return res;
	}
	public static R max( Stream<R> stream ){ return stream.reduce( R.INF_N, R::max ); }
	public static R min( R... list ){
		if( list.length == 0 )	return R.INF_P;
		R res = list[0];
		for( int i = 1; i < list.length; i++ )	res = R.min( res, list[ i ] );
		return res;
	}
	public static R min( double... list ){
		if( list.length == 0 )	return R.INF_P;
		R res = new R( list[0] );
		for( int i = 1; i < list.length; i++ )	res = R.min( res, list[ i ] );
		return res;
	}
	public static R min( Collection<R> col ){
		if( col.isEmpty() )	return R.INF_P;
		R res = R.INF_P;
		for( R r : col )	res = R.min( res, r );
		return res;
	}
	public static R min( Stream<R> stream ){ return stream.reduce( R.INF_P, R::min ); }
	public static R[] minMax( R... list ){
		if( list.length == 0 )	return new R[]{ R.INF_P, R.INF_N };
		R resMin = list[0], resMax = list[0];
		for( int i = 1; i < list.length; i++ ){
			resMin = R.min( resMin, list[ i ] );
			resMax = R.max( resMax, list[ i ] );
		}
		return new R[]{ resMin, resMax };
	}
	public static R[] minMax( double... list ){
		if( list.length == 0 )	return new R[]{ R.INF_P, R.INF_N };
		R first = new R( list[0] );
		R resMin = first, resMax = first;
		for( int i = 1; i < list.length; i++ ){
			resMin = R.min( resMin, list[ i ] );
			resMax = R.max( resMax, list[ i ] );
		}
		return new R[]{ resMin, resMax };
	}
	public static R[] minMax( Collection<R> col ){
		if( col.isEmpty() )	return new R[]{ R.INF_P, R.INF_N };
		R resMin = R.INF_P, resMax = R.INF_N;
		for( R r : col ){
			resMin = R.min( resMin, r );
			resMax = R.max( resMax, r );
		}
		return new R[]{ resMin, resMax };
	}
	public static R[] meanSD( boolean sample, R... list ){
		if( list.length == 0 )	return new R[]{ R.NAN, R.NAN };
		if( list.length == 1 )	return new R[]{ list[0], ZERO };
		R aux, s = ZERO, s2 = ZERO;
		for( int i = 0; i < list.length; i++ ){
			aux = list[ i ];
			s = s.add( aux );
			s2 = s2.add( aux.mul( aux ) );
		}
		R mean = s.div( list.length );
		R variance = s2.div( (sample ? list.length - 1 : list.length) ).sub( mean.sqr() );
		return new R[]{ mean, R.sqrt( variance ) };
	}
	public static R[] meanSD( R... list ){ return meanSD( false, list ); }
	public static R[] meanSD( boolean sample, double... list ){
		if( list.length == 0 )	return new R[]{ R.NAN, R.NAN };
		if( list.length == 1 )	return new R[]{ new R( list[0] ), ZERO };
		R aux, s = ZERO, s2 = ZERO;
		for( int i = 0; i < list.length; i++ ){
			aux = new R( list[ i ] );
			s = s.add( aux );
			s2 = s2.add( aux.mul( aux ) );
		}
		R mean = s.div( list.length );
		R variance = s2.div( (sample ? list.length - 1 : list.length) ).sub( mean.sqr() );
		return new R[]{ mean, R.sqrt( variance ) };
	}
	public static R[] meanSD( double... list ){ return meanSD( false, list ); }
	public static R[] meanSD( boolean sample, Collection<R> col ){
		if( col.isEmpty() )	return new R[]{ R.NAN, R.NAN };
		int size = col.size();
		if( size == 1 ){
			R inside = new R();
			for( R r : col )	inside = r;
			return new R[]{ inside, ZERO };
		}
		R s = ZERO, s2 = ZERO;
		for( R r : col ){
			s = s.add( r );
			s2 = s2.add( r.mul( r ) );
		}
		R mean = s.div( size );
		R variance = s2.div( (sample ? size - 1 : size) ).sub( mean.sqr() );
		return new R[]{ mean, R.sqrt( variance ) };
	}
	public static R[] meanSD( Collection<R> col ){ return meanSD( false, col ); }
}
