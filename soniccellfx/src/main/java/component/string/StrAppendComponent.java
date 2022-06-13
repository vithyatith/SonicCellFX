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
package component.string;

import component.basic.*;
import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import java.util.HashMap;

/**
 *
 * @author yada
 */
public class StrAppendComponent extends ComponentAbstraction {

    private String str1 = "";
    private String str2 = "";
    private boolean readyBool1 = false;
    private boolean readyBool2 = false;
    private String data = "";

    public StrAppendComponent() {
        setName("Append");
        this.setProperty("str1", str1);
        this.setProperty("str2", str2);

        this.addInput(new DataTypeIO("string","String 1","str1"));
        this.addInput(new DataTypeIO("string","String 2","str2"));
        this.addOutput(new DataTypeIO("string"));

    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public Object onExecute() {

        if ((readyBool1) && (readyBool2)) {
            readyBool1 = false;
            readyBool2 = false;
            sendData(0, str1 + str2);
            return data;
        }
        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {

        if (thru == 0) {
            readyBool1 = true;
            str1 = (String) obj;
        }else if (thru == 1) {
            readyBool2 = true;
            str2 = (String) obj;
        }
    }

    @Override
    public boolean start() {

        readyBool1 = false;
        readyBool2 = false;
        str1 = this.getProperty("str1").toString();
        str2 = this.getProperty("str2").toString();
        if(!str1.equalsIgnoreCase("")){
            readyBool1 = true;
        }
        if(!str2.equalsIgnoreCase("")){
            readyBool2 = true;
        }
        return true;
    }

    @Override
    public void stop() {

    }

    @Override
    public void loadProperty(String key, Object value) {
        if (key.equalsIgnoreCase("str1")) {
            str1 = this.getProperty("str1").toString();
        } else if (key.equalsIgnoreCase("str2")) {
            str2 = this.getProperty("str2").toString();
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
