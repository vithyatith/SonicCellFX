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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author yada
 */
public class FileWriterTextComponent extends ComponentAbstraction {

    private String filename = "";
    private boolean processingBool = false;
    private boolean append = false;
    private FileWriter fstream = null;
    private BufferedWriter out = null;
    private String message = "";
    private String NL = System.getProperty("line.separator");
    private boolean oneTime = true;

    public FileWriterTextComponent() {
        setName("WritTextFile");
        this.setProperty("filename", filename);
        this.setProperty("append", append);
        this.setProperty("oneTime", oneTime);

        this.addInput(new DataTypeIO("string", "Text"));
        this.addInput(new DataTypeIO("string", "Output file name", "filename"));
        this.addOutput(new DataTypeIO("bool", "Done processing write to file"));
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
                
                Object o= getProperty("oneTime");
         
                if(o instanceof Boolean){
                    oneTime = (Boolean)o;
                }else{
                    oneTime = Boolean.parseBoolean(getProperty("oneTime").toString());
                }
                
                
                if ((out == null) || (oneTime)) {
                    boolean b = startFileProcessing();
                    if (b == false) {
                        return null;
                    }
                }

                out.write(message + NL);
                out.flush();
                if (oneTime) {
                    closeFile();
                }
                sendData(0, new Boolean("true"));
            } catch (Exception e) {
                e.printStackTrace();
                PubSubSingleton.getIntance().send("Print", e.getMessage());
                sendData(0, new Boolean("false"));
            }
        }

        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {

        if (thru == 0) {
            message = (String) obj;
            processingBool = true;
        } else if (thru == 1) {
            filename = (String) obj;
            startFileProcessing();
        }
    }

    @Override
    public boolean start() {
        startFileProcessing();
        return true;
    }

    private boolean startFileProcessing() {
        oneTime = Boolean.parseBoolean(getProperty("oneTime").toString());
        append = Boolean.parseBoolean(getProperty("append").toString());
        filename = getProperty("filename").toString();
        closeFile();
        if (!filename.trim().equalsIgnoreCase("")) {

//            try {
//                File f = new File(filename);
//                if (f.exists()) {
//                    fstream = new FileWriter(filename, append);
//                    out = new BufferedWriter(fstream);
//                    processingBool = true;
//                    return true;
//                }else{
//                    PubSubSingleton.getIntance().send("Print", "file "+filename+" doesn't exist");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                
//            }
            try {

                fstream = new FileWriter(filename, append);
                out = new BufferedWriter(fstream);
                processingBool = true;
                return true;

            } catch (Exception e) {
                PubSubSingleton.getIntance().send("Print", "file " + filename + " doesn't exist");
                e.printStackTrace();

            }
        }

        return false;
    }

    private void closeFile() {
        processingBool = false;
        try {
            if (fstream != null) {
                fstream.flush();
                fstream.close();
            }
            fstream = null;
        } catch (java.io.IOException e) {
            //e.printStackTrace();
            PubSubSingleton.getIntance().send("Print", e.getMessage());
        } catch (Exception e) {
            //e.printStackTrace();
            PubSubSingleton.getIntance().send("Print", e.getMessage());
        }

        try {
            if (out != null) {
                out.flush();
                out.close();
            }

            out = null;
        } catch (java.io.IOException e) {
            //e.printStackTrace();
            //PubSubSingleton.getIntance().send("Print", e.getMessage());
        } catch (Exception e) {
            //e.printStackTrace();
            //PubSubSingleton.getIntance().send("Print", e.getMessage());
        }
    }

    @Override
    public void stop() {
        closeFile();
    }

    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("filename")) {
            filename = (String) val;
        } else if (key.equalsIgnoreCase("append")) {
            if (val instanceof Boolean) {
                append = (Boolean) val;
            } else {
                append = Boolean.parseBoolean((String) val);
            }
        } else if (key.equalsIgnoreCase("oneTime")) {
            if (val instanceof Boolean) {
                oneTime = (Boolean) val;
            } else {
                oneTime = Boolean.parseBoolean((String) val);
            }
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
