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
package com.sonicmsgr.soniccell;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author yada
 */
public class SonicCellSdk {

    private int MAX_COMPONENT = 80;
    private ComponentAbstraction componentArray[] = new ComponentAbstraction[MAX_COMPONENT];
    private ComponentDataManager componentDataManager = new ComponentDataManager();
    private MessageSender messageSender = new MessageSender(componentArray, componentDataManager);
    private ExecManagerThread execManagerThread = null;
    private Gson gson = new Gson();

    private ArrayList<Integer> componentIDArray = new ArrayList<Integer>();

    private final String TAG = "SonicCellSDK";

    private final ConcurrentHashMap<Integer, ArrayList<ComponentInfo>> functionsHashMap = new ConcurrentHashMap<Integer, ArrayList<ComponentInfo>>();

    public SonicCellSdk() {
    }

    public boolean setNumOfInOutputToFunction(FunctionComponent fc, int numberOfInput, int numberOfOutput) {
        return setNumOfInOutputToFunction(fc.getId(), numberOfInput, numberOfOutput);
    }

    public void removeAllThroughputReference(int id) {
        if (id < 0) {
            Log.v("VT", "Component has not been added");
            return;
        }
        componentDataManager.removeAllThroughputReference(id);
    }

    public boolean setNumOfInOutputToFunction(int id, int numberOfInput, int numberOfOutput) {

        if (id < 0) {
            Log.v("VT", "Component has not been added");
            return false;
        }
        FunctionComponent fc = (FunctionComponent) componentArray[id];
        componentDataManager.removeAllThroughputReference(id);
        fc.setNumberOfInOutput(numberOfInput, numberOfOutput);

        ConcurrentHashMap<Integer, ComponentInfo> infoHash = componentDataManager.getComponentInfoHashMap();

        ComponentInfo componentInfo = infoHash.get(id);
        // set the number of in and out.
        if (numberOfInput > -1) {
            componentInfo.numOfInput = numberOfInput;
        }
        if (numberOfOutput > -1) {
            componentInfo.numOfOutput = numberOfOutput;
        }

        return true;
    }

    private void refreshFunctionList() {
        functionsHashMap.clear();

        ConcurrentHashMap<Integer, ComponentInfo> componentInfoHashMap = componentDataManager.getComponentInfoHashMap();

        for (Map.Entry<Integer, ComponentInfo> entry : componentInfoHashMap.entrySet()) {
            ComponentInfo c = entry.getValue();
            if (c.functionId > -1) {

                if (!functionsHashMap.containsKey((Integer) c.functionId)) {
                    functionsHashMap.put((Integer) c.functionId, new ArrayList<ComponentInfo>());
                }
                ArrayList<ComponentInfo> al = functionsHashMap.get((Integer) c.functionId);
                al.add(c);
            }
        }
    }

    public ArrayList<Integer> getAllComponentFunctionList(int functionId) {

        refreshFunctionList();

        ArrayList<Integer> result = new ArrayList<Integer>();
        doFunctionList(functionId, result);
        return result;
    }

    private void doFunctionList(int functionId, ArrayList<Integer> result) {
        ArrayList<ComponentInfo> al = functionsHashMap.get((Integer) functionId);

        for (int i = 0; i < al.size(); i++) {
            ComponentInfo info = al.get(i);
            if (info.isFunction) {
                doFunctionList(info.id, result);
            }
            if (!result.contains(info.id)) {
                result.add(info.id);
            }
        }
    }

    public FunctionComponent getFunctionComponentByAlias(String alis) {
        int id = this.findIdByAlias(alis);
        if (id == -1) {
            return null;
        }
        return (FunctionComponent) componentArray[id];
    }

    public FunctionComponent getFuctionComponent(int id) {
        return (FunctionComponent) componentArray[id];
    }

    public ComponentAbstraction getComponent(int id) {

        if (id < 0) {
            return null;
        }
        return componentArray[id];
    }

    public FunctionComponent createFunctionComponent(int col, int row) {
        FunctionComponent function = new FunctionComponent();
        addFunctionCompnent(function, col, row);
        setAlias(function, "func");
        return function;
    }

