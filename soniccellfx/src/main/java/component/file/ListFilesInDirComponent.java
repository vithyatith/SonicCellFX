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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author yada
 */
public class ListFilesInDirComponent extends ComponentAbstraction {

    private String folderName = ".";
    private boolean processingBool = false;
    private String extension = "*";

    public ListFilesInDirComponent() {
        setName("ListFiles");
        this.setProperty("folderName", folderName);
        this.setProperty("extension", extension);
        this.addOutput(new DataTypeIO("string[]", "List all files in folder"));
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
                String files[] = com.sonicmsgr.util.FileUtils.listAllFilesFromDirectoryStringArray(folderName, extension);
                sendData(0, files);
            } catch (Exception e) {
                printToConsole(e.getLocalizedMessage());
            }
        }

        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {

//            processingBool = true;
//
//            folderName = (String) obj;
    }

    @Override
    public boolean start() {
        folderName = getProperty("folderName").toString();
         extension = getProperty("extension").toString();

        File file = new File(folderName);
        if (file.exists()) {
            processingBool = true;
        } else {
            processingBool = false;
            printToConsole(folderName + " doesn't exist.");
        }

        return true;
    }

    @Override
    public void stop() {
        processingBool = false;
    }

    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("folderName")) {
            folderName = (String) val;
        } else if (key.equalsIgnoreCase("extension")) {
            extension = (String) val;
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
                    folderName = selectedFile.getAbsoluteFile().toString();
                    setProperty("folderName", folderName);
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
