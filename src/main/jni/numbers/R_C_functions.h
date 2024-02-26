#ifndef _R_C_FUNCTIONS_H_
#define _R_C_FUNCTIONS_H_
//---------------------------------------------------------------------------

#include <iostream>
#include <memory>
#include <quadmath.h>
#include <string>
#include <vector>

#include "number_types.h"

using namespace std;

/**
 * R Functions
 *
 * Required: libquadmath.a
 * See https://github.com/gcc-mirror/gcc/blob/master/libquadmath/math/exp2q.c
 */
class RF {
private:
	// For printing
	static const size_t BUF_SIZE = 128;
	static char buf[ BUF_SIZE ];			// Remember that this is NOT thread safe -> use: createBuffersFor( totalThreads )
	// For threading
	//static vector<array<char, RF::BUF_SIZE>>
	static vector<unique_ptr<char[]>> vBuf;
	static size_t ARRAY_SIZE;			// = 0
	static const char EMPTY[ 1 ];		// = ['\0']

	RF(){}
public:
	static constexpr R PRECISION	= 1e-30q;	// Quadruple precision
	static constexpr R MAX			= FLT128_MAX;
	static constexpr R MIN			= FLT128_MIN;
	static const R INF_P;
	static const R INF_N;
	static const R NAN;
	// For printing
	static const size_t MAX_DIGIT_PREC = 33;// Use 33 as max (= FLT128_DIG)

