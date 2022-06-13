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

/**
 *
 * @author vithya
 */
public class TimerDataExpireUtil {

    private long timeToExpireMillisecond=10000;

    private String previousMessage = "";
    private long previousTime = Long.MIN_VALUE;

    public TimerDataExpireUtil(){

    }
    public TimerDataExpireUtil(long _timeToExpireMillisecond){
        timeToExpireMillisecond = _timeToExpireMillisecond;
    }

    public void setTimeToExpire(long millisecond){
        timeToExpireMillisecond = millisecond;
    }

    public boolean hasTimeExpiredOrNewData(String decodedMessage){

        // Get the current time to setup the time expire
        long currenTime = System.currentTimeMillis();
        boolean timeRequirementBool = false;

        // Not equal then execute
        if(!previousMessage.equalsIgnoreCase(decodedMessage)){
            timeRequirementBool = true;
        }else{
            // Now its the previous
            long diff = currenTime - previousTime;
            // If older than 10 second than time expire and set it to true
            if(diff>=timeToExpireMillisecond){
                timeRequirementBool = true;
            }
        }

        // Set the current time to previous time
        previousMessage = decodedMessage;
        previousTime = currenTime;

        return timeRequirementBool;
    }
}
