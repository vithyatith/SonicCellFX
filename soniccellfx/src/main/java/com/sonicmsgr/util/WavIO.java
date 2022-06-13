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
import com.sonicmsgr.audio.AudioDataInf;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WavIO {

    public boolean saveAsWav(String rawFileName, String wavFileName) {
        try {

            int myChunkSize;
            int myDataSize;
            int myRawFileData;
            int mySubChunk1Size = 16;
            int myFormat = 1;
            int myChannels = 1;
            int mySampleRate = 44100;
            int myByteRate = 88200;
            int myBlockAlign = 2;
            int myBitsPerSample = 16;

            File rawFile, wavFile;

            rawFile = new File(rawFileName);

            myChunkSize = (int) rawFile.length() + 36;
            myRawFileData = (int) rawFile.length();

            DataOutputStream outFile = new DataOutputStream(
                    new FileOutputStream(wavFileName));

            // write the WAV file per the WAV file format
            outFile.writeBytes("RIFF"); // 00 - RIFF
            outFile.write(intToByteArray(myChunkSize), 0, 4); // 04 - how big is
            // the rest of
            // this file?
            outFile.writeBytes("WAVE"); // 08 - WAVE
            outFile.writeBytes("fmt "); // 12 - fmt
            outFile.write(intToByteArray(mySubChunk1Size), 0, 4); // 16 - size
            // of this
            // chunk
            outFile.write(intToByteArray(myFormat), 0, 2); // 20 - what is the
            // audio format? 1
            // for PCM = Pulse
            // Code Modulation
            outFile.write(intToByteArray(myChannels), 0, 2); // 22 - mono or
            // stereo? 1 or
            // 2? (or 5 or
            // ???)
            outFile.write(intToByteArray(mySampleRate), 0, 4); // 24 - samples
            // per second
            // (numbers per
            // second)
            outFile.write(intToByteArray(myByteRate), 0, 4); // 28 - bytes per
            // second
            outFile.write(intToByteArray(myBlockAlign), 0, 2); // 32 - # of
            // bytes in one
            // sample, for
            // all channels
            outFile.write(intToByteArray(myBitsPerSample), 0, 2); // 34 - how
            // many bits
            // in a
            // sample(number)?
            // usually
            // 16 or 24
            outFile.writeBytes("data"); // 36 - data
            outFile.write(intToByteArray(myRawFileData), 0, 4); // 40 - how big
            // is this data
            // chunk

            DataInputStream inFile = new DataInputStream(new FileInputStream(rawFileName));

            byte[] myData = new byte[(int) rawFile.length()];
            inFile.read(myData);
            outFile.write(myData); // 44 - the actual data itself - just a long
            // string of numbers
            inFile.close();
            //   rawFile.delete();
            outFile.close();
        } catch (Exception e) {
            System.out.println("VT = " + e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean saveWavToPCM(String waveFile, String pcmFileName) {

        try {
            FileChannel fcIn = null;
            File file = new File(pcmFileName);
            
            // append or overwrite the file
            boolean append = false;
            
            FileChannel fcOut = new FileOutputStream(file, append).getChannel();
            
            int fftHardware = 2048;
            
            String state = Environment.getExternalStorageState();
            int bufferSizeInBytes = fftHardware * 2;
            
            ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSizeInBytes);
         //   float data_float[] = null;
            ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
            
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                try {
                    
                    if (waveFile == null) {

                        return false;
                    }
                    
                     file = new File(waveFile);
                    
                    if (!file.exists()) {
                        return false;
                    }
                    
                    fcIn = new FileInputStream(file).getChannel();
                    
                    int N = fftHardware * 2;  // total short
                    if (byteBuffer == null) {
                        byteBuffer = ByteBuffer.allocateDirect(N); // Set
                    } else if (byteBuffer.capacity() != N) {
                        byteBuffer = ByteBuffer.allocateDirect(N);
                    }
                    byteBuffer.rewind();
                    byteBuffer.order(byteOrder);
                    // byteBuffers.clear();
                    
                    int nRead = 0;
                                        
                    try {
                        fcIn.position(44); //Skip the 44 wav header file
                        
                        while ((nRead = fcIn.read(byteBuffer)) > 0) { // Rewind
                            int i = 0;
                            byteBuffer.rewind();
                            fcOut.write(byteBuffer);
                            byteBuffer.rewind();
                            
                            
                            if (Thread.currentThread().isInterrupted()) {
                                break;
                            }
                        }
                        
                        fcIn.close();
                        fcOut.close();
                        return true;
                        
                    } catch (IOException e) {
                         Log.v("VT", " wavIO 1 = " + e.getMessage());
                        e.printStackTrace();
                        
                    }
                    
                } catch (Exception e) {
                     Log.v("VT", " wavIO 2 = " + e.getMessage());
                    e.printStackTrace();
                    
                }
            }
        } catch (FileNotFoundException e) {
           Log.v("VT", " wavIO 3 = " + e.getMessage());
        }

        return false;
    }

    public static boolean savePCMToWav(String rawMonoPCMFileName, String wavFileName,
            int numOfChannels, int sampleRate) {
        try {

            int chunkSize;
            int dataSize;
            int subchunk2Size;
            int subChunk1Size = 16;
            int format = 1; // 1 for PCM
            int bitsPerSample = 16;
            int shortPerBytes = 2;

            int blockAlign = numOfChannels * bitsPerSample / 8; // 2; // ==
            // NumChannels *
            // BitsPerSample/8
            int avgBytesPerSec = sampleRate * blockAlign; // SampleRate *
            // NumChannels *
            // BitsPerSample/8
            // int myByteRate = 88200 ; //SampleRate * NumChannels *
            // BitsPerSample/8

            File rawFile;
            File wavFile;

            rawFile = new File(rawMonoPCMFileName);
            long totalBytes = rawFile.length(); // to simulate 2 channel stereo
            // from mono
            long numOfSamples = (totalBytes / numOfChannels) / shortPerBytes;

            subchunk2Size = (int) numOfSamples * numOfChannels * bitsPerSample
                    / 8; // NumSamples * NumChannels * BitsPerSample/8

            chunkSize = subchunk2Size + 36;

            DataOutputStream outFile = new DataOutputStream(
                    new FileOutputStream(wavFileName));

            // write the WAV file per the WAV file format
            outFile.writeBytes("RIFF"); // 00 - RIFF
            outFile.write(intToByteArray(chunkSize), 0, 4); // 04 - how big is

            // the rest of
            // this file?
            outFile.writeBytes("WAVE"); // 08 - WAVE
            outFile.writeBytes("fmt "); // 12 - fmt
            outFile.write(intToByteArray(subChunk1Size), 0, 4); // 16 - size
            // of this
            // chunk
            outFile.write(intToByteArray(format), 0, 2); // 20 - what is the
            // audio format? 1
            // for PCM = Pulse
            // Code Modulation
            outFile.write(intToByteArray(numOfChannels), 0, 2); // 22 - mono or
            // stereo? 1 or
            // 2? (or 5 or
            // ???)
            outFile.write(intToByteArray(sampleRate), 0, 4); // 24 - samples
            // per second
            // (numbers per
            // second)
            outFile.write(intToByteArray(avgBytesPerSec), 0, 4); // 28 - bytes
            // per
            // second
            outFile.write(intToByteArray(blockAlign), 0, 2); // 32 - # of
            // bytes in one
            // sample, for
            // all channels
            outFile.write(intToByteArray(bitsPerSample), 0, 2); // 34 - how

            // many bits
            // in a
            // sample(number)?
            // usually
            // 16 or 24
            outFile.writeBytes("data"); // 36 - data from
            outFile.write(intToByteArray(subchunk2Size), 0, 4); // 40 - how big
            // is this data
            // chunk

            DataInputStream inFile = new DataInputStream(new FileInputStream(
                    rawMonoPCMFileName));

            // ///////////
            byte[] myData = new byte[2048];

            int readCount = 0;

            while ((readCount = inFile.read(myData)) != -1) {
                outFile.write(myData); // 44 - the actual data itself - just a
                // long
            }

            inFile.close();
            outFile.flush();

            // rawFile.delete();
            outFile.close();
        } catch (Exception e) {
            System.out.println("VT = " + e.getMessage());
            return false;
        }
        return true;
    }
    // ////////////
    // Write wav file header
    // ///////////

    public static boolean writeWavFileHeaderToPCM(String wavFileName, int numOfChannels,
            int sampleRate) {

        try {

            File file = new File(wavFileName);

            RandomAccessFile ra = new RandomAccessFile(file, "rw");

            FileChannel fc = ra.getChannel();

            // FileChannel fc = new FileOutputStream(file,true).getChannel();
            int chunkSize;
            int subchunk2Size;
            int subChunk1Size = 16;
            int format = 1; // 1 for PCM

            long totalBytes = file.length(); // to simulate 2 channel stereo
            // from mono

            int bitsPerSample = 16;
            int shortPerBytes = 2;

            int blockAlign = numOfChannels * bitsPerSample / 8; // 2; // ==
            // NumChannels *
            // BitsPerSample/8
            int avgBytesPerSec = sampleRate * blockAlign; // SampleRate *
            // NumChannels *
            // BitsPerSample/8

            long numOfSamples = (totalBytes / numOfChannels) / shortPerBytes;

            subchunk2Size = (int) numOfSamples * numOfChannels * bitsPerSample
                    / 8; // NumSamples * NumChannels * BitsPerSample/8

            chunkSize = subchunk2Size + 36;

            // outFile.writeBytes("RIFF"); // 00 - RIFF
            fc.write(ByteBuffer.wrap("RIFF".getBytes()), 0);

            // outFile.write(intToByteArray(chunkSize), 0, 4); // 04 - how big
            // is
            fc.write(ByteBuffer.wrap(intToByteArray(chunkSize)), 4);

            // the rest of
            // this file?
            // outFile.writeBytes("WAVE"); // 08 - WAVE
            fc.write(ByteBuffer.wrap("WAVE".getBytes()), 8);

            // outFile.writeBytes("fmt "); // 12 - fmt
            fc.write(ByteBuffer.wrap("fmt ".getBytes()), 12);

            // outFile.write(intToByteArray(subChunk1Size), 0, 4); // 16 - size
            fc.write(ByteBuffer.wrap(intToByteArray(subChunk1Size)), 16);
            // of this
            // chunk
            // outFile.write(intToByteArray(format), 0, 2); // 20 - what is
            fc.write(ByteBuffer.wrap(intToByteArray(format)), 20);

            // audio format? 1
            // for PCM = Pulse
            // Code Modulation
            // outFile.write(intToByteArray(numOfChannels), 0, 2); // 22 - mono
            // or
            fc.write(ByteBuffer.wrap(intToByteArray(numOfChannels)), 22);
            // stereo? 1 or
            // 2? (or 5 or
            // ???)
            // outFile.write(intToByteArray(sampleRate), 0, 4); // 24 - samples
            fc.write(ByteBuffer.wrap(intToByteArray(sampleRate)), 24);
            // per second
            // (numbers per
            // second)
            // outFile.write(intToByteArray(avgBytesPerSec), 0, 4); // 28 -
            // bytes per
            fc.write(ByteBuffer.wrap(intToByteArray(avgBytesPerSec)), 28);
            // second
            // outFile.write(intToByteArray(blockAlign), 0, 2); // 32 - #
            fc.write(ByteBuffer.wrap(intToByteArray(blockAlign)), 32);

            // bytes in one
            // sample, for
            // all channels
            // outFile.write(intToByteArray(bitsPerSample), 0, 2); // 34 - how
            fc.write(ByteBuffer.wrap(intToByteArray(bitsPerSample)), 34);
            // many bits
            // in a
            // sample(number)?
            // usually
            // 16 or 24
            // outFile.writeBytes("data"); // 36 - data
            fc.write(ByteBuffer.wrap("data".getBytes()), 36);
            // outFile.write(intToByteArray(subchunk2Size), 0, 4); // 40 - how
            fc.write(ByteBuffer.wrap(intToByteArray(subchunk2Size)), 40);

            fc.close();
            return true;

            // /////////
        } catch (Exception e) {
            System.out.println("VT = " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static short[] fillAndRepeastShortData(short[] data, int number_of_repeat, int numberOfZeroFill) {

        int totalData = data.length * number_of_repeat + (number_of_repeat - 1) * numberOfZeroFill;

        short[] returnData = new short[totalData];
        int dataLen = data.length;

        int n = 0;
        for (int k = 0; k < number_of_repeat; k++) {
            for (int i = 0; i < dataLen; i++) {
                returnData[n] = data[i];
                n++;
            }
            if (k < (number_of_repeat - 1)) {

                for (int j = 0; j < numberOfZeroFill; j++) {
                    returnData[n] = data[j];
                    n++;
                }
            }
        }

        return returnData;

    }

    public static boolean saveShorBufferTsWav(short[] data, int sampleRate,
                                              int nChannel, String wavFileName) {

        try {

            int myChunkSize;
            int myDataSize;
            int myRawFileData;
            int mySubChunk1Size = 16;
            int myFormat = 1;

            int myBitsPerSample = 16;

            int myBlockAlign = nChannel * myBitsPerSample / 8; // 2; // ==
            // NumChannels *
            // BitsPerSample/8
            // int myByteRate = 44100*myChannels*myBitsPerSample/8 ;
            // //SampleRate * NumChannels * BitsPerSample/8
            int myByteRate = sampleRate * nChannel * myBitsPerSample / 8; // SampleRate
            // *
            // NumChannels
            // *
            // BitsPerSample/8

            File wavFile;

            int totalDataInBytes = data.length * 2; // because 2 bytes equal 1
            // short.

            myChunkSize = totalDataInBytes + 36;
            myRawFileData = totalDataInBytes;

            DataOutputStream outFile = new DataOutputStream(
                    new FileOutputStream(wavFileName));

            // write the WAV file per the WAV file format
            outFile.writeBytes("RIFF"); // 00 - RIFF
            outFile.write(intToByteArray(myChunkSize), 0, 4); // 04 - how big is
            // the rest of
            // this file?
            outFile.writeBytes("WAVE"); // 08 - WAVE
            outFile.writeBytes("fmt "); // 12 - fmt
            outFile.write(intToByteArray(mySubChunk1Size), 0, 4); // 16 - size
            // of this
            // chunk
            outFile.write(intToByteArray(myFormat), 0, 2); // 20 - what is the
            // audio format? 1
            // for PCM = Pulse
            // Code Modulation
            outFile.write(intToByteArray(nChannel), 0, 2); // 22 - mono or
            // stereo? 1 or
            // 2? (or 5 or
            // ???)
            outFile.write(intToByteArray(sampleRate), 0, 4); // 24 - samples
            // per second
            // (numbers per
            // second)
            outFile.write(intToByteArray(myByteRate), 0, 4); // 28 - bytes per
            // second
            outFile.write(intToByteArray(myBlockAlign), 0, 2); // 32 - # of
            // bytes in one
            // sample, for
            // all channels
            outFile.write(intToByteArray(myBitsPerSample), 0, 2); // 34 - how
            // many bits
            // in a
            // sample(number)?
            // usually
            // 16 or 24
            outFile.writeBytes("data"); // 36 - data
            outFile.write(intToByteArray(myRawFileData), 0, 4); // 40 - how big
            // is this data
            // chunk

            ByteBuffer result = ByteBuffer.allocate(data.length * 2);

            result.order(ByteOrder.nativeOrder());

            for (short s : data) {
                result.putShort(s);
            }

            outFile.write(result.array()); // 44 - the actual data itself - just
            // a long

            outFile.flush();
            outFile.close();
        } catch (Exception e) {
            System.out.println("VT = " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean saveAsWavMonoToStero(String rawMonoPCMFileName,
            String wavFileName) {
        try {

            int chunkSize;
            int dataSize;
            int subchunk2Size;
            int subChunk1Size = 16;
            int format = 1; // 1 for PCM
            int numOfChannels = 2;
            int sampleRate = 44100;
            int bitsPerSample = 16;
            int shortPerBytes = 2;

            int blockAlign = numOfChannels * bitsPerSample / 8; // 2; // ==
            // NumChannels *
            // BitsPerSample/8
            int avgBytesPerSec = sampleRate * blockAlign; // SampleRate *
            // NumChannels *
            // BitsPerSample/8
            // int myByteRate = 88200 ; //SampleRate * NumChannels *
            // BitsPerSample/8

            File rawFile;
            File wavFile;

            rawFile = new File(rawMonoPCMFileName);
            long totalBytes = rawFile.length() * numOfChannels; // to simulate 2
            // channel
            // stereo from
            // mono
            long numOfSamples = (totalBytes / numOfChannels) / shortPerBytes;

            subchunk2Size = (int) numOfSamples * numOfChannels * bitsPerSample
                    / 8; // NumSamples * NumChannels * BitsPerSample/8

            chunkSize = subchunk2Size + 36;

            DataOutputStream outFile = new DataOutputStream(
                    new FileOutputStream(wavFileName));

            // write the WAV file per the WAV file format
            outFile.writeBytes("RIFF"); // 00 - RIFF
            outFile.write(intToByteArray(chunkSize), 0, 4); // 04 - how big is

            // the rest of
            // this file?
            outFile.writeBytes("WAVE"); // 08 - WAVE
            outFile.writeBytes("fmt "); // 12 - fmt
            outFile.write(intToByteArray(subChunk1Size), 0, 4); // 16 - size
            // of this
            // chunk
            outFile.write(intToByteArray(format), 0, 2); // 20 - what is the
            // audio format? 1
            // for PCM = Pulse
            // Code Modulation
            outFile.write(intToByteArray(numOfChannels), 0, 2); // 22 - mono or
            // stereo? 1 or
            // 2? (or 5 or
            // ???)
            outFile.write(intToByteArray(sampleRate), 0, 4); // 24 - samples
            // per second
            // (numbers per
            // second)
            outFile.write(intToByteArray(avgBytesPerSec), 0, 4); // 28 - bytes
            // per
            // second
            outFile.write(intToByteArray(blockAlign), 0, 2); // 32 - # of
            // bytes in one
            // sample, for
            // all channels
            outFile.write(intToByteArray(bitsPerSample), 0, 2); // 34 - how
            // many bits
            // in a
            // sample(number)?
            // usually
            // 16 or 24
            outFile.writeBytes("data"); // 36 - data
            outFile.write(intToByteArray(subchunk2Size), 0, 4); // 40 - how big
            // is this data
            // chunk

            DataInputStream inFile = new DataInputStream(new FileInputStream(
                    rawMonoPCMFileName));

            // ///////////
            byte[] myDataIn = new byte[1024];
            byte[] myDataOut = new byte[2048];

            int readCount = 0;

            long start = System.currentTimeMillis();
            while ((readCount = inFile.read(myDataIn)) != -1) {

                int len = readCount / 4;
                for (int i = 0; i < len; i++) {

                    int index = i * 4;
                    int index2 = i * 8;

                    myDataOut[index2 + 0] = myDataIn[index];
                    myDataOut[index2 + 1] = myDataIn[index + 1];
                    myDataOut[index2 + 2] = myDataIn[index + 2];
                    myDataOut[index2 + 3] = myDataIn[index + 3];
                    myDataOut[index2 + 4] = 0;
                    myDataOut[index2 + 5] = 0;
                    myDataOut[index2 + 6] = 0;
                    myDataOut[index2 + 7] = 0;
                }

                outFile.write(myDataOut);

            }
            long stop = System.currentTimeMillis();
            long diff = (stop - start) / 1000;

            inFile.close();
            outFile.flush();

            // rawFile.delete();
            outFile.close();
        } catch (Exception e) {
            System.out.println("VT = " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean saveAsWavMonoToStero__new(String rawMonoPCMFileName,
            String wavFileName) {
        try {

            int chunkSize;
            int dataSize;
            int subchunk2Size;
            int subChunk1Size = 16;
            int format = 1; // 1 for PCM
            int numOfChannels = 2;
            int sampleRate = 44100;
            int bitsPerSample = 16;
            int shortPerBytes = 2;

            int blockAlign = numOfChannels * bitsPerSample / 8; // 2; // ==
            // NumChannels *
            // BitsPerSample/8
            int avgBytesPerSec = sampleRate * blockAlign; // SampleRate *
            // NumChannels *
            // BitsPerSample/8
            // int myByteRate = 88200 ; //SampleRate * NumChannels *
            // BitsPerSample/8

            File rawFile;
            File wavFile;

            rawFile = new File(rawMonoPCMFileName);
            long totalBytes = rawFile.length() * numOfChannels; // to simulate 2
            // channel
            // stereo from
            // mono
            long numOfSamples = (totalBytes / numOfChannels) / shortPerBytes;

            subchunk2Size = (int) numOfSamples * numOfChannels * bitsPerSample
                    / 8; // NumSamples * NumChannels * BitsPerSample/8

            chunkSize = subchunk2Size + 36;

            DataOutputStream outFile = new DataOutputStream(
                    new FileOutputStream(wavFileName));

            // write the WAV file per the WAV file format
            outFile.writeBytes("RIFF"); // 00 - RIFF
            outFile.write(intToByteArray(chunkSize), 0, 4); // 04 - how big is

            // the rest of
            // this file?
            outFile.writeBytes("WAVE"); // 08 - WAVE
            outFile.writeBytes("fmt "); // 12 - fmt
            outFile.write(intToByteArray(subChunk1Size), 0, 4); // 16 - size
            // of this
            // chunk
            outFile.write(intToByteArray(format), 0, 2); // 20 - what is the
            // audio format? 1
            // for PCM = Pulse
            // Code Modulation
            outFile.write(intToByteArray(numOfChannels), 0, 2); // 22 - mono or
            // stereo? 1 or
            // 2? (or 5 or
            // ???)
            outFile.write(intToByteArray(sampleRate), 0, 4); // 24 - samples
            // per second
            // (numbers per
            // second)
            outFile.write(intToByteArray(avgBytesPerSec), 0, 4); // 28 - bytes
            // per
            // second
            outFile.write(intToByteArray(blockAlign), 0, 2); // 32 - # of
            // bytes in one
            // sample, for
            // all channels
            outFile.write(intToByteArray(bitsPerSample), 0, 2); // 34 - how
            // many bits
            // in a
            // sample(number)?
            // usually
            // 16 or 24
            outFile.writeBytes("data"); // 36 - data
            outFile.write(intToByteArray(subchunk2Size), 0, 4); // 40 - how big
            // is this data
            // chunk

            DataInputStream inFile = new DataInputStream(new FileInputStream(
                    rawMonoPCMFileName));

            // ///////////
            byte[] myDataIn = new byte[1024];
            byte[] myDataOut = new byte[2048];

            int readCount = 0;

            long start = System.currentTimeMillis();
            while ((readCount = inFile.read(myDataIn)) != -1) {

                int len = readCount / 4;
                for (int i = 0; i < len; i++) {

                    int index = i * 4;
                    int index2 = i * 8;

                    myDataOut[index2 + 0] = myDataIn[index];
                    myDataOut[index2 + 1] = myDataIn[index + 1];
                    myDataOut[index2 + 2] = myDataIn[index + 2];
                    myDataOut[index2 + 3] = myDataIn[index + 3];
                    myDataOut[index2 + 4] = 0;
                    myDataOut[index2 + 5] = 0;
                    myDataOut[index2 + 6] = 0;
                    myDataOut[index2 + 7] = 0;
                }

                outFile.write(myDataOut);

            }
            long stop = System.currentTimeMillis();
            long diff = (stop - start) / 1000;

            inFile.close();
            outFile.flush();

            // rawFile.delete();
            outFile.close();
        } catch (Exception e) {
            System.out.println("VT = " + e.getMessage());
            return false;
        }
        return true;
    }

    public static void convertShortBufferToByteBuffer(short[] shortArray,
                                                      ByteBuffer result) {

        // ByteBuffer bb = ByteBuffer.allocate(shortArray.length * 2);
        for (short s : shortArray) {
            result.putShort(s);
        }
        // return bb.array();
    }

    public static byte[] convertShortBufferToByteBuffer(short[] shortArray) {

        ByteBuffer result = ByteBuffer.allocate(shortArray.length * 2);
        for (short s : shortArray) {
            result.putShort(s);
        }
        return result.array();
    }

    // ===========================
    // CONVERT BYTES TO JAVA TYPES
    // ===========================
    // these two routines convert a byte array to a unsigned short
    public static int byteArrayToInt(byte[] b) {
        int start = 0;
        int low = b[start] & 0xff;
        int high = b[start + 1] & 0xff;
        return high << 8 | low;
    }

    // these two routines convert a byte array to an unsigned integer
    public static long byteArrayToLong(byte[] b) {
        int start = 0;
        int i = 0;
        int len = 4;
        int cnt = 0;
        byte[] tmp = new byte[len];
        for (i = start; i < (start + len); i++) {
            tmp[cnt] = b[i];
            cnt++;
        }
        long accum = 0;
        i = 0;
        for (int shiftBy = 0; shiftBy < 32; shiftBy += 8) {
            accum |= ((long) (tmp[i] & 0xff)) << shiftBy;
            i++;
        }
        return accum;
    }

    // ===========================
    // CONVERT JAVA TYPES TO BYTES
    // ===========================
    // returns a byte array of length 4
    public static byte[] intToByteArray(int i) {
        byte[] b = new byte[4];
        b[0] = (byte) (i & 0x00FF);
        b[1] = (byte) ((i >> 8) & 0x000000FF);
        b[2] = (byte) ((i >> 16) & 0x000000FF);
        b[3] = (byte) ((i >> 24) & 0x000000FF);
        return b;
    }

    // convert a short to a byte array
    public static byte[] shortToByteArray(short data) {
        return new byte[]{(byte) (data & 0xff), (byte) ((data >>> 8) & 0xff)};
    }
}
