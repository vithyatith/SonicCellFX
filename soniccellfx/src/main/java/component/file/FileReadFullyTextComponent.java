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
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author yada
 */
public class FileReadFullyTextComponent extends ComponentAbstraction {

    private String filename = "";
    private String encoding = "UTF-8";
    private String NL = System.getProperty("line.separator");
    private boolean processingBool = false;
    private boolean exitBool = false;
    private FileInputStream fis = null;

    public FileReadFullyTextComponent() {
        setName("ReadFullTxt");
        this.setProperty("filename", filename);
        this.addInput(new DataTypeIO("string", "File name", "filename"));
        this.addOutput(new DataTypeIO("string", "Next line"));
        this.addOutput(new DataTypeIO("bool", "end of line"));
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

                File file = new File(filename);
                if(!file.exists()){
                     printToConsole(filename+" not found.");
                    return null;
                }
                
                fis = new FileInputStream(filename);

                byte[] data = new byte[(int) file.length()];
                try {
                    fis.read(data);
                    fis.close();
                    String str = new String(data, encoding);
                    sendData(0, str);
                } catch (IOException ex) {
                    printToConsole(ex.getLocalizedMessage());
                }
                sendData(1, true);

            } catch (FileNotFoundException e) {
                printToConsole(e.getLocalizedMessage());
            }
        }

        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {
        filename = (String) obj;
    }

    @Override
    public boolean start() {
        
        filename = getProperty("filename").toString();
        processingBool = true;
        exitBool = false;
        return true;
    }

    @Override
    public void stop() {
        processingBool = false;
        exitBool = true;
        if (fis != null) {
            try {
                fis.close();
            } catch (Exception e) {

            }
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
