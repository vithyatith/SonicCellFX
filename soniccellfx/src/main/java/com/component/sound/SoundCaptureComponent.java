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
package com.component.sound;

import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class SoundCaptureComponent extends ComponentAbstraction {

    private boolean startSonicMsgr = false;
    private Thread sonicThread = null;
    private int size = 2048;

    private boolean reInitBool = false;

    public SoundCaptureComponent() {
        setName("SoundCapture");
        this.setProperty("size", size);
        this.addOutput(new DataTypeIO("float[]", "Capture Raw data")); //0
    }

    @Override
    public Object onExecute() {

        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {

    }

    @Override
    public boolean start() {
        startSonicMsgr();
        return true;
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    // private int hardwareFFTSize = 16384; // 4096, 8192, 16384, these are he recommend value
    private ByteBuffer byteBuffer = null;
    private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
    private int circularTrack = 0;
    private boolean successfullRecording = false;
    private int dataLen = -99;
    private float data_float[] = null;
    private static int circular_second = 30;
    public static final int MAX_SAMPLE_CIRCULAR = 44100 * circular_second;
    private short circularData[] = new short[MAX_SAMPLE_CIRCULAR];
    // This is the callback from the detection algorithm


    /* convert from UTF-8 encoded HTML-Pages -> internal Java String Format */
    public static String convertFromUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("ISO-8859-1"), "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    /* convert from internal Java String Format -> UTF-8 encoded HTML/JSP-Pages  */
    public static String convertToUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("UTF-8"));
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

    private void startSonicMsgr() {

        startSonicMsgr = true;

        size = (int) Double.parseDouble(getProperty("size").toString());
        reInitBool = false;

        sonicThread = new Thread() {
            @Override
            public void run() {
                AudioFormat format = new AudioFormat(44100, 16, 1, true, true);

                DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
                if (!AudioSystem.isLineSupported(targetInfo)) {
                    System.out.println("Is not supported...");
                } else {
                    System.out.println("Is supported...");
                }

                try {
                    TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
                    long info = targetLine.getLongFramePosition();

                    System.out.println(info);

                    targetLine.open(format);
                    targetLine.start();

                    int numOfBytesRead;

                    int bufferSizeInBytes = size * 2;

                    byteBuffer = ByteBuffer.allocate(bufferSizeInBytes);
                    byteBuffer.order(byteOrder);

                    byte[] rawBytesDataBuffer = new byte[bufferSizeInBytes];

                    circularTrack = 0;
                    int baseTrack = 0;

                    int i = 0;
                    int maxCountCir = MAX_SAMPLE_CIRCULAR / size;

                    while (startSonicMsgr) {

                        if (reInitBool) {
                            reInitBool = false;
                            bufferSizeInBytes = size * 2;

                            byteBuffer = ByteBuffer.allocate(bufferSizeInBytes);
                            byteBuffer.order(byteOrder);

                            rawBytesDataBuffer = new byte[bufferSizeInBytes];

                            circularTrack = 0;
                            baseTrack = 0;

                            i = 0;
                            maxCountCir = MAX_SAMPLE_CIRCULAR / size;
                        }

                        numOfBytesRead = targetLine.read(rawBytesDataBuffer, 0, rawBytesDataBuffer.length);

                        if (numOfBytesRead == -1) {
                            break;
                        }

                        successfullRecording = true;

                        if (dataLen != numOfBytesRead / 2) {
                            // Divide by to convert back to short
                            data_float = new float[numOfBytesRead / 2];
                            dataLen = data_float.length;
                        }

                        if (numOfBytesRead < 1) {
                            continue;
                        }

                        byteBuffer.rewind();
                        byteBuffer.put(rawBytesDataBuffer);

                        i = 0;

                        byteBuffer.rewind();

                        try {
                            baseTrack = circularTrack * size;
                            while (byteBuffer.hasRemaining()) {
                                short s = byteBuffer.getShort();
                                data_float[i] = s;
                                circularData[i + baseTrack] = s;
                                i++;
                            }
                            circularTrack++;
                            if (circularTrack >= maxCountCir) {
                                circularTrack = 0;
                            }

                            sendData(0, data_float);
                        } catch (java.lang.ArrayIndexOutOfBoundsException e) {

                            System.out.println("SoundCaptureComponent: " + e.getMessage());

                            ////////////////////////////
                            bufferSizeInBytes = size * 2;

                            byteBuffer = ByteBuffer.allocate(bufferSizeInBytes);
                            byteBuffer.order(byteOrder);

                            rawBytesDataBuffer = new byte[bufferSizeInBytes];

                            circularTrack = 0;
                            baseTrack = 0;

                            i = 0;
                            maxCountCir = MAX_SAMPLE_CIRCULAR / size;

                            ////////////////////////
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(e);
                }
            }

        };

        sonicThread.start();

    }

    @Override
    public void stop() {
        startSonicMsgr = false;
        try {
            Thread t = sonicThread;
            t.interrupt();
            sonicThread = null;
            t = null;
        } catch (Exception e) {

        }
    }

    @Override
    public void loadProperty(String key, Object val) {
        onPropertyChanged(key, val);
    }

    @Override
    public String getHelp() {

        String doc = "";

        return doc;
    }

    @Override
    public void mouseDoubleClick() {
    }

    @Override
    public int getPlatformSupport() {
        return 1;
    }

    @Override
    public void onPropertyChanged(String key, Object value) {
        if (key.equalsIgnoreCase("size")) {
            size = (int) Double.parseDouble(getProperty("size").toString());
            reInitBool = true;
        }
    }

    @Override
    public void onDestroy() {

    }

}
