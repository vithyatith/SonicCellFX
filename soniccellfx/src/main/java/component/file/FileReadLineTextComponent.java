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
import java.util.HashMap;
import java.util.Scanner;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author yada
 */
public class FileReadLineTextComponent extends ComponentAbstraction {

    private String filename = "";
    private String encoding = "UTF-8";
    private String NL = System.getProperty("line.separator");
    private boolean processingBool = false;
    private boolean exitBool = false;
    private Scanner scanner;
    private int skipLine = 0;
    private int sleep = 1;

    public FileReadLineTextComponent() {
        setName("ReadLIne");
        this.setProperty("filename", filename);
        this.setProperty("skipLine", skipLine);
        this.setProperty("sleep", sleep);
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

                scanner = new Scanner(new FileInputStream(filename),
                        encoding);
                try {
                    long count = 0;
                    boolean bool = true;
                    while (scanner.hasNextLine()) {
                        String s = scanner.nextLine();
                        if (count >= skipLine) {
                            try {
                                Thread.sleep(sleep);
                            } catch (Exception e) {

                            }
                            if (exitBool) {
                                bool = false;
                                break;
                            }

                            sendData(0, s);
                        }
                        count++;

                    }
                    sendData(1, bool);
                } finally {
                    scanner.close();
                }
            } catch (FileNotFoundException e) {
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
    }

    @Override
    public boolean start() {
        skipLine = (int) Double.parseDouble(getProperty("skipLine").toString());
        sleep = (int) Double.parseDouble(getProperty("sleep").toString());
        filename = getProperty("filename").toString();
        processingBool = true;
        exitBool = false;
        return true;
    }

    @Override
    public void stop() {
        processingBool = false;
        exitBool = true;
        if (scanner != null) {
            try {
                scanner.close();
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("filename")) {
            filename = (String) val;
        } else if (key.equalsIgnoreCase("skipLine")) {
            skipLine = (int)Double.parseDouble(this.getProperty(key).toString());
        }else if (key.equalsIgnoreCase("sleep")) {
            sleep = (int)Double.parseDouble(this.getProperty(key).toString());
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
