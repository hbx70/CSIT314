package com.yinyang.project.utils;

/**
 * ThreadLocal Util
 */
@SuppressWarnings("all")
public class ThreadLocalUtil {
    // ThreadLocal Object,
    private static final ThreadLocal THREAD_LOCAL = new ThreadLocal();

    // Get key-pair
    public static <T> T get(){
        return (T) THREAD_LOCAL.get();
    }
	
    // Store key-pair
    public static void set(Object value){
        THREAD_LOCAL.set(value);
    }

    // Remove ThreadLocal, prevent memory leak
    public static void remove(){
        THREAD_LOCAL.remove();
    }
}
