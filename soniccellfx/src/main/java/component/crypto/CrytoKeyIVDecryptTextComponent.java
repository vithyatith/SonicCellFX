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
import component.basic.*;
import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.msg.PubSubSingleton;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author yada
 */
public class CrytoKeyIVDecryptTextComponent extends ComponentAbstraction {

    private String message = "";
    private String key="";
    private String iv="";
    private boolean doEncrypt = false;

    private SonicCryptLib sonicCryptLib = null;
    
    private boolean readyBool = false;

    public CrytoKeyIVDecryptTextComponent() {
        setName("TxtKeyIVDe");
        this.setProperty("key", key);
        this.setProperty("iv", iv);
        this.setProperty("message", message);

        this.addInput(new DataTypeIO("string", "Message to encryt","message"));
        this.addOutput(new DataTypeIO("string", "Decryt string"));
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public Object onExecute() {
        if(readyBool==false){
            return null;
        }
        readyBool = false;

        if (sonicCryptLib != null) {
            try {
                if (doEncrypt) {
                    String encodeMsg = sonicCryptLib.encrypt(message);
                    sendData(0, encodeMsg);
                    return encodeMsg;
                } else {
                    String decodeMsg = sonicCryptLib.decrypt(message);
                    sendData(0, decodeMsg);
                    return decodeMsg;
                }
            } catch (Exception e) {

                PubSubSingleton.getIntance().send("Print", "Password doesn't match");
            }
        }
        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {
        message = (String) obj;
        readyBool = true;
    }

    @Override
    public boolean start() {
        try {
            key = getProperty("key").toString();
            iv = getProperty("iv").toString();
            message = getProperty("message").toString();
            if(!message.equalsIgnoreCase("")){
                readyBool = true;
            }
            sonicCryptLib = new SonicCryptLib(key,iv);
            return true;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CrytoKeyIVDecryptTextComponent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(CrytoKeyIVDecryptTextComponent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public void stop() {

    }

    @Override
    public void loadProperty(String key2, Object val) {
        if (key2.equalsIgnoreCase("key")) {
            key = getProperty("key").toString();
        } else if (key2.equalsIgnoreCase("iv")) {
            iv = getProperty("iv").toString();
        } else if (key2.equalsIgnoreCase("message")) {
            message = getProperty("message").toString();
        } 
        
        else if (key2.equalsIgnoreCase("doEncrypt")) {
           // doEncrypt = Boolean.parseBoolean(getProperty("doEncrypt").toString());
        }
    }

    @Override
    public void mouseDoubleClick() {
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
