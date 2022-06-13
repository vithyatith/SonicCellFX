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
package component.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author vithya
 */
public class DataArrayPool {

    private int maxPool = 20;
    private int MAXIMUM_POOL = 50;
    private List<float[]> dataArrayListPool = null;
    private int poolTrack = maxPool;

    private int poolResizeCount = 0;
    private int bufferSize = 1024;
    private int currentTrack = 0;

    public DataArrayPool() {

        maxPool = 50;
        this.bufferSize = 1024;
        resizePool(maxPool, bufferSize);
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public DataArrayPool(int bufferSize) {

        maxPool = 50;
        this.bufferSize = bufferSize;
        resizePool(maxPool, bufferSize);
    }

    public DataArrayPool(int sizePool, int bufferSize) {
        maxPool = sizePool;
        this.bufferSize = bufferSize;
        resizePool(maxPool, bufferSize);
    }

    public synchronized void resizePool(int sizePool, int sizeBuffer) {
        this.bufferSize = sizeBuffer;

        maxPool = sizePool;
        if (dataArrayListPool != null) {
            for (int i = 0; i < dataArrayListPool.size(); i++) {
                try {
                    float f[] = dataArrayListPool.get(i);
                    f = null;
                } catch (IndexOutOfBoundsException e) {

                }
            }

            dataArrayListPool.clear();
        } else {
            //dataArrayListPool = new ArrayList<float[]>(sizePool);

            dataArrayListPool = Collections.synchronizedList(new ArrayList<float[]>(sizePool));

        }

        for (int i = 0; i < sizePool; i++) {
            float d[] = new float[sizeBuffer];
            dataArrayListPool.add(d);
        }

    }

    public float[] copyDataPool(float data[]) {
        float dataPool[] = getSonicCryptLibPool();
        int len1 = dataPool.length;
        int len2 = data.length;
        if (len1 == len2) {
            for (int i = 0; i < len1; i++) {
                dataPool[i] = data[i];
            }
            return dataPool;
        }
        return dataPool;
    }

//    public float[] getCurrentData() {
//        if (currentTrack >= maxPool) {
//            currentTrack = 0;
//        }
//        float d[] = dataArrayListPool.get(currentTrack);
//        currentTrack++;
//        return d;
//
//    }
    public float[] getSonicCryptLibPool() {
        int len = dataArrayListPool.size();
//        if (poolResizeCount >= maxPool) {
//            maxPool = maxPool + MAXIMUM_POOL;
//            resizePool(maxPool, bufferSize);
//            poolResizeCount = 0;
//            currentTrack = 0;
//        }

        if (poolResizeCount >= maxPool) {
            // System.out.println("inside...........getSonicCryptLibPool");
            maxPool = maxPool + MAXIMUM_POOL;
            resizePool(maxPool, bufferSize);
            poolResizeCount = 0;
            currentTrack = 0;
        }

        if (poolTrack >= len) {
            poolTrack = 0;
        }
        // System.out.println("len = " + len + "   poolTrack = " + poolTrack + "  poolResizeCount = " + poolResizeCount);
        float hm[] = dataArrayListPool.get(poolTrack);
        poolTrack++;
        poolIncrement();
        return hm;
    }

    public void resetPool() {
        currentTrack = 0;
        poolTrack = 0;
        poolResizeCount = 0;
        bufferSize = 1024;
    }

    public void poolIncrement() {
        poolResizeCount++;
    }

    public void poolDecrement() {
        if (poolResizeCount > 0) {
            poolResizeCount--;
        }
    }
}
