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
public class TestComponent extends ComponentAbstraction {
    
    private HashMap hashMap = new HashMap();
    private float value = 0f;
    
    public TestComponent(){
        setName("Float");
        this.setProperty("value", value);
        this.addInput(new DataTypeIO("float"));
        this.addInput(new DataTypeIO("float"));
        this.addInput(new DataTypeIO("float"));

        this.addOutput(new DataTypeIO("float"));
        this.addOutput(new DataTypeIO("float"));
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public Object onExecute() {
      //  System.out.println("Float: "+value);
        sendData(0, value);
        return value;
    }
    
    @Override
    public void handleMessage(int thru, Object obj) {
        value = (Integer)obj;
      //  System.out.println("From float = "+obj);
    }
    @Override
    public boolean start() {
    
        value = Float.parseFloat(getProperty("value").toString());
        
        return true;
    }

    @Override
    public void stop() {
   
    }
    @Override
    public void loadProperty(String key, Object val){
         if(key.equalsIgnoreCase("value")){
             value = Float.parseFloat(getProperty("value").toString());
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
