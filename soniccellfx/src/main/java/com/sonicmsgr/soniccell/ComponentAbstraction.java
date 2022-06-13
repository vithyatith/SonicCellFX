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

//import java.util.*;
import com.sonicmsgr.soniccell.msg.PubSubSingleton;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public abstract class ComponentAbstraction implements Cloneable {

    private String name = "";
    private String alias = "";
    private int ID = -1;
    private MessageSender messageSender = null;
    protected ArrayList<DataTypeIOList> inputArrayList = new ArrayList<>();
    protected ArrayList<DataTypeIOList> outputArrayList = new ArrayList<>();
    private LinkedHashMap<String, Object> propHash = new LinkedHashMap<>();

    protected boolean isFunctionComponent = false;
    protected boolean enabled = true;
    private int boydSize = 1;

    private boolean hasUISupport = false;
    
    private boolean iniLoadPropertyFromFileBool = true;

    public ComponentAbstraction() {

    }

    public void setHasUISupport(boolean b) {
        hasUISupport = b;
    }

    public boolean getHasUISupport() {
        return hasUISupport;
    }

    public int getNumberOfInputOccupy() {
        return messageSender.getNumerOfConnectionInputOccupy(ID);
    }

    public ArrayList<DataTypeIOList> getInputDataType() {
        return inputArrayList;
    }

    public ArrayList<DataTypeIOList> getOutputDataType() {
        return outputArrayList;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setEnable(boolean b) {
        enabled = b;
    }

    public boolean getEnable() {
        return enabled;
    }

    protected void setMessageSender(MessageSender ms) {
        messageSender = ms;
    }

    public void setName(String _name) {
        name = _name;
    }

    public int getNumberOfOutputSize() {
        return outputArrayList.size();
    }

    public int getNumberOfInputSize() {
        return inputArrayList.size();
    }

    public void sendData(int thruID, Object obj) {
        messageSender.senData(ID, thruID, obj);

    }

    public void sendManualData(int srcId,int thruID, Object obj) {
        messageSender.senManualData(srcId, thruID, obj);

    }

    // Get the componentIDNum
    public int getId() {
        return ID;
    }

    // Set the compoenntIDNum
    protected void setComponentIDNum(int _id) {
        ID = _id;
    }

    public LinkedHashMap<String, Object> getHashMapProperty() {
        return propHash;
    }

    public Object getProperty(String key) {
        return propHash.get(key);
    }

    public void setProperty(String key, Object value) {
        propHash.put(key, value);
    }

    protected void setHashMapProperty(LinkedHashMap<String, Object> propHashe) {
        this.propHash = propHashe;
    }

    public String getName() {
        return name;
    }

    public void addInput(DataTypeIO dataTypeIO) {
        DataTypeIOList dataTypeList = new DataTypeIOList();
        dataTypeList.addDataType(dataTypeIO);
        addInput(dataTypeList);
    }

    public void addInput(DataTypeIOList dataTypeList) {
        inputArrayList.add(dataTypeList);

        updateBodySize();
    }

    public void addOutput(DataTypeIO dataTypeIO) {
        DataTypeIOList dataTypeList = new DataTypeIOList();
        dataTypeList.addDataType(dataTypeIO);
        addOutput(dataTypeList);
    }

    public void addOutput(DataTypeIOList dataTypeList) {
        outputArrayList.add(dataTypeList);
        updateBodySize();
    }

    public int getBodySize() {
        return boydSize;
    }

    public void printToConsole(String msg) {
        PubSubSingleton.getIntance().send("Print", msg);
        
        
    }

    public void updatePropertiesChanges() {
        PubSubSingleton.getIntance().send("UPDATE_PROPERTIES", getId());
    }

    public void stopComponent() {
        PubSubSingleton.getIntance().send("STOP_COMPONENT", getId());
    }

    private void updateBodySize() {
        float numInput = getNumberOfInputSize();
        float numOutput = getNumberOfOutputSize();

        float maxSize = numOutput;

        if (numInput > numOutput) {
            maxSize = numInput;
        }

        boydSize = (int) Math.ceil(maxSize / 2.0f);
    }

    public void sendError(String msg) {
        PubSubSingleton.getIntance().send("Print", msg);
    }
    
    public boolean getIniEnableLoadPropertyFromFile(){
        return iniLoadPropertyFromFileBool;
    }
    
    public void setIniEnableLoadPropertyFromFile(boolean b){
         iniLoadPropertyFromFileBool = b;
    }

    ////////////////////// Future Release will be depreciated ///////////////////
    // This method is executed for all available of times.
    // When the user
    public abstract Object onExecute();

    public abstract void mouseDoubleClick();

    // Handle message
    public abstract void handleMessage(int thruId, Object obj);

    /**
     * This method will be first executed before any other method is call. Use
     * this to initalize variable first.
     */
    public abstract boolean start();
    

    /**
     *
     * This method is the last process executed. Use this for cleanup of codes.
     */
    public abstract void stop();

    public abstract void onPropertyChanged(String key, Object value);

    public abstract void loadProperty(String key, Object value);

    public abstract int getPlatformSupport();

    public abstract void onDestroy();

    public abstract String getHelp();
    
    public abstract void doneLoadingThisClassFromProjectFile();

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
