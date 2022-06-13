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
public class NumberLongComponent extends ComponentAbstraction {
    private long value = 0;
    public NumberLongComponent() {
        setName("Long");
        this.setProperty("value", value);
        this.addInput(new DataTypeIO("long","value","value"));
        this.addOutput(new DataTypeIO("long"));
    }

    @Override
    public Object onExecute() {
        sendData(0, value);
        return value;
    }

    @Override
    public void handleMessage(int thru, Object obj) {
        value = (Long) obj;
    }

    @Override
    public boolean start() {
        value = (long)(Double.parseDouble(getProperty("value").toString()));
        return true;
    }

    @Override
    public void stop() {

    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("value")) {
            
            value = (long)Double.parseDouble(val.toString());
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
