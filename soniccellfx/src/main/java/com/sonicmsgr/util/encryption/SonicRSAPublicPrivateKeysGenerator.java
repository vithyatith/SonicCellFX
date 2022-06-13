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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

public class SonicRSAPublicPrivateKeysGenerator {

    private KeyPairGenerator keyGen;
    private KeyPair pair;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private final int keySizeDefault = 1024;

    public SonicRSAPublicPrivateKeysGenerator(int keyLength) throws NoSuchAlgorithmException, NoSuchProviderException {
        init(keyLength,null);
    }
    public SonicRSAPublicPrivateKeysGenerator(int keyLength, String seed) throws NoSuchAlgorithmException, NoSuchProviderException {

        init(keyLength,seed);
    }
    public SonicRSAPublicPrivateKeysGenerator() throws NoSuchAlgorithmException, NoSuchProviderException {
        init(keySizeDefault,null);
    }
    public SonicRSAPublicPrivateKeysGenerator(String seed) throws NoSuchAlgorithmException, NoSuchProviderException {
        init(keySizeDefault,seed);
    }

    private void init(int keyLength, String seed) throws NoSuchAlgorithmException, NoSuchProviderException {
        this.keyGen = KeyPairGenerator.getInstance("RSA");
        if (seed != null) {
            SecureRandom rand = new SecureRandom();
            rand.setSeed(seed.getBytes());
            this.keyGen.initialize(keyLength, rand);

        }else{
            this.keyGen.initialize(keyLength);
        }
    }

    public void createKeys() {
        this.pair = this.keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public void writeToFile(String path, byte[] key) throws IOException {
        File f = new File(path);
        f.getParentFile().mkdirs();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();
    }

    public void createKeys(String path, String filename) {
        try {
            createKeys();
            writeToFile(path + File.separator + filename + "_publicKey", getPublicKey().getEncoded());
            writeToFile(path + File.separator + filename + "_privateKey", getPrivateKey().getEncoded());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
