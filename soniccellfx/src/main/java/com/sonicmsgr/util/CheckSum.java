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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 *
 * @author yada
 */
public class CheckSum {

    enum Hash {

        MD5("MD5"),
        SHA1("SHA1"),
        SHA256("SHA-256"),
        SHA512("SHA-512");

        private final String name;

        Hash(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public byte[] checksumByFile(File input) {
            try (InputStream in = new FileInputStream(input)) {
                MessageDigest digest = MessageDigest.getInstance(getName());
                byte[] block = new byte[4096];
                int length;
                while ((length = in.read(block)) > 0) {
                    digest.update(block, 0, length);
                }
                return digest.digest();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public byte[] checksumByBytes(byte[] bytes) {
            try {
                MessageDigest digest = MessageDigest.getInstance(getName());
                digest.update(bytes);
                return digest.digest();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public byte[] checksumByText(String words) {
            if (words == null) {
                return null;
            }
            try {
                MessageDigest digest = MessageDigest.getInstance(getName());
                byte[] block = words.getBytes();
                int length;

                digest.update(block);
                return digest.digest();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private String MD5 = null;
    private String SHA1 = null;
    private String SHA256 = null;
    private String SHA512 = null;

    public boolean processChecksumByBytes(byte[] bytes) {

        MD5 = toHex(Hash.MD5.checksumByBytes(bytes));
        SHA1 = toHex(Hash.SHA1.checksumByBytes(bytes));
        SHA256 = toHex(Hash.SHA256.checksumByBytes(bytes));
        SHA512 = toHex(Hash.SHA512.checksumByBytes(bytes));
        return true;

    }

    public boolean processChecksumFile(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            MD5 = toHex(Hash.MD5.checksumByFile(file));
            SHA1 = toHex(Hash.SHA1.checksumByFile(file));
            SHA256 = toHex(Hash.SHA256.checksumByFile(file));
            SHA512 = toHex(Hash.SHA512.checksumByFile(file));
            return true;
        } else {
            return false;
        }
    }

    public boolean processChecksumText(String words) {

        MD5 = toHex(Hash.MD5.checksumByText(words));
        SHA1 = toHex(Hash.SHA1.checksumByText(words));
        SHA256 = toHex(Hash.SHA256.checksumByText(words));
        SHA512 = toHex(Hash.SHA512.checksumByText(words));
        return true;
    }

    public String getMD5() {
        return MD5;
    }

    public String getMD5_noSpace() {
        return MD5.replace(" ", "");
    }

    public String getSHA1() {
        return SHA1;
    }

    public String getSHAL1_noSpace() {
        return SHA1.replace(" ", "");
    }

    public String getSHA256() {
        return SHA256;
    }

    public String getSHA256_noSpace() {
        return SHA256.replace(" ", "");
    }

    public String getSHA512() {
        return SHA512;
    }

    public String getSHA512_noSpace() {
        return SHA512.replace(" ", "");
    }

    private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 3);
        for (int b : bytes) {
            b &= 0xff;
            sb.append(HEXDIGITS[b >> 4]);
            sb.append(HEXDIGITS[b & 15]);
            sb.append(' ');
        }
        return sb.toString();
    }
    
    public static String checksum512TextStatic(String words){
        CheckSum checkSum = new CheckSum();
        checkSum.processChecksumText(words);
        return checkSum.getSHA512_noSpace();
    }
}
