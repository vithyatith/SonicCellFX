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
package component.basic;

import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import java.util.HashMap;

/**
 *
 * @author yada
 */
public class AddNumberComponent extends ComponentAbstraction {

    private HashMap hashMap = new HashMap();
    private float sum = 0f;
    private float f1;
    private float f2;
    private boolean b1 = false;
    private boolean b2 = false;

    public AddNumberComponent() {
        setName("Add Num");
        this.addInput(new DataTypeIO("float"));
        this.addInput(new DataTypeIO("float"));
        this.addOutput(new DataTypeIO("float"));
    }

    @Override
    public Object onExecute() {
        if ((b1 == true) && (b2 == true)) {
            sum = f1 + f2;
            sendData(0, sum);
            b1 = false;
            b2 = false;
            return sum;
        }
        return null;
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }

    @Override
    public void handleMessage(int thru, Object obj) {

        if (thru == 0) {
            f1 = (Float) obj;
            b1 = true;
        }

        if (thru == 1) {
            f2 = (Float) obj;
            b2 = true;
        }

    }

    @Override
    public boolean start() {

        b1 = false;
        b2 = false;
        sum = 0;
        return true;
    }

    @Override
    public void stop() {

    }

    @Override
    public void loadProperty(String key, Object val) {

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
