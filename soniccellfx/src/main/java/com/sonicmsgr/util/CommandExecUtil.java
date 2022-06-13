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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author yada
 */
public class CommandExecUtil {

    private Thread mainThread;
    private boolean runBool = false;
    private BufferedReader bufferReader;
    private ProcessBuilder processBuilder = null;
    private InputStreamReader inputStreamReader = null;
    private Process process = null;
    private ExecutorService executor = null;

    public CommandExecUtil() {

    }

    public void start(String commandline, final CommandResponse response) {
        if (response == null) {
            return;
        }
        try {

            processBuilder = new ProcessBuilder(commandline.split(" "));
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();

            runBool = true;
            mainThread = new Thread() {
                public void run() {
                    inputStreamReader = new InputStreamReader(process.getInputStream());
                    bufferReader = new BufferedReader(inputStreamReader);
                    String line = null;
                    try {
                        while ((line = bufferReader.readLine()) != null) {
                            if (runBool == false) {
                                System.out.println("Exiting processor while");
                                break;
                            }
                            response.onResult(line);
                            
                        }

                       
                    } catch (IOException e) {
                        Log.v("VT","CommandExec error = "+e.getMessage());
                        return;
                    }

                }
            };
            mainThread.setDaemon(true);
            mainThread.setPriority(Thread.MIN_PRIORITY);
            
            
   
            
            executor  = Executors.newSingleThreadExecutor();
            executor.submit(mainThread);
            mainThread.start();

        } catch (Exception ex) {

        }
    }

    public void stop() {
        runBool = false;

        if (inputStreamReader != null) {
            try {
                if (process != null) {
                   
                    process.getInputStream().close();
                    process.getOutputStream().flush();
                     process.getOutputStream().close();
                    System.out.print("exit value = "+process.exitValue());
                }
                inputStreamReader.close();

            } catch (Exception e) {

            }
        }

        if (bufferReader != null) {
            try {
                
                bufferReader.close();
            } catch (Exception e) {

            }
        }
        if (process != null) {
            try {
                System.out.println("Before Process kill before");
                process.destroy();
                System.out.println("After Process kill before");
            } catch (Exception e) {

            }
        }
        inputStreamReader = null;
        bufferReader = null;

        process = null;


        if (mainThread != null) {
            try {
                mainThread.interrupt();
            } catch (Exception e) {

            }
            Thread t = mainThread;
            t = null;
            mainThread = null;
        }

        if(executor!=null){
            executor.shutdownNow();
            System.out.println("Shutdown now");
        }
    }

    public interface CommandResponse {

        void onResult(String result);

    }
}
