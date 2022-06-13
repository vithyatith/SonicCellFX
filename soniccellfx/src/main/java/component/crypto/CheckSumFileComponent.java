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
package component.crypto;

import com.sonicmsgr.util.CheckSum;
import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.msg.PubSubSingleton;
import java.io.File;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Vithya Tith
 */
public class CheckSumFileComponent extends ComponentAbstraction {

    private String filename = "file.bin";
    private boolean readyBool = false;
    private String previousFileName = "";
    private final CheckSum checkSum = new CheckSum();
    private boolean shortFormat = true;

    public CheckSumFileComponent() {
        setName("ChecksumFile");
        this.setProperty("filename", filename);
        this.setProperty("shortFormat", shortFormat);

        this.addInput(new DataTypeIO("string", "File  to checksum", "filename"));

        this.addOutput(new DataTypeIO("string", "md5"));
        this.addOutput(new DataTypeIO("string", "sha1"));
        this.addOutput(new DataTypeIO("string", "sha-256"));
        this.addOutput(new DataTypeIO("string", "sha-512"));
        this.setHasUISupport(true);
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public Object onExecute() {
        if (readyBool == false) {
            return null;
        }
        readyBool = false;
        try {

            checkSum.processChecksumFile(filename);

            if (shortFormat) {
                sendData(0, checkSum.getMD5().replace(" ", ""));
                sendData(1, checkSum.getSHA1().replace(" ", ""));
                sendData(2, checkSum.getSHA256().replace(" ", ""));
                sendData(3, checkSum.getSHA512().replace(" ", ""));
            } else {
                sendData(0, checkSum.getMD5());
                sendData(1, checkSum.getSHA1());
                sendData(2, checkSum.getSHA256());
                sendData(3, checkSum.getSHA512());
            }

        } catch (Exception e) {
            PubSubSingleton.getIntance().send("Print", "Error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {

        if (thru == 0) {
            filename = (String) obj;
        }

        filename = (String) obj;
        if (!previousFileName.equalsIgnoreCase(filename)) {
            previousFileName = filename;
            File file = new File(filename);
            if (file.exists()) {
                readyBool = true;
            } else {
                readyBool = false;
            }
        }

    }

    @Override
    public boolean start() {
        readyBool = false;
        filename = getProperty("filename").toString();
        shortFormat = Boolean.parseBoolean(getProperty("shortFormat").toString());

        int size = this.getNumberOfInputOccupy();
        System.out.println("size = " + size);

        File file = new File(filename);
        if (file.exists()) {
            readyBool = true;
            previousFileName = filename;
        } else {
            PubSubSingleton.getIntance().send("Print", "File doesn't exist " + filename);
        }

        return true;
    }

    @Override
    public void stop() {

    }

    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("filename")) {
            filename = getProperty("filename").toString();
        }  else if (key.equalsIgnoreCase("method")) {
            shortFormat = Boolean.parseBoolean(getProperty("shortFormat").toString());
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

    public static void main(String rgs[]) {
        CheckSumFileComponent com = new CheckSumFileComponent();
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