    private void setComponentIdToArray(int id, ComponentAbstraction com) {

        resize(id);

        if (com == null) {
            this.componentIDArray.remove((Integer) id);
        } else {
            this.componentIDArray.add((Integer) id);
        }
        this.componentArray[id] = com;
    }

    private void resize(int id) {
        if (id >= componentArray.length) {
            MAX_COMPONENT = MAX_COMPONENT + 40;

            ComponentAbstraction componentArrayTmp[] = new ComponentAbstraction[componentArray.length];

            // copy to tmp
            for (int i = 0; i < componentArray.length; i++) {
                componentArrayTmp[i] = componentArray[i];
            }
            componentArray = new ComponentAbstraction[MAX_COMPONENT];

            // copy to tmp
            for (int i = 0; i < componentArrayTmp.length; i++) {
                componentArray[i] = componentArrayTmp[i];
            }
            messageSender.setComponentArray(componentArray);

        }
    }

    public ArrayList<Integer> getComponentIdArray() {
        return componentIDArray;
    }

    public boolean addFunctionCompnent(FunctionComponent componentAbstraction, int col, int row) {
        boolean b = false;
        if (componentAbstraction instanceof FunctionComponent) {
            b = addComponent(componentAbstraction, col, row);
            if (b) {
                int id = componentAbstraction.getId();
                ComponentInfo c = componentDataManager.getComponentInfoHashMap().get(id);
                c.isFunction = true;
                c.col = col;
                c.row = row;
            }
        }
        return b;
    }

    public boolean addComponentToFunction(ComponentAbstraction componentAbstraction, int x, int y, FunctionComponent functionAbstraction) {

        boolean b = addComponent(functionAbstraction.getId(), componentAbstraction, x, y);
        if (b) {
            int comId = componentAbstraction.getId();
            int funId = functionAbstraction.getId();
            ComponentInfo c = componentDataManager.getComponentInfoHashMap().get(comId);
            c.functionId = funId;
            c.col = x;
            c.row = y;

            if (componentAbstraction instanceof FunctionInputComponent) {
                c.isFunctionInput = true;
                c.isFunctionOutput = false;
            }

            if (componentAbstraction instanceof FunctionOutputComponent) {
                c.isFunctionInput = false;
                c.isFunctionOutput = true;
            }
        }
        return b;
    }

    public boolean addThroughputReference(int ioType, ComponentAbstraction componentSrc, int srcThruId, ComponentAbstraction componentDest, int destThruId) {
        return addThroughputReference(ioType, componentSrc.getId(), srcThruId, componentDest.getId(), destThruId);
    }

    public boolean addThroughputReference(int ioType, int srcId, int srcThruId, int destId, int destThruId) {

        ThroughputReferenceInfo th = new ThroughputReferenceInfo();
        th.ioType = ioType;
        th.srcId = srcId;
        th.srcThruId = srcThruId;
        th.destId = destId;
        th.destThruId = destThruId;

        return componentDataManager.addThroughputReference(th);
    }

    public void updateThroughputReference(int idOld, int idNew) {
        componentDataManager.updateThroughputReference(idOld, idNew);
    }

    public int addComponentByClassName(String componentAbstractionClassName) {

        int id = componentDataManager.getAvailableId();
        resize(id);
        String classname = componentAbstractionClassName;

        // Does a class loader
        Class c = null;
        try {
            c = Class.forName(classname);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.v(TAG, "error = " + e.getMessage());
            return -1;
        }
        Class[] argtypes = new Class[]{};
        Constructor cons;
        ComponentAbstraction componentAbstraction = null;
        try {
            cons = c.getConstructor(argtypes);

            Object[] args = new Object[]{};
            componentAbstraction = (ComponentAbstraction) cons.newInstance(args);
            componentAbstraction.setComponentIDNum(id);
            componentAbstraction.setMessageSender(messageSender);
            //  componentArray[id] = componentAbstraction;
        } catch (NoSuchMethodException ex) {
            Log.v(TAG, "addComponentByClassName Error: " + ex.getMessage());
            return -1;
        } catch (SecurityException ex) {
            Log.v(TAG, "addComponentByClassName 2 Error: " + ex.getMessage());
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(TAG, "addComponentByClassName 3 Error: " + e.getMessage());
            return -1;
        }

        ComponentInfo info = new ComponentInfo();
        info.id = id;
        info.className = componentAbstraction.getClass().getName();
        info.propHash = componentAbstraction.getHashMapProperty();
        info.name = componentAbstraction.getName();
        info.alias = componentDataManager.getAvailableAlias(info.name);
        info.numOfInput = componentAbstraction.getNumberOfInputSize();
        info.numOfOutput = componentAbstraction.getNumberOfOutputSize();

        // Add to the data Manager
        componentDataManager.addComponentInfo(id, info);
        // Add to the databse
        //componentArray[id] = componentAbstraction;
        setComponentIdToArray(id, componentAbstraction);

        return id;
    }

    private boolean addComponent(ComponentAbstraction componentAbstraction) {

        if (componentAbstraction.getId() > -1) {
            Log.v(TAG, "Commponet already added");
            return false;
        }

        int id = componentDataManager.getAvailableId();
        resize(id);
        componentAbstraction.setComponentIDNum(id);
        ComponentInfo info = new ComponentInfo();
        componentAbstraction.setMessageSender(messageSender);
        info.id = id;

        info.className = componentAbstraction.getClass().getName();
        info.propHash = componentAbstraction.getHashMapProperty();
        info.name = componentAbstraction.getName();
        info.alias = info.name + "id";
        info.numOfInput = componentAbstraction.getNumberOfInputSize();
        info.numOfOutput = componentAbstraction.getNumberOfOutputSize();

        // Add to the data Manager
        componentDataManager.addComponentInfo(id, info);
        // Add to the databse
        setComponentIdToArray(id, componentAbstraction);
        return true;
    }

    public boolean addComponent(ComponentAbstraction componentAbstraction, int x, int y) {
        return addComponent(-1, componentAbstraction, x, y);
    }

    public ComponentInfo getComponentInfo(int id) {
        ConcurrentHashMap<Integer, ComponentInfo> componentHashMap = componentDataManager.getComponentInfoHashMap();
        return componentHashMap.get(id);
    }

    public void setEnableComponent(ComponentAbstraction com, boolean enable) {
        setEnableComponent(com.getId(), enable);
    }

    public void setEnableComponent(int id, boolean enable) {
        ConcurrentHashMap<Integer, ComponentInfo> componentHashMap = componentDataManager.getComponentInfoHashMap();
        componentHashMap.get(id).enabled = enable;
        getComponent(id).setEnable(enable);
    }

    private boolean addComponent(int methodId, ComponentAbstraction componentAbstraction, int x, int y) {

        // Checkt to make sure x and y are not overlapping
        ConcurrentHashMap<Integer, ComponentInfo> componentHashMap = componentDataManager.getComponentInfoHashMap();
        Iterator it = componentHashMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            int id = (Integer) pair.getKey();
            ComponentInfo componentInfo = (ComponentInfo) pair.getValue();

            // Avoding checking itself
            if (id == componentAbstraction.getId()) {
                continue;
            }

            if (componentInfo.functionId == methodId) {
                // Bypass it because creating funcion on the fly
                if (!(componentAbstraction instanceof FunctionComponent)) {
                    if ((componentInfo.col == x) && (componentInfo.row == y)) {
                        Log.v(TAG, " x and y coordinates is already taken. Select another x and y");
                        return false;
                    }
                }
            }
        }

