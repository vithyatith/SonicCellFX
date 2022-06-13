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
package com.sonicmsgr.util.encryption;

import com.sonicmsgr.util.CheckSum;

//SonicID is 8 digits   = seed hashing only
//SonicID is 9 digits   = seed hashing + Phone number
//SonicID is 10 digits  = seed hashing + Mac Address
//SonicID is 11 digits  = seed hashing + Vin Number
//SonicID is 12 digits  = seed hashing + Words
/**
 *
 * @author yada
 */
public class SonicIDGenerator {

    /**
     * This code will create the Sonic ID from a checksum. The output of the
     * string will be alpha numeric.
     *
     * @param checksumPublicKey The checksum
     * @param digitSize How many digits of the SonicID
     * @return
     */
    private static final int maxSize = 23;

    public static String generateSonicID(String checksumPublicKey) {
        return generateSonicID(checksumPublicKey, null, null, null, null, maxSize) + "000";
    }

    public static String generateSonicIDPhoneNum(String checksumPublicKey, String phoneNumber) {
        return generateSonicID(checksumPublicKey, CheckSum.checksum512TextStatic(phoneNumber), null, null, null, maxSize) + "P00";
    }

    public static String generateSonicIDMacAddr(String checksumPublicKey, String mac) {
        return generateSonicID(checksumPublicKey, CheckSum.checksum512TextStatic(mac), null, null, null, maxSize) + "M00";
    }

//    public static String generateSonicIDVinNum(String checksumPublicKey, String vin) {
//        return generateSonicID(checksumPublicKey, CheckSum.checksum512TextStatic(vin), null, null,null, maxSize) + "V00";
//    }
    public static String generateSonicIDWord(String checksumPublicKey, String word) {
        return generateSonicID(checksumPublicKey, CheckSum.checksum512TextStatic(word), null, null, null, maxSize) + "W00";
    }

    public static String generateSonicIDEmail(String checksumPublicKey, String email) {
        return generateSonicID(checksumPublicKey, CheckSum.checksum512TextStatic(email), null, null, null, maxSize) + "E00";
    }

    public static String generateSonicIDPhoneMac(String checksumPublicKey, String phoneNumber, String mac) {
        return generateSonicID(checksumPublicKey, CheckSum.checksum512TextStatic(phoneNumber), CheckSum.checksum512TextStatic(mac), null, null, maxSize) + "PM0";
    }

//    public static String generateSonicIDPhoneMacVin(String checksumPublicKey, String phoneNumber, String mac, String vin) {
//        return generateSonicID(checksumPublicKey, CheckSum.checksum512TextStatic(phoneNumber), CheckSum.checksum512TextStatic(mac), CheckSum.checksum512TextStatic(vin), null,maxSize) + "PMV";
//    }
    public static String generateSonicIDPhoneMacWord(String checksumPublicKey, String phoneNumber, String mac, String word) {
        return generateSonicID(checksumPublicKey, CheckSum.checksum512TextStatic(phoneNumber), CheckSum.checksum512TextStatic(mac), CheckSum.checksum512TextStatic(word), null, maxSize) + "PMW";
    }

    public static String generateSonicIDPhoneWord(String checksumPublicKey, String phoneNumber, String word) {
        return generateSonicID(checksumPublicKey, CheckSum.checksum512TextStatic(phoneNumber), CheckSum.checksum512TextStatic(word), null, null, maxSize) + "PW0";
    }

    public static String generateSonicIDPhoneEmail(String checksumPublicKey, String phoneNumber, String email) {
        return generateSonicID(checksumPublicKey, CheckSum.checksum512TextStatic(phoneNumber), CheckSum.checksum512TextStatic(email), null, null, maxSize) + "PE0";
    }

    public static String generateSonicIDPhoneMacEmail(String checksumPublicKey, String phoneNumber, String mac, String email) {
        return generateSonicID(checksumPublicKey, CheckSum.checksum512TextStatic(phoneNumber), CheckSum.checksum512TextStatic(mac), CheckSum.checksum512TextStatic(email), null, maxSize) + "PME";
    }

