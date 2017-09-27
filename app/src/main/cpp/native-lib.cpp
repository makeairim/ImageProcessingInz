#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring

JNICALL
Java_agh_edu_pl_imageprocessing_ImageProcessingApplication_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
