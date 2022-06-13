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

import com.sonicmsgr.util.encryption.SonicCryptLib;
import com.sonicmsgr.util.encryption.SonicRSAPublicPrivateKeysGenerator;
import component.basic.*;
import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.msg.PubSubSingleton;
import java.io.File;
import java.util.HashMap;
import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author yada
 */
public class RSACreatorComponent extends ComponentAbstraction {

    private String folderPath = "rsakey";
    private String filename = "rsa";

    private boolean readyBool = false;
    private SonicRSAPublicPrivateKeysGenerator rsa = null;
    private boolean thru0Bool = false;
    private boolean thru1Bool = false;
    private String seed = "";
    private int keyLength = 2048;

    public RSACreatorComponent() {
        setName("RSACreat");
        this.setProperty("folderPath", folderPath);
        this.setProperty("filename", filename);
        this.setProperty("seed", seed);
        this.setProperty("keyLength", keyLength);

        this.addInput(new DataTypeIO("string", "folder path","folderPath"));
      //  this.addInput(new DataTypeIO("string", "file name"));
        this.addOutput(new DataTypeIO("boolean", "Is success"));
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
        thru0Bool = false;
        thru1Bool = false;
        readyBool = false;

        try {
            if (seed.trim().equalsIgnoreCase("")) {
                rsa = new SonicRSAPublicPrivateKeysGenerator(keyLength);
            } else {
                rsa = new SonicRSAPublicPrivateKeysGenerator(keyLength,seed);
            }
            rsa.createKeys();
            rsa.writeToFile(folderPath + File.separator + filename + "_publicKey", rsa.getPublicKey().getEncoded());
            rsa.writeToFile(folderPath + File.separator + filename + "_privateKey", rsa.getPrivateKey().getEncoded());

            sendData(0, true);

        } catch (Exception e) {
            sendData(0, false);
            PubSubSingleton.getIntance().send("Print", "Password doesn't match");
        }
        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {

        if (thru == 0) {
            folderPath = (String) obj;
            thru0Bool = true;
        } 
        
//        else if (thru == 1) {
//            filename = (String) obj;
//            thru1Bool = true;
//        }

        if ((thru0Bool) && (thru1Bool)) {
            readyBool = true;
        }

        File file = new File(folderPath);
        if (!file.exists()) {
            file.mkdir();
        }

    }

    @Override
    public boolean start() {
        folderPath = getProperty("folderPath").toString();
        filename = getProperty("filename").toString();
        seed = getProperty("seed").toString();
        try {
            keyLength = (int) Double.parseDouble(getProperty("keyLength").toString());
        } catch (NumberFormatException e) {
            keyLength = 1024;
        }

        File file = new File(folderPath);
        if (!file.exists()) {
            file.mkdir();
        }
        readyBool = true;
        return true;
    }

    @Override
    public void stop() {

    }

    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("folderPath")) {
            folderPath = getProperty("folderPath").toString();
        } else if (key.equalsIgnoreCase("filename")) {
            filename = getProperty("filename").toString();
        } else if (key.equalsIgnoreCase("seed")) {
            seed = getProperty("seed").toString();
        } else if (key.equalsIgnoreCase("keyLength")) {
            try {
                keyLength = (int) Double.parseDouble(getProperty("keyLength").toString());
            } catch (NumberFormatException e) {
                keyLength = 1024;
            }
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
                    folderPath = selectedFile.getAbsoluteFile().toString();
                    setProperty("folderPath", folderPath);
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
