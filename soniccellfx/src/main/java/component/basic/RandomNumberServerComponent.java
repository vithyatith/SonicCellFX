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
public class RandomNumberServerComponent extends ComponentAbstraction {

    private HashMap hashMap = new HashMap();
    private String value = "";
    private boolean initBool = false;
    private boolean runBool = false;

    public RandomNumberServerComponent() {
        setName("Random String");
        this.setProperty("value", value);
        this.addOutput(new DataTypeIO("string"));
    }

    @Override
    public Object onExecute() {
        if (initBool == true) {
            while (runBool) {
                System.out.println("Random...");
                try {
                    
                   sendData(0, Math.random()+"");
                   Thread.sleep(2000);
                } catch (Exception e) {
                    break;
                }
            }
            initBool = false;
        }
        return null;
    }

//    @Override
//    public void runNTime() {
//        runAvailableTime();
//    }

    @Override
    public void handleMessage(int thru, Object obj) {

    }

    @Override
    public boolean start() {

        value = this.getProperty("value").toString();
        initBool = true;
        runBool = true;
        return true;
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public void stop() {
        initBool = false;
    }

    @Override
    public void loadProperty(String key, Object value) {
        if (key.equalsIgnoreCase("value")) {
            value = this.getProperty("value").toString();
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
