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
public class StrFindReplaceComponent extends ComponentAbstraction {

    private String findStr = "";
    private String replaceStr = "";
    private boolean readyBool = false;
    private String data = "";
    private boolean isEmptyBool = true;

    public StrFindReplaceComponent() {
        setName("StrFindReplace");
        this.setProperty("find", findStr);
        this.setProperty("replace", replaceStr);

        this.addInput(new DataTypeIO("string"));

        this.addOutput(new DataTypeIO("string"));
        this.addOutput(new DataTypeIO("bool"));

    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public Object onExecute() {
        if (isEmptyBool) {
            return null;
        }
        if (readyBool) {
            readyBool = false;
            if (data.indexOf(findStr) > -1) {
                data = data.replaceAll(findStr, replaceStr);
                sendData(0, data);
                sendData(1, true);
                return data;
            } else {
                sendData(1, false);
            }
        }
        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {
        readyBool = true;
        data = (String) obj;
    }

    @Override
    public boolean start() {

        readyBool = false;
        findStr = this.getProperty("find").toString();
        replaceStr = this.getProperty("replace").toString();

        if (findStr.trim().equalsIgnoreCase("")&&replaceStr.trim().equalsIgnoreCase("")) {
            isEmptyBool = true;
        } else {
            isEmptyBool = false;
        }

        return true;
    }

    @Override
    public void stop() {

    }

    @Override
    public void loadProperty(String key, Object value) {
        if (key.equalsIgnoreCase("find")) {
            findStr = this.getProperty("find").toString();
        } else if (key.equalsIgnoreCase("replace")) {
            replaceStr = this.getProperty("replace").toString();
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
