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

import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.msg.PubSubSingleton;
import java.io.File;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author yada
 */
public class FileReaderFullyBinaryComponent extends ComponentAbstraction {

    private String filename = "input.bin";
    private boolean processingBool = false;

    public FileReaderFullyBinaryComponent() {
        setName("ReadBinFull");
        this.setProperty("filename", filename);
        this.addInput(new DataTypeIO("string", "Input file name", "filename"));
        this.addOutput(new DataTypeIO("byte[]", "byte array"));
        this.addOutput(new DataTypeIO("boolean", "Is successfule read?"));
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
                byte bytes[] = com.sonicmsgr.util.FileUtils.readBinaryFile(filename);
                sendData(0, bytes);
                sendData(1, true);
            } catch (Exception e) {
                sendData(1, false);
                e.printStackTrace();
                PubSubSingleton.getIntance().send("Print", e.getMessage());
            }
        }

        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {

        if (thru == 0) {
            filename = (String) obj;
            File file = new File(filename);
            if (file.exists()) {
                processingBool = true;
            } else {
                processingBool = false;
                sendData(1, false);
                PubSubSingleton.getIntance().send("Print", filename + " doesn't exist");
            }

        }
    }

    @Override
    public boolean start() {
        filename = getProperty("filename").toString();
        if(filename.trim().equalsIgnoreCase("")){
            
            return false;
        }
        File file = new File(filename);
        if (file.exists()) {
            processingBool = true;

        } else {
            sendData(1, false);
            PubSubSingleton.getIntance().send("Print", "'"+filename + "' doesn't exist");
        }
        return true;
    }

    @Override
    public void stop() {

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
