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
public class EncrypteTextRSAComponent extends ComponentAbstraction {

    private String text = "";
    private String publicKeyFileName = "SonicMsgr/secretKey/54b29aa9_publicKey";

    private boolean readyBool = false;
    private SonicRSAPublicPrivateKeysGenerator rsa = null;
    private boolean thru0Bool = false;
    private boolean thru1Bool = false;
    private SonicRSACrypto sonicRSACrypto = null;
    public EncrypteTextRSAComponent() {
        setName("EncodeTxtRSA");
        this.setProperty("publicKeyFileName", publicKeyFileName);
        this.setProperty("text", text);
       
        this.addInput(new DataTypeIO("string", "file name","publicKeyFileName"));
        this.addOutput(new DataTypeIO("string", "encrypted text"));
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
            
            String en = sonicRSACrypto.encryptText(text, publicKeyFileName);
            
            sendData(0,en);

        } catch (Exception e) {
            sendData(0,"Key doesn't match");
            PubSubSingleton.getIntance().send("Print", "Key doesn't match");
        }
        return null;
    }
    

    @Override
    public void handleMessage(int thru, Object obj) {
       
        if(thru==0){
           text = (String) obj;
           thru0Bool = true;
        }else if(thru==1){
           publicKeyFileName   = (String) obj;
           thru1Bool = true;
        }
        
        if((thru0Bool)&&(thru1Bool)){
            readyBool = true;
        }
     
    }

    @Override
    public boolean start() {
        
        try {
            sonicRSACrypto = new SonicRSACrypto();
            text = getProperty("text").toString();
            
            publicKeyFileName = getProperty("publicKeyFileName").toString();
            File file = new File(publicKeyFileName);
            if(!file.exists()){
                readyBool = false;
            }else{
                readyBool = true;
            }
            return true;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(EncrypteTextRSAComponent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(EncrypteTextRSAComponent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public void stop() {

    }

    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("text")) {
            text = getProperty("text").toString();
        } else if (key.equalsIgnoreCase("publicKeyFileName")) {
            publicKeyFileName = getProperty("publicKeyFileName").toString();
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
                    publicKeyFileName = selectedFile.getAbsoluteFile().toString();
                    setProperty("publicKeyFileName", publicKeyFileName);
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
