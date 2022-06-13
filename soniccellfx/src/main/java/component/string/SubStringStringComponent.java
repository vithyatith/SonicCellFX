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
public class SubStringStringComponent extends ComponentAbstraction {

    private String strBegin = "";
    private String strEnd = "";
    private boolean readyBool = false;
    private String data = "";

    public SubStringStringComponent() {
        setName("SubStrStr");
        this.setProperty("strBegin", strBegin);
        this.setProperty("strEnd", strEnd);

        this.addInput(new DataTypeIO("string"));
        this.addOutput(new DataTypeIO("string"));

    }

    @Override
    public Object onExecute() {

        if (readyBool) {
            readyBool = false;

            int begin = data.indexOf(strBegin);
            if (begin < 0) {
                begin = 0;
            }else{
                begin = begin+strBegin.length();
            }

            int end = data.indexOf(strEnd);
            if (end < 0) {
                end = data.length();
            }

            data = data.substring(begin, end);
            sendData(0, data);
            return data;
        }
        return null;
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    
    @Override
    public void handleMessage(int thru, Object obj) {
        readyBool = true;
        data = (String) obj;
    }

    @Override
    public boolean start() {

        readyBool = false;
        strBegin = this.getProperty("strBegin").toString();
        strEnd = this.getProperty("strEnd").toString();

        return true;
    }

    @Override
    public void stop() {

    }

    @Override
    public void loadProperty(String key, Object value) {
        if (key.equalsIgnoreCase("strBegin")) {
            strBegin = this.getProperty("strBegin").toString();
        } else if (key.equalsIgnoreCase("strEnd")) {
            strEnd = this.getProperty("strEnd").toString();
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
