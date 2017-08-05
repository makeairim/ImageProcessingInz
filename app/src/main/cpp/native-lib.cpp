#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring

JNICALL
Java_agh_edu_pl_imageprocessing_HomeActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
