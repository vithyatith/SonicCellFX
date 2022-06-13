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

import com.google.gson.internal.LinkedTreeMap;

import java.util.Map;

/**
 *
 * @author vithya
 */
public class HashCodeExpireMap {
    private LinkedTreeMap<Long, Long> hashCodeHistory = new LinkedTreeMap<>();
    public HashCodeExpireMap() {

    }
    public void setHashCodeMapData(LinkedTreeMap<Long, Long> hashCodeHistory) {
       this.hashCodeHistory = hashCodeHistory;
    }

    public LinkedTreeMap<Long, Long> getHashCodeMapData() {
        return hashCodeHistory;
    }

    public boolean hasTimeExpiredOrNewData(String words, int seconds) {

        // Get the current time to setup the time expire
        long currenTime = System.currentTimeMillis();

        // Put the hashcode
        long currentWordHashCode = words.hashCode();
        if (!hashCodeHistory.containsKey(currentWordHashCode)) {
            hashCodeHistory.put(currentWordHashCode, currenTime);
            return true;
        }

        for (Map.Entry<Long, Long> entry : hashCodeHistory.entrySet()) {

            long thisHashCode = entry.getKey();

            if (currentWordHashCode == thisHashCode) {
                long thisHashCodeTime = entry.getValue();
                long diffTimeInSec = (System.currentTimeMillis() - thisHashCodeTime) / 1000;
                if (diffTimeInSec < seconds) {
                    return false;
                } else {
                    hashCodeHistory.put(thisHashCode, currenTime);
                    return true;
                }
            }
        }

        return false;
    }
/*
    public static void main(String args[]) {

        TimerHistoryStringHashCodeExpireMap timerHashCode = new TimerHistoryStringHashCodeExpireMap();
        String words = "cat";
        int seconds = 4;
        boolean bool = timerHashCode.hasTimeExpiredOrNewData(words, seconds);
        System.out.println("b 0 " + bool);
        try {
            Thread.sleep(3000);
        } catch (Exception e) {

        }
        bool = timerHashCode.hasTimeExpiredOrNewData(words, seconds);
        System.out.println("b 1 " + bool);

        try {
            Thread.sleep(3000);
        } catch (Exception e) {

        }

        bool = timerHashCode.hasTimeExpiredOrNewData(words, seconds);
        System.out.println("b 2 " + bool);

    }
*/
}
