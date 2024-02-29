#include "R_native_functions.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlongArray JNICALL
Java_edu_spl_R_initWithDouble( JNIEnv *env, jobject obj, jdouble value ){
	R v = value;
	int64_t low, high;
	RF::toDataInt64( v, low, high );

	jlong outCArray[] = { low, high };

	// Convert the C's Native jlong[] to JNI jlongarray, and return
	jlongArray outJNIArray = env->NewLongArray( 2 );			// allocate
	if( NULL == outJNIArray )	return NULL;
	env->SetLongArrayRegion( outJNIArray, 0 , 2, outCArray );	// copy
	return outJNIArray;
}

JNIEXPORT jlongArray JNICALL
Java_edu_spl_R_initWithLong( JNIEnv *env, jobject obj, jlong value ){
	R v = value;
	int64_t low, high;
	RF::toDataInt64( v, low, high );

	jlong outCArray[] = { low, high };

	// Convert the C's Native jlong[] to JNI jlongarray, and return
	jlongArray outJNIArray = env->NewLongArray( 2 );			// allocate
	if( NULL == outJNIArray )	return NULL;
	env->SetLongArrayRegion( outJNIArray, 0 , 2, outCArray );	// copy
	return outJNIArray;
}

JNIEXPORT jlongArray JNICALL
Java_edu_spl_R_initWithString( JNIEnv *env, jobject obj, jstring value ){
	const char* valueCharPointer = env->GetStringUTFChars( value, NULL );
	R v = RF::valueOf( valueCharPointer );
	int64_t low, high;
	RF::toDataInt64( v, low, high );

	jlong outCArray[] = { low, high };

	// Convert the C's Native jlong[] to JNI jlongarray, and return
	jlongArray outJNIArray = env->NewLongArray( 2 );			// allocate
	if( NULL == outJNIArray )	return NULL;
	env->SetLongArrayRegion( outJNIArray, 0 , 2, outCArray );	// copy
	return outJNIArray;
}

JNIEXPORT jstring JNICALL
Java_edu_spl_R_toStr( JNIEnv *env, jobject obj, jlong low, jlong high, jint prec ){
	if( prec <= 0 )	prec = RF::MAX_DIGIT_PREC;
	R v = RF::fromDataInt64( low, high );
	//return env->NewStringUTF( RF::printed( v, prec ) );
	// Thread safe:
	char buf[ RF::getBufSize() ];
	return env->NewStringUTF( RF::printed( v, buf, prec ) );
}

JNIEXPORT jlong JNICALL
Java_edu_spl_R_toLong( JNIEnv *env, jobject obj, jlong low, jlong high ){
	R v = RF::fromDataInt64( low, high );
	return (jlong)v;
}

JNIEXPORT jdouble JNICALL
Java_edu_spl_R_toDouble( JNIEnv *env, jobject obj, jlong low, jlong high ){
	R v = RF::fromDataInt64( low, high );
	return (jdouble)v;
}

JNIEXPORT jlongArray JNICALL
Java_edu_spl_R_initConstant( JNIEnv *env, jobject obj, jint value ){
	R v;
	switch( value ){
		case -6:	v = RF::NAN;		break;
		case -5:	v = RF::INF_P;		break;
		case -4:	v = RF::INF_N;		break;
		case -3:	v = RF::MAX;		break;
		case -2:	v = RF::MIN;		break;
		case -1:	v = RF::PRECISION;	break;
		// Math constants ---------------------------
		case 0:		v = RF::M_E;		break;
		case 1:		v = RF::M_1_LN2;	break;
		case 2:		v = RF::M_1_LN10;	break;
		case 3:		v = RF::M_LN2;		break;
		case 4:		v = RF::M_LN10;		break;
		case 5:		v = RF::M_PI;		break;
		case 6:		v = RF::M_PI_2;		break;
		case 7:		v = RF::M_PI_4;		break;
		case 8:		v = RF::M_1_PI;		break;
		case 9:		v = RF::M_2_PI;		break;
		case 10:	v = RF::M_2_SQRTPI;	break;
		case 11:	v = RF::M_SQRT2;	break;
		case 12:	v = RF::M_1_SQRT2;	break;
		default:	v = RF::NAN;
	}
	int64_t low, high;
	RF::toDataInt64( v, low, high );

	jlong outCArray[] = { low, high };

	// Convert the C's Native jlong[] to JNI jlongarray, and return
	jlongArray outJNIArray = env->NewLongArray( 2 );			// allocate
	if( NULL == outJNIArray )	return NULL;
	env->SetLongArrayRegion( outJNIArray, 0 , 2, outCArray );	// copy
	return outJNIArray;
}

JNIEXPORT jboolean JNICALL
Java_edu_spl_R_areEquals( JNIEnv *env, jobject obj, jlong lLow, jlong lHigh, jlong rLow, jlong rHigh ){
	R lv = RF::fromDataInt64( lLow, lHigh ), rv = RF::fromDataInt64( rLow, rHigh );
	return (jboolean)RF::areEquals( lv, rv );
}

JNIEXPORT jint JNICALL
Java_edu_spl_R_compare( JNIEnv *env, jobject obj, jlong lLow, jlong lHigh, jlong rLow, jlong rHigh ){
	R lv = RF::fromDataInt64( lLow, lHigh ), rv = RF::fromDataInt64( rLow, rHigh );
	R dif = lv - rv;
	if( dif < 0 )	return -1;
	if( dif > 0 )	return 1;
	return 0;
}

// TODO new >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
JNIEXPORT jbyteArray JNICALL
Java_edu_spl_R_toBytes( JNIEnv *env, jobject obj, jlong low, jlong high ){
	R v = RF::fromDataInt64( low, high );

	jbyte outCArray[ sizeof( R ) ];
    RF::toBytes( v, outCArray );

	// Convert the C's Native jlong[] to JNI jlongarray, and return
	jbyteArray outJNIArray = env->NewByteArray( sizeof( R ) );			// allocate
	if( NULL == outJNIArray )	return NULL;
	env->SetByteArrayRegion( outJNIArray, 0 , sizeof( R ), outCArray );	// copy
	return outJNIArray;
}

JNIEXPORT jlongArray JNICALL
Java_edu_spl_R_fromBytesNat( JNIEnv *env, jobject obj, jbyteArray src ){
	jbyte *pSrc = env->GetByteArrayElements( src, 0 );
	R v = RF::fromBytes( pSrc );
	env->ReleaseByteArrayElements( src, pSrc, 0 );

	int64_t low, high;
	RF::toDataInt64( v, low, high );

	jlong outCArray[] = { low, high };

	// Convert the C's Native jlong[] to JNI jlongarray, and return
	jlongArray outJNIArray = env->NewLongArray( 2 );			// allocate
	if( NULL == outJNIArray )	return NULL;
	env->SetLongArrayRegion( outJNIArray, 0 , 2, outCArray );	// copy
	return outJNIArray;
}
// TODO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

void exeOperation2Args( R &out, jint ope, const R &lValue, const R &rValue ){
	switch( ope ){
		case 0: out = lValue + rValue;					break;
		case 1: out = lValue - rValue;					break;
		case 2: out = lValue * rValue;					break;
		case 3: out = lValue / rValue;					break;
		case 4: out = RF::hypot( lValue, rValue );		break;
		case 5: out = RF::pow( lValue, rValue );		break;
		case 6: out = RF::logn( lValue, rValue );		break;
		case 7: out = RF::atan2( lValue, rValue );		break;
		case 8: out = RF::max( lValue, rValue );		break;
		case 9: out = RF::min( lValue, rValue );		break;
		case 10: out = RF::fmod( lValue, rValue );		break;
		case 11: out = RF::rem( lValue, rValue );		break;
		default: out = RF::NAN;
	}
}

JNIEXPORT jlongArray JNICALL
Java_edu_spl_R_operation( JNIEnv *env, jobject obj, jlong lLow, jlong lHigh, jlong rLow, jlong rHigh, jint ope ){
	R v, lv = RF::fromDataInt64( lLow, lHigh ), rv = RF::fromDataInt64( rLow, rHigh );
	exeOperation2Args( v, ope, lv, rv );

	int64_t low, high;
	RF::toDataInt64( v, low, high );

	jlong outCArray[] = { low, high };

	// Convert the C's Native jlong[] to JNI jlongarray, and return
	jlongArray outJNIArray = env->NewLongArray( 2 );			// allocate
	if( NULL == outJNIArray )	return NULL;
	env->SetLongArrayRegion( outJNIArray, 0 , 2, outCArray );	// copy
	return outJNIArray;
}

