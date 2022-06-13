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
package component.condition;

import component.basic.*;
import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import java.util.HashMap;

/**
 *
 * @author yada
 */
public class IfConditionComponent extends ComponentAbstraction {

    private String condition = "equal";
    private boolean readyBool = false;
    private String inputValue = "";
    private String compareString = "";
    private boolean ignoreCase = true;

    public IfConditionComponent() {
        setName("IF");
        this.setProperty("condition", condition);
        this.setProperty("compareString", compareString);
        this.setProperty("ignoreCase", ignoreCase);
        this.addInput(new DataTypeIO("string"));
        this.addOutput(new DataTypeIO("string"));
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public Object onExecute() {

        if (readyBool == false) {
            return "";
        }
        readyBool = false;
        if (condition.equalsIgnoreCase("equal")) {
            if (ignoreCase) {
                if (inputValue.equalsIgnoreCase(compareString)) {
                    sendData(0, inputValue);
                    return inputValue;
                }
            } else {
                if (inputValue.equals(compareString)) {
                    sendData(0, inputValue);
                    return inputValue;
                }
            }
        } else if (condition.equalsIgnoreCase("notequal")) {
            if (ignoreCase) {
                if (!inputValue.equalsIgnoreCase(compareString)) {
                    sendData(0, inputValue);
                    return inputValue;
                }
            } else {
                if (!inputValue.equals(compareString)) {
                    sendData(0, inputValue);
                    return inputValue;
                }
            }
        } else if (condition.equalsIgnoreCase("contain")) {
            if (inputValue.indexOf(compareString) > -1) {
                sendData(0, inputValue);
                return inputValue;
            }

        } else if (condition.equalsIgnoreCase("indexof")) {
            int index = inputValue.indexOf(compareString);
            if (index > -1) {
                sendData(0, index + "");
                return inputValue;
            }
        }

        return "";
    }

    @Override
    public void handleMessage(int thru, Object obj) {

        readyBool = true;
        inputValue = (String) obj;

    }

    @Override
    public boolean start() {
        condition = this.getProperty("condition").toString();
        compareString = this.getProperty("compareString").toString();
        return true;
    }

    @Override
    public void stop() {

    }

    @Override
    public void loadProperty(String key, Object value) {
        if (key.equalsIgnoreCase("condition")) {
            condition = this.getProperty("condition").toString();
        }
        if (key.equalsIgnoreCase("compareString")) {
            compareString = this.getProperty("compareString").toString();
        }

        if (key.equalsIgnoreCase("ignoreCase")) {
            ignoreCase = Boolean.parseBoolean(getProperty("ignoreCase").toString());
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
