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
	if( prec == 0 )	prec = 40;
	R v = RF::fromDataInt64( low, high );
	const char *p = RF::printed( v, prec );
	return env->NewStringUTF( p );
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

JNIEXPORT jlongArray JNICALL
Java_edu_spl_R_operation( JNIEnv *env, jobject obj, jlong lLow, jlong lHigh, jlong rLow, jlong rHigh, jint ope ){
	R v, lv = RF::fromDataInt64( lLow, lHigh ), rv = RF::fromDataInt64( rLow, rHigh );
	switch( ope ){
		case 0: v = lv + rv;	break;
		case 1: v = lv - rv;	break;
		case 2: v = lv * rv;	break;
		case 3: v = lv / rv;	break;
		default: v = RF::NAN;
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

JNIEXPORT jlongArray JNICALL
Java_edu_spl_R_operation2( JNIEnv *env, jobject obj, jlong lLow, jlong lHigh, jdouble right, jint ope ){
	R v, lv = RF::fromDataInt64( lLow, lHigh ), rv = right;
	switch( ope ){
		case 0: v = lv + rv;	break;
		case 1: v = lv - rv;	break;
		case 2: v = lv * rv;	break;
		case 3: v = lv / rv;	break;
		default: v = RF::NAN;
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

JNIEXPORT jlongArray JNICALL
Java_edu_spl_R_operation3( JNIEnv *env, jobject obj, jdouble left, jlong rLow, jlong rHigh, jint ope ){
	R v, lv = left, rv = RF::fromDataInt64( rLow, rHigh );
	switch( ope ){
		case 0: v = lv + rv;	break;
		case 1: v = lv - rv;	break;
		case 2: v = lv * rv;	break;
		case 3: v = lv / rv;	break;
		default: v = RF::NAN;
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

JNIEXPORT jlongArray JNICALL
Java_edu_spl_R_operation4( JNIEnv *env, jobject obj, jdouble left, jdouble right, jint ope ){
	R v, lv = left, rv = right;
	switch( ope ){
		case 0: v = lv + rv;	break;
		case 1: v = lv - rv;	break;
		case 2: v = lv * rv;	break;
		case 3: v = lv / rv;	break;
		default: v = RF::NAN;
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

void exeOperation( R &out, jint ope, const R &value ){
	switch( ope ){
		case 0: out = RF::abs( value );			break;
		case 1: out = RF::floor( value );		break;
		case 2: out = RF::ceil( value );		break;
		case 3: out = RF::trunc( value );		break;
		case 4: out = RF::round( value );		break;
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

#ifdef __cplusplus
}
#endif