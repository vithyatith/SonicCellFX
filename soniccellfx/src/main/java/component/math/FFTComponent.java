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
package component.math;

import com.sonicmsgr.signalprocessing.SonicMsgrFFT;
import com.sonicmsgr.signalprocessing.Windowing;
import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.util.MathUtil;
import org.apache.commons.math3.util.MathUtils;

/**
 *
 * @author Administrator
 */
public class FFTComponent extends ComponentAbstraction {

    private SonicMsgrFFT fft = new SonicMsgrFFT(1);
    private float data_complex[] = null;
    private float data_complex_half[] = null;
    private float[] data_real = new float[1];
    private float[] data_real_tmp = new float[1];
    private int fftSize = 2048;
    private int average = 1;
    private int trackAvg = 0;

    private boolean readyBool = false;
    private float windowing[] = new float[1];
    private float windowingOne[] = new float[1];
    private float windowingRef[] = null;
    private String window = "hamming";
    private boolean enableWindowing = false;
    private boolean sendHalf = false;

    public FFTComponent() {
        setName("FFT");
        setProperty("fftSize", fftSize);
        setProperty("average", average);
        setProperty("window", window);
        setProperty("enableWindowing", enableWindowing);
        setProperty("sendHalf", sendHalf);
        this.addInput(new DataTypeIO("float[]", "Real float data "));
        this.addOutput(new DataTypeIO("float[]", "Complex float data of [r i r i....]"));

    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public void handleMessage(int thru, Object obj) {

        float refData[] = (float[]) obj;
        int len = refData.length;

        if (len != data_real.length) {
            data_real = new float[len];
            data_real_tmp = new float[len];
            trackAvg = 0;
        }

        if (average < 2) {
            // data_real = (float[]) obj;
            for (int i = 0; i < len; i++) {
                data_real[i] = refData[i];
            }
            readyBool = true;
        } else {

            for (int i = 0; i < len; i++) {
                data_real_tmp[i] = refData[i];
                data_real[i] = data_real[i] + data_real_tmp[i];
            }

            trackAvg++;

            if (trackAvg >= average) {

                for (int i = 0; i < len; i++) {
                    data_real[i] = data_real[i] / average;
                }

                readyBool = true;
                trackAvg = 0;
            }

        }
    }

    @Override
    public Object onExecute() {
        if (readyBool) {
            readyBool = false;
            if (isPowerOf2 == false) {
                return null;
            }
            try {
                int dataLen = data_real.length;

                int numOfIteration = dataLen / fftSize;

                int startIndex = 0;
                for (int k = 0; k < numOfIteration; k++) {
                    startIndex = fftSize * k;
                    for (int i = 0; i < fftSize; i++) {
                        //data_complex[i] = data_real[i + startIndex];
                        data_complex[i] = data_real[i + startIndex] * windowingRef[i];
                        data_complex[i + fftSize] = 0;

                        // Zero out data
                        data_real[i + startIndex] = 0;
                    }
                    fft.realForwardFull(data_complex);

                    if (sendHalf == false) {

                        sendData(0, data_complex);
                    } else {
                        int l = data_complex_half.length;
                        for (int i = 0; i < l; i++) {
                            data_complex_half[i] = data_complex[i];
                        }

                        sendData(0, data_complex_half);
                    }

                }

                int firstEnd = dataLen - numOfIteration * fftSize;

                if (firstEnd > 0) {
                    startIndex = fftSize * numOfIteration;
                    for (int i = 0; i < firstEnd; i++) {
                        data_complex[i] = data_real[i + startIndex] * windowingRef[i];
                        // data_complex[i] = data_real[i + startIndex];
                        data_complex[i + fftSize] = 0;

                        // Zero
                        data_real[i + startIndex] = 0;
                    }

                    int lastEnd = fftSize - firstEnd;
                    for (int i = firstEnd; i < fftSize; i++) {
                        data_complex[i] = 0;
                        data_complex[i + fftSize] = 0;
                    }
                    fft.realForwardFull(data_complex);

                    if (sendHalf == false) {

                        sendData(0, data_complex);
                    } else {
                        int l = data_complex_half.length;
                        for (int i = 0; i < l; i++) {
                            data_complex_half[i] = data_complex[i];
                        }

                        sendData(0, data_complex_half);
                    }
                }

                return null;
            } catch (ArrayIndexOutOfBoundsException e) {
                updateAll();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void mouseDoubleClick() {

    }

    private boolean isPowerOf2 = false;

    @Override
    public boolean start() {
//        readyBool = false;
//        fftSize = (int) Double.parseDouble(getProperty("fftSize").toString());
//        average = (int) Double.parseDouble(getProperty("average").toString());
//        window = getProperty("window").toString();
//        enableWindowing = Boolean.parseBoolean(getProperty("enableWindowing").toString());
//
//        // 
//        trackAvg = 0;
//
//        // Zero data
//        for (int i = 0; i < data_real.length; i++) {
//            data_real[i] = 0;
//            data_real_tmp[i] = 0;
//        }
//
//        updateWindowing();
//
//        isPowerOf2 = MathUtil.isPowerOfTwo(fftSize);
//        if (isPowerOf2) {
//            fft = new SonicMsgrFFT(fftSize);
//            data_complex = new float[fftSize * 2];
//        } else {
//            printToConsole("FFT size " + fftSize + " is not a power of 2.");
//        }
        updateAll();

        return true;
    }

    private void updateAll() {
        readyBool = false;
        fftSize = (int) Double.parseDouble(getProperty("fftSize").toString());
        average = (int) Double.parseDouble(getProperty("average").toString());
        window = getProperty("window").toString();
        enableWindowing = Boolean.parseBoolean(getProperty("enableWindowing").toString());
        sendHalf = Boolean.parseBoolean(getProperty("sendHalf").toString());

        // 
        trackAvg = 0;

        if (data_real.length != fftSize) {
            data_real = new float[fftSize];
            data_real_tmp = new float[fftSize];
        }
        // Zero data

        for (int i = 0; i < fftSize; i++) {
            data_real[i] = 0;
            data_real_tmp[i] = 0;
        }

        updateWindowing();

        isPowerOf2 = MathUtil.isPowerOfTwo(fftSize);
        if (isPowerOf2) {
            fft = new SonicMsgrFFT(fftSize);
            data_complex = new float[fftSize * 2];
            data_complex_half = new float[fftSize];
        } else {
            printToConsole("FFT size " + fftSize + " is not a power of 2.");
        }
    }

    private void updateWindowing() {
        if (windowing.length != fftSize) {
            windowing = new float[fftSize];
            windowingOne = new float[fftSize];
            Windowing.computeWindowing(Windowing.WINDOW_HAMMING, fftSize, windowing);
            for (int i = 0; i < fftSize; i++) {
                windowingOne[i] = 1;
            }
            trackAvg = 0;
        }
        if (enableWindowing) {
            windowingRef = windowing;
        } else {
            windowingRef = windowingOne;
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void onPropertyChanged(String key, Object value) {
        loadProperty(key, value);
    }

    @Override
    public void loadProperty(String key, Object value) {

        if (key.equalsIgnoreCase("fftSize")) {
            fftSize = (int) Double.parseDouble(getProperty("fftSize").toString());
            //updateWindowing();
            updateAll();
        } else if (key.equalsIgnoreCase("average")) {
            average = (int) Double.parseDouble(getProperty("average").toString());
        } else if (key.equalsIgnoreCase("window")) {
            window = getProperty("window").toString();
        } else if (key.equalsIgnoreCase("enableWindowing")) {
            enableWindowing = Boolean.parseBoolean(getProperty("enableWindowing").toString());
            updateWindowing();
        } else if (key.equalsIgnoreCase("sendHalf")) {
            sendHalf = Boolean.parseBoolean(getProperty("sendHalf").toString());
        }
    }

    @Override
    public int getPlatformSupport() {

        return 0;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public String getHelp() {

        String doc = "";

        return doc;
    }
}
