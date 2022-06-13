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
package com.sonicmsgr.util;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 *
 * @author Vithya Tith
 */
public class TypeBufferConverterUtil {

    public static void strArrayToObjectArray(String[] stringsArrayData, Object returnObj) {

        if ((returnObj == null) || (stringsArrayData == null)) {
            return;
        }

        int len = stringsArrayData.length;

        if (returnObj instanceof short[]) {
            short[] s = (short[]) returnObj;
            for (int i = 0; i < len; i++) {
                s[i] = Short.parseShort(stringsArrayData[i]);
            }
        } else if (returnObj instanceof float[]) {
            float[] s = (float[]) returnObj;
            for (int i = 0; i < len; i++) {
                s[i] = Float.parseFloat(stringsArrayData[i]);
            }
        }else if (returnObj instanceof long[]) {
            long[] s = (long[]) returnObj;
            for (int i = 0; i < len; i++) {
                s[i] = Long.parseLong(stringsArrayData[i]);
            }
        }else if (returnObj instanceof double[]) {
            double[] s = (double[]) returnObj;
            for (int i = 0; i < len; i++) {
                s[i] = Double.parseDouble(stringsArrayData[i]);
            }
        }

    }

    public static void bytesToShortsArray(byte[] bytesData, short[] returnShortData, boolean littleEndian) {

        if ((returnShortData == null) || (bytesData == null)) {
            return;
        }

        int len = returnShortData.length;
        if ((len * 2) != bytesData.length) {
            return;
        }

        if (littleEndian) {
            for (int i = 0; i < len; i++) {
                int r = bytesData[i * 2 + 1] & 0xFF;
                r = (r << 8) | (bytesData[i * 2] & 0xFF);
                returnShortData[i] = (short) r;
            }
        } else {
            for (int i = 0; i < len; i++) {
                int r = bytesData[i * 2] & 0xFF;
                r = (r << 8) | (bytesData[i * 2 + 1] & 0xFF);
                returnShortData[i] = (short) r;
            }
        }

    }

    public static void bytesToIntegerArray(byte[] bytesData, int[] returnIntData, boolean littleEndian) {

        if ((returnIntData == null) || (bytesData == null)) {
            return;
        }

        int len = returnIntData.length;
        if ((len * 4) != bytesData.length) {
            return;
        }

        if (littleEndian) {
            for (int i = 0; i < len; i++) {
                int val = (bytesData[i * 4 + 3] << 24) & 0xff000000
                        | (bytesData[i * 4 + 2] << 16) & 0x00ff0000
                        | (bytesData[i * 4 + 1] << 8) & 0x0000ff00
                        | (bytesData[i * 4] << 0) & 0x000000ff;
                returnIntData[i] = val;
            }
        } else {

            for (int i = 0; i < len; i++) {
                int val = (bytesData[i * 4] << 24) & 0xff000000
                        | (bytesData[i * 4 + 1] << 16) & 0x00ff0000
                        | (bytesData[i * 4 + 2] << 8) & 0x0000ff00
                        | (bytesData[i * 4 + 3] << 0) & 0x000000ff;
                returnIntData[i] = val;
            }
        }

    }

    public static void bytesToFloatArray(byte[] bytesData, float[] returnFloatData, boolean littleEndian) {

        if ((returnFloatData == null) || (bytesData == null)) {
            return;
        }

        int len = returnFloatData.length;
        if ((len * 4) != bytesData.length) {
            return;
        }

        if (littleEndian) {
            for (int i = 0; i < len; i++) {
                int val = (bytesData[i * 4 + 3] << 24) & 0xff000000
                        | (bytesData[i * 4 + 2] << 16) & 0x00ff0000
                        | (bytesData[i * 4 + 1] << 8) & 0x0000ff00
                        | (bytesData[i * 4] << 0) & 0x000000ff;
                returnFloatData[i] = Float.intBitsToFloat(val);

            }
        } else {

            for (int i = 0; i < len; i++) {
                int val = (bytesData[i * 4] << 24) & 0xff000000
                        | (bytesData[i * 4 + 1] << 16) & 0x00ff0000
                        | (bytesData[i * 4 + 2] << 8) & 0x0000ff00
                        | (bytesData[i * 4 + 3] << 0) & 0x000000ff;
                returnFloatData[i] = Float.intBitsToFloat(val);
            }
        }

    }

    public static void bytesToLongArray(byte[] bytesData, long[] returnLongData, boolean littleEndian) {

        if ((returnLongData == null) || (bytesData == null)) {
            return;
        }

        int len = returnLongData.length;
        if ((len * 8) != bytesData.length) {
            return;
        }

        if (littleEndian) {
            for (int i = 0; i < len; i++) {
                long l = ((long) (bytesData[i * 8 + 0] & 0xff))
                        | ((long) (bytesData[i * 8 + 1] & 0xff) << 8)
                        | ((long) (bytesData[i * 8 + 2] & 0xff) << 16)
                        | ((long) (bytesData[i * 8 + 3] & 0xff) << 24)
                        | ((long) (bytesData[i * 8 + 4] & 0xff) << 32)
                        | ((long) (bytesData[i * 8 + 5] & 0xff) << 40)
                        | ((long) (bytesData[i * 8 + 6] & 0xff) << 48)
                        | ((long) (bytesData[i * 8 + 7] & 0xff) << 56);
                returnLongData[i] = l;
            }
        } else {

            for (int i = 0; i < len; i++) {
                long l = ((long) (bytesData[i * 8] & 0xff) << 56)
                        | ((long) (bytesData[i * 8 + 1] & 0xff) << 48)
                        | ((long) (bytesData[i * 8 + 2] & 0xff) << 40)
                        | ((long) (bytesData[i * 8 + 3] & 0xff) << 32)
                        | ((long) (bytesData[i * 8 + 4] & 0xff) << 24)
                        | ((long) (bytesData[i * 8 + 5] & 0xff) << 16)
                        | ((long) (bytesData[i * 8 + 6] & 0xff) << 8)
                        | ((long) (bytesData[i * 8 + 7] & 0xff));
                returnLongData[i] = l;
            }
        }

    }

    public static void bytesToDoubleArray(byte[] bytesData, double[] returnDoubleData, boolean littleEndian) {

        if ((returnDoubleData == null) || (bytesData == null)) {
            return;
        }

        int len = returnDoubleData.length;
        if ((len * 8) != bytesData.length) {
            return;
        }

        if (littleEndian) {
            for (int i = 0; i < len; i++) {
                long l = ((long) (bytesData[i * 8 + 0] & 0xff))
                        | ((long) (bytesData[i * 8 + 1] & 0xff) << 8)
                        | ((long) (bytesData[i * 8 + 2] & 0xff) << 16)
                        | ((long) (bytesData[i * 8 + 3] & 0xff) << 24)
                        | ((long) (bytesData[i * 8 + 4] & 0xff) << 32)
                        | ((long) (bytesData[i * 8 + 5] & 0xff) << 40)
                        | ((long) (bytesData[i * 8 + 6] & 0xff) << 48)
                        | ((long) (bytesData[i * 8 + 7] & 0xff) << 56);
                returnDoubleData[i] = Double.longBitsToDouble(l);
            }
        } else {

            for (int i = 0; i < len; i++) {
                long l = ((long) (bytesData[i * 8] & 0xff) << 56)
                        | ((long) (bytesData[i * 8 + 1] & 0xff) << 48)
                        | ((long) (bytesData[i * 8 + 2] & 0xff) << 40)
                        | ((long) (bytesData[i * 8 + 3] & 0xff) << 32)
                        | ((long) (bytesData[i * 8 + 4] & 0xff) << 24)
                        | ((long) (bytesData[i * 8 + 5] & 0xff) << 16)
                        | ((long) (bytesData[i * 8 + 6] & 0xff) << 8)
                        | ((long) (bytesData[i * 8 + 7] & 0xff));
                returnDoubleData[i] = Double.longBitsToDouble(l);
            }
        }

    }
//    public static byte[] shortArrayToByteArray(short[] input, boolean bigEndian) {
//        int index;
//        int iterations = input.length;
//
//        ByteBuffer bb = ByteBuffer.allocate(input.length * 2);
//        if (bigEndian) {
//            bb.order(ByteOrder.BIG_ENDIAN);
//        } else {
//            bb.order(ByteOrder.LITTLE_ENDIAN);
//        }
//
//        for (index = 0; index != iterations; ++index) {
//            bb.putShort(input[index]);
//        }
//
//        return bb.array();
//    }

    public static byte[] shortArrayToByteArrayFast(short[] input, boolean bigEndian) {
        int short_index, byte_index;
        int iterations = input.length;

        byte[] buffer = new byte[input.length * 2];

        short_index = byte_index = 0;

        if (bigEndian) {
            for (/*NOP*/; short_index != iterations; /*NOP*/) {
                buffer[byte_index + 1] = (byte) (input[short_index] & 0x00FF);
                buffer[byte_index] = (byte) ((input[short_index] & 0xFF00) >> 8);
                ++short_index;
                byte_index += 2;
            }
        } else {
            for (/*NOP*/; short_index != iterations; /*NOP*/) {
                buffer[byte_index] = (byte) (input[short_index] & 0x00FF);
                buffer[byte_index + 1] = (byte) ((input[short_index] & 0xFF00) >> 8);
                ++short_index;
                byte_index += 2;
            }
        }

        return buffer;
    }

//    public static void shortArrayToBytes(short[] input, byte[] output, boolean littleEndian) {
//        int short_index, byte_index;
//        int iterations = input.length;
//        short_index = byte_index = 0;
//
//        if (littleEndian) {
//            for (/*NOP*/; short_index != iterations; /*NOP*/) {
//                output[byte_index] = (byte) (input[short_index] & 0x00FF);
//                output[byte_index + 1] = (byte) ((input[short_index] & 0xFF00) >> 8);
//                ++short_index;
//                byte_index += 2;
//            }
//
//        } else {
//
//            for (/*NOP*/; short_index != iterations; /*NOP*/) {
//                output[byte_index + 1] = (byte) (input[short_index] & 0x00FF);
//                output[byte_index] = (byte) ((input[short_index] & 0xFF00) >> 8);
//                ++short_index;
//                byte_index += 2;
//            }
//        }
//
//    }
    public static void shortArrayToBytesArray(short[] shortData, byte[] returnBytesData, boolean littleEndian) {

        if ((shortData == null) || (returnBytesData == null)) {
            return;
        }

        int len = shortData.length;
        if ((len * 2) != returnBytesData.length) {
            return;
        }

        if (littleEndian) {
            for (int i = 0; i < len; i++) {
                short v = shortData[i];
                returnBytesData[i * 2] = (byte) (v & 0xFF);
                returnBytesData[i * 2 + 1] = (byte) (v >> 8 & 0xFF);
            }
        } else {
            for (int i = 0; i < len; i++) {
                short v = shortData[i];
                returnBytesData[i * 2] = (byte) (v >> 8 & 0xFF);
                returnBytesData[i * 2 + 1] = (byte) (v & 0xFF);
            }
        }

    }

    public static void shortToBytes(short shortData, byte[] returnBytesData, boolean littleEndian) {

        if ((returnBytesData == null)) {
            return;
        }
        if (littleEndian) {
            short v = shortData;
            returnBytesData[0] = (byte) (v & 0xFF);
            returnBytesData[1] = (byte) (v >> 8 & 0xFF);
        } else {

            short v = shortData;
            returnBytesData[0] = (byte) (v >> 8 & 0xFF);
            returnBytesData[1] = (byte) (v & 0xFF);
        }

    }

    public static void intArrayToBytes(int[] inData, byte[] outData, boolean littleEndian) {
        int j = 0;
        int length = inData.length;
        if (littleEndian) {
            for (int i = 0; i < length; i++) {

                int v = inData[i];
                outData[j++] = ((byte) ((v >> 0)));
                outData[j++] = ((byte) ((v >> 8)));
                outData[j++] = ((byte) ((v >> 16)));
                outData[j++] = ((byte) ((v >> 24)));
            }
        } else {
            for (int i = 0; i < length; i++) {
                int v = inData[i];
                outData[j++] = ((byte) ((v >> 24)));
                outData[j++] = ((byte) ((v >> 16)));
                outData[j++] = ((byte) ((v >> 8)));
                outData[j++] = ((byte) ((v)));
            }
        }
    }

    public static void intToBytes(int intData, byte[] returnBytesData, boolean littleEndian) {
        if ((returnBytesData == null)) {
            return;
        }
        if (littleEndian) {
            returnBytesData[0] = ((byte) ((intData >> 0)));
            returnBytesData[1] = ((byte) ((intData >> 8)));
            returnBytesData[2] = ((byte) ((intData >> 16)));
            returnBytesData[3] = ((byte) ((intData >> 24)));
        } else {
            returnBytesData[0] = (byte) (intData >> 24);
            returnBytesData[1] = (byte) (intData >> 16);
            returnBytesData[2] = (byte) (intData >> 8);
            returnBytesData[3] = (byte) intData;
        }
    }

    public static void floatArrayToBytes(float[] inData, byte[] outData, boolean littleEndian) {
        int j = 0;
        int length = inData.length;
        if (littleEndian) {
            for (int i = 0; i < length; i++) {
                int data = Float.floatToIntBits(inData[i]);
                outData[j++] = (byte) (data >>> 0);
                outData[j++] = (byte) (data >>> 8);
                outData[j++] = (byte) (data >>> 16);
                outData[j++] = (byte) (data >>> 24);
            }

        } else {

            for (int i = 0; i < length; i++) {
                int data = Float.floatToIntBits(inData[i]);
                outData[j++] = (byte) (data >>> 24);
                outData[j++] = (byte) (data >>> 16);
                outData[j++] = (byte) (data >>> 8);
                outData[j++] = (byte) (data >>> 0);
            }
        }
    }

    public static void floatToBytes(float inData, byte[] outData, boolean littleEndian) {
        int j = 0;
        int length = 4;
        if (littleEndian) {

            int data = Float.floatToIntBits(inData);
            outData[j++] = (byte) (data >>> 0);
            outData[j++] = (byte) (data >>> 8);
            outData[j++] = (byte) (data >>> 16);
            outData[j++] = (byte) (data >>> 24);

        } else {
            int data = Float.floatToIntBits(inData);
            outData[0] = (byte) (data >>> 24);
            outData[1] = (byte) (data >>> 16);
            outData[2] = (byte) (data >>> 8);
            outData[3] = (byte) (data >>> 0);
        }
    }

    public static void longArrayToBytes(long[] inData, byte[] outData, boolean littleEndian) {
        int j = 0;
        int length = inData.length;
        if (littleEndian) {
            for (int i = 0; i < length; i++) {
                long value = inData[i];
                outData[j++] = (byte) ((value >>> 0) & 0xFF);
                outData[j++] = (byte) ((value >>> 8) & 0xFF);
                outData[j++] = (byte) ((value >>> 16) & 0xFF);
                outData[j++] = (byte) ((value >>> 24) & 0xFF);
                outData[j++] = (byte) ((value >>> 32) & 0xFF);
                outData[j++] = (byte) ((value >>> 40) & 0xFF);
                outData[j++] = (byte) ((value >>> 48) & 0xFF);
                outData[j++] = (byte) ((value >>> 56) & 0xFF);
            }
        } else {
            for (int i = 0; i < length; i++) {
                long value = inData[i];
                outData[j++] = (byte) ((value >>> 56) & 0xFF);
                outData[j++] = (byte) ((value >>> 48) & 0xFF);
                outData[j++] = (byte) ((value >>> 40) & 0xFF);
                outData[j++] = (byte) ((value >>> 32) & 0xFF);
                outData[j++] = (byte) ((value >>> 24) & 0xFF);
                outData[j++] = (byte) ((value >>> 16) & 0xFF);
                outData[j++] = (byte) ((value >>> 8) & 0xFF);
                outData[j++] = (byte) ((value >>> 0) & 0xFF);
            }
        }
    }

    public static void longToBytes(long inData, byte[] outData, boolean littleEndian) {

        if (littleEndian) {
            outData[0] = (byte) ((inData >>> 0) & 0xFF);
            outData[1] = (byte) ((inData >>> 8) & 0xFF);
            outData[2] = (byte) ((inData >>> 16) & 0xFF);
            outData[3] = (byte) ((inData >>> 24) & 0xFF);
            outData[4] = (byte) ((inData >>> 32) & 0xFF);
            outData[5] = (byte) ((inData >>> 40) & 0xFF);
            outData[6] = (byte) ((inData >>> 48) & 0xFF);
            outData[7] = (byte) ((inData >>> 56) & 0xFF);

        } else {
            outData[0] = (byte) ((inData >>> 56) & 0xFF);
            outData[1] = (byte) ((inData >>> 48) & 0xFF);
            outData[2] = (byte) ((inData >>> 40) & 0xFF);
            outData[3] = (byte) ((inData >>> 32) & 0xFF);
            outData[4] = (byte) ((inData >>> 24) & 0xFF);
            outData[5] = (byte) ((inData >>> 16) & 0xFF);
            outData[6] = (byte) ((inData >>> 8) & 0xFF);
            outData[7] = (byte) ((inData >>> 0) & 0xFF);
        }
    }

    public static void doubleArrayToBytes(double[] doubleData, byte[] outData, boolean littleEndian) {
        int j = 0;
        int length = doubleData.length;
        if (littleEndian) {
            for (int i = 0; i < length; i++) {
                long value = Double.doubleToLongBits(doubleData[i]);
                outData[j++] = (byte) ((value >>> 0) & 0xFF);
                outData[j++] = (byte) ((value >>> 8) & 0xFF);
                outData[j++] = (byte) ((value >>> 16) & 0xFF);
                outData[j++] = (byte) ((value >>> 24) & 0xFF);
                outData[j++] = (byte) ((value >>> 32) & 0xFF);
                outData[j++] = (byte) ((value >>> 40) & 0xFF);
                outData[j++] = (byte) ((value >>> 48) & 0xFF);
                outData[j++] = (byte) ((value >>> 56) & 0xFF);
            }
        } else {
            for (int i = 0; i < length; i++) {
                long value = Double.doubleToLongBits(doubleData[i]);
                outData[j++] = (byte) ((value >>> 56) & 0xFF);
                outData[j++] = (byte) ((value >>> 48) & 0xFF);
                outData[j++] = (byte) ((value >>> 40) & 0xFF);
                outData[j++] = (byte) ((value >>> 32) & 0xFF);
                outData[j++] = (byte) ((value >>> 24) & 0xFF);
                outData[j++] = (byte) ((value >>> 16) & 0xFF);
                outData[j++] = (byte) ((value >>> 8) & 0xFF);
                outData[j++] = (byte) ((value >>> 0) & 0xFF);
            }
        }
    }

    public static void doubleToBytes(double doubleValue, byte[] outData, boolean littleEndian) {

        if (littleEndian) {
            long inData = Double.doubleToLongBits(doubleValue);
            outData[0] = (byte) ((inData >>> 0) & 0xFF);
            outData[1] = (byte) ((inData >>> 8) & 0xFF);
            outData[2] = (byte) ((inData >>> 16) & 0xFF);
            outData[3] = (byte) ((inData >>> 24) & 0xFF);
            outData[4] = (byte) ((inData >>> 32) & 0xFF);
            outData[5] = (byte) ((inData >>> 40) & 0xFF);
            outData[6] = (byte) ((inData >>> 48) & 0xFF);
            outData[7] = (byte) ((inData >>> 56) & 0xFF);

        } else {
            long inData = Double.doubleToLongBits(doubleValue);
            outData[0] = (byte) ((inData >>> 56) & 0xFF);
            outData[1] = (byte) ((inData >>> 48) & 0xFF);
            outData[2] = (byte) ((inData >>> 40) & 0xFF);
            outData[3] = (byte) ((inData >>> 32) & 0xFF);
            outData[4] = (byte) ((inData >>> 24) & 0xFF);
            outData[5] = (byte) ((inData >>> 16) & 0xFF);
            outData[6] = (byte) ((inData >>> 8) & 0xFF);
            outData[7] = (byte) ((inData >>> 0) & 0xFF);
        }
    }

    //////////////
    public static short[] converByteArrayToShortArray(byte[] rawBytes, boolean useLittleEndain) {

        ByteBuffer byteBuffer = ByteBuffer.wrap(rawBytes);

        if (useLittleEndain) {
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        } else {
            byteBuffer.order(ByteOrder.BIG_ENDIAN);
        }

        Buffer buffer;

        buffer = byteBuffer.asShortBuffer();
        short[] shortArray = new short[rawBytes.length / 2];
        ((ShortBuffer) buffer).get(shortArray);

        return shortArray;
    }

    public static float[] converByteArrayToFloat(byte[] rawBytes, boolean useLittleEndain) {

        ByteBuffer byteBuffer = ByteBuffer.wrap(rawBytes);
        if (useLittleEndain) {
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        } else {
            byteBuffer.order(ByteOrder.BIG_ENDIAN);
        }

        Buffer buffer;

        buffer = byteBuffer.asFloatBuffer();
        float[] shortArray = new float[rawBytes.length / 4];
        ((FloatBuffer) buffer).get(shortArray);

        return shortArray;
    }

    public static float[] castShortArrayToFloatArray(short[] data, float scalingFactor) {
        float[] floaters = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            floaters[i] = scalingFactor * data[i];
        }
        return floaters;
    }

    public static void floatArrayToByteArrayLittleEndian(float[] inData, byte[] outData) {
        int j = 0;
        int length = inData.length;
        for (int i = 0; i < length; i++) {
            int data = Float.floatToRawIntBits(inData[i]);
            outData[j++] = (byte) ((data >> 0) & 0xFF);
            outData[j++] = (byte) ((data >> 8) & 0xFF);
            outData[j++] = (byte) ((data >> 16) & 0xFF);
            outData[j++] = (byte) ((data >> 24) & 0xFF);
        }

    }

    public static float swap(float value) {
        int intValue = Float.floatToRawIntBits(value);
        intValue = swap(intValue);
        return Float.intBitsToFloat(intValue);
    }

    public static int swap(int value) {

        int b1 = (value >> 0) & 0xff;
        int b2 = (value >> 8) & 0xff;
        int b3 = (value >> 16) & 0xff;
        int b4 = (value >> 24) & 0xff;

        return b1 << 24 | b2 << 16 | b3 << 8 | b4 << 0;
    }

    public static float swap(float value, int endianNess) {

        int bytesForFloat = 4;
        ByteBuffer buffer = ByteBuffer.allocate(bytesForFloat);

        if (endianNess == 0) {
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        } else {
            buffer.order(ByteOrder.BIG_ENDIAN);
        }
        buffer.putFloat(0, value);
        buffer.rewind();
        return buffer.getFloat();
    }

    public static void main(String[] args) {

        String[] str = {"1", "2", "3"};
        double[] data = new double[3];

        //TypeBufferConverterUtil.strArrayToShortsArray(str, null);
        TypeBufferConverterUtil.strArrayToObjectArray(str, data);
        int size = data.length;
        for (int i = 0; i < size; i++) {
            System.out.println(data[i]);
        }

//        byte b[] = {-20, 100, -23, 89, 13, -56, -18, 50};
//        double s[] = new double[1];
//        //       TypeBufferConverterUtil.bytesToShortsArray(b, s, false);
//
//        TypeBufferConverterUtil.bytesToDoubleArray(b, s, true);
//        int len = s.length;
//        for (int i = 0; i < len; i++) {
//            System.out.println(s[i]);
//        }
//
//        ByteBuffer byteBuffer = ByteBuffer.allocate(b.length);
//        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
//        byteBuffer.put(b);
//        byteBuffer.rewind();
//        double val = byteBuffer.getDouble();
//        System.out.println(val);
//        byte b[] = new byte[2];
//        short value = -3453;
//        TypeBufferConverterUtil.shortToBytes(value, b, true);
//        int len = b.length;
//        for (int i = 0; i < len; i++) {
//            System.out.println("A: " + b[i]);
//        }
//
//        ByteBuffer byteBuffer = ByteBuffer.allocate(b.length);
//        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
//        byteBuffer.putShort(value);
//        b = byteBuffer.array();
//        for (int i = 0; i < len; i++) {
//            System.out.println("B:" + b[i]);
//        }
    }
}
