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
import com.sonicmsgr.util.encryption.SonicRSACrypto;
import com.sonicmsgr.util.encryption.SonicRSAPublicPrivateKeysGenerator;
import component.basic.*;
import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.Log;
import com.sonicmsgr.soniccell.msg.PubSubSingleton;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author yada
 */
public class DecryptTextRSAComponent extends ComponentAbstraction {

    private String encryptedText = "";
    private String privateKeyFileName = "SonicMsgr/secretKey/54b29aa9_privateKey";

    private boolean readyBool = false;
    private SonicRSAPublicPrivateKeysGenerator rsa = null;
    private boolean thru0Bool = false;
    private boolean thru1Bool = false;
    private SonicRSACrypto sonicRSACrypto = null;

    public DecryptTextRSAComponent() {
        setName("DecryptTxtRSA");
        this.setProperty("privateKeyFileName", privateKeyFileName);
        this.setProperty("encryptedText", encryptedText);

        this.addInput(new DataTypeIO("string", "file name","privateKeyFileName"));
        this.addOutput(new DataTypeIO("string", "encrypted text"));
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

            String en = sonicRSACrypto.decryptText(encryptedText, privateKeyFileName);
            if (en != null) {
                sendData(0, en);
            } else {
                PubSubSingleton.getIntance().send("Print", "data is null");
            }

        } catch (Exception e) {
            sendData(0, "Key doesn't match");
            PubSubSingleton.getIntance().send("Print", "Key doesn't match");
        }

        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {

        if (thru == 0) {
            encryptedText = (String) obj;
            thru0Bool = true;
        } else if (thru == 1) {
            privateKeyFileName = (String) obj;
            thru1Bool = true;
        }

        if ((thru0Bool) && (thru1Bool)) {
            readyBool = true;
        }

    }

    @Override
    public boolean start() {

        try {
            sonicRSACrypto = new SonicRSACrypto();
            encryptedText = getProperty("encryptedText").toString();
            privateKeyFileName = getProperty("privateKeyFileName").toString();
            File file = new File(privateKeyFileName);
            if (!file.exists()) {
                readyBool = false;
            } else {
                readyBool = true;
            }
            return true;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(DecryptTextRSAComponent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(DecryptTextRSAComponent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public void stop() {

    }

    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("encryptedText")) {
            encryptedText = getProperty("encryptedText").toString();
        } else if (key.equalsIgnoreCase("privateKeyFileName")) {
            privateKeyFileName = getProperty("privateKeyFileName").toString();
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
                    privateKeyFileName = selectedFile.getAbsoluteFile().toString();
                    setProperty("privateKeyFileName", privateKeyFileName);
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
