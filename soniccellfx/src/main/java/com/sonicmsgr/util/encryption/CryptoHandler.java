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
package com.sonicmsgr.util.encryption;

/**
 *
 * @author yada
 */
//import com.sonicmsgr.apache.commons.codec.binary.Base64;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.tomcat.util.codec.binary.Base64;

//import android.util.Base64;


public class CryptoHandler {

    String SecretKey = "12345678901234567890123456789012";
    String IV = "12345678901234567890123456789012";

    private static CryptoHandler instance = null;

    public static CryptoHandler getInstance() {

        if (instance == null) {
            instance = new CryptoHandler();
        }
        return instance;
    }

    public String encrypt(String message) throws NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException,
            UnsupportedEncodingException, InvalidAlgorithmParameterException {

        byte[] srcBuff = message.getBytes(StandardCharsets.UTF_8);
        //here using substring because AES takes only 16 or 24 or 32 byte of key 
        SecretKeySpec skeySpec = new 
        SecretKeySpec(SecretKey.substring(0,32).getBytes(), "AES");
        IvParameterSpec ivSpec = new 
        IvParameterSpec(IV.substring(0,16).getBytes());
        Cipher ecipher = Cipher.getInstance("AES");
        ecipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
        byte[] dstBuff = ecipher.doFinal(srcBuff);
        String base64 = Base64.encodeBase64String(dstBuff);
       // Base64.encodeBase64String(srcBuff)
        return base64;
    }

    public String decrypt(String encrypted) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException {

        SecretKeySpec skeySpec = new SecretKeySpec(SecretKey.substring(0,32).getBytes(), "AES");
        IvParameterSpec ivSpec = new 
        IvParameterSpec(IV.substring(0,16).getBytes());
        Cipher ecipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        ecipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
        byte[] raw = Base64.decodeBase64(encrypted);
       // Base64.decodeBase64(raw)
        byte[] originalBytes = ecipher.doFinal(raw);
        String original = new String(originalBytes, StandardCharsets.UTF_8);
        return original;
    }
    
//    public static void main(String args[]){
//        
//        try {
//            String dec = CryptoHandler.getInstance().encrypt("gABhCzhq61hPXyXfwMIeAojF+K4vUdJtS77W78UqY3+r3CuDQvOE6t7o4u/b6JXJIB2kVk8KR0TdiLd6axhNZg==");
//       
//            System.out.println("dec = "+dec);
//        
//        } catch (NoSuchAlgorithmException ex) {
//            Logger.getLogger(CryptoHandler.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (NoSuchPaddingException ex) {
//            Logger.getLogger(CryptoHandler.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvalidKeyException ex) {
//            Logger.getLogger(CryptoHandler.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvalidAlgorithmParameterException ex) {
//            Logger.getLogger(CryptoHandler.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalBlockSizeException ex) {
//            Logger.getLogger(CryptoHandler.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (BadPaddingException ex) {
//            Logger.getLogger(CryptoHandler.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(CryptoHandler.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//    }
}