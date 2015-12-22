package com.slheavner.wvubus.utils;

import android.util.Log;

/**
 * Created by Sam on 12/21/2015.
 */
public class Logger {

    private static String PREFIX = "MBT-";

    public static void info(Object c, String message){
        Log.i(tag(c.getClass()), message);
    }

    public static void warn(Object c, String message){
        Log.w(tag(c.getClass()), message);
    }

    public static void debug(Object c, String message){
        Log.d(tag(c.getClass()), message);
    }

    public static void error(Object c, String message){
        Log.e(tag(c.getClass()), message);
    }

    private static String tag(Class c){
        return PREFIX + c.getSimpleName();
    }

}
