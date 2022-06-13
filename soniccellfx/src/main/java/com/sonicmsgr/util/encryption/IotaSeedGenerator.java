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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 *
 * @author yada
 */
public class IotaSeedGenerator {

    public static String seedGenerator() {
        // our secure randomness source
     String character = "9ABCDEFGHIJKLMNOPQRSTUVWXYZ";
     int seedlen = 81;
     SecureRandom sr;
        try {
            
            sr = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            // this should not happen!
            e.printStackTrace();
            return "";
        }

        // the resulting seed
        StringBuilder sb = new StringBuilder(seedlen);

        for (int i = 0; i < seedlen; i++) {
            int n = sr.nextInt(27);
            char c = character.charAt(n);

            sb.append(c);
        }
        return sb.toString();
    }
}
