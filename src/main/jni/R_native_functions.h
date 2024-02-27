#ifndef _R_NATIVE_FUNCTIONS_H_
#define _R_NATIVE_FUNCTIONS_H_
//------------------------------------

#include <jni.h>
#include <stdio.h>

#include "./numbers/R_C_functions.h"

#ifdef __cplusplus
extern "C" {
#endif

// https://docs.tibco.com/pub/enterprise-runtime-for-R/6.1.1/doc/html/Language_Reference/terrJava/dotJavaMethod.html
// https://oldmat.unicam.it/piergallini/home/materiale/gc/java/native1.1/implementing/method.html
// https://www3.ntu.edu.sg/home/ehchua/programming/java/javanativeinterface.html
// https://www.baeldung.com/jni

JNIEXPORT jlongArray	JNICALL Java_edu_spl_R_initWithDouble( JNIEnv *env, jobject obj, jdouble value );
JNIEXPORT jlongArray	JNICALL Java_edu_spl_R_initWithLong( JNIEnv *env, jobject obj, jlong value );
JNIEXPORT jlongArray	JNICALL Java_edu_spl_R_initWithString( JNIEnv *env, jobject obj, jstring value );
JNIEXPORT jstring		JNICALL Java_edu_spl_R_toStr( JNIEnv *env, jobject obj, jlong low, jlong high, jint prec );
JNIEXPORT jlong			JNICALL Java_edu_spl_R_toLong( JNIEnv *env, jobject obj, jlong low, jlong high );
JNIEXPORT jdouble		JNICALL Java_edu_spl_R_toDouble( JNIEnv *env, jobject obj, jlong low, jlong high );
JNIEXPORT jlongArray	JNICALL Java_edu_spl_R_initConstant( JNIEnv *env, jobject obj, jint value );
JNIEXPORT jboolean		JNICALL Java_edu_spl_R_areEquals( JNIEnv *env, jobject obj, jlong lLow, jlong lHigh, jlong rLow, jlong rHigh );
JNIEXPORT jint			JNICALL Java_edu_spl_R_compare( JNIEnv *env, jobject obj, jlong lLow, jlong lHigh, jlong rLow, jlong rHigh );
JNIEXPORT jlongArray	JNICALL Java_edu_spl_R_operation( JNIEnv *env, jobject obj, jlong lLow, jlong lHigh, jlong rLow, jlong rHigh, jint ope );
JNIEXPORT jlongArray	JNICALL Java_edu_spl_R_operation2( JNIEnv *env, jobject obj, jlong lLow, jlong lHigh, jdouble right, jint ope );
JNIEXPORT jlongArray	JNICALL Java_edu_spl_R_operation3( JNIEnv *env, jobject obj, jdouble left, jlong rLow, jlong rHigh, jint ope );
JNIEXPORT jlongArray	JNICALL Java_edu_spl_R_operation4( JNIEnv *env, jobject obj, jdouble left, jdouble right, jint ope );
JNIEXPORT jlongArray	JNICALL Java_edu_spl_R_operation5( JNIEnv *env, jobject obj, jlong vLow, jlong vHigh, jint ope );
JNIEXPORT jlongArray	JNICALL Java_edu_spl_R_operation6( JNIEnv *env, jobject obj, jdouble dv, jint ope );
JNIEXPORT jlongArray	JNICALL Java_edu_spl_R_operation7( JNIEnv *env, jobject obj, jlong vLow, jlong vHigh, jint ope );
JNIEXPORT jlongArray	JNICALL Java_edu_spl_R_operation8( JNIEnv *env, jobject obj, jdouble dv, jint ope );

#ifdef __cplusplus
}
#endif

//------------------------------------
#endif // _R_NATIVE_FUNCTIONS_H_