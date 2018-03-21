package com.hzf.utils.ExUtil;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ThrowUtil {

    public static String printStackTraceToString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw, true));
        return sw.getBuffer().toString();
    }

}
