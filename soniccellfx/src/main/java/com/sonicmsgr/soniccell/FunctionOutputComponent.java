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
package com.sonicmsgr.soniccell;

/**
 *
 * @author yada
 */
public class FunctionOutputComponent extends ComponentAbstraction {
    
    private Object objValue;
    private boolean thruSetBool = false;
    public FunctionOutputComponent(){
        setName("Output");
        this.addInput(new DataTypeIO("all"));
    }

    @Override
    public Object onExecute() {
        
        if(thruSetBool){
           sendData(0, objValue);
           thruSetBool = false;
           return objValue;
        }
        return objValue;
    }
    
    @Override
    public void handleMessage(int thru, Object obj) {
        thruSetBool = true;
        objValue = obj;
    }
    @Override
    public boolean start() {
        return true;
    }

    @Override
    public void stop() {
   
    }
    @Override
    public void loadProperty(String key, Object val){

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
    
        @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
}
