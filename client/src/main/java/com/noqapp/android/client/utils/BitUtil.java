/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.noqapp.android.client.utils;

/**
 * A variety of high efficiency bit twiddling routines.
 *
 * @lucene.internal
 */
public final class BitUtil {

    // magic numbers for bit interleaving
    private static final long MAGIC[] = {
            0x5555555555555555L, 0x3333333333333333L,
            0x0F0F0F0F0F0F0F0FL, 0x00FF00FF00FF00FFL,
            0x0000FFFF0000FFFFL, 0x00000000FFFFFFFFL,
            0xAAAAAAAAAAAAAAAAL
    };
    // shift values for bit interleaving
    private static final short SHIFT[] = {1, 2, 4, 8, 16};

    private BitUtil() {
    } // no instance

    // The pop methods used to rely on bit-manipulation tricks for speed but it
    // turns out that it is faster to use the Long.bitCount method (which is an
    // intrinsic since Java 6u18) in a naive loop, see LUCENE-2221

    /**
     * Deinterleaves long value back to two concatenated 32bit values
     */
    public static long deinterleave(long b) {
        b &= MAGIC[0];
        b = (b ^ (b >>> SHIFT[0])) & MAGIC[1];
        b = (b ^ (b >>> SHIFT[1])) & MAGIC[2];
        b = (b ^ (b >>> SHIFT[2])) & MAGIC[3];
        b = (b ^ (b >>> SHIFT[3])) & MAGIC[4];
        b = (b ^ (b >>> SHIFT[4])) & MAGIC[5];
        return b;
    }

    /**
     * flip flops odd with even bits
     */
    public static final long flipFlop(final long b) {
        return ((b & MAGIC[6]) >>> 1) | ((b & MAGIC[0]) << 1);
    }
}
