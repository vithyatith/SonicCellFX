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
public class TimerHistoryStringHashCodeExpireUtil1 {
    private long timeToExpireMillisecond=10000;

    private LinkedTreeMap<Long, Long> detectedHashCodeHistory = new LinkedTreeMap<>();

    public TimerHistoryStringHashCodeExpireUtil1(){

    }
    public TimerHistoryStringHashCodeExpireUtil1(long _timeToExpireMillisecond){
        timeToExpireMillisecond = _timeToExpireMillisecond;
    }

    public void setTimeToExpire(long millisecond){
        timeToExpireMillisecond = millisecond;
        
        // Clear this everytime we set the time.
        detectedHashCodeHistory.clear();
        
    }
    
    public long getTimeToExpireMilliSec(){
        return timeToExpireMillisecond;
    }
    
    public void setHashCodeMapData(LinkedTreeMap<Long, Long> _detectedHashCodeHistory){
        detectedHashCodeHistory = _detectedHashCodeHistory;
    }
    public LinkedTreeMap<Long, Long> getHashCodeMapData(){
        return detectedHashCodeHistory;
    }
    public boolean hasTimeExpiredOrNewData(String words){

                // Get the current time to setup the time expire
        long currenTime = System.currentTimeMillis();
        
        // Put the hashcode
        long currentWordHashCode = words.hashCode();
        if(!detectedHashCodeHistory.containsKey(currentWordHashCode)){
            detectedHashCodeHistory.put(currentWordHashCode, currenTime);
            return true;
        }
       
        for (Map.Entry<Long, Long> entry : detectedHashCodeHistory.entrySet()) {
                        
            long thisHashCode = entry.getKey();
                        
            if(currentWordHashCode==thisHashCode){
                long thisHashCodeTime = entry.getValue();
                long diffTimeInMilliSec = (System.currentTimeMillis() - thisHashCodeTime);
                if(diffTimeInMilliSec<timeToExpireMillisecond){
                    return false;
                }else{
                    detectedHashCodeHistory.put(thisHashCode, currenTime);
                    return true;
                }
            }
        }

        return false;
    }
}
