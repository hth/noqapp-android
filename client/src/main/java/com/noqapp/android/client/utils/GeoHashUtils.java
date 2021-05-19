/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.noqapp.android.client.utils;

/**
 * Utilities for converting to/from the GeoHash standard
 * <p>
 * The geohash long format is represented as lon/lat (x/y) interleaved with the 4 least significant bits
 * representing the level (1-12) [xyxy...xyxyllll]
 * <p>
 * This differs from a morton encoded value which interleaves lat/lon (y/x).*
 */
public class GeoHashUtils {
    private static final char[] BASE_32 = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n',
            'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    private static final String BASE_32_STRING = new String(BASE_32);

    /**
     * maximum precision for geohash strings
     */
    private static final int PRECISION = 12;
    /**
     * number of bits used for quantizing latitude and longitude values
     */
    private static final short BITS = 31;
    /**
     * scaling factors to convert lat/lon into unsigned space
     */
    private static final double LAT_SCALE = (0x1L << BITS) / 180.0D;
    private static final double LON_SCALE = (0x1L << BITS) / 360.0D;
    private static final short MORTON_OFFSET = (BITS << 1) - (PRECISION * 5);

    // No instance:
    private GeoHashUtils() {
    }

    /**
     * Encode to a morton long value from a given geohash string
     */
    private static long mortonEncode(final String hash) {
        int level = 11;
        long b;
        long l = 0L;
        for (char c : hash.toCharArray()) {
            b = (long) (BASE_32_STRING.indexOf(c));
            l |= (b << ((level-- * 5) + MORTON_OFFSET));
        }
        return BitUtil.flipFlop(l);
    }

    /**
     * decode longitude value from morton encoded geo point
     */
    private static double decodeLongitude(final long hash) {
        return unscaleLon(BitUtil.deinterleave(hash));
    }

    /**
     * decode latitude value from morton encoded geo point
     */
    private static double decodeLatitude(final long hash) {
        return unscaleLat(BitUtil.deinterleave(hash >>> 1));
    }

    private static double unscaleLon(final long val) {
        return (val / LON_SCALE) - 180;
    }

    private static double unscaleLat(final long val) {
        return (val / LAT_SCALE) - 90;
    }

    /**
     * returns the latitude value from the string based geohash
     */
    public static double decodeLatitude(final String geohash) {
        return decodeLatitude(mortonEncode(geohash));
    }

    /**
     * returns the latitude value from the string based geohash
     */
    public static double decodeLongitude(final String geohash) {
        return decodeLongitude(mortonEncode(geohash));
    }
}