JNIEXPORT jlongArray JNICALL
Java_edu_spl_R_operation2( JNIEnv *env, jobject obj, jlong lLow, jlong lHigh, jdouble right, jint ope ){
	R v, lv = RF::fromDataInt64( lLow, lHigh ), rv = right;
	exeOperation2Args( v, ope, lv, rv );

	int64_t low, high;
	RF::toDataInt64( v, low, high );

	jlong outCArray[] = { low, high };

	// Convert the C's Native jlong[] to JNI jlongarray, and return
	jlongArray outJNIArray = env->NewLongArray( 2 );			// allocate
	if( NULL == outJNIArray )	return NULL;
	env->SetLongArrayRegion( outJNIArray, 0 , 2, outCArray );	// copy
	return outJNIArray;
}

JNIEXPORT jlongArray JNICALL
Java_edu_spl_R_operation3( JNIEnv *env, jobject obj, jdouble left, jlong rLow, jlong rHigh, jint ope ){
	R v, lv = left, rv = RF::fromDataInt64( rLow, rHigh );
	exeOperation2Args( v, ope, lv, rv );

	int64_t low, high;
	RF::toDataInt64( v, low, high );

	jlong outCArray[] = { low, high };

	// Convert the C's Native jlong[] to JNI jlongarray, and return
	jlongArray outJNIArray = env->NewLongArray( 2 );			// allocate
	if( NULL == outJNIArray )	return NULL;
	env->SetLongArrayRegion( outJNIArray, 0 , 2, outCArray );	// copy
	return outJNIArray;
}

JNIEXPORT jlongArray JNICALL
Java_edu_spl_R_operation4( JNIEnv *env, jobject obj, jdouble left, jdouble right, jint ope ){
	R v, lv = left, rv = right;
	exeOperation2Args( v, ope, lv, rv );

	int64_t low, high;
	RF::toDataInt64( v, low, high );

	jlong outCArray[] = { low, high };

	// Convert the C's Native jlong[] to JNI jlongarray, and return
	jlongArray outJNIArray = env->NewLongArray( 2 );			// allocate
	if( NULL == outJNIArray )	return NULL;
	env->SetLongArrayRegion( outJNIArray, 0 , 2, outCArray );	// copy
	return outJNIArray;
}

void exeOperation( R &out, jint ope, const R &value ){
	switch( ope ){
		case 0: out = RF::abs( value );			break;
		case 1: out = RF::floor( value );		break;
		case 2: out = RF::ceil( value );		break;
		case 3: out = RF::trunc( value );		break;
		case 4: out = RF::round( value );		break;
		case 5: out = RF::sqrt( value );		break;
		case 6: out = RF::cbrt( value );		break;
		case 7: out = RF::exp( value );			break;
		case 8: out = RF::exp2( value );		break;
		case 9: out = RF::expm1( value );		break;
		case 10: out = RF::expW( value );		break;
		case 11: out = RF::ln( value );			break;
		case 12: out = RF::ln1p( value );		break;
		case 13: out = RF::log2( value );		break;
		case 14: out = RF::log10( value );		break;
		case 15: out = RF::sin( value );		break;
		case 16: out = RF::cos( value );		break;
		case 17: out = RF::tan( value );		break;
		case 18: out = RF::asin( value );		break;
		case 19: out = RF::acos( value );		break;
		case 20: out = RF::atan( value );		break;
		case 21: out = RF::sinh( value );		break;
		case 22: out = RF::cosh( value );		break;
		case 23: out = RF::tanh( value );		break;
		case 24: out = RF::asinh( value );		break;
		case 25: out = RF::acosh( value );		break;
		case 26: out = RF::atanh( value );		break;
		case 27: out = RF::j0( value );			break;
		case 28: out = RF::j1( value );			break;
		case 29: out = RF::y0( value );			break;
		case 30: out = RF::y1( value );			break;
		case 31: out = RF::erf( value );		break;
		case 32: out = RF::erfc( value );		break;
		case 33: out = RF::lgamma( value );		break;
		case 34: out = RF::tgamma( value );		break;
		default: out = RF::NAN;
	}
}

JNIEXPORT jlongArray JNICALL
Java_edu_spl_R_operation5( JNIEnv *env, jobject obj, jlong vLow, jlong vHigh, jint ope ){
	R v, value = RF::fromDataInt64( vLow, vHigh );
	exeOperation( v, ope, value );

	int64_t low, high;
	RF::toDataInt64( v, low, high );

	jlong outCArray[] = { low, high };

	// Convert the C's Native jlong[] to JNI jlongarray, and return
	jlongArray outJNIArray = env->NewLongArray( 2 );			// allocate
	if( NULL == outJNIArray )	return NULL;
	env->SetLongArrayRegion( outJNIArray, 0 , 2, outCArray );	// copy
	return outJNIArray;
}

