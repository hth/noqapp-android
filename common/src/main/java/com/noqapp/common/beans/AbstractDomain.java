package com.noqapp.common.beans;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by hitender on 4/2/18.
 */
public abstract class AbstractDomain {
    private static final String TAG = AbstractDomain.class.getSimpleName();

    public static final String ISO8601_FMT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /**
     * @deprecated (This adds tons of accept charset.)
     * Converts this object to JSON representation;
     * do not use annotation as this breaks and content length is set to -1
     */
    @Deprecated
    public String asJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Writer writer = new StringWriter();
            mapper.writeValue(writer, this);
            return writer.toString();
        } catch (IOException e) {
            Log.e(TAG, "Failed JSON transforming object error=" + e.getLocalizedMessage(), e);
            return "{}";
        }
    }
}