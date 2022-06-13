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

import java.util.Arrays;

/**
 *
 * @author vithya
 */
public final class XCrossUtil {

    private static final float[] tmpMinMax = new float[2];

    private static long stopwatchValue;

    /**
     * Start a static stopwatch
     */
    public static void startStopWatch() {
        stopwatchValue = System.currentTimeMillis();
    }

    /**
     * Stop a static stopwatch and returns the long value.
     *
     * @return
     */
    public long stopStopWatch() {
        long result = System.currentTimeMillis() - stopwatchValue;
        return result;
    }

    /**
     * Interpolate when you have more data than pixels.
     *
     * @param srcData - source data
     * @param srcPos - typical is 0
     * @param srcEnd - typical is length of signal
     * @param des - destination data
     * @param desPos - the begin position to copy, typical is zero
     */
    private static void interpMoreDataThanPixelOrig(float[] srcData, int srcPos, int srcEnd, float[] des, int desPos) {
        int destLen = des.length;
        int srcLen = srcEnd - srcPos;
        float r = srcLen / (float) destLen;
        if (r == 0) {
            r = 1;
        }
        int startIndx = 0;
        int endIndx = 0;

        for (int i = srcPos; i < srcEnd; i++) {
            startIndx = (int) ((i - srcPos) * r + srcPos);
            endIndx = startIndx + (int) r;
            if (endIndx > srcEnd) {
                endIndx = srcEnd - 1;
            }

            int k = i - srcPos + desPos;
            if (k >= des.length) {
                break;
            }
            float maxValue = Float.NEGATIVE_INFINITY;
            float sum = 0;
            for (int j = startIndx; j < endIndx; j++) {
                sum = sum + srcData[j];
                if (srcData[j] > maxValue) {
                    maxValue = srcData[j];
                }
            }
            float avg = sum / r;
            des[k] = avg;
            // des[k] = maxValue;
        }

        // Zero out before
        for (int i = 0; i < desPos; i++) {
            des[i] = 0;
        }

        // Zero out after
        for (int i = desPos + srcLen - 1; i < des.length; i++) {
            des[i] = 0;
        }
    }

    private static void interpMoreDataThanPixelMethod(float[] srcData, int srcPos, int srcEnd, float[] des, int desPos, int method) {
        int destLen = des.length;
        int srcLen = srcEnd - srcPos;
        float r = srcLen / (float) destLen;
        if (r == 0) {
            r = 1;
        }
        int startIndx = 0;
        int endIndx = 0;

        int middleIndex = 0;

        if (method == 0) { // Average begin, middle, end
            for (int i = srcPos; i < srcEnd; i++) {
                startIndx = (int) ((i - srcPos) * r + srcPos);
                endIndx = startIndx + (int) r;
                if (endIndx > srcEnd) {
                    endIndx = srcEnd - 1;
                }

                int k = i - srcPos + desPos;
                if (k >= des.length) {
                    break;
                }

                middleIndex = (startIndx + endIndx) / 2;

                float avg2 = (srcData[startIndx] + srcData[middleIndex] + srcData[endIndx - 1]) / 3;

                des[k] = avg2;
            }

        } else if (method == 1) {   // Average
            for (int i = srcPos; i < srcEnd; i++) {
                startIndx = (int) ((i - srcPos) * r + srcPos);
                endIndx = startIndx + (int) r;
                if (endIndx > srcEnd) {
                    endIndx = srcEnd - 1;
                }

                int k = i - srcPos + desPos;
                if (k >= des.length) {
                    break;
                }
                float maxValue = Float.NEGATIVE_INFINITY;
                float sum = 0;
                for (int j = startIndx; j < endIndx; j++) {
                    sum = sum + srcData[j];
                    if (srcData[j] > maxValue) {
                        maxValue = srcData[j];
                    }
                }

                float avg = sum / r;
                des[k] = avg;
            }

        } else if (method == 2) {  // Max find
            for (int i = srcPos; i < srcEnd; i++) {
                startIndx = (int) ((i - srcPos) * r + srcPos);
                endIndx = startIndx + (int) r;
                if (endIndx > srcEnd) {
                    endIndx = srcEnd - 1;
                }

                int k = i - srcPos + desPos;
                if (k >= des.length) {
                    break;
                }
                float maxValue = Float.NEGATIVE_INFINITY;
                for (int j = startIndx; j < endIndx; j++) {
                    if (srcData[j] > maxValue) {
                        maxValue = srcData[j];
                    }
                }
                des[k] = maxValue;
            }

        }

        // Zero out before
        for (int i = 0; i < desPos; i++) {
            des[i] = 0;
        }

        // Zero out after
        for (int i = desPos + srcLen - 1; i < des.length; i++) {
            des[i] = 0;
        }
    }

    /**
     * Does linear interpolation. When you have more pixel than data. This
     * method use the linear method.
     *
     * @param x
     * @param y
     * @param xi
     * @param yi
     */
    private static void computeLinearInterp(float[] x, float[] y, float[] xi, float[] yi) {
        int lenXi = xi.length;
        int lenXi_stop = xi.length - 1;
        int lenX = x.length;

        for (int k = 0; k < lenXi; k++) {
            for (int i = 0; i < (lenX - 1); i++) {
                while (xi[k] <= x[i + 1]) {
                    yi[k] = y[i] + (xi[k] - x[i]) * (y[i + 1] - y[i]) / (x[i + 1] - x[i]);

                    //	Log.v("VT"," k = "+k);
                    k++;
//					if (k == (lenXi - 1)) {
//						break;
//					}

                    if (k > lenXi_stop) {
                        break;
                    }
                }
//				if (k == (lenXi - 1)) {
//					break;
//				}
                if (k > lenXi_stop) {
                    break;
                }
            }
        }

    }

    private static final float[] tmpData = new float[2];
    private static final float[] tmpX = new float[2];

    /**
     * Interpolate the data. This is use for mostly graphing. More data than
     * pixel, more pixel than data. The start and stop are base on actual data
     * indices. The return valus is xi and yi. If more data than pixel, average
     * method is use. If more pixel than data, linear interpolation is use to
     * fill in the spaces.
     *
     * @param data
     * @param startIndex , startIndex of the data
     * @param stopIndex , stopIndex of the data
     * @param xi_ret , the return value for the x interpolated.
     * @param yi_ret , the return value for the y interpolated
     */
    // /teno
    /**
     * Matlab like linspace.
     *
     * @param x_start
     * @param x_end
     * @param N
     * @param newXArray
     */
    public static void linspace(float x_start, float x_end, int N, float[] newXArray) {
        if (N < 1) {
            return;
        }
        newXArray[N - 1] = x_end;
        for (int i = 0; i < N - 1; i++) {
            newXArray[i] = x_start + (float) ((x_end - x_start) / Math.floor(N - 1)) * i;
        }
    }

    /**
     * Return the largrithmic in linear form
     *
     * @param x_start
     * @param x_end
     * @param N
     * @param newXArray
     */
    public static void linspaceWithLogarithmicLinear(float x_start, float x_end, int N, float[] newXArray) {
        if (N < 1) {
            return;
        }

        if (x_start < 1) {
            x_start = 1;
        }

        newXArray[N - 1] = (float) Math.pow(10, x_end); // x_end;
        float v = 0;
        for (int i = 0; i < N - 1; i++) {
            v = x_start + (float) ((x_end - x_start) / Math.floor(N - 1)) * i;
            newXArray[i] = (float) Math.pow(10, v);
        }
    }

    /**
     * Use for graphing to an actual drawing of the pixel. Assuming the data[]
     * and pixelData[] are the same length. This method convert the data to to
     * pixel values for drawing.
     *
     * @param data
     * @param pixelData
     * @param boxY
     * @param boxHeight
     * @param minDataValue
     * @param maxDataValue
     */
    public static void processDataArrayToPixeDataArray(float[] data, float[] pixelData, int boxY, int boxHeight, int minDataValue, int maxDataValue) {
        int len = data.length;

        for (int i = 0; i < len; i++) {
            float pixValue = (float) (boxY + (double) boxHeight * (1 - (data[i] - minDataValue) / (double) (maxDataValue - minDataValue)));
            if (pixValue < boxY) {
                pixValue = boxY;
            }
            pixelData[i] = pixValue;
        }
    }

