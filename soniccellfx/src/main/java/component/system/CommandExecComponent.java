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
package component.system;

import com.sonicmsgr.util.CommandExecUtil;
import com.sonicmsgr.util.CommandExecUtil.CommandResponse;
import component.basic.*;
import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import java.util.HashMap;

/**
 *
 * @author yada
 */
public class CommandExecComponent extends ComponentAbstraction {

    private String commandLine="";
    private CommandExecUtil exec = new CommandExecUtil();
    private boolean readyBool = false;

    public CommandExecComponent() {
        setName("CommandExec");
        this.setProperty("commandLine", commandLine);
        this.addInput(new DataTypeIO("string","commandLine"));
        this.addOutput(new DataTypeIO("string"));
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    
    @Override
    public Object onExecute() {
        if (readyBool) {
            exec.start(commandLine, new CommandExecUtil.CommandResponse() {
                @Override
                public void onResult(String result) {
                    sendData(0, result);
                }
            });
            readyBool = false;
        }

        //  sendData(0, value);
        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {
        commandLine = (String) obj;
        if (!commandLine.trim().equalsIgnoreCase("")) {
            readyBool = true;
        }else{
            readyBool = false;
        }
    }

    @Override
    public boolean start() {
        exec.stop();
        readyBool = true;
        commandLine = getProperty("commandLine").toString().trim();
        if (!commandLine.equalsIgnoreCase("")) {
            readyBool = true;
        }
        return true;
    }

    @Override
    public void stop() {
        readyBool = false;
        exec.stop();
    }

    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("commandLine")) {
            commandLine = getProperty("commandLine").toString().trim();
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
