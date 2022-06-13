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


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author yada
 */
public class FileCrypto {

//    public static void main(String args[]) {
//
//         encrypt("/Users/yada/charlotte_biking.jpg","/Users/yada/charlotte_biking.jpg.des2","1234567890");
//        decrypt("/Users/yada/charlotte_biking.jpg.des2","/Users/yada/out22.jpg","1234567890");
//    }

    public static boolean encrypt(String inputFile, String ouputFile, String password) {

        FileInputStream inFile = null;
        try {
            inFile = new FileInputStream(inputFile);
            FileOutputStream outFile = new FileOutputStream(ouputFile);
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
            SecretKeyFactory secretKeyFactory;

            secretKeyFactory = SecretKeyFactory
                    .getInstance("PBEWithMD5AndTripleDES");

            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);
            byte[] salt = new byte[8];
            Random random = new Random();
            random.nextBytes(salt);
            PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 100);
            Cipher cipher = Cipher.getInstance("PBEWithMD5AndTripleDES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, pbeParameterSpec);
            outFile.write(salt);
            byte[] input = new byte[64];
            int bytesRead;
            while ((bytesRead = inFile.read(input)) != -1) {
                byte[] output = cipher.update(input, 0, bytesRead);
                if (output != null) {
                    outFile.write(output);
                }
            }
            byte[] output = cipher.doFinal();
            if (output != null) {
                outFile.write(output);
            }
            inFile.close();
            outFile.flush();
            outFile.close();
            return true;
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getLocalizedMessage());
        } catch (InvalidKeySpecException ex) {
            System.out.println(ex.getLocalizedMessage());
        } catch (NoSuchPaddingException ex) {
            System.out.println(ex.getLocalizedMessage());
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        } catch (InvalidKeyException ex) {
            System.out.println(ex.getLocalizedMessage());
        } catch (InvalidAlgorithmParameterException ex) {
            System.out.println(ex.getLocalizedMessage());
        } catch (IllegalBlockSizeException ex) {
            System.out.println(ex.getLocalizedMessage());
        } catch (BadPaddingException ex) {
            System.out.println(ex.getLocalizedMessage());
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getLocalizedMessage());
        } finally {
            try {
                inFile.close();
            } catch (IOException ex) {
             //   System.out.println(ex.getLocalizedMessage());
            }
        }
        return false;
    }

    public static boolean decrypt(String encryptedFile, String outputFile, String password) {

        try {
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
            SecretKeyFactory secretKeyFactory = SecretKeyFactory
                    .getInstance("PBEWithMD5AndTripleDES");
            SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);

            FileInputStream fis = new FileInputStream(encryptedFile);
            byte[] salt = new byte[8];
            fis.read(salt);

            PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 100);

            Cipher cipher = Cipher.getInstance("PBEWithMD5AndTripleDES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, pbeParameterSpec);
            FileOutputStream fos = new FileOutputStream(outputFile);
            byte[] in = new byte[64];
            int read;
            while ((read = fis.read(in)) != -1) {
                byte[] output = cipher.update(in, 0, read);
                if (output != null) {
                    fos.write(output);
                }
            }

            byte[] output = cipher.doFinal();
            if (output != null) {
                fos.write(output);
            }

            fis.close();
            fos.flush();
            fos.close();
            return true;
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getLocalizedMessage());
        } catch (InvalidKeySpecException ex) {
            System.out.println(ex.getLocalizedMessage());
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getLocalizedMessage());
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        } catch (NoSuchPaddingException ex) {
            System.out.println(ex.getLocalizedMessage());
        } catch (InvalidKeyException ex) {
            System.out.println(ex.getLocalizedMessage());
        } catch (InvalidAlgorithmParameterException ex) {
            System.out.println(ex.getLocalizedMessage());
        } catch (IllegalBlockSizeException ex) {
            System.out.println(ex.getLocalizedMessage());
        } catch (BadPaddingException ex) {
            System.out.println(ex.getLocalizedMessage());
        }

        return false;
    }
}
