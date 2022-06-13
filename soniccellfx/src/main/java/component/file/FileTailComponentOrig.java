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
package component.file;

import component.basic.*;
import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.msg.PubSubSingleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Scanner;
import org.apache.commons.io.input.TailerListener;

/**
 *
 * @author yada
 */
public class FileTailComponentOrig extends ComponentAbstraction {

    private String filename = "";
    private String encoding = "UTF-8";
    private String NL = System.getProperty("line.separator");
    private boolean processingBool = false;
    private boolean runningBool = false;
    private int updateInterval = 1000;
    private long filePointer = 0;
    private File file = new File("/Users/yada/tmp/test.out");
    private boolean initBool = false;
    
    TailerListener tail;

    public FileTailComponentOrig() {
        setName("Tail");
        this.setProperty("filename", filename);
        this.setProperty("updateInterval", updateInterval);
        this.addInput(new DataTypeIO("string", "File name"));
        this.addOutput(new DataTypeIO("string", "Tail new line"));
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public Object onExecute() {
        if (processingBool) {
            processingBool = false;
            try {

                while (runningBool) {
                    Thread.sleep(updateInterval);
                    long len = file.length();
                    if (len < filePointer) {
                        // Log must have been jibbled or deleted.
                        // this.appendMessage("Log file was reset. Restarting logging from start of file.");
                        filePointer = len;
                    } else if (len > filePointer) {
                        // File must have had something added to it!
                        RandomAccessFile raf = new RandomAccessFile(file, "r");
                        
                        if(initBool==false){
                            filePointer = file.length();
                            initBool = true;
                        }
                        raf.seek(filePointer);
                        
                        
                        String line = null;
                        while ((line = raf.readLine()) != null) {
                            if(runningBool==false){
                                break;
                            }
                           
                            if(line.equalsIgnoreCase("")){
                               System.out.println(" continue because space detected");
                               continue;   
                            }
                            sendData(0,line);
                        }
                        filePointer = raf.getFilePointer();
                        raf.close();
                    }
                }
            } catch (FileNotFoundException e) {
                //  e.printStackTrace();
                PubSubSingleton.getIntance().send("Print", e.getMessage());
                //  return e.getMessage();
            } catch (Exception e) {
                //  e.printStackTrace();
                PubSubSingleton.getIntance().send("Print", e.getMessage());
                //  return e.getMessage();
            }
            // return text.toString();
        }

        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {
        filename = (String) obj;
        startFileProcessing();
    }

    @Override
    public boolean start() {
        filename = getProperty("filename").toString();
        startFileProcessing();
        return true;
    }

    @Override
    public void stop() {
        processingBool = false;
        runningBool = false;

    }

    private void startFileProcessing() {

        File fileTmp = new File(filename);
        if (fileTmp.exists()) {
            processingBool = true;
            runningBool = true;
            file = fileTmp;
        }else{
            PubSubSingleton.getIntance().send("Print", "File not found "+filename);
        }

    }

    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("filename")) {
            filename = (String) val;
        }
    }

    @Override
    public void mouseDoubleClick() {
    }

    @Override
    public int getPlatformSupport() {

        return 0;
    }

    @Override
    public void onPropertyChanged(String key, Object value) {

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
