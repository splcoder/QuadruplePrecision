package edu.spl;

import java.io.Serializable;
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
	public native void print();		// TODO delete <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	private static native long[] initWithDouble( double value );
	private static native long[] initWithLong( long value );
	private static native long[] initWithString( String value );
	private static native String toStr( long low, long high, int precision );
	private static native long toLong( long low, long high );
	private static native double toDouble( long low, long high );

	// Constants -------------------------------------------------------------------------------------------------------
	private static native long[] initConstant( int cte );
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

	@Override
	public boolean equals( Object o ){
		if( this == o ) return true;
		if( o == null || getClass() != o.getClass() ) return false;
		R r = (R)o;
		return low == r.low && high == r.high;	// TODO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	}

	public static boolean areEquals( R l, R r ){ return l.low == r.low && l.high == r.high; }	// Check exact

	@Override
	public int compareTo( R o ){
		return 0;	// TODO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	}

	@Override
	public int hashCode(){
		int hash = 7;
		hash = 31 * hash + Long.hashCode( low );
		hash = 31 * hash + Long.hashCode( high );
		return hash;
	}

	// Basic functions -------------------------------------------------------------------------------------------------
	// TODO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
}
