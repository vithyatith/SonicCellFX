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

import com.sonicmsgr.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.tomcat.util.codec.binary.Base64;

public class SonicRSACrypto {

    private final Cipher cipher;

    private boolean useAndroidLibrary = false;

    public SonicRSACrypto() throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.cipher = Cipher.getInstance("RSA/ECB/NoPadding");

        ClassLoader classLoader = SonicRSACrypto.class.getClassLoader();
        try {
            Class aClass = classLoader.loadClass("android.os.Build");
            useAndroidLibrary = true;

        } catch (ClassNotFoundException e) {
            useAndroidLibrary = false;
        }
    }

    // https://docs.oracle.com/javase/8/docs/api/java/security/spec/PKCS8EncodedKeySpec.html
    public PrivateKey getPrivate(String filename) throws Exception {
        RandomAccessFile f = new RandomAccessFile(filename, "r");
        byte[] keyBytes = new byte[(int) f.length()];
        f.readFully(keyBytes);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        return kf.generatePrivate(spec);
    }

    public PrivateKey getPrivate(byte[] keyBytes) throws Exception {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        return kf.generatePrivate(spec);
    }

    // https://docs.oracle.com/javase/8/docs/api/java/security/spec/X509EncodedKeySpec.html
    public PublicKey getPublic(String filename) throws Exception {
        //byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        RandomAccessFile f = new RandomAccessFile(filename, "r");
        byte[] keyBytes = new byte[(int) f.length()];
        f.readFully(keyBytes);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    // https://docs.oracle.com/javase/8/docs/api/java/security/spec/X509EncodedKeySpec.html
    public PublicKey getPublic(byte[] keyBytes) throws Exception {
        //byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public void encryptFile(byte[] input, File output, PublicKey key)
            throws IOException, GeneralSecurityException {
        this.cipher.init(Cipher.ENCRYPT_MODE, key);
        writeToFile(output, this.cipher.doFinal(input));
    }

    public void decryptFile(byte[] input, File output, PrivateKey key)
            throws IOException, GeneralSecurityException {
        this.cipher.init(Cipher.DECRYPT_MODE, key);
        writeToFile(output, this.cipher.doFinal(input));
    }

    private void writeToFile(File output, byte[] toWrite)
            throws IllegalBlockSizeException, BadPaddingException, IOException {
        FileOutputStream fos = new FileOutputStream(output);
        fos.write(toWrite);
        fos.flush();
        fos.close();
    }

    public String encryptText(String msg, byte[] publicKeyByte) {
        try {
            PublicKey publicKey = this.getPublic(publicKeyByte);
            return encryptText(msg, publicKey);
        } catch (javax.crypto.BadPaddingException e) {
            return null;
        } catch (Exception ex) {
            Log.v("VT", "Error in SonicRSACrypto = " + ex.getMessage());
        }
        return null;
    }

    public String encryptText(String msg, String publicKeyFilename) {
        try {
            PublicKey publicKey = this.getPublic(publicKeyFilename);
            return encryptText(msg, publicKey);
        } catch (Exception ex) {
            Log.v("VT", "Error in SonicRSACrypto = " + ex.getMessage());
        }
        return null;
    }

    public String encryptText(String msg, PublicKey key)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            UnsupportedEncodingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException {
        this.cipher.init(Cipher.ENCRYPT_MODE, key);
        try {

            byte[] results = cipher.doFinal(msg.getBytes(StandardCharsets.UTF_8));

            if (useAndroidLibrary) {
                try {
                    Class<?> c = Class.forName("android.util.Base64");

                    Class[] argTypes = new Class[]{byte[].class, int.class};

                    Method encodeToString_method = c.getDeclaredMethod("encodeToString", argTypes);
                    int CRLF = 4;
                    int DEFAULT = 0;
                    int NO_CLOSE = 16;
                    int NO_PADDING = 1;
                    int NO_WRAP = 2;
                    int URL_SAFE = 8;
                    return (String) encodeToString_method.invoke(null, results, DEFAULT);

                } catch (Exception e) {
                }

            } else {
                return Base64.encodeBase64String(results);
            }
        } catch (Exception e) {
            Log.v("VT", "Error at encryptText = " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public String decryptText(String msg, byte[] privateKeyByte) {
        try {
            String decrypt = decryptText(msg, this.getPrivate(privateKeyByte));

            if (decrypt != null) {
                return decrypt.trim();
            } else {
                return decrypt;
            }
        } catch (javax.crypto.BadPaddingException e) {
            // e.printStackTrace();
            Log.v("VT", " decryptText 33 =  " + e.getMessage());

        } catch (Exception ex) {
            Log.v("VT", " decryptText 55 =  " + ex.getMessage());
            //Logger.getLogger(SonicRSACrypto.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String decryptText(String msg, String privateKeyFilename) {

        try {
            return decryptText(msg, this.getPrivate(privateKeyFilename));
        } catch (javax.crypto.BadPaddingException e) {
            Log.v("VT", "decryptText 44 Error in " + e.getMessage());
        } catch (Exception ex) {
            Log.v("VT", "decryptText 11 Error in " + ex.getMessage());
            return "";
        }
        return null;
    }

    public String decryptText(String msg, PrivateKey key)
            throws InvalidKeyException, UnsupportedEncodingException,
            IllegalBlockSizeException, BadPaddingException {
        this.cipher.init(Cipher.DECRYPT_MODE, key);

        if (useAndroidLibrary) {

            try {
                Class<?> c = Class.forName("android.util.Base64");

                Class[] argTypes = new Class[]{byte[].class, int.class};

                Method decode_method = c.getDeclaredMethod("decode", argTypes);

                int CRLF = 4;
                int DEFAULT = 0;
                int NO_CLOSE = 16;
                int NO_PADDING = 1;
                int NO_WRAP = 2;
                int URL_SAFE = 8;
                byte[] decodedValue = (byte[]) decode_method.invoke(null, msg.getBytes(), DEFAULT);

                return new String(cipher.doFinal(decodedValue));

            } catch (Exception e) {
                Log.v("VT", "decryptText 22 Error in " + e.getMessage());
                return null;
            }

        } else {
            try {
                // java
                return new String(cipher.doFinal(Base64.decodeBase64(msg)), StandardCharsets.UTF_8);
            } catch (javax.crypto.BadPaddingException e) {
                Log.v("VT", "Badding exception e" + e.getMessage());
                return "";
            } catch (javax.crypto.IllegalBlockSizeException e) {
                Log.v("VT", "SonicRSACrypto 11 " + e.getMessage());
                return "";
            }
        }

    }

    public byte[] getFileInBytes(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        byte[] fbytes = new byte[(int) f.length()];
        fis.read(fbytes);
        fis.close();
        return fbytes;
    }
}
