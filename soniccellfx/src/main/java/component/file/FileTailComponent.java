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
import com.sonicmsgr.util.FileTail;
import com.sonicmsgr.util.FileTail.CallBack;
import java.io.File;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

/**
 *
 * @author yada
 */
public class FileTailComponent extends ComponentAbstraction {

    private String filename = "";
    private String encoding = "UTF-8";
    private String NL = System.getProperty("line.separator");

    private int updateInterval = 1000;
    private long filePointer = 0;

    private boolean initBool = false;

    private TailerListener tailerListener;
    private Tailer tailer = null;

    private FileTail fileTail = new FileTail();

    public FileTailComponent() {
        setName("Tail");
        this.setProperty("filename", filename);
        this.setProperty("updateInterval", updateInterval);
        this.addInput(new DataTypeIO("string", "File name", "filename"));
        this.addOutput(new DataTypeIO("string", "Changed text"));
        this.setHasUISupport(true);
    }

    private void processTail() {
        fileTail.stopTail();

        File file = new File(filename);
        if(filename.trim().equalsIgnoreCase("")){
            return;
        }
        if (!file.exists()) {
            PubSubSingleton.getIntance().send("Print", "File not found " + filename);
            return;
        }

        try {
            System.out.println("Start tail");
            fileTail.startTail(filename, updateInterval, new CallBack() {
                @Override
                public void onReceived(String data) {

                    if (data.trim().equalsIgnoreCase("")) {
                        return;
                    }
                    if (data.trim().equalsIgnoreCase("\n")) {
                        return;
                    }

                    sendData(0, data);
                }

            });
        } catch (Exception e) {
            PubSubSingleton.getIntance().send("Print", e.getMessage());
        }
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public Object onExecute() {

        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {
        filename = (String) obj;
        processTail();
    }

    @Override
    public boolean start() {
        filename = getProperty("filename").toString();
        updateInterval = (int) Double.parseDouble(getProperty("updateInterval").toString());
        processTail();
        return true;
    }

    @Override
    public void stop() {

        fileTail.stopTail();
    }


    // Load from a file
    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("filename")) {
            filename = (String) val;
        }

        if (key.equalsIgnoreCase("updateInterval")) {

            if (val instanceof Double) {
                Double d = (Double) val;
                updateInterval = d.intValue();
            } else if (val instanceof String) {
                Double d = (Double.parseDouble((String) val));
                updateInterval = d.intValue();
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

        loadProperty(key,value);
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
