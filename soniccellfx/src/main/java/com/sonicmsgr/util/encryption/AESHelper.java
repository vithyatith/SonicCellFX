/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sonicmsgr.util.encryption;

//import com.sonicmsgr.apache.commons.codec.binary.Base64;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.util.encoders.Base64;
//import org.apache.tomcat.util.codec.binary.Base64;
//import org.bouncycastle.util.encoders.Base64;

/**
 *
 * @author yada
 */
public class AESHelper {

    private static final String TAG = "AESHelper";

    public static byte[] encrypt(byte[] data, String initVector, String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            SecretKeySpec k = new SecretKeySpec(Base64.decode(key), "AES");
            c.init(Cipher.ENCRYPT_MODE, k, iv);
            return c.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] decrypt(byte[] data, String initVector, String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            SecretKeySpec k = new SecretKeySpec(Base64.decode(key), "AES");
            c.init(Cipher.DECRYPT_MODE, k, iv);
            return c.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
//    public static void main(String args[]){
//        
//
//    String SecretKey = "Ax!&)s*?L#4LK)?!123";
//    String IV = "M@tc3ll@D@d@Y@y@";
//    String data = "vt";
//
//    byte b[] = AESHelper.encrypt(data.getBytes(),IV,SecretKey);
//    System.out.println(b);
//
//    }

//    public static String keyGenerator() throws NoSuchAlgorithmException {
//        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//        keyGenerator.init(192);
//
//        return Base64.encodeToString(keyGenerator.generateKey().getEncoded());
//    }
}