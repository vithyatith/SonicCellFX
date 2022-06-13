/*
 * Copyright 2018 Vithya Tith
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.ributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See t
 */
package com.sonicmsgr.soniccell.util;

/**
 *
 * @author yada
 */
public class ConnectionUtil {

    private static int keys[] = new int[4];
    private static int firstOffsetMult = 10000000;
    private static int secondOffsetMult = 100000;
    private static int thirdOffsetMult = 100;

    public static int[] keyDecode(long key) {
        long a = (key / firstOffsetMult);
        long b = (key - a * firstOffsetMult) / secondOffsetMult;
        long c = (key - a * firstOffsetMult - b * secondOffsetMult) / thirdOffsetMult;
        long d = (key - a * firstOffsetMult - b * secondOffsetMult - c * thirdOffsetMult);

        keys[0] = (int) a;
        keys[1] = (int) b;
        keys[2] = (int) c;
        keys[3] = (int) d;
        return keys;
    }

    public static long keyEncode(int srcId, int srcOutputId, int destId, int destInputId) {
        long key = srcId * firstOffsetMult + srcOutputId * secondOffsetMult + destId * thirdOffsetMult + destInputId;
        return key;
    }
}
