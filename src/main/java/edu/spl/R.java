package edu.spl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

	public static R abs( R r ){
		long[] v = operation5( r.low, r.high, 0 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R floor( R r ){
		long[] v = operation5( r.low, r.high, 1 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R ceil( R r ){
		long[] v = operation5( r.low, r.high, 2 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R trunc( R r ){
		long[] v = operation5( r.low, r.high, 3 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	public static R round( R r ){
		long[] v = operation5( r.low, r.high, 4 );
		if( v == null )		throw new RuntimeException( "R -> the native array could not be allocated" );
		return new R( v[0], v[1] );
	}
	// Power and Root functions ----------------------------------------------------------------------------------------
	// Logarithm and Exponential functions -----------------------------------------------------------------------------
	// Trigonometric functions -----------------------------------------------------------------------------------------
	// Hyperbolic functions --------------------------------------------------------------------------------------------
	// Bessel functions ------------------------------------------------------------------------------------------------
	// Other functions -------------------------------------------------------------------------------------------------
	public static R sum( List<R> list ){
		R res = new R();	// 0
		int length = list.size();
		for( int i = 0; i < length; i++ )	res = res.add( list.get( i ) );
		return res;
	}
	public static R product( List<R> list ){
		R res = new R( 1 );
		int length = list.size();
		for( int i = 0; i < length; i++ )	res = res.mul( list.get( i ) );
		return res;
	}
	// TODO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	// TODO ???  +2.306323558737156172766198381637374e+34	<<< tan( PI/2 ) << NOT INF !!!
}