	static R valueOf( const char *str ){
		return strtoflt128( str, NULL );
	}
	static bool areEquals( const R &l, const R &r ){
		R dif = l - r;
		if( dif < 0 )	return (-dif) < RF::PRECISION;
		return dif < RF::PRECISION;
	}
	// Specials --------------------------------------------------------------------------------
	// Save/Load data from 2 int64_t
	static R fromDataInt64( int64_t low, int64_t high ){
		R value;
		((int64_t*)(&value))[0] = low;
		((int64_t*)(&value))[1] = high;
		return value;
	}
	static void fromDataInt64( R &value, int64_t low, int64_t high ){
		((int64_t*)(&value))[0] = low;
		((int64_t*)(&value))[1] = high;
	}
	static void toDataInt64( const R &value, int64_t &low, int64_t &high ){
		low  = ((int64_t*)(&value))[0];
		high = ((int64_t*)(&value))[1];
	}
	// Printing --------------------------------------------------------------------------------
	static void print( const R &r, size_t prec = MAX_DIGIT_PREC ){
		if( prec > MAX_DIGIT_PREC )   prec = MAX_DIGIT_PREC;
		int n = quadmath_snprintf( RF::buf, sizeof RF::buf, "%+-#46.*Qe", prec, r );
		if( (size_t) n < sizeof RF::buf )   printf( "%s\n", RF::buf );
	}
	static const char* printed( const R &r, size_t prec = MAX_DIGIT_PREC ){
		if( prec > MAX_DIGIT_PREC )   prec = MAX_DIGIT_PREC;
		int n = quadmath_snprintf( RF::buf, sizeof RF::buf, "%+-#46.*Qe", prec, r );
		if( (size_t) n < sizeof RF::buf )	return RF::buf;
		return RF::EMPTY;
	}
	static std::string toString( const R &r, size_t prec = MAX_DIGIT_PREC ){
		if( prec > MAX_DIGIT_PREC )   prec = MAX_DIGIT_PREC;
		int n = quadmath_snprintf( RF::buf, sizeof RF::buf, "%+-#46.*Qe", prec, r );
		if( (size_t) n < sizeof RF::buf )	return std::string( RF::buf );
		return std::string( RF::EMPTY );
	}
	// Printing using threading ----------------------------------------------------------------
	static void createBuffersFor( size_t totalThreads ){
		if( totalThreads > RF::ARRAY_SIZE ){
			size_t addThs = totalThreads - RF::ARRAY_SIZE;
			for( size_t i = 0; i < addThs; i++ ){
				RF::vBuf.push_back( make_unique<char[]>( RF::BUF_SIZE ) );
			}
			RF::ARRAY_SIZE = RF::vBuf.size();
		}
	}
	static void print( size_t threadPos, const R &r, size_t prec = MAX_DIGIT_PREC ){
		if( RF::ARRAY_SIZE && threadPos < RF::ARRAY_SIZE ){
			if( prec > MAX_DIGIT_PREC )   prec = MAX_DIGIT_PREC;
			char* buf = RF::vBuf[ threadPos ].get();
			int n = quadmath_snprintf( buf, RF::BUF_SIZE, "%+-#46.*Qe", prec, r );
			if( (size_t) n < RF::BUF_SIZE )   printf( "%s\n", buf );
		}
	}
	static const char* printed( size_t threadPos, const R &r, size_t prec = MAX_DIGIT_PREC ){
		if( RF::ARRAY_SIZE && threadPos < RF::ARRAY_SIZE ){
			if( prec > MAX_DIGIT_PREC )   prec = MAX_DIGIT_PREC;
			char* buf = RF::vBuf[ threadPos ].get();
			int n = quadmath_snprintf( buf, RF::BUF_SIZE, "%+-#46.*Qe", prec, r );
			if( (size_t) n < RF::BUF_SIZE )   return buf;
		}
		return RF::EMPTY;
	}
	static std::string toString( size_t threadPos, const R &r, size_t prec = MAX_DIGIT_PREC ){
		if( RF::ARRAY_SIZE && threadPos < RF::ARRAY_SIZE ){
			if( prec > MAX_DIGIT_PREC )   prec = MAX_DIGIT_PREC;
			char* buf = RF::vBuf[ threadPos ].get();
			int n = quadmath_snprintf( buf, RF::BUF_SIZE, "%+-#46.*Qe", prec, r );
			if( (size_t) n < RF::BUF_SIZE )   return std::string( buf );
		}
		return std::string( RF::EMPTY );
	}
	//------------------------------------------------------------------------------------------
	// Check functions -------------------------------------------------------------------------
	static bool isFin( const R &x ){ return finiteq( x ); }				// check finiteness of value
	static bool isInf( const R &x ){ return isinfq( x ); }				// check for infinity
	static bool isNan( const R &x ){ return isnanq( x ); }				// check for not a number
	static bool isSignaling( const R &x ){ return issignalingq( x ); }	// check for signaling not a number
	static R nan( const char* str = "" ){ return nanq( str ); }			// return quiet NaN
	// Math functions --------------------------------------------------------------------------
	static int signBit( const R &x ){ return signbitq( x ); }			// return sign bit
	static R fma( const R &x, const R &y, const R &z ){ return fmaq( x, y, z ); }	// Computes x*y+z
	static R copySign( const R &x, const R &y ){ return copysignq( x, y ); }
	static R fdim( const R &x, const R &y ){ return fdimq( x, y ); }	// https://www.javatpoint.com/cpp-math-fdim-function
	static R frexp( const R &x, int* ex ){ return frexpq( x, ex ); }	// extract mantissa and exponent
	static R ldexp( const R &x, int ex ){ return ldexpq( x, ex ); }		// load exponent of the value
	static R modf( const R &x, R* ex ){ return modfq( x, ex ); }		// decompose the floating-point number
	// Basic functions -------------------------------------------------------------------------
	static R max( const R &x, const R &y ){ return fmaxq( x, y ); }
	static R min( const R &x, const R &y ){ return fminq( x, y ); }
	static R fmod( const R &x, const R &y ){ return fmodq( x, y ); }		// Remainder
	static R rem( const R &x, const R &y ){ return remainderq( x, y ); }	// Remainder	https://stackoverflow.com/questions/25734144/difference-between-c-functions-remainder-and-fmod
	static R remquo( const R &x, const R &y, int* p ){ return remquoq( x, y, p ); }	// remainder and part of quotient
	// Round functions -------------------------------------------------------------------------
	static R abs( const R &x ){ return fabsq( x ); }
	static R floor( const R &x ){ return floorq( x ); }
	static R ceil( const R &x ){ return ceilq( x ); }
	static R trunc( const R &x ){ return truncq( x ); }							// round to integer, towards zero
	static R rint( const R &x ){ return rintq( x ); }							// round-to-nearest integral value
	static R round( const R &x ){ return roundq( x ); }							// round-to-nearest integral value
	static long int rintL( const R &x ){ return lrintq( x ); }					// round to nearest integer value
	static long int roundL( const R &x ){ return lroundq( x ); }				// round to nearest integer value away from zero
	static long long int rintLL( const R &x ){ return llrintq( x ); }			// round to nearest integer value
	static long long int roundLL( const R &x ){ return llroundq( x ); }			// round to nearest integer value away from zero
	static R nearbyint( const R &x ){ return nearbyintq( x ); }					// round to nearest integer
	static R nextafter( const R &x, const R &y ){ return nextafterq( x, y ); }	// next representable floating-point number
	// Power and Root --------------------------------------------------------------------------
	static R hypot( const R &x, const R &y ){ return hypotq( x, y ); }			// Eucledian distance
	static R sqrt( const R &x ){ return sqrtq( x ); }
	static R cbrt( const R &x ){ return cbrtq( x ); }
	static R pow( const R &x, const R &y ){ return powq( x, y ); }
	// Logarithm and Exponential ---------------------------------------------------------------
	static R exp( const R &x ){ return expq( x ); }			// e^x
	static R exp2( const R &x ){ return exp2q( x ); }		// 2^x
	static R expm1( const R &x ){ return expm1q( x ); }		// exponential minus 1 function
	static R expW( const R &x ){ return x*expq( x ); }		// x*(e^x)
	static R scalbn( const R &x, int e ){ return scalbnq( x, e ); }			// compute exponent using FLT_RADIX
	static R scalbln( const R &x, long int e ){ return scalblnq( x, e ); }	// compute exponent using FLT_RADIX
	static int ilogb( const R &x ){ return ilogbq( x ); }	// get exponent of the value
	static R    logb( const R &x ){ return logbq( x ); }	// get exponent of the value
	static R ln( const R &x ){ return logq( x ); }			// natural logarithm function
	static R ln1p( const R &x ){ return log1pq( x ); }		// compute natural logarithm of the value plus one
	static R log2( const R &x ){ return log2q( x ); }		// base 2 logarithm function
	static R log10( const R &x ){ return log10q( x ); }		// base 10 logarithm function
	static R logn( const R &x, const R &n ){ return logq( x )/logq( n ); }// base n logarithm function
	// Trigonometric ---------------------------------------------------------------------------
	static R sin( const R &x ){ return sinq( x ); }
	static R cos( const R &x ){ return cosq( x ); }
	static R tan( const R &x ){ return tanq( x ); }
	static void sinCos( const R &x, R *s, R *c ){ sincosq( x, s, c ); }	// calculate sine and cosine simultaneously
	static R asin( const R &x ){ return asinq( x ); }
	static R acos( const R &x ){ return acosq( x ); }
	static R atan( const R &x ){ return atanq( x ); }
	static R atan2( const R &y, const R &x ){ return atan2q( y, x ); }
	// Hyperbolic ------------------------------------------------------------------------------
	static R sinh( const R &x ){ return sinhq( x ); }
	static R cosh( const R &x ){ return coshq( x ); }
	static R tanh( const R &x ){ return tanhq( x ); }
	static R asinh( const R &x ){ return asinhq( x ); }
	static R acosh( const R &x ){ return acoshq( x ); }
	static R atanh( const R &x ){ return atanhq( x ); }
	// Bessel functions ------------------------------------------------------------------------
	static R j0( const R &x ){ return j0q( x ); }			// Bessel function of the first kind, first order
	static R j1( const R &x ){ return j1q( x ); }			// Bessel function of the first kind, second order
	static R jn( int n, const R &x ){ return jnq( n, x ); }	// Bessel function of the first kind, n-th order
	static R y0( const R &x ){ return y0q( x ); }			// Bessel function of the second kind, first order
	static R y1( const R &x ){ return y1q( x ); }			// Bessel function of the second kind, second order
	static R yn( int n, const R &x ){ return ynq( n, x ); }	// Bessel function of the second kind, n-th order
	// Other functions -------------------------------------------------------------------------
	static R erf( const R &x ){ return erfq( x ); }			// Error function
	static R erfc( const R &x ){ return erfcq( x ); }		// Complementary Error function
	static R lgamma( const R &x ){ return lgammaq( x ); }	// logarithmic gamma function
	static R tgamma( const R &x ){ return tgammaq( x ); }	// true gamma function
	// Constants -------------------------------------------------------------------------------
	static constexpr R M_E			= M_Eq;			// e
	static constexpr R M_1_LN2		= M_LOG2Eq;		// 1/ln(2)
	static constexpr R M_1_LN10 	= M_LOG10Eq;	// 1/ln(10)
	static constexpr R M_LN2		= M_LN2q;		// ln(2)
	static constexpr R M_LN10		= M_LN10q;		// ln(10)
	static constexpr R M_PI			= M_PIq;		// pi
	static constexpr R M_PI_2		= M_PI_2q;		// pi/2
	static constexpr R M_PI_4		= M_PI_4q;		// pi/4
	static constexpr R M_1_PI		= M_1_PIq;		// 1/pi
	static constexpr R M_2_PI		= M_2_PIq;		// 2/pi
	static constexpr R M_2_SQRTPI	= M_2_SQRTPIq;	// 2/sqrt(pi)
	static constexpr R M_SQRT2		= M_SQRT2q;		// sqrt(2)
	static constexpr R M_1_SQRT2	= M_SQRT1_2q;	// 1/sqrt(2)
};

