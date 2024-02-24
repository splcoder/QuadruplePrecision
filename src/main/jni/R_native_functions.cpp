#include "R_native_functions.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL
Java_edu_spl_R_print( JNIEnv *env, jobject obj ){
	printf("Hello From C++ World!\n");
	R a = 12, b = 7;
	R c = a + b;
	char buf[128];
	R r = expq( c );

	int n = quadmath_snprintf( buf, sizeof buf, "%+-#46.*Qe", 30, r );
    if( (size_t) n < sizeof buf )   printf( "QUAD: %s\n", buf );

	printf("-> Ended.\n");
	return;
}

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

#ifdef __cplusplus
}
#endif