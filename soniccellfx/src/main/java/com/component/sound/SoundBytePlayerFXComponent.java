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
import com.sonicmsgr.soniccell.Log;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author yada
 */
public class SoundBytePlayerFXComponent extends ComponentAbstraction {

    private AudioFormat audioFormat = null;
    private DataLine.Info dataLineInfo = null;
    private SourceDataLine sourceLine = null;
    private int sampleRate = 44100;
    private boolean bigEndian = false;
    private byte dataBytes[] = null;
    private boolean replayBool = false;

    private boolean dataReady = false;
    private boolean startBool = false;
    private Thread playerThread = null;

    public SoundBytePlayerFXComponent() {
        setName("WavPlayer");
        this.setProperty("sampleRate", (Integer) sampleRate);
        this.setProperty("bigEndian", (Boolean) bigEndian);
        this.setProperty("replayBool", (Boolean) replayBool);
        this.addInput(new DataTypeIO("byte[]", "Raw byte data"));
    }

    @Override
    public Object onExecute() {
        try {
            if (dataReady) {
                dataReady = false;
                sourceLine.open(audioFormat, 8192);
                sourceLine.start();

                playerThread = new Thread() {
                    @Override
                    public void run() {
                        do {
                            if (dataBytes != null) {
                                sourceLine.write(dataBytes, 0, dataBytes.length);
                            }
                            if (startBool == false) {
                                break;
                            }

                        } while (replayBool);
                    }
                };
                playerThread.start();
            }

        } catch (Exception e) {
            Log.v("VT", "SoundPlayer exception " + e.getMessage());
        }

        return null;
    }

    private void cleanUp() {
        startBool = false;
        if (playerThread != null) {
            try {
                Thread t = playerThread;
                t.interrupt();
                playerThread = null;
                t = null;
            } catch (Exception e) {

            }
            playerThread = null;
        }
        try {
            if (sourceLine != null) {
                sourceLine.flush();
                sourceLine.drain();
                sourceLine.stop();
                sourceLine.close();

                sourceLine = null;
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void handleMessage(int thru, Object obj) {
        dataReady = true;
        dataBytes = (byte[]) obj;
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public boolean start() {
        cleanUp();
        startBool = true;
        try {
            sampleRate = (int)Double.parseDouble(getProperty("sampleRate").toString());
            bigEndian = Boolean.parseBoolean(getProperty("bigEndian").toString());
            replayBool = Boolean.parseBoolean(getProperty("replayBool").toString());

            audioFormat = new AudioFormat(sampleRate, 16, 1, true, bigEndian);
            dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);

            return true;
        } catch (LineUnavailableException ex) {
            return false;
        }
    }

    @Override
    public void stop() {
        cleanUp();
    }
    @Override
    public String getHelp() {

        String doc = "";
        
        return doc;
    }
    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("sampleRate")) {
            sampleRate = (int) Double.parseDouble(getProperty("sampleRate").toString());
        }
        if (key.equalsIgnoreCase("bigEndian")) {
            bigEndian = (Boolean) Boolean.parseBoolean(getProperty("bigEndian").toString());
        }
        if (key.equalsIgnoreCase("replayBool")) {
            replayBool = (Boolean) Boolean.parseBoolean(getProperty("replayBool").toString());
        }
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

    }
        @Override
    public void onDestroy() {

    }
    

}