// For printing:
// cout << "Value: " << RF::M_PI << endl;
std::ostream& operator<< ( std::ostream& out, const R& r );


/**
 * C Functions
 */
class CF {
private:
	CF(){}

public:
	static constexpr C ZERO	= 0;
	static constexpr C ONE	= 1;
	static constexpr C I	= 1i;	// i number

	static C valueOf( const char *strReal ){
		return strtoflt128( strReal, NULL );
	}
	static C valueOf( const char *strReal, const char *strImag ){
		C c;
		__real__ c = strtoflt128( strReal, NULL );
		__imag__ c = strtoflt128( strImag, NULL );
		return c;
	}
	static C valueOf( R real, R imag ){
		C c;
		__real__ c = real;
		__imag__ c = imag;
		return c;
	}
	static bool areEquals( const C &l, const C &r ){
		C dif = l - r;
		R difR = crealq( dif );
		R difI = cimagq( dif );
		if( difR < 0 ){
			if( difI < 0 )	return (-difR) < RF::PRECISION && (-difI) < RF::PRECISION;
			return (-difR) < RF::PRECISION && difI < RF::PRECISION;
		}
		if( difI < 0 )	return difR < RF::PRECISION && (-difI) < RF::PRECISION;
		return difR < RF::PRECISION && difI < RF::PRECISION;
	}
	// Printing --------------------------------------------------------------------------------
	static void print( const C &c, size_t prec = RF::MAX_DIGIT_PREC ){
		const char* buf = RF::printed( crealq( c ), prec );
		printf( "(%s", buf );
		const char* bufI = RF::printed( cimagq( c ) );
		printf( ", %s)\n", bufI );
	}
	/*static const char* printed( const R &r, size_t prec = RF::MAX_PREC ){
		if( prec > RF::MAX_PREC )   prec = RF::MAX_PREC;
		int n = quadmath_snprintf( RF::buf, sizeof RF::buf, "%+-#46.*Qe", prec, r );
		if( (size_t) n < sizeof RF::buf )	return RF::buf;
		return RF::EMPTY;
	}
	static std::string toString( const R &r, size_t prec = RF::MAX_PREC ){
		if( prec > RF::MAX_PREC )   prec = RF::MAX_PREC;
		int n = quadmath_snprintf( RF::buf, sizeof RF::buf, "%+-#46.*Qe", prec, r );
		if( (size_t) n < sizeof RF::buf )	return std::string( RF::buf );
		return std::string( RF::EMPTY );
	}
	// For threading
	static void createBuffersFor( size_t totalThreads ){
		if( totalThreads > RF::ARRAY_SIZE ){
			size_t addThs = totalThreads - RF::ARRAY_SIZE;
			for( size_t i = 0; i < addThs; i++ ){
				RF::vBuf.push_back( make_unique<char[]>( RF::BUF_SIZE ) );
			}
			RF::ARRAY_SIZE = RF::vBuf.size();
		}
	}
	static void print( size_t threadPos, const R &r, size_t prec = RF::MAX_PREC ){
		if( RF::ARRAY_SIZE && threadPos < RF::ARRAY_SIZE ){
			if( prec > RF::MAX_PREC )   prec = RF::MAX_PREC;
			char* buf = RF::vBuf[ threadPos ].get();
			int n = quadmath_snprintf( buf, RF::BUF_SIZE, "%+-#46.*Qe", prec, r );
			if( (size_t) n < RF::BUF_SIZE )   printf( "%s\n", buf );
		}
	}
	static const char* printed( size_t threadPos, const R &r, size_t prec = RF::MAX_PREC ){
		if( RF::ARRAY_SIZE && threadPos < RF::ARRAY_SIZE ){
			if( prec > RF::MAX_PREC )   prec = RF::MAX_PREC;
			char* buf = RF::vBuf[ threadPos ].get();
			int n = quadmath_snprintf( buf, RF::BUF_SIZE, "%+-#46.*Qe", prec, r );
			if( (size_t) n < RF::BUF_SIZE )   return buf;
		}
		return RF::EMPTY;
	}
	static std::string toString( size_t threadPos, const R &r, size_t prec = RF::MAX_PREC ){
		if( RF::ARRAY_SIZE && threadPos < RF::ARRAY_SIZE ){
			if( prec > RF::MAX_PREC )   prec = RF::MAX_PREC;
			char* buf = RF::vBuf[ threadPos ].get();
			int n = quadmath_snprintf( buf, RF::BUF_SIZE, "%+-#46.*Qe", prec, r );
			if( (size_t) n < RF::BUF_SIZE )   return std::string( buf );
		}
		return std::string( RF::EMPTY );
	}*/
	//------------------------------------------------------------------------------------------
	// Complex parts ---------------------------------------------------------------------------
	static R real( const C &x ){ return crealq( x ); }			// real part of complex number
	static R imag( const C &x ){ return cimagq( x ); }			// imaginary part of complex number
	static R abs( const C &x ){ return cabsq( x ); }			// complex absolute value function
	static R arg( const C &x ){ return cargq( x ); }			// calculate the argument
	// Basic functions -------------------------------------------------------------------------
	static C conj( const C &x ){ return conjq( x ); }			// complex conjugate function
	static C proj( const C &x ){ return cprojq( x ); }			// project into Riemann Sphere
	// Power and Root --------------------------------------------------------------------------
	static C sqrt( const C &x ){ return csqrtq( x ); }
	static C pow( const C &x, const C &y ){ return cpowq( x, y ); }
	// Logarithm and Exponential ---------------------------------------------------------------
	static C exp( const C &x ){ return cexpq( x ); }
	static C expi( const R &x ){ return cexpiq( x ); }							// computes the exponential function of “i” times a real value
	static C ln( const C &x ){ return clogq( x ); }								// natural logarithm function
	static C log10( const C &x ){ return clog10q( x ); }						// base 10 logarithm function
	static C logn( const C &x, const C &n ){ return clogq( x )/clogq( n ); }	// base n logarithm function
	// Trigonometric ---------------------------------------------------------------------------
	static C sin( const C &x ){ return csinq( x ); }
	static C cos( const C &x ){ return ccosq( x ); }
	static C tan( const C &x ){ return ctanq( x ); }
	//static void sinCos( const C &x, C *s, C *c ){ sincosq( x, s, c ); }	// calculate sine and cosine simultaneously
	static C asin( const C &x ){ return casinq( x ); }
	static C acos( const C &x ){ return cacosq( x ); }
	static C atan( const C &x ){ return catanq( x ); }
	//static R atan2( const R &y, const R &x ){ return atan2q( y, x ); }
	// Hyperbolic ------------------------------------------------------------------------------
	static C sinh( const C &x ){ return csinhq( x ); }
	static C cosh( const C &x ){ return ccoshq( x ); }
	static C tanh( const C &x ){ return ctanhq( x ); }
	static C asinh( const C &x ){ return casinhq( x ); }
	static C acosh( const C &x ){ return cacoshq( x ); }
	static C atanh( const C &x ){ return catanhq( x ); }

	// TODO with branch selection <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
};

//---------------------------------------------------------------------------
#endif
