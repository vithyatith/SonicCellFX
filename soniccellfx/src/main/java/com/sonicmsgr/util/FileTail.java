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
package com.sonicmsgr.util;

/**
 *
 * @author yada
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author https://crunchify.com
 */
public class FileTail implements Runnable {

    private final boolean debug = true;

    private int updateIntervals = 2000;
    private long lastKnownPosition = 0;
    private boolean shouldIRun = true;
    private File file = null;
    private static int crunchifyCounter = 0;

    private boolean initStart = true;
    private CallBack callBack = null;

    public FileTail() {

    }

    private void send(String message) {
        if(callBack!=null){
            callBack.onReceived(message);
        }
    }

    private ExecutorService executorService = null;

    public void startTail(String fileName, int updateIntervals, CallBack callBack) {
        executorService = Executors.newFixedThreadPool(4);
        shouldIRun = true;
        file = new File(fileName);
        this.updateIntervals = updateIntervals;
        initStart = true;
        this.callBack = callBack;
        executorService.execute(this);
    }

    public void stopTail() {
        shouldIRun = false;
        try {
            executorService.shutdown();
        } catch (Exception e) {

        }
    }

    public void run() {
        try {
            while (shouldIRun) {
                Thread.sleep(updateIntervals);
                long fileLength = file.length();
                if (fileLength > lastKnownPosition) {

                    if (initStart) {
                        initStart = false;
                        lastKnownPosition = fileLength;
                    }

                    // Reading and writing file
                    RandomAccessFile readWriteFileAccess = new RandomAccessFile(file, "rw");
                    readWriteFileAccess.seek(lastKnownPosition);
                    String line = null;
                    while ((line = readWriteFileAccess.readLine()) != null) {
                       send(line);
                        crunchifyCounter++;
                    }
                    lastKnownPosition = readWriteFileAccess.getFilePointer();
                    readWriteFileAccess.close();
                } else if (fileLength < lastKnownPosition) {

                    // Reading and writing file
                    RandomAccessFile readWriteFileAccess = new RandomAccessFile(file, "rw");
                    readWriteFileAccess.seek(0);
                    crunchifyCounter = 0;
                    String crunchifyLine = null;
                    String lastGoodLine = "";
                    while ((crunchifyLine = readWriteFileAccess.readLine()) != null) {

                        lastGoodLine = crunchifyLine;
                        crunchifyCounter++;
                    }
                    lastKnownPosition = readWriteFileAccess.getFilePointer();
                    readWriteFileAccess.close();

                    this.send(lastGoodLine);

                } 
            }
        } catch (Exception e) {
            stopTail();
        }
    }

//    public static void main(String argv[]) {
//
//        // Replace username with your real value
//        // For windows provide different path like: c:\\temp\\crunchify.log
//        String filePath = "/home/vithya/tail.txt";
//        FileTail fileTail = new FileTail();
//        fileTail.startTail(filePath, 2000, new CallBack(){
//            @Override
//            public void onReceived(String data) {
//
//                System.out.println(data);
//            }
//            
//        });
//        
//        fileTail.stopTail();
//
//    }
    
    public interface CallBack{
        void onReceived(String data);
    }
}