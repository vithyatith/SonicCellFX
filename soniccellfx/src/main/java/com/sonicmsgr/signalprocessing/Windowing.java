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
package com.sonicmsgr.signalprocessing;

/**
 *
 * @author vithya
 */
public class Windowing {

    private final int windowing = 0;

    private final float[] windowingBuff = new float[2];

    public static final int WINDOW_NONE = 0;
    public static final int WINDOW_HANNING = 1;
    public static final int WINDOW_HANN = 2;
    public static final int WINDOW_HAMMING = 3;
    public static final int WINDOW_GAUSSIAN = 4;
    public static final int WINDOW_BARLETHANN = 5;
    public static final int WINDOW_BLACKMAN = 6;
    public static final int WINDOW_FLATTOP = 7;
    public static final int WINDOW_TRIANGLULAR = 8;
    public static final int WINDOW_HIGHPASS_10KHZ = 9;
    public static final int WINDOW_LANCZOS = 10;

    public static final int WINDOW_COSINE = 11;
    public static final int WINDOW_NUTTALL_FIRST_DEV = 12;

    public static int currentWindowing = WINDOW_NONE;

    public static String getName(int id) {
        String name = "Unknown";

        switch (id) {
            case 0:
                name = "None";
                break;
            case 1:
                name = "Hanning";
                break;
            case 2:
                name = "Hann";
                break;
            case 3:
                name = "Hamming";
                break;
            case 4:
                name = "Gaussian";
                break;
            case 5:
                name = "Barlethann";
                break;
            case 6:
                name = "Blackman";
                break;
            case 7:
                name = "Flattop";
                break;
            case 8:
                name = "Traingular";
                break;
            case 9:
                name = "HighPass 10kHz";
                break;

            case 10:
                name = "Lanczos";
                break;

            default:
                name = "None";
        }

        return name;
    }

    public static void computeWindowing(int id, int size, float[] windowingBuff) {

        if (windowingBuff.length != size) {
            windowingBuff = new float[size];
        }
        currentWindowing = id;
        // Log.v("VT","Updaing windowing................. id = "+id);
        switch (id) {
            case WINDOW_NONE:
                XCrossUtil.ones(windowingBuff);
                break;
            case WINDOW_HANNING:
                XCrossUtil.hanning(windowingBuff);
                break;
            case WINDOW_HANN:
                XCrossUtil.hann(windowingBuff);
                break;
            case WINDOW_HAMMING:
                XCrossUtil.hamming(windowingBuff);
                break;
            case WINDOW_GAUSSIAN:
                XCrossUtil.gaussianWindow(windowingBuff);
                break;
            case WINDOW_BARLETHANN:
                XCrossUtil.bartlettHann(windowingBuff);
                break;
            case WINDOW_BLACKMAN:
                XCrossUtil.blackman(windowingBuff);
                break;
            case WINDOW_FLATTOP:
                XCrossUtil.flatTop(windowingBuff);
                break;
            case WINDOW_TRIANGLULAR:
                XCrossUtil.triangular(windowingBuff);
                break;
            case WINDOW_COSINE:
                XCrossUtil.cosineWindow(windowingBuff);
                break;

            case WINDOW_NUTTALL_FIRST_DEV:
                XCrossUtil.nuttal_first_dev(windowingBuff);
                break;
            default:
                XCrossUtil.ones(windowingBuff);
                break;
        }
    }
}
