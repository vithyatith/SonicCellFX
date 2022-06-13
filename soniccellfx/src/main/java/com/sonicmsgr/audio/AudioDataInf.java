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
package com.sonicmsgr.audio;

import java.nio.ByteBuffer;

public interface AudioDataInf {

    int STOP_BUTTON = 0;
    int TOAST_MESSAGE = 1;
    int UPDATE_BACKBUTTON = 2;
    int SEND_EMAIL_DETECT = 3;
    int SEND_EMAIL_APPSTART = 4;
    int SEND_EMAIL_APPSTOP = 5;
    int DISMISS_DIALOG = 6;
    int SHUTDOWN_PROCESS = 7;

    void initAudioProcessing(String waveFileName, int sampleRate, int nChannel);

    // ORIG
    //public Object processData(float[] msg, ByteBuffer byteBuffer);
    Object processData(float[] msg, ByteBuffer byteBuffer);

    void doneAudioProcessing(String savedWavFileName);

    void doneAudioProcessing(String savedWavFileName, int sampleRate, int nChannel);

    void donePlayingFile();

    void audioException(String msg);

    void setErrorStopPlaying(boolean b);

}
