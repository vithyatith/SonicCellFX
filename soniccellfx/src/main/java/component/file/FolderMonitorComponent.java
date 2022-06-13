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
import com.sonicmsgr.util.FileTail;
import com.sonicmsgr.util.FileTail.CallBack;
import com.sonicmsgr.util.FolderMonitor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

/**
 *
 * @author yada
 */
public class FolderMonitorComponent extends ComponentAbstraction {

    private String foldername = "";
    private FolderMonitor fileWatcher = null;
    
    private boolean startBool = false;
    private Thread mainThread = null;
    private FolderMonitor.FileHandler handler = new FolderMonitor.FileHandler() {

        @Override
        public void handle(File file, WatchEvent.Kind<?> fileEvent) throws InterruptedException {

            String filename = file.getAbsolutePath();
            sendData(0, filename);

        }
    };
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    private void stopThread() {

        if (mainThread != null) {
            try {
                Thread th = mainThread;
                th.interrupt();
                th = null;
                mainThread = null;
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }


    public FolderMonitorComponent() {
        setName("FolderMon");
        this.setProperty("foldername", foldername);
        this.addOutput(new DataTypeIO("string", "Changed text"));
        this.setHasUISupport(true);
    }

    @Override
    public Object onExecute() {
        if (startBool) {
            try {
                startBool = false;
                stopThread();
                fileWatcher = null;
                fileWatcher = new FolderMonitor(foldername, handler, true, StandardWatchEventKinds.ENTRY_CREATE);
                mainThread = new Thread(fileWatcher);
                mainThread.start();

            } catch (IOException ex) {

            }

        }

        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {
    }

    @Override
    public boolean start() {
        foldername = getProperty("foldername").toString();
        startBool = true;

        return true;
    }

    @Override
    public void stop() {
        startBool = false;
        stopThread();
    }

    // Load from a file
    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("foldername")) {
            foldername = (String) val;
        }
    }

    @Override
    public void mouseDoubleClick() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                DirectoryChooser fileChooser = new DirectoryChooser();
            
                Stage stage = new Stage();
     
                File selectedFile = fileChooser.showDialog(stage);

                if (selectedFile != null) {
                    foldername = selectedFile.getAbsoluteFile().toString();
                    setProperty("foldername", foldername);
                    PubSubSingleton.getIntance().send("UPDATE_PROPERTIES", getId());
                }
            }

        });
    }

    @Override
    public int getPlatformSupport() {

        return 0;
    }

    @Override
    public void onPropertyChanged(String key, Object value) {
        loadProperty(key, value);
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
