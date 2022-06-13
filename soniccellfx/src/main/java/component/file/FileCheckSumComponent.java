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

import com.sonicmsgr.util.CheckSum;
import component.basic.*;
import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.msg.PubSubSingleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author yada
 */
public class FileCheckSumComponent extends ComponentAbstraction {

    private String filename = "";
    private boolean processingBool = false;
    private CheckSum checkSum = new CheckSum();

    public FileCheckSumComponent() {
        setName("CheckSum");
        this.setProperty("filename", filename);
        this.addInput(new DataTypeIO("string", "File name", "filename"));
        this.addOutput(new DataTypeIO("string", "MD5"));
        this.addOutput(new DataTypeIO("string", "SHA1"));
        this.addOutput(new DataTypeIO("string", "SHA-256"));
        this.addOutput(new DataTypeIO("string", "SHA-512"));
        this.setHasUISupport(true);
    }
    
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public Object onExecute() {
        if (processingBool) {
            processingBool = false;

            checkSum.processChecksumFile(filename);
            sendData(0, checkSum.getMD5());
            sendData(1, checkSum.getSHA1());
            sendData(2, checkSum.getSHA256());
            sendData(3, checkSum.getSHA512());
        }

        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {
        String thisFileName = (String) obj;

        if (!thisFileName.equalsIgnoreCase(filename)) {
            File file = new File(filename);
            if (file.exists()) {
                processingBool = true;
            } else {
                processingBool = false;
            }
        } else {
            processingBool = true;
        }

        filename = thisFileName;

    }

    @Override
    public boolean start() {
        filename = getProperty("filename").toString();
        File file = new File(filename);
        if (file.exists()) {
            processingBool = true;
        } else {
            processingBool = false;
        }

        return true;
    }

    @Override
    public void stop() {
        processingBool = false;
    }

    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("filename")) {
            filename = (String) val;
        }
    }

    @Override
    public void mouseDoubleClick() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FileChooser fileChooser = new FileChooser();
                Stage stage = new Stage();
                File selectedFile = fileChooser.showOpenDialog(stage);

                if (selectedFile != null) {
                    filename = selectedFile.getAbsoluteFile().toString();
                    setProperty("filename", filename);
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