        boolean b = addComponent(componentAbstraction);
        if (b) {
            setComponentColRow(componentAbstraction, x, y);
        }
        return b;
    }

    public boolean setAlias(ComponentAbstraction componentAbstraction, String aliasName) {
        return setAlias(componentAbstraction.getId(), aliasName);
    }

    public boolean setAlias(int id, String aliasName) {

        if (componentDataManager.setAlias(id, aliasName)) {
            componentArray[id].setAlias(aliasName);
            return true;
        } else {
            return false;
        }
    }

    public void setName(int id, String name) {
        getComponent(id).setName(name);
        getComponentInfo(id).name = name;
    }

    public boolean getEnable(ComponentAbstraction componentAbstraction) {
        return componentAbstraction.enabled;
    }

    public boolean getEnable(int id) {
        return componentArray[id].enabled;
    }

    // Set to run in order mode
    public void setOrderMode(boolean isOrderMode) {
        componentDataManager.orderMode = isOrderMode;
    }

    public boolean getOrderMode() {
        return componentDataManager.orderMode;
    }

    public void setMultiThreadMode(boolean multiThreadMode) {
        componentDataManager.multiThreadMode = multiThreadMode;
    }

    public boolean removeComponentByAlias(String alias) {
        int id = this.findIdByAlias(alias);
        return removeComponentById(id);
    }

    public boolean removeComponentById(int id) {
        if (id < 0) {
            return false;
        }

        ComponentAbstraction com = this.getComponent(id);
        if (com != null) {
            // Calling on destroy
            com.onDestroy();
        }

        componentDataManager.removeComponentInfo((Integer) id);

        setComponentIdToArray(id, null);
        return true;
    }

    public boolean removeAllFuncionIOComponentById(int funcionId) {
        return removeAllFuncionIOComponentById(funcionId, -1);
    }

    public boolean removeAllFuncionIOComponentById(int funcionId, int ioType) {
        // Checkt to make sure x and y are not overlapping
        ConcurrentHashMap<Integer, ComponentInfo> componentHashMap = componentDataManager.getComponentInfoHashMap();
        Iterator it = componentHashMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            int id = (Integer) pair.getKey();
            ComponentInfo componentInfo = (ComponentInfo) pair.getValue();

            // Avoding checking itself
            if (id == funcionId) {
                continue;
            }

            if (componentInfo.functionId == funcionId) {
                ComponentAbstraction componentAbstraction = componentArray[id];
                // Bypass it because creating funcion on the fly

                // input only 
                if (ioType == 0) {
                    if ((componentAbstraction instanceof FunctionInputComponent)) {
                        removeComponentById(id);
                    }
                } // output only 
                else if (ioType == 0) {
                    if ((componentAbstraction instanceof FunctionOutputComponent)) {
                        removeComponentById(id);
                    }

                } // all input 
                else {
                    if ((componentAbstraction instanceof FunctionInputComponent) || (componentAbstraction instanceof FunctionOutputComponent)) {
                        removeComponentById(id);
                    }
                }
            }
        }
        return true;
    }

    public boolean removeComponent(ComponentAbstraction componentAbstraction) {

        return removeComponentById(componentAbstraction.getId());
    }

    public boolean setComponentColRow(ComponentAbstraction componentAbstraction, int x, int y) {
        return setComponentColRowById(componentAbstraction.getId(), x, y);
    }

    public boolean setComponentColRowById(int id, int x, int y) {
        if (id < 0) {
            return false;
        }
        componentDataManager.setComponentInfoXY(id, x, y);
        return true;
    }

    public boolean containColRow(int functionId, int col, int row) {
        ConcurrentHashMap<Integer, ComponentInfo> componentHashMap = componentDataManager.getComponentInfoHashMap();
        Iterator it = componentHashMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            int id = (Integer) pair.getKey();
            ComponentInfo c = (ComponentInfo) pair.getValue();
            if (c.functionId == functionId) {

                if (componentArray[id] != null) {
                    int bodySize = componentArray[id].getBodySize();

                    if (col == c.col) {
                        if ((row >= c.row) && (row < (c.row + bodySize))) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean containColRowOrig(int col, int row) {
        ConcurrentHashMap<Integer, ComponentInfo> componentHashMap = componentDataManager.getComponentInfoHashMap();
        Iterator it = componentHashMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            int id = (Integer) pair.getKey();
            ComponentInfo c = (ComponentInfo) pair.getValue();
            int bodySize = componentArray[id].getBodySize();

            if (col == c.col) {
                if ((row >= c.row) && (row < (c.row + bodySize))) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean setComponentColRowByAlias(String alias, int x, int y) {
        int id = componentDataManager.findIdByAlias(alias);
        return setComponentColRowById(id, x, y);
    }

    public boolean addConnectComponentInOutput(ComponentAbstraction componentSrc, int srcOutputId, ComponentAbstraction componentDest, int destInputId) {

        return addConnectComponentInOutput(componentSrc.getId(), srcOutputId, componentDest.getId(), destInputId);
    }

    public boolean checkIfIOConnectionTypeIsMatched(int srcId, int srcOutputId, int destId, int destInputId) {

        ArrayList<DataTypeIO> typesSrc = componentArray[srcId].getOutputDataType().get(srcOutputId).typeArrayList;
        ArrayList<DataTypeIO> typesDest = componentArray[destId].getInputDataType().get(destInputId).typeArrayList;

        boolean contained = false;
        for (int i = 0; i < typesSrc.size(); i++) {
            String srcType = typesSrc.get(i).getTypeName();
            if (srcType.equalsIgnoreCase("all")) {
                contained = true;
                break;
            }
            for (int k = 0; k < typesDest.size(); k++) {
                String destType = typesDest.get(k).getTypeName();
                if (srcType.equalsIgnoreCase(destType)) {
                    contained = true;
                    break;
                } else if (destType.equalsIgnoreCase("all")) {
                    contained = true;
                    break;
                }
            }
            if (contained) {
                break;
            }
        }

        if (contained == false) {
            Log.v("VT", "Type doesn't match.... type src = " + typesSrc);

            for (int i = 0; i < typesSrc.size(); i++) {
                String srcType = typesSrc.get(i).getTypeName();
                System.out.println("error from above srcType = " + srcType);
            }

            return false;
        }

        return contained;
    }

    public boolean checkIfIOConnectionTypeIsMatched(String srcType, int id, int thruId) {

        ArrayList<DataTypeIO> typesDest = componentArray[id].getInputDataType().get(thruId).typeArrayList;

        boolean contained = false;
        if (srcType.equalsIgnoreCase("all")) {
            contained = true;

        }
        for (int k = 0; k < typesDest.size(); k++) {
            String destType = typesDest.get(k).getTypeName();
            if (srcType.equalsIgnoreCase(destType)) {
                contained = true;
                break;
            } else if (destType.equalsIgnoreCase("all")) {
                contained = true;
                break;
            }
        }

        return contained;
    }

    public boolean addConnectComponentInOutput(int srcId, int srcOutputId, int destId, int destInputId) {
        /*
        //ArrayList<DataTypeIOList> tmp = componentArray[srcId].getOutputDataType();
        ArrayList<DataTypeIO> typesSrc = componentArray[srcId].getOutputDataType().get(srcOutputId).typeArrayList;
        ArrayList<DataTypeIO> typesDest = componentArray[destId].getInputDataType().get(destInputId).typeArrayList;

        boolean contained = false;
        for (int i = 0; i < typesSrc.size(); i++) {
            String srcType = typesSrc.get(i).getTypeName();
            if (srcType.equalsIgnoreCase("all")) {
                contained = true;
                break;
            }
            for (int k = 0; k < typesDest.size(); k++) {
                String destType = typesDest.get(k).getTypeName();
                if (srcType.equalsIgnoreCase(destType)) {
                    contained = true;
                    break;
                } else if (destType.equalsIgnoreCase("all")) {
                    contained = true;
                    break;
                }
            }
            if (contained) {
                break;
            }
        }

        if (contained == false) {
            Log.v("VT", "Type doesn't match.... type src = " + typesSrc);

            for (int i = 0; i < typesSrc.size(); i++) {
                String srcType = typesSrc.get(i).getTypeName();
                System.out.println("error from above srcType = " + srcType);
            }

            return false;
        }
        
                componentDataManager.addComponentConnection(srcId, srcOutputId, destId, destInputId);
        return true;
         */

        boolean b = checkIfIOConnectionTypeIsMatched(srcId, srcOutputId, destId, destInputId);
        if (b) {
            componentDataManager.addComponentConnection(srcId, srcOutputId, destId, destInputId);
        }
        return b;
    }

    public boolean removeConnectComponentInOutput(ComponentAbstraction componentSrc, int srcOutputId, ComponentAbstraction componentDest, int destInputId) {
        return removeConnectComponentInOutputById(componentSrc.getId(), srcOutputId, componentDest.getId(), destInputId);
    }

    public boolean removeConnectComponentInOutputById(int id, int srcOutputId, int destId, int destInputId) {
        if (id < 0) {
            return false;
        }
        componentDataManager.removeComponentConnection(id, srcOutputId, destId, destInputId);
        return true;
    }

    public int getThruOutputConnectionSize(int srcID, int srcThruOutID) {
        return componentDataManager.componentThuOutSize[srcID][srcThruOutID];
    }

    public int[][] getComponentConnection(int srcID, int srcThruOutID) {

        return componentDataManager.componentConnection[srcID][srcThruOutID];
    }

    private void shutdownExecManagerThread() {

        if (execManagerThread != null) {
            try {
                Thread t = execManagerThread;
                t.interrupt();
                execManagerThread = null;
                t = null;
            } catch (Exception e) {

            }

            execManagerThread = null;
        }

    }

    public void start() {
//        shutdownExecManagerThread();
//        execManagerThread = new ExecManagerThread(componentArray, componentDataManager);
//        execManagerThread.startProcess();

        start(-1);
    }

    public void start(int componentId) {
        shutdownExecManagerThread();

        execManagerThread = new ExecManagerThread(componentArray, componentDataManager);
        execManagerThread.startProcess(componentId);

    }

    public void startComponentById(int id) {
        shutdownExecManagerThread();
        execManagerThread = new ExecManagerThread(componentArray, componentDataManager);
        execManagerThread.startProcess();

    }

    public void stops() {
        if (execManagerThread != null) {
            execManagerThread.stopProcess();
            shutdownExecManagerThread();
        }
    }

    public void stops(int componentId) {
        shutdownExecManagerThread();
        execManagerThread = new ExecManagerThread(componentArray, componentDataManager);
        execManagerThread.stopProcess(componentId);

    }

    public void stopsManual(int componentId) {
        if (execManagerThread != null) {
            execManagerThread.stopProcess(componentId);
            // shutdownExecManagerThread();
        }
    }

//    public void stops(int componentId) {
//        shutdownExecManagerThread();
//        execManagerThread = new ExecManagerThread(componentArray, componentDataManager);
//        execManagerThread.stopProcess(componentId);
//
//    }
    public void pauseProcess() {
        if (execManagerThread != null) {
            execManagerThread.pauseProcess();
        }
    }

    public void resumeProcess() {
        if (execManagerThread != null) {
            execManagerThread.resumeProcess();
        }
    }

    private int findIdByAlias(String alias) {
        return componentDataManager.findIdByAlias(alias);
    }

    public void sendData(int srcId, int thruId, Object data) {
        componentArray[srcId].sendData(thruId, data);
    }

    public boolean sendDirectDataByComponent(ComponentAbstraction com, int thruId, Object data) {
        return sendDirectDataById(com.getId(), thruId, data);
    }

    public boolean sendDirectDataByAlias(String alias, int thruId, Object data) {
        int id = findIdByAlias(alias);
        return sendDirectDataById(id, thruId, data);
    }

    public boolean sendDirectDataById(int id, int thruId, Object data) {
        if (id > -1) {
            if (componentArray[id].enabled) {
                componentArray[id].handleMessage(thruId, data);
                componentArray[id].onExecute();
            }
            return true;
        }
        return false;
    }

    public Object execComponent(int id, int thruId, Object data) {
        if (id > -1) {
            if (componentArray[id].enabled) {
                componentArray[id].handleMessage(thruId, data);
                return componentArray[id].onExecute();
            }
        }
        return null;
    }

    public boolean setProperty(int srcId, String key, Object value) {
        if (srcId < 0) {
            return false;
        }
        if (componentArray[srcId] == null) {
            return false;
        }
        componentArray[srcId].setProperty(key, value);
        return true;
    }

    public void setOutputType(int componentId, int outThruId, String newType) {
        DataTypeIOList dataTypeList = new DataTypeIOList();
        dataTypeList.addDataType(new DataTypeIO(newType));
        getComponent(componentId).getOutputDataType().set(outThruId, dataTypeList);
        getComponentInfo(componentId).propHash.put("outputType", newType);
    }

    public void setInputType(int componentId, int inThruId, String newType) {
        DataTypeIOList dataTypeList = new DataTypeIOList();
        dataTypeList.addDataType(new DataTypeIO(newType));
        getComponent(componentId).getOutputDataType().set(inThruId, dataTypeList);
        getComponentInfo(componentId).propHash.put("inputType", newType);
    }

    public void clearAllData() {
        componentDataManager.clearAllData();
    }

    public boolean loadProjectFromJsonFile(String jsonFile) {

        componentDataManager.clearAllData();

        // clean data
        for (int i = 0; i < componentArray.length; i++) {
            setComponentIdToArray(i, null);
        }

        String line = null;
        StringBuilder jsonSB = new StringBuilder();
        try {
            FileReader fileReader
                    = new FileReader(jsonFile);
            BufferedReader bufferedReader
                    = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                jsonSB.append(line);
            }

            // Always close files.
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            Log.v("VT", "loadProjectFromJsonFile 1 Error: " + ex.getMessage());
            return false;
        } catch (IOException ex) {
            Log.v("VT", "loadProjectFromJsonFile 2 Error: " + ex.getMessage());
            return false;
        }

        String json = jsonSB.toString();
        componentDataManager = gson.fromJson(json, ComponentDataManager.class);
        messageSender = new MessageSender(componentArray, componentDataManager);

        ConcurrentHashMap<Integer, ComponentInfo> componentHashMap = componentDataManager.getComponentInfoHashMap();
        Iterator it = componentHashMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            int id = (Integer) pair.getKey();
            // Resize
            resize(id);

            ComponentInfo componentInfo = (ComponentInfo) pair.getValue();
            String classname = componentInfo.className;

            // Does a class loader
            Class c = null;
            try {
                c = Class.forName(classname);
            } catch (ClassNotFoundException e) {
                //  e.printStackTrace();
                Log.v("VT", "error = " + e.getMessage());
                continue;
            }
            Class[] argtypes = new Class[]{};
            Constructor cons;
            try {
                cons = c.getConstructor(argtypes);

                Object[] args = new Object[]{};
                ComponentAbstraction componentAbstraction = (ComponentAbstraction) cons.newInstance(args);

                componentAbstraction.setEnable(componentInfo.enabled);

                if (componentAbstraction instanceof FunctionComponent) {
                    FunctionComponent fc = (FunctionComponent) componentAbstraction;
                    fc.setNumberOfInOutput(componentInfo.numOfInput, componentInfo.numOfOutput);
                }

                componentAbstraction.setComponentIDNum(id);
                componentAbstraction.setMessageSender(messageSender);
                componentAbstraction.setName(componentInfo.name);
                componentAbstraction.setHashMapProperty(componentInfo.propHash);
                componentAbstraction.setAlias(componentInfo.alias);

                Iterator iterator = componentInfo.propHash.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry propMap = (Map.Entry) iterator.next();
                    String key = propMap.getKey().toString();
                    Object value = propMap.getValue();
                    //  System.out.println(key + " : " + value);
                    // if (key.equalsIgnoreCase("value")) {
                    //    System.out.println("here..");
                    //  }

                    componentAbstraction.setProperty(propMap.getKey().toString(), propMap.getValue());
                    if (componentAbstraction.getIniEnableLoadPropertyFromFile()) {
                        componentAbstraction.loadProperty(propMap.getKey().toString(), propMap.getValue());
                    }
                }

                if (componentInfo.propHash.get("outputType") != null) {
                    //      if (componentAbstraction instanceof TypeCastConverterComponent) {
                    //TypeCastConverterComponent tcc = (TypeCastConverterComponent) componentAbstraction;

                    DataTypeIOList dataTypeList = new DataTypeIOList();
                    dataTypeList.addDataType(new DataTypeIO(componentInfo.propHash.get("outputType").toString()));

                    componentAbstraction.getOutputDataType().set(0, dataTypeList);
                    //  setOutputType(componentAbstraction.getId(), 0, componentInfo.propHash.get("outputType").toString());
                    // int componentId, int outThruId, String newType

                }

                // componentArray[id] = componentAbstraction;
                setComponentIdToArray(id, componentAbstraction);
                // Tell the component I'm done loading the class
                componentAbstraction.doneLoadingThisClassFromProjectFile();

            } catch (NoSuchMethodException ex) {
                Log.v("VT", "loadProjectFromJsonFile 77 Error: " + ex.getLocalizedMessage());
                ex.printStackTrace();
                return false;
            } catch (SecurityException ex) {
                Log.v("VT", "loadProjectFromJsonFile 55 Error: " + ex.getLocalizedMessage());
                ex.printStackTrace();
                return false;
            } catch (Exception e) {

                Log.v("VT", "loadProjectFromJsonFile 44 Error: " + e.getLocalizedMessage());
                e.printStackTrace();
                return false;
            }

        }
        return true;
    }

    public void saveProjectFile(String filname) {
        String json = gson.toJson(componentDataManager);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filname));
            writer.write(json);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addMessageOutputListener(ComponentAbstraction comp, int thruOutId, MessageListener messageListener) {
        addMessageOutputListener(comp.getId(), thruOutId, messageListener);
    }

    public void addMessageOutputListener(int id, int thruOutId, MessageListener messageListener) {
        int key = id * 1000000 + thruOutId;;
        messageSender.messageListenerHashMap.put(key, messageListener);
    }

    public void addMessageOutputListenerByAlias(String alias, int thruOutId, MessageListener messageListener) {
        int id = this.findIdByAlias(alias);
        addMessageOutputListener(id, thruOutId, messageListener);
    }

    public void removeMessageOutputListenerByAlias(String alias, int thruOutId) {
        int id = this.findIdByAlias(alias);
        removeMessageOutputListener(id, thruOutId);
    }

    public void removeMessageOutputListener(ComponentAbstraction comp, int thruOutId) {
        removeMessageOutputListener(comp.getId(), thruOutId);
    }

    public void removeMessageOutputListener(int id, int thruOutId) {
        int key = id * 1000000 + thruOutId;
        messageSender.messageListenerHashMap.remove(key);
    }

    public void printInfo() {
        // componentDataManager.printInfo();

        ConcurrentHashMap<Integer, ComponentInfo> componentHashMap = componentDataManager.getComponentInfoHashMap();

        for (Map.Entry<Integer, ComponentInfo> entry : componentHashMap.entrySet()) {
            ComponentInfo c = entry.getValue();

            int id = c.id;
            ComponentAbstraction com = componentArray[id];
            Log.v("VT", "id = " + c.id + " name = " + c.name + " alias = " + c.alias + " className = " + c.className + " x = " + c.col + " y = " + c.row + " #input = " + com.getNumberOfInputSize() + " #output = " + com.getNumberOfOutputSize());
            int numOfOutput = com.getNumberOfOutputSize();
            for (int k = 0; k < numOfOutput; k++) {
                int comOutSize = componentDataManager.componentThuOutSize[id][k];
                int dataMatrix[][] = componentDataManager.componentConnection[id][k];
                for (int i = 0; i < comOutSize; i++) {
                    int destID = dataMatrix[i][0];
                    int destThruInID = dataMatrix[i][1];
                    Log.v("VT", "------> Out from srcId " + id + " and outputId: " + k + " to desId: " + destID + " to inputThruId: " + destThruInID);
                }
            }

        }
    }

    public void cloneComponentInfo(ComponentInfo infoSrc, ComponentInfo infoDes) {

        infoDes.alias = componentDataManager.getAvailableAlias(infoDes.alias);
        //infoDes.name = infoSrc.name + "_copy";
        infoDes.name = infoSrc.name;
        HashMap<String, Object> prop = infoSrc.propHash;
        Iterator it = prop.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String key = (String) pair.getKey();
            Object obj = pair.getValue();
            infoDes.propHash.put(key, obj);
        }
    }

}
