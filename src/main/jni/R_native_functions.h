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

JNIEXPORT void			JNICALL Java_edu_spl_R_print( JNIEnv *env, jobject obj );
JNIEXPORT jlongArray	JNICALL Java_edu_spl_R_initWithDouble( JNIEnv *env, jobject obj, jdouble value );
JNIEXPORT jlongArray	JNICALL Java_edu_spl_R_initWithLong( JNIEnv *env, jobject obj, jlong value );
JNIEXPORT jlongArray	JNICALL Java_edu_spl_R_initWithString( JNIEnv *env, jobject obj, jstring value );
JNIEXPORT jstring		JNICALL Java_edu_spl_R_toStr( JNIEnv *env, jobject obj, jlong low, jlong high, jint prec );

#ifdef __cplusplus
}
#endif

//------------------------------------
#endif // _R_NATIVE_FUNCTIONS_H_