package com.dxh.globe3d.utils;

import android.util.Log;

/**
 * Created by XHD on 2024/07/15
 */
public class Logger {
    public static void e(String message) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        Log.e("(" + elements[3].getFileName() + ":" + elements[3].getLineNumber() + ") " + elements[3].getMethodName(), message);
    }
}