    private static String generateSonicID(String checksum1, String checksum2, String checksum3, String checksum4, String checksum5, int digitSize) {

        String checksum = checksum1;

        if (checksum1 == null) {
            checksum1 = "";
        }

        if (checksum2 == null) {
            checksum2 = "";
        }

        if (checksum3 == null) {
            checksum3 = "";
        }

        if (checksum4 == null) {
            checksum4 = "";
        }
        if (checksum5 == null) {
            checksum5 = "";
        }

        String combineWord = checksum1.trim() + checksum2.trim() + checksum3.trim() + checksum4.trim() + checksum5.trim();
        checksum = CheckSum.checksum512TextStatic(combineWord);

        int index = 0;

        // Find how many characters that will represent 1 digit.
        int lenWork = checksum.length() / digitSize;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < digitSize; i++) {
            int sum = 0;
            for (int j = 0; j < lenWork; j++) {
                index = i * lenWork + j;
                sum = sum + checksum.charAt(index); // It the decimal value of the character
            }

            // Find average of the character
            int avg = sum / lenWork;

            if ((avg >= 48) && (avg <= 57)) {
                char c = (char) avg;
                sb.append(c);
            } else if ((avg > 57) && (avg < 65)) {
                char c = (char) (avg - 57 + 48);
                sb.append(c);
            } else if ((avg < 91) && (avg >= 65)) {
                char c = (char) avg;
                sb.append(c);
            } else if ((avg > 90)) {
                int v = (avg - 90 + 48);
                if (v > 57) {
                    v = (v - 57) + 48;
                }
                char c = (char) (v);
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static boolean isSonicIDValid(String sonicID, byte[] publicKeyByte, String phone, String mac, String vin, String word, String email) {

        CheckSum checkSum = new CheckSum();
        checkSum.processChecksumByBytes(publicKeyByte);
        String publicKeyCheckSum = checkSum.getSHA512_noSpace();
        return isSonicIDValid(sonicID, publicKeyCheckSum, phone, mac, vin, word, email);
    }

    public static boolean isSonicIDValid(String sonicID, String publicKeyCheckSum, String phone, String mac, String vin, String word, String email) {

        if (sonicID == null) {
            return false;
        }

        int len = sonicID.length();
        if (len < 10) {
            return false;
        }

        String type = sonicID.substring(sonicID.length() - 3);

        String thisSonicID = "";

        if (type.equalsIgnoreCase("000")) {
            thisSonicID = generateSonicID(publicKeyCheckSum);
        } else if (type.equalsIgnoreCase("P00")) {
            thisSonicID = generateSonicIDPhoneNum(publicKeyCheckSum, phone);
        } else if (type.equalsIgnoreCase("M00")) {
            thisSonicID = generateSonicIDMacAddr(publicKeyCheckSum, mac);
        } else if (type.equalsIgnoreCase("V00")) {
            // thisSonicID = generateSonicIDVinNum(publicKeyCheckSum, vin);
        } else if (type.equalsIgnoreCase("W00")) {
            thisSonicID = generateSonicIDWord(publicKeyCheckSum, word);
        } else if (type.equalsIgnoreCase("PM0")) {
            thisSonicID = generateSonicIDPhoneMac(publicKeyCheckSum, phone, mac);
        } else if (type.equalsIgnoreCase("PMV")) {
            //  thisSonicID = generateSonicIDPhoneMacVin(publicKeyCheckSum, phone, mac, vin);
        } else if (type.equalsIgnoreCase("PW0")) {
            thisSonicID = generateSonicIDPhoneWord(publicKeyCheckSum, phone, word);
        } else if (type.equalsIgnoreCase("PMW")) {
            thisSonicID = generateSonicIDPhoneMacWord(publicKeyCheckSum, phone, mac, word);
        } else if (type.equalsIgnoreCase("E00")) {
            thisSonicID = generateSonicIDEmail(publicKeyCheckSum, email);
        } else if (type.equalsIgnoreCase("PE0")) {
            thisSonicID = generateSonicIDPhoneEmail(publicKeyCheckSum, phone, email);
        } else if (type.equalsIgnoreCase("PME")) {
            thisSonicID = generateSonicIDPhoneMacEmail(publicKeyCheckSum, phone, mac, email);
        }

        return sonicID.equalsIgnoreCase(thisSonicID);
    }
}
