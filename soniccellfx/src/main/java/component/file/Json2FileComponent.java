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
import com.sonicmsgr.util.JsonUtil;
import java.io.File;
import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author yada
 */
public class Json2FileComponent extends ComponentAbstraction {

    private String savePath = "path";
    private boolean processingBool = false;
    private String data = "";

    public Json2FileComponent() {
        setName("Json2File");
        this.setProperty("savePath", savePath);
        this.addInput(new DataTypeIO("string", "json"));
        this.addOutput(new DataTypeIO("boolean", "Success or Fail"));
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
                JsonUtil.jsonToFile(data, savePath);
                sendData(0,true);
            } catch (Exception e) {
                PubSubSingleton.getIntance().send("Print", e.getMessage());
            }
        }

        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {

        if (thru == 0) {
            data = (String) obj;
             processingBool = true;
        }
    }

    @Override
    public boolean start() {
        savePath = getProperty("savePath").toString();
        processingBool = true;
        
        return true;
    }

    @Override
    public void stop() {
       processingBool = false;
    }

    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("savePath")) {
            savePath = (String) val;
        }
    }

    @Override
    public void mouseDoubleClick() {
                Platform.runLater(new Runnable() {
            @Override
            public void run() {
                DirectoryChooser fileChooser = new DirectoryChooser();
                fileChooser.setTitle("Saved File Path");
                Stage stage = new Stage();
                File selectedFile = fileChooser.showDialog(stage);

                if (selectedFile != null) {
                    savePath = selectedFile.getAbsoluteFile().toString();
                    setProperty("savePath", savePath);
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