    /**
     * Get the minimum and maximum value of the data
     *
     * @param data
     * @param returnValue
     */
    public static void calculateMinMaxData(float[] data, float[] returnValue) {
        float maxDataValue = Float.MIN_VALUE;
        float minDataValue = Float.MAX_VALUE;
        if (data == null) {
            return;
        }
        if (returnValue == null) {
            return;
        }

        int len = data.length;
        for (int i = 0; i < len; i++) {

            if (data[i] < minDataValue) {
                minDataValue = data[i];
            }

            if (data[i] > maxDataValue) {
                maxDataValue = data[i];
            }
        }
        returnValue[0] = minDataValue;
        returnValue[1] = maxDataValue;
    }

    /**
     * Get the minimum and maximum value of the data
     *
     * @param data
     * @param returnValue
     */
    public static void calculateMinMaxData(float[] data, float[] returnValue, int startIndex, int stopIndex) {
        float maxDataValue = Float.NEGATIVE_INFINITY;
        float minDataValue = Float.POSITIVE_INFINITY;
        if (data == null) {
            return;
        }
        if (returnValue == null) {
            return;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        int len = data.length;
        if (stopIndex > len) {
            stopIndex = len;
        }

        for (int i = startIndex; i < stopIndex; i++) {
            if (data[i] < minDataValue) {
                minDataValue = data[i];
            }

            if (data[i] > maxDataValue) {
                maxDataValue = data[i];
            }
        }
        returnValue[0] = minDataValue;
        returnValue[1] = maxDataValue;
    }

    public static void interpolateData2(float[] data, int startIndex, int stopIndex, float xIndexStart, float xIndexEnd, float[] xi_ret, float[] yi_ret, float[] tmpData, float[] tmpX, boolean creatNewXIBool, int nethod) {
        // Adding some smart to avoid contious c
        int lenDat = data.length;
        int lenXi = xi_ret.length;
        int lenYi = xi_ret.length;

        // The new length
        int newLenData = stopIndex - startIndex;
        if (newLenData < 1) {
            return;
        }

        // if different create length
        if (tmpData.length != newLenData) {
            tmpData = new float[newLenData];
            tmpX = new float[newLenData];
        }

        // The new interpolated for the x, always return and use
        if (creatNewXIBool) {
            linspace(xIndexStart, xIndexEnd, lenXi, xi_ret);
        }

        // This is the length of the drawing screen the return
        // must be the same
        // More data than pixel
        if (newLenData > lenXi) {

            // No mater what create the x-axis data. Always copy the data to
            // a temp
            System.arraycopy(data, startIndex, tmpData, 0, newLenData);

            // more data than pixels
            interpMoreDataThanPixelMethod(tmpData, 0, tmpData.length, yi_ret, 0, nethod);

        } else if (newLenData < lenXi) { // more pixel than data

            // No mater what create the x-axis data. Always copy the data to a
            // temp
            System.arraycopy(data, startIndex, tmpData, 0, newLenData);

            // more pixels than data
            // get the temp of x
            linspace(xIndexStart, xIndexEnd, newLenData, tmpX);
            // pass in the tmpX, tmpData(should be same length as tmpX), interp
            // X and it will return the a new
            // interp y.
            computeLinearInterp(tmpX, tmpData, xi_ret, yi_ret);
        } else { // data must be equal

            // No mater what create the x-axis data. Always copy the data to a
            // temp
            // System.arraycopy(data, startIndex, tmpData, 0, newLenData);
            // No mater what create the x-axis data. Always copy the data to a
            // temp
            System.arraycopy(data, startIndex, tmpData, 0, newLenData);

            // straight copy data is equal
            System.arraycopy(tmpData, 0, yi_ret, 0, newLenData);
        }
    }

    public static void interpolateDataXandY(float[] data, int startIndex, int stopIndex, int xIndexStart, int xIndexEnd, int yIndexStart, int yIndexEnd, float[] xi_ret, float[] yi_ret, float[] tmpData, float[] tmpX) {
        // Adding some smart to avoid contious c
        int lenDat = data.length;
        int lenXi = xi_ret.length;
        int lenYi = xi_ret.length;

        // The new length
        int newLenData = stopIndex - startIndex;
        if (newLenData < 1) {
            return;
        }

        // if different create length
        if (tmpData.length != newLenData) {
            tmpData = new float[newLenData];
            tmpX = new float[newLenData];
        }
        // No mater what create the x-axis data. Always copy the data to a temp
        System.arraycopy(data, startIndex, tmpData, 0, newLenData);

        // The new interpolated for the x, always return and use
        linspace(xIndexStart, xIndexEnd, lenXi, xi_ret);

        // This is the length of the drawing screen the return
        // must be the same
        // More data than pixel
        if (newLenData > lenXi) {
            // more data than pixels
            interpMoreDataThanPixelMethod(tmpData, 0, tmpData.length, yi_ret, 0, 1);
        } else if (newLenData < lenXi) { // more pixel than data
            // more pixels than data
            // get the temp of x
            linspace(xIndexStart, xIndexEnd, newLenData, tmpX);
            // pass in the tmpX, tmpData(should be same length as tmpX), interp
            // X and it will return the a new
            // interp y.
            computeLinearInterp(tmpX, tmpData, xi_ret, yi_ret);
        } else { // data must be equal
            // straight copy data is equal
            System.arraycopy(tmpData, 0, yi_ret, 0, newLenData);
        }
    }

    /**
     * Compute Lagrange interpolation. x[] and y[] are the same length. xi[] and
     * yi[] are the same length. The output is yi[].
     *
     * @param x
     * @param y
     * @param xi
     * @param yi
     */
    public static void computeLagrange(float[] x, float[] y, float[] xi, float[] yi) {
        int lenXi = xi.length;
        int lenX = x.length;
        for (int k = 0; k < lenXi; k++) {
            float sum = 0;
            for (int i = 0; i < lenX; i++) {
                float multiTrack = 1;
                for (int j = 0; j < lenX; j++) {
                    if (i != j) {
                        float v = (xi[k] - x[j]) / (x[i] - x[j]);
                        multiTrack = multiTrack * v;
                    }
                }
                sum = sum + y[i] * multiTrack;
            }
            yi[k] = sum;
        }
    }

//    public static void main(String args[]) {
//        int N = 20000;
//        int nWidth = 400;
//
//        float data[] = new float[N];
//        float xi[] = new float[nWidth];
//        float yi[] = new float[nWidth];
//
//        for (int i = 0; i < N; i++) {
//            data[i] = (float) Math.random();
//        }
//
//        // interpolateData(data, 0, 4000, xi, yi);
//
//        for (int i = 0; i < xi.length; i++) {
//            // System.out.println(xi[i] + ":" + yi[i]);
//        }
//    }
    public static int findMaxIndexValue(float[] data) {
        int maxValueIndex = 0;
        float maxValue = Float.NEGATIVE_INFINITY;
        int len = data.length;
        float value = 0;

        for (int i = 0; i < len; i++) {
            value = data[i];
            if (value > maxValue) {
                maxValue = value;
                maxValueIndex = i;
            }
        }
        return maxValueIndex;
    }

    public static void cw(float ampl, float[] results, float fs, float freq) {
        int N = results.length;
        float t = 0;
        for (int i = 0; i < N; i++) {
            t = i / fs;
            results[i] = (float) (ampl * Math.cos(2 * Math.PI * freq * t));
        }
    }

    public static void cw_shorts(float ampl, short[] results, float fs, float freq) {
        int N = results.length;
        float t = 0;
        for (int i = 0; i < N; i++) {
            t = i / fs;
            results[i] = (short) (ampl * Math.cos(2 * Math.PI * freq * t) * Short.MAX_VALUE);
        }
    }

    public static void cw_shorts(float ampl, short[] results, float fs, float freq1, float freq2, float freq3) {
        int N = results.length;
        float t = 0;
        for (int i = 0; i < N; i++) {
            t = i / fs;
            double r1 = ampl * Math.cos(2 * Math.PI * freq1 * t);
            double r2 = ampl * Math.cos(2 * Math.PI * freq2 * t);
            double r3 = ampl * Math.cos(2 * Math.PI * freq3 * t);
            results[i] = (short) (((r1 + r2 + r3) / 3f) * Short.MAX_VALUE);
        }
    }

    public static void cw_bytes(float ampl, byte[] results, float fs, float freq) {
        int N = results.length / 2;
        float t = 0;
        int j = -1;
        for (int i = 0; i < N; i++) {
            t = i / fs;
            short x = (short) (ampl * Math.cos(2 * Math.PI * freq * t) * Short.MAX_VALUE);

            // Little Endian
            results[++j] = (byte) x;
            results[++j] = (byte) (x >> 8);

//            // Big Endian
//            results[i] = (byte) (x >> 8);
//            results[++i] = (byte) x;
        }
    }

    public static void geometric_comb(float amplitude, float[] result_real, float fs, float freq1, float freq2, int nTaps) {
        int N = result_real.length;
        double v = 0;
        double t = 0;
        float PI = (float) Math.PI;
        float[] fn = new float[nTaps];
        fn[0] = freq1;

        float spacing = freq2 - freq1;
        float r = freq2 / freq1;

        float alpha = 0;
        float[] w = new float[N];

        hanning(w);

        float max = Float.NEGATIVE_INFINITY;

        for (int kk = 0; kk < N; kk++) {
            result_real[kk] = 0;
            for (int n = 1; n < nTaps; n++) {
                t = kk / fs;
                fn[n] = fn[n - 1] + (float) (Math.pow(r, (n - 2)) * spacing);
                v = 2 * Math.PI * fn[n - 1] * (t - alpha);
                float real = (float) Math.cos(v);
                result_real[kk] = result_real[kk] + real;
            }
            result_real[kk] = result_real[kk] * w[kk];
            if (result_real[kk] > max) {
                max = result_real[kk];
            }
        }
        for (int kk = 0; kk < N; kk++) {
            result_real[kk] = result_real[kk] / max;
        }
    }

    public static void coxcombReal(float amplitude, float[] result_real, float fs, float freq, float spacing, int nTaps) {
        int N = result_real.length;
        double v = 0;
        double t = 0;
        float PI = (float) Math.PI;
        float[] fn = new float[nTaps];
        fn[0] = freq;

        float alpha = 0;
        float[] w = new float[N];

        hanning(w);

        float max = Float.NEGATIVE_INFINITY;

        for (int kk = 0; kk < N; kk++) {
            result_real[kk] = 0;
            for (int n = 1; n < nTaps; n++) {
                t = kk / fs;
                fn[n] = fn[n - 1] + spacing;
                v = 2 * Math.PI * fn[n] * (t - alpha);
                float real = (float) Math.cos(v);
                result_real[kk] = result_real[kk] + real;
            }
            result_real[kk] = result_real[kk] * w[kk];
            if (result_real[kk] > max) {
                max = result_real[kk];
            }
        }
        for (int kk = 0; kk < N; kk++) {
            result_real[kk] = result_real[kk] / max;
        }
    }

    public static void costaWaveformReal(float amplitude, float[] result, float fs, float freq, float bandWidth) {
        int len = result.length;
        double v = 0;
        double t = 0;
        float PI = (float) Math.PI;
        float[] costas_code = {2, 4, 8, 5, 10, 9, 7, 3, 6, 1};
        float[] fn = new float[costas_code.length];
        int costas_len = fn.length;
        for (int i = 0; i < costas_len; i++) {
            fn[i] = (freq - bandWidth / 2) + (costas_code[i] - 1) * bandWidth / (costas_len - 1);
        }

        int nTimePulse = len / costas_len;

        int i = 0;
        for (int k = 0; k < costas_len; k++) {
            for (int n = 0; n < nTimePulse; n++) {
                t = n / fs;
                v = 2 * Math.PI * fn[k] * t;
                result[i] = (float) (amplitude * Math.cos(v));
                // result_complex[len + i] = -1 * (float) (amplitude *
                // Math.sin(v));
                // System.out.println(result_complex[i] + " " +
                // result_complex[len + i] + "i");
                i++;
            }

        }
    }

    public static void prng(float amplitude, float[] result, float[] r, float fs, float freq) {
        int len = result.length;
        double v = 0;
        double t = 0;
        float PI = (float) Math.PI;
        for (int i = 0; i < len; i++) {
            t = i / fs;
            result[i] = (float) (amplitude * Math.sin(2 * PI * freq * t) * r[i]);

        }
    }

    public static void hfm(float amplitude, float[] result, float fs, float freq1, float freq2, int nSecond) {
        float bandWidth = freq2 - freq1;
        int len = result.length;
        double v = 0;
        double t = 0;
        for (int i = 0; i < len; i++) {
            t = i / fs;
            v = -2 * Math.PI * (freq1 * freq2 * nSecond / bandWidth) * Math.log(freq2 * nSecond - bandWidth * t);
            result[i] = (float) (amplitude * Math.cos(v));
        }
    }

    public static float calulcateQuadraticForumula(float x1, float y1, float x2, float y2) {
        float diffX = Math.abs(x1 - x2);
        float diffY = Math.abs(y1 - y2);
        float result = (float) Math.sqrt(diffX * diffX + diffY * diffY);
        return result;
    }

    // ///////////////////////////////////////////
    public static void hanning(float[] results) {
        int N = results.length;
        for (int i = 1; i <= N; i++) {
            results[i - 1] = (float) (0.5 * (1 - Math.cos(2 * Math.PI * i / (N + 1.0))));
        }
    }

    public static void flatTop(float[] results) {
        int N = results.length;
        for (int i = 0; i < N; i++) {
            results[i] = (float) (0.3635819 - .4891775 * Math.cos((2 * Math.PI * i) / (N - 1)) + .1365995 * Math.cos((4 * Math.PI * i) / (N - 1)) - 0.0106411 * Math.cos((6 * Math.PI * i) / (N - 1)));
        }
    }

    public static void blackman(float[] results) {
        int N = results.length;
        float alpha = .16f;
        for (int i = 0; i < N; i++) {
            results[i] = (float) ((1 - alpha) / 2.0 - .5 * Math.cos((2 * Math.PI * i) / (N - 1)) + (alpha / 2.0) * Math.cos((4 * Math.PI * i) / (N - 1)));
        }
    }

    public static void bartlettHann(float[] results) {
        int N = results.length;
        for (int i = 0; i < N; i++) {
            results[i] = (float) (0.62 - 0.48 * Math.abs(i / (N - 1.0f) - .5) - 0.38 * Math.cos((2 * Math.PI * i) / (N - 1)));
        }
    }

    public static void gaussianWindow(float[] results) {
        float alpha = 2.5f;
        int N = results.length;
        float k = 0;
        for (int i = 0; i < N; i++) {
            k = -N / 2f + i;
            double v = (-1 / 2.0) * (alpha * k / (N / 2.0)) * (alpha * k / (N / 2.0));

            results[i] = (float) Math.exp(v);
            // System.out.println(results[i - 1]);
        }
    }

    public static void triangular(float[] results) {
        int N = results.length;
        for (int i = 0; i < N; i++) {
            results[i] = (float) ((2 / (N * 1.0)) * ((N) / 2.0 - Math.abs(i - (N - 1) / 2.0)));
        }
    }

    public static void lanczos(float[] results) {
        int N = results.length;
        for (int i = 0; i < N; i++) {
            double v = (2 * i / (N - 1.0));
            results[i] = (float) (Math.sin(Math.PI * v) / (Math.PI * v));
        }
    }

    public static void cosineWindow(float[] results) {
        int N = results.length;
        for (int i = 0; i < N; i++) {
            results[i] = (float) (Math.sin(Math.PI * i / (N - 1.0)));
        }
    }

    public static void hamming(float[] results) {
        int N = results.length;
        for (int i = 0; i < N; i++) {
            results[i] = (float) (0.54 - 0.46 * Math.cos(2 * Math.PI * i / (N - 1.0)));
            // System.out.println(results[i - 1]);
        }
    }

    public static void hann(float[] results) {
        int N = results.length;
        for (int i = 0; i < N; i++) {
            results[i] = (float) (0.5f - 0.5f * Math.cos(2 * Math.PI * i / (N - 1.0)));
            // System.out.println(results[i - 1]);
        }
    }

    public static void ones(float[] results) {
        int N = results.length;
        for (int i = 0; i < N; i++) {
            results[i] = 1;
        }
    }

    public static void nuttal_first_dev(float[] results) {
        int N = results.length;

        float a0 = 0.355768f;
        float a1 = 0.487396f;
        float a2 = 0.144232f;
        float a3 = 0.012604f;

        for (int i = 0; i < N; i++) {
            results[i] = (float) (a0 - a1 * Math.cos((2 * Math.PI * i) / (N - 1))
                    + a2 * Math.cos((4 * Math.PI * i) / (N - 1))
                    - a3 * Math.cos((6 * Math.PI * i) / (N - 1)));
        }
    }

    public static int xcorrMaxIndex(float[] x1, float[] x2) {
        int len1 = x1.length;
        int len2 = x2.length;
      //  float result[] = new float[len1 * len2];
        int index = 0;
        float sum = 0;
        float maxValue = Float.MIN_VALUE;
        int maxIndex = 0;
        for (int i = 0; i < len1; i++) {
            sum = 0;
            for (int j = 0; j < len2; j++) {
                float v = x1[i] * x2[len2 - 1 - j];
                System.out.println(v);
                sum = sum + v;
                index++;

            }
            float avg = sum / len2;
            if (avg > maxValue) {
                maxValue = avg;
                maxIndex = i;
            }
        }
        
        System.out.println(maxValue + " : " + maxIndex);
        return maxIndex;
    }

    public static final float[] interpLinear(float[] x, float[] y, float[] xi, float[] yi) throws IllegalArgumentException {

        if (x.length != y.length) {
            throw new IllegalArgumentException("X and Y must be the same length");
        }
        if (x.length == 1) {
            throw new IllegalArgumentException("X must contain more than one value");
        }
        float[] dx = new float[x.length - 1];
        float[] dy = new float[x.length - 1];
        float[] slope = new float[x.length - 1];
        float[] intercept = new float[x.length - 1];

        // Calculate the line equation (i.e. slope and intercept) between each point
        for (int i = 0; i < x.length - 1; i++) {
            dx[i] = x[i + 1] - x[i];
            if (dx[i] == 0) {
                throw new IllegalArgumentException("X must be montotonic. A duplicate " + "x-value was found");
            }
            if (dx[i] < 0) {
                throw new IllegalArgumentException("X must be sorted");
            }
            dy[i] = y[i + 1] - y[i];
            slope[i] = dy[i] / dx[i];
            intercept[i] = y[i] - x[i] * slope[i];
        }

        // Perform the interpolation here
        for (int i = 0; i < xi.length; i++) {
            if ((xi[i] > x[x.length - 1]) || (xi[i] < x[0])) {
                yi[i] = Float.NaN;
            } else {
                int loc = Arrays.binarySearch(x, xi[i]);
                if (loc < -1) {
                    loc = -loc - 2;
                    yi[i] = slope[loc] * xi[i] + intercept[loc];
                } else {
                    yi[i] = y[loc];
                }
            }
        }

        return yi;
    }
    public static void main(String[] args) {
        float[] x1 = new float[]{1, 2, 3, 4, 1, 2, 3};
        float[] x2 = new float[]{1, 22, 3, 2, 3, 40, 5};
        XCrossUtil xcor = new XCrossUtil();
        XCrossUtil.xcorrMaxIndex(x1, x2);

    }
}
