package com.sonicmsgr.util.encryption;

/*
 * MIT License
 *
 * Copyright (c) 2017 Kavin Varnan

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import com.sonicmsgr.util.Log;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Map;
import org.apache.tomcat.util.codec.binary.Base64;

public class SonicCryptLib {

    private boolean useAndroidLibrary = false;
    private String key = "cdfasq$#90";
    private String iv = "!0fjm&%dbgeh";
    private final boolean isValid = false;

    private enum EncryptMode {
        ENCRYPT, DECRYPT
    }

    // cipher to be used for encryption and decryption
    private Cipher _cx;

    // encryption key and initialization vector
    byte[] _key, _iv;

    public SonicCryptLib(String mkey, String miv) throws NoSuchAlgorithmException, NoSuchPaddingException { 
        process(mkey, miv);
    }

    public SonicCryptLib(String password) throws NoSuchAlgorithmException, NoSuchPaddingException {
        if ((password == null) || (password.trim().equals(""))) {
            return;
        }
        String mkey = password;
        String miv = mkey + "x!@*ccv";
        
        process(mkey, miv);
    }

    private void process(String mkey, String miv) throws NoSuchAlgorithmException, NoSuchPaddingException {
                
      //  System.out.println("Process SonicCrypib: key = "+mkey);
      //  System.out.println("Process SonicCrypib: iv = "+miv)        ;
        ClassLoader classLoader = SonicCryptLib.class.getClassLoader();
        try {
            Class aClass = classLoader.loadClass("android.os.Build");
            useAndroidLibrary = true;
        } catch (ClassNotFoundException e) {
            useAndroidLibrary = false;
        }

        init(useAndroidLibrary, mkey, miv);
    }

    private void init(boolean _useAndroidLibrary, String mkey, String miv) throws NoSuchAlgorithmException, NoSuchPaddingException {
        key = mkey;
        iv = miv;
        useAndroidLibrary = _useAndroidLibrary;

        // if(!useAndroidLibrary){
        fixKeyLength();
        //  }
        // initialize the cipher with transformation AES/CBC/PKCS5Padding
        _cx = Cipher.getInstance("AES/CBC/PKCS5Padding");
       // _cx = Cipher.getInstance("AES/PKCS5Padding");
        
        _key = new byte[32]; //256 bit key space
        _iv = new byte[16]; //128 bit IV
    }

    public SonicCryptLib(boolean _useAndroidLibrary, String mkey, String miv) throws NoSuchAlgorithmException, NoSuchPaddingException {
        init(_useAndroidLibrary, mkey, miv);
    }

    /**
     * Note: This function is no longer used. This function generates md5 hash
     * of the input string
     *
     * @param inputString
     * @return md5 hash of the input string
     */
    private final String md5(final String inputString) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(inputString.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     *
     * @param _inputText Text to be encrypted or decrypted
     * @param _encryptionKey Encryption key to used for encryption / decryption
     * @param _mode specify the mode encryption / decryption
     * @param _initVector Initialization vector
     * @return encrypted or decrypted string based on the mode
     * @throws UnsupportedEncodingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private byte[] encryptDecrypt(byte[] _inputText, String _encryptionKey,
                                  EncryptMode _mode, String _initVector) throws UnsupportedEncodingException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {

        String ss = new String(_inputText);

        String s = encryptDecrypt(ss, _encryptionKey, _mode, _initVector);

        return s.getBytes();
    }

    private String encryptDecrypt(String _inputText, String _encryptionKey,
            EncryptMode _mode, String _initVector) throws UnsupportedEncodingException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {
        String _out = "";

        int len = _encryptionKey.getBytes(StandardCharsets.UTF_8).length; // length of the key	provided

        if (_encryptionKey.getBytes(StandardCharsets.UTF_8).length > _key.length) {
            len = _key.length;
        }

        int ivlen = _initVector.getBytes(StandardCharsets.UTF_8).length;

        if (_initVector.getBytes(StandardCharsets.UTF_8).length > _iv.length) {
            ivlen = _iv.length;
        }
        
  
        System.arraycopy(_encryptionKey.getBytes(StandardCharsets.UTF_8), 0, _key, 0, len);
        System.arraycopy(_initVector.getBytes(StandardCharsets.UTF_8), 0, _iv, 0, ivlen);
        //KeyGenerator _keyGen = KeyGenerator.getInstance("AES");
        //_keyGen.init(128);

        SecretKeySpec keySpec = new SecretKeySpec(_key, "AES"); // Create a new SecretKeySpec
        // for the
        // specified key
        // data and
        // algorithm
        // name.

        IvParameterSpec ivSpec = new IvParameterSpec(_iv); // Create a new
        // IvParameterSpec
        // instance with the
        // bytes from the
        // specified buffer
        // iv used as
        // initialization
        // vector.

        // encryption
        if (_mode.equals(EncryptMode.ENCRYPT)) {
            // Potentially insecure random numbers on Android 4.3 and older.
            // Read
            // https://android-developers.blogspot.com/2013/08/some-securerandom-thoughts.html
            // for more info.
            _cx.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);// Initialize this cipher instance
            byte[] results = _cx.doFinal(_inputText.getBytes(StandardCharsets.UTF_8)); // Finish
            // multi-part
            // transformation
            // (encryption)

            if (useAndroidLibrary) {
                try {
                    Class<?> c = Class.forName("android.util.Base64");

                    Class[] argTypes = new Class[]{byte[].class, int.class};

                    Method encodeToString_method = c.getDeclaredMethod("encodeToString", argTypes);
                    System.out.format("invoking %s.main()%n", c.getName());
                    _out = (String) encodeToString_method.invoke(null, results, 0);

                } catch (Exception e) {

                }
            } else {
                _out = Base64.encodeBase64String(results);
            }
            // output
        }

        // decryption
        if (_mode.equals(EncryptMode.DECRYPT)) {
            _cx.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);// Initialize this ipher instance

            byte[] decodedValue = null;

            if (useAndroidLibrary) {

                try {
                    Class<?> c = Class.forName("android.util.Base64");

                    Class[] argTypes = new Class[]{byte[].class, int.class};

                    Method decode_method = c.getDeclaredMethod("decode", argTypes);
                    decodedValue = (byte[]) decode_method.invoke(null, _inputText.getBytes(), 0);

                } catch (Exception e) {
//                    Log.v("VT", "here2....." + e.getMessage());
                }

                //      decodedValue = android.util.Base64.decode(_inputText.getBytes(),
                //              android.util.Base64.DEFAULT);
            } else {
                decodedValue = Base64.decodeBase64(_inputText.getBytes());
            }
            try {

                byte[] decryptedVal = _cx.doFinal(decodedValue); // Finish
                // multi-part
                // transformation
                // (decryption)
                _out = new String(decryptedVal);
            } catch (javax.crypto.BadPaddingException e) {
                Log.v("VT,", "Error = SonicCryptLib when trying to decrypt = " + e.getMessage());
            }
        }
        return _out; // return encrypted/decrypted string
    }

    /**
     * *
     * This function computes the SHA256 hash of input string
     *
     * @param text input text whose SHA256 hash has to be computed
     * @param length length of the text to be returned
     * @return returns SHA256 hash of input text
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private String SHA256(String text, int length) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        
        String resultStr;
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        md.update(text.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();

        StringBuffer result = new StringBuffer();
        for (byte b : digest) {
            result.append(String.format("%02x", b)); //convert to hex
        }

        if (length > result.toString().length()) {
            resultStr = result.toString();
        } else {
            resultStr = result.toString().substring(0, length);
        }

        return resultStr;

    }

    /**
     * *
     * This function encrypts the plain text to cipher text using the key
     * provided. You'll have to use the same key for decryption
     *
     * @param _plainText Plain text to be encrypted
     * @param _key Encryption Key. You'll have to use the same key for
     * decryption
     * @param _iv initialization Vector
     * @return returns encrypted (cipher) text
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private String encrypt(String _plainText, String _key, String _iv)
            throws InvalidKeyException, UnsupportedEncodingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException {
        return encryptDecrypt(_plainText, _key, EncryptMode.ENCRYPT, _iv);
    }

    public String encrypt(String _plainText)
            throws InvalidKeyException, UnsupportedEncodingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException, NoSuchAlgorithmException {
        _plainText = _plainText.trim();
      
        return encryptDecrypt(_plainText, SHA256(key, 32), EncryptMode.ENCRYPT, iv);
    }

    /**
     * *
     * This funtion decrypts the encrypted text to plain text using the key
     * provided. You'll have to use the same key which you used during
     * encryprtion
     *
     * @param _encryptedText Encrypted/Cipher text to be decrypted
     * @param _key Encryption key which you used during encryption
     * @param _iv initialization Vector
     * @return encrypted value
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    private String decrypt(String _encryptedText, String _key, String _iv)
            throws InvalidKeyException, UnsupportedEncodingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException {
        return encryptDecrypt(_encryptedText, _key, EncryptMode.DECRYPT, _iv);
    }

    public byte[] decrypt(byte[] encrypt) throws InvalidKeyException, UnsupportedEncodingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException, NoSuchAlgorithmException {

        String s = new String(encrypt);
        if(s!=null){
            String r = decrypt(s);
            if(r!=null){
                return r.getBytes();
            }
        }

        return null;
    }

    public String decrypt(String _encryptedText)
            throws InvalidKeyException, UnsupportedEncodingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException, NoSuchAlgorithmException {
        _encryptedText = _encryptedText.trim();
        return encryptDecrypt(_encryptedText, SHA256(key, 32), EncryptMode.DECRYPT, iv);
    }

    /**
     * this function generates random string for given length
     *
     * @param length Desired length
     * * @return
     */
    private String generateRandomIV(int length) {
        SecureRandom ranGen = new SecureRandom();
        byte[] aesKey = new byte[16];
        ranGen.nextBytes(aesKey);
        StringBuffer result = new StringBuffer();
        for (byte b : aesKey) {
            result.append(String.format("%02x", b)); //convert to hex
        }
        if (length > result.toString().length()) {
            return result.toString();
        } else {
            return result.toString().substring(0, length);
        }
    }

    private void fixKeyLength() {
        String errorString = "Failed manually overriding key-length permissions.";
        int newMaxKeyLength;
        try {
            if ((newMaxKeyLength = Cipher.getMaxAllowedKeyLength("AES")) < 256) {
                Class c = Class.forName("javax.crypto.CryptoAllPermissionCollection");
                Constructor con = c.getDeclaredConstructor();
                con.setAccessible(true);
                Object allPermissionCollection = con.newInstance();
                Field f = c.getDeclaredField("all_allowed");
                f.setAccessible(true);
                f.setBoolean(allPermissionCollection, true);

                c = Class.forName("javax.crypto.CryptoPermissions");
                con = c.getDeclaredConstructor();
                con.setAccessible(true);
                Object allPermissions = con.newInstance();
                f = c.getDeclaredField("perms");
                f.setAccessible(true);
                ((Map) f.get(allPermissions)).put("*", allPermissionCollection);

                c = Class.forName("javax.crypto.JceSecurityManager");
                f = c.getDeclaredField("defaultPolicy");
                f.setAccessible(true);
                Field mf = Field.class.getDeclaredField("modifiers");
                mf.setAccessible(true);
                mf.setInt(f, f.getModifiers() & ~Modifier.FINAL);
                f.set(null, allPermissions);

                newMaxKeyLength = Cipher.getMaxAllowedKeyLength("AES");
            }
        } catch (Exception e) {
            throw new RuntimeException(errorString, e);
        }
        if (newMaxKeyLength < 256) {
            throw new RuntimeException(errorString); // hack failed
        }
    }
    
//    public static void main(String args[]){
//        
//        try {
//            String hexKey = "2034F6E32958647FDFF75D265B455EBF40C80E6D597092B3A802B3E5863F878C";
//            String plainText = "Thank you Mr Warrender, Reinforcements have arrived! Send biscuits";
//            String hexIV = "010932650CDD998833CC0AFF9AFF00FF";
//            
//            SonicCryptLib sonicCryptLib = new SonicCryptLib(hexKey, hexIV);
//            String en = sonicCryptLib.encrypt(plainText);
//            System.out.println(en);
//        } catch (NoSuchAlgorithmException ex) {
//            Logger.getLogger(SonicCryptLib.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (NoSuchPaddingException ex) {
//            Logger.getLogger(SonicCryptLib.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvalidKeyException ex) {
//            Logger.getLogger(SonicCryptLib.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(SonicCryptLib.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvalidAlgorithmParameterException ex) {
//            Logger.getLogger(SonicCryptLib.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalBlockSizeException ex) {
//            Logger.getLogger(SonicCryptLib.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (BadPaddingException ex) {
//            Logger.getLogger(SonicCryptLib.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

}
