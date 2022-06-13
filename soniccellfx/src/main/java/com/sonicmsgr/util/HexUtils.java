/*
 * Copyright 2022 Vithya Tith
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
package com.sonicmsgr.util;

public class HexUtils {

    public static byte[] parseHexBinary(String s) {
        final int len = s.length();

        // "111" is not a valid hex encoding.
        if (len % 2 != 0) {
            throw new IllegalArgumentException("hexBinary needs to be even-length: " + s);
        }

        byte[] out = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            int h = hexToBin(s.charAt(i));
            int l = hexToBin(s.charAt(i + 1));
            if (h == -1 || l == -1) {
                throw new IllegalArgumentException("contains illegal character for hexBinary: " + s);
            }

            out[i / 2] = (byte) (h * 16 + l);
        }

        return out;
    }

    public static void shortArrayToBytesArray(short[] shortData, byte[] returnBytesData, boolean littleEndian) {
        shortArrayToBytesArray(shortData, returnBytesData, 1, littleEndian);
    }
        public static void shortArrayToBytesArray(short[] shortData, byte[] returnBytesData, int gain, boolean littleEndian) {

        if ((shortData == null) || (returnBytesData == null)) {

            return;
        }

        int len = shortData.length;
        if ((len * 2) != returnBytesData.length) {
            return;
        }

        if (littleEndian) {
            for (int i = 0; i < len; i++) {
                short v = (short)(gain*shortData[i]);
                returnBytesData[i * 2] = (byte) (v & 0xFF);
                returnBytesData[i * 2 + 1] = (byte) (v >> 8 & 0xFF);
            }
        } else {
            for (int i = 0; i < len; i++) {
                short v = (short)(gain*shortData[i]);
                returnBytesData[i * 2] = (byte) (v >> 8 & 0xFF);
                returnBytesData[i * 2 + 1] = (byte) (v & 0xFF);
            }
        }

    }

    private static int hexToBin(char ch) {
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        }
        if ('A' <= ch && ch <= 'F') {
            return ch - 'A' + 10;
        }
        if ('a' <= ch && ch <= 'f') {
            return ch - 'a' + 10;
        }
        return -1;
    }

    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    public static String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

    private final StringBuilder tmpSB = new StringBuilder("    ");

    public boolean hex16BitToShortArray(byte[] data, short[] returnValue) {

        if ((data == null) || (returnValue == null)) {
            System.out.println("Data is null");
            return false;
        }

        int len = data.length / 2;
        int lenReturn = returnValue.length;

        if (len != lenReturn) {
            System.out.println("Size doesn't match.");
            return false;
        }

        for (int i = 0; i < len; i++) {

            tmpSB.setCharAt(0, hexCode[(data[i * 2] >> 4) & 0xF]);
            tmpSB.setCharAt(1, hexCode[(data[i * 2] & 0xF)]);
            tmpSB.setCharAt(2, hexCode[(data[i * 2 + 1] >> 4) & 0xF]);
            tmpSB.setCharAt(3, hexCode[(data[i * 2 + 1] & 0xF)]);

            String s = tmpSB.toString();

            int val = (Integer.parseInt(s, 16));
            returnValue[i] = (short) val;
        }
        return true;
    }

    public boolean hex16BitToShortArrayInterLeave(byte[] data, short[] returnValue, int channelIndex, int maxChannel) {

        if ((data == null) || (returnValue == null)) {
            System.out.println("Data is null");
            return false;
        }
        if (maxChannel < 1) {
            System.out.println("Channel can not be less than 1.");
            return false;
        }

        if (channelIndex < 1) {
            System.out.println("Channel Index can not be less than 1.");
            return false;
        }

        channelIndex = channelIndex - 1;

        int len = (data.length / 2) / maxChannel;
        int lenReturn = returnValue.length;

        if (len != lenReturn) {
            System.out.println("Size doesn't match.");
            return false;
        }

        for (int i = 1; i < len; i++) {
            int index = (i * 2) * maxChannel + (channelIndex) * maxChannel;
            tmpSB.setCharAt(0, hexCode[(data[index] >> 4) & 0xF]);
            tmpSB.setCharAt(1, hexCode[(data[index] & 0xF)]);

            tmpSB.setCharAt(2, hexCode[(data[index + 1] >> 4) & 0xF]);
            tmpSB.setCharAt(3, hexCode[(data[index + 1] & 0xF)]);

            String s = tmpSB.toString();

            int val = (Integer.parseInt(s, 16));
            returnValue[i] = (short) val;
        }
        return true;
    }
}
