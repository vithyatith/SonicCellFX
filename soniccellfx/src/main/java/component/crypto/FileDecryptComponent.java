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

import component.file.*;
import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.msg.PubSubSingleton;
import com.sonicmsgr.util.encryption.FileCrypto;
import java.io.File;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author yada
 */
public class FileDecryptComponent extends ComponentAbstraction {

    private String encryptedFile = "bin.bin";
    private String outputFilename = "out.bin";
    private boolean processingBool = false;
    private String password = "sonic123";

    public FileDecryptComponent() {
        setName("WriteBinFull");
        this.setProperty("encryptedFile", encryptedFile);
        this.setProperty("outputFilename", outputFilename);
        this.setProperty("password", password);
        this.addInput(new DataTypeIO("string", "File to decrypt", "encryptedFile"));
        this.addInput(new DataTypeIO("string", "Save encrypted file as..", outputFilename));

        this.addOutput(new DataTypeIO("boolean", "result"));
        this.setHasUISupport(true);
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public Object onExecute() {

        if (processingBool) {
            processingBool = false;
            try {
                boolean b = FileCrypto.decrypt(encryptedFile, outputFilename, password);
                sendData(0, b);
            } catch (Exception e) {
                sendData(0, false);
                e.printStackTrace();
                PubSubSingleton.getIntance().send("Print", e.getMessage());
            }
        }

        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {

        if (thru == 0) {
            encryptedFile = (String) obj;
        } else if (thru == 1) {
            outputFilename = (String) obj;
        }
        processingBool = true;
    }

    @Override
    public boolean start() {
        encryptedFile = getProperty("encryptedFile").toString();
        outputFilename = getProperty("outputFilename").toString();
        password = getProperty("password").toString();

        File file = new File(encryptedFile);
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
        if (key.equalsIgnoreCase("encryptedFile")) {
            encryptedFile = (String) val;
        } else if (key.equalsIgnoreCase("outputFilename")) {
            outputFilename = (String) val;
        } else if (key.equalsIgnoreCase("password")) {
            password = (String) val;
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
                    encryptedFile = selectedFile.getAbsoluteFile().toString();
                    setProperty("encryptedFile", encryptedFile);
                
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
