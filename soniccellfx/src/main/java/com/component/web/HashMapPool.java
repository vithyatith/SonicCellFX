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
package com.component.web;
import java.util.LinkedHashMap;
/**
 *
 * @author vithya
 */
public class HashMapPool {
    private int maxPool = 25;
    private int increamentPool = 10;
    private LinkedHashMap gpsHashPool[] = null;
    private int poolTrack=maxPool;
    
    private int maxIncrement = 4;
    private int incrementTrack = maxIncrement ;
    
    public HashMapPool(){
        maxPool = 25;
    }
    
    public HashMapPool(int _maxPool){
        maxPool = _maxPool;
    }
    
    private void initPool(int size){
        maxPool = size;
        gpsHashPool = new LinkedHashMap[size];
        for(int i=0; i<size; i++){
            gpsHashPool[i] = new LinkedHashMap();
        }
    }
    public LinkedHashMap getHashMapPool(){
         if((poolTrack>=maxPool)||(gpsHashPool==null)){

             poolTrack = 0;
             if(maxPool<512){
                incrementTrack++;
                if(incrementTrack>maxIncrement){
                   initPool(maxPool + increamentPool);
                   incrementTrack = 0;
                }
             }
         }
         
         
        LinkedHashMap hm =  gpsHashPool[poolTrack];
        poolTrack++;
        return hm;
    }
    public void resetPool(){
         poolTrack = 0;
    }
}
