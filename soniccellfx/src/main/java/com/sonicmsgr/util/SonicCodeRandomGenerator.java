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

import java.security.SecureRandom;
import java.util.Date;

/**
 *
 * @author vithya
 */
public class SonicCodeRandomGenerator {

    private static final String[] characters = {"a", "b", "c", "d", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private static final int MAX_LEN = characters.length;
    private static final SecureRandom rand = new SecureRandom();
    private static int generateRandomInteger(int min, int max) {
        rand.setSeed(new Date().getTime());
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
    
    public static String getRandomSonicCode(int len){
       StringBuffer sb = new StringBuffer();
       for(int i=0; i<len; i++){
           int index = generateRandomInteger(0,MAX_LEN-1);
           sb.append(characters[index]);
       }
       return sb.toString();
    }
}
