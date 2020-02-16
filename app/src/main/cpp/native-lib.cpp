#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_id_ac_ui_cs_mobileprogramming_wikansetiaji_mediatodo_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jint JNICALL
Java_id_ac_ui_cs_mobileprogramming_wikansetiaji_mediatodo_MainActivity_milliToSecond( JNIEnv *env, jobject, jint millis) {

    return (millis/1000)%60;
}

extern "C" JNIEXPORT jint JNICALL
Java_id_ac_ui_cs_mobileprogramming_wikansetiaji_mediatodo_MainActivity_milliToMinute( JNIEnv *env, jobject, jint millis) {

    return (millis/60000)%60;
}

extern "C" JNIEXPORT jint JNICALL
Java_id_ac_ui_cs_mobileprogramming_wikansetiaji_mediatodo_MainActivity_milliToHour( JNIEnv *env, jobject, jint millis) {

    return (millis/3600000);
}