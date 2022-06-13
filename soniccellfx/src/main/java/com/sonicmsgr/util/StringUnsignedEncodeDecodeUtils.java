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

/**
 *
 * @author yada
 */
public class StringUnsignedEncodeDecodeUtils {

    public static String decodeStringByteValueArrayToStr(String encodedStringByteValueArray) {

        String[] tmps = encodedStringByteValueArray.split(",");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tmps.length; i++) {
            sb.append((char) Byte.parseByte(tmps[i]));
        }
        return sb.toString();
    }

    public static String encodeBytesToUnsignedStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        int len = bytes.length;
        for (int i = 0; i < len; i++) {
            byte b = bytes[i];
            if (b < 0) {
                int num = 256 + b;
                sb.append((char) num);
            } else {
                sb.append((char) b);
            }
        }
        return sb.toString();
    }

    public static String encodeBytesToSignedStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        int len = bytes.length;
        int lastIndex = len - 1;
        for (int i = 0; i < len; i++) {
            byte b = bytes[i];
            sb.append(b);
            if (i < lastIndex) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public static byte[] decodeSignedStrToBytes(String signedStr) {
        String[] str = signedStr.split(",");
        int len = str.length;
        byte[] bytes = new byte[str.length];
        for (int i = 0; i < len; i++) {
            byte b = Byte.parseByte(str[i]);
            bytes[i] = b;
        }
        return bytes;
    }

    public static byte[] decodeUnsignedStrToBytes(String unsignedStr) {

        byte[] bytes = new byte[unsignedStr.length()];

        for (int i = 0; i < unsignedStr.length(); i++) {
            char c = unsignedStr.charAt(i);
            byte b = (byte) c;
            bytes[i] = b;

        }
        return bytes;
    }
}
