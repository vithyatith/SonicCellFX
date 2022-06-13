/*
 * Copyright 2022 Vithya Tith
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
package component.message;

import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author yada
 */
public class MsgQueStopComponent extends ComponentAbstraction {

    private boolean readyBool1 = false;
    private boolean readyBool2 = false;
    private Object data = null;
    private int id = 0;

    public MsgQueStopComponent() {
        setName("MsgQueEnd");
        this.addInput(new DataTypeIO("float[]", "Float array data"));
        this.addInput(new DataTypeIO("int", "id"));
        this.addOutput(new DataTypeIO("all", "Data"));
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public Object onExecute() {
        if ((readyBool1==true)&&(readyBool1==true)) {
            readyBool1 = false;
            readyBool2 = false;
            
            sendData(0,data);
            sendManualData(id, 1, null);

        }

        return null;
    }

    @Override
    public void mouseDoubleClick() {

    }

    @Override
    public void handleMessage(int thruId, Object obj) {

        if (thruId == 0) {
            data = obj;
            readyBool1 = true;
        } else if (thruId == 1) {
            id = (Integer)obj;
            readyBool2 = true;
        }

    }

    @Override
    public boolean start() {
        readyBool1 = false;
        readyBool2 = false;
        return true;
    }

    @Override
    public void stop() {

    }

    @Override
    public void onPropertyChanged(String key, Object value) {

    }

    @Override
    public void loadProperty(String key, Object value) {
        onPropertyChanged(key, value);
    }

    @Override
    public int getPlatformSupport() {

        return 0;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public String getHelp() {
        return "";
    }

}