JNIEXPORT jlongArray JNICALL
Java_edu_spl_R_operation6( JNIEnv *env, jobject obj, jdouble dv, jint ope ){
	R v, value = dv;
	exeOperation( v, ope, value );

	int64_t low, high;
	RF::toDataInt64( v, low, high );

	jlong outCArray[] = { low, high };

	// Convert the C's Native jlong[] to JNI jlongarray, and return
	jlongArray outJNIArray = env->NewLongArray( 2 );			// allocate
	if( NULL == outJNIArray )	return NULL;
	env->SetLongArrayRegion( outJNIArray, 0 , 2, outCArray );	// copy
	return outJNIArray;
}

void exeOperation1Arg2Out( R &out1, R &out2, jint ope, const R &value ){
	switch( ope ){
		case 0: RF::sinCos( value, &out1, &out2 );		break;
		case 1: out1 = RF::modf( value, &out2 );		break;
        default: out1 = RF::NAN; out2 = RF::NAN;
	}
}

// Returns an array of 2 R values
JNIEXPORT jlongArray JNICALL
Java_edu_spl_R_operation7( JNIEnv *env, jobject obj, jlong vLow, jlong vHigh, jint ope ){
	R out1, out2, value = RF::fromDataInt64( vLow, vHigh );
	exeOperation1Arg2Out( out1, out2, ope, value );

	int64_t lLow, lHigh, rLow, rHigh;
	RF::toDataInt64( out1, lLow, lHigh );
	RF::toDataInt64( out2, rLow, rHigh );

	jlong outCArray[] = { lLow, lHigh, rLow, rHigh };

	// Convert the C's Native jlong[] to JNI jlongarray, and return
	jlongArray outJNIArray = env->NewLongArray( 4 );			// allocate
	if( NULL == outJNIArray )	return NULL;
	env->SetLongArrayRegion( outJNIArray, 0 , 4, outCArray );	// copy
	return outJNIArray;
}

JNIEXPORT jlongArray JNICALL
Java_edu_spl_R_operation8( JNIEnv *env, jobject obj, jdouble dv, jint ope ){
	R out1, out2, value = dv;
	exeOperation1Arg2Out( out1, out2, ope, value );

	int64_t lLow, lHigh, rLow, rHigh;
	RF::toDataInt64( out1, lLow, lHigh );
	RF::toDataInt64( out2, rLow, rHigh );

	jlong outCArray[] = { lLow, lHigh, rLow, rHigh };

	// Convert the C's Native jlong[] to JNI jlongarray, and return
	jlongArray outJNIArray = env->NewLongArray( 4 );			// allocate
	if( NULL == outJNIArray )	return NULL;
	env->SetLongArrayRegion( outJNIArray, 0 , 4, outCArray );	// copy
	return outJNIArray;
}

void exeOperation2ArgsWithInt( R &out, jint ope, const R &value, jint order ){
	switch( ope ){
		case 0: out = RF::jn( order, value );			break;
		case 1: out = RF::yn( order, value );			break;
		default: out = RF::NAN;
	}
}

JNIEXPORT jlongArray JNICALL
Java_edu_spl_R_operation9( JNIEnv *env, jobject obj, jint order, jlong vLow, jlong vHigh, jint ope ){
	R v, value = RF::fromDataInt64( vLow, vHigh );
	exeOperation2ArgsWithInt( v, ope, value, order );

	int64_t low, high;
	RF::toDataInt64( v, low, high );

	jlong outCArray[] = { low, high };

	// Convert the C's Native jlong[] to JNI jlongarray, and return
	jlongArray outJNIArray = env->NewLongArray( 2 );			// allocate
	if( NULL == outJNIArray )	return NULL;
	env->SetLongArrayRegion( outJNIArray, 0 , 2, outCArray );	// copy
	return outJNIArray;
}

JNIEXPORT jlongArray JNICALL
Java_edu_spl_R_operation10( JNIEnv *env, jobject obj, jint order, jdouble dv, jint ope ){
	R v, value = dv;
	exeOperation2ArgsWithInt( v, ope, value, order );

	int64_t low, high;
	RF::toDataInt64( v, low, high );

	jlong outCArray[] = { low, high };

	// Convert the C's Native jlong[] to JNI jlongarray, and return
	jlongArray outJNIArray = env->NewLongArray( 2 );			// allocate
	if( NULL == outJNIArray )	return NULL;
	env->SetLongArrayRegion( outJNIArray, 0 , 2, outCArray );	// copy
	return outJNIArray;
}

#ifdef __cplusplus
}
#endif