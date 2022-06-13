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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 *
 * @author Vithya Tith
 */
public class FunctionComponent extends ComponentAbstraction {

    private int ids[] = null;
    private ComponentAbstraction componentArray[] = null;
    private int inputCount = 0;
    private int inputLength = 0;
    private int outputLength = 0;

    private ComponentDataManager componentDataManager = null;
    private int inDest[][] = null;

    private int outDest[][] = null;

    private Object results[] = null;

    private ExecManagerThread execManagerThread = null;

    public FunctionComponent() {
        setName("Function");
        setAlias("Func");
        isFunctionComponent = true;

    }

    protected void setExecutingComponentIds(int ids[], ComponentAbstraction comArray[], ComponentDataManager componentDataManager) {
        if (execManagerThread != null) {
            execManagerThread = new ExecManagerThread(componentArray, componentDataManager);
            // execManagerThread.startProcess();
        }
        setExecutingComponentIds(false, ids, comArray, componentDataManager);

    }

    public HashSet<Integer> listAllConnectionsFromNode(int id) {
        HashSet<Integer> rootHashSet = new HashSet<Integer>();
        try {
            LinkedHashSet hashSet = componentDataManager.listAllComponentConnectionById(id);
            Iterator<Integer> rootIT = hashSet.iterator();
            while (rootIT.hasNext()) {
                int srcId = rootIT.next();

                if (srcId == id) {
                    System.out.println("Avoid calling itself 1" + id);
                    continue;
                }
                rootHashSet.add(srcId);

                HashSet<Integer> childHashSet = listAllConnectionsFromNode(srcId);

                Iterator<Integer> childIT = childHashSet.iterator();
                while (childIT.hasNext()) {
                    srcId = childIT.next();

                    rootHashSet.add(srcId);
                }
            }
        } catch (StackOverflowError e) {
            System.out.println("id ===========" + id);
            e.printStackTrace();
        }

        return rootHashSet;
    }

    protected void setExecutingComponentIds(boolean runOneTimeBool, int ids[], ComponentAbstraction comArray[], ComponentDataManager componentDataManager) {
        this.ids = ids;
        componentArray = comArray;
        this.componentDataManager = componentDataManager;

        ComponentAbstraction com = null;
        int len = ids.length;
        for (int i = 0; i < len; i++) {

            int id = ids[i];

            com = componentArray[id];

            if (com.enabled) {

                ComponentInfo componentInfo = componentDataManager.getComponentInfoHashMap().get(id);
                // Only execute if on the same plan
                if (componentInfo.functionId != this.getId()) {
                    continue;
                }

                //////////////////////////////////////////
                int orderArray[] = componentDataManager.getIdExecOrderArray(id);
                int orderSize = orderArray.length;
                HashSet<Integer> hashSet = listAllConnectionsFromNode(id);
                Iterator<Integer> it = hashSet.iterator();
                while (it.hasNext()) {
                    int srcId = it.next();
                    componentInfo = componentDataManager.getComponentInfoHashMap().get(id);
                    if (componentInfo.isFunctionOutput) {
                        for (int ii = 0; ii < orderSize; ii++) {
                            int idTmp = orderArray[ii];
                            ComponentAbstraction comp = componentArray[idTmp];
                            if (comp != null) {
                                if (comp.enabled) {
                                    if (comp.getId() == srcId) {
                                        // comp.start();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                ///////////////////////////////////
                if (componentInfo.isFunction) {
                    int idsTmp[] = componentDataManager.getIdExecOrderArrayForMethodId(ids[i]);

                    FunctionComponent fc = (FunctionComponent) com;
                    fc.setExecutingComponentIds(idsTmp, componentArray, componentDataManager);
                }

                //   com.start();
            }
        }

//        if(runOneTimeBool){
//            onExecute();
//        }
    }

    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }

    protected boolean setInDest(int srcThruId, int destId, int destThruId) {
        if (srcThruId >= inDest.length) {
            System.out.println("SetInDest if out of bound. thruId is " + srcThruId + " inDest length is " + inDest.length);
            return false;
        }
        inDest[srcThruId][0] = destId;
        inDest[srcThruId][1] = destThruId;
        return true;
    }

    @Override
    public String getHelp() {

        String doc = "";

        return doc;
    }

    protected boolean setOutDest(int srcThruId, int destId, int destThruId) {
        if (srcThruId >= outDest.length) {
            System.out.println("Setting destination out is out of order");
            return false;
        }
        outDest[srcThruId][0] = destId;
        outDest[srcThruId][1] = destThruId;
        return false;
    }

    protected void setComponentArray(ComponentAbstraction componentArray[]) {
        this.componentArray = componentArray;
    }

    public void setNumberOfInput(int lenIn) {
        setNumberOfInOutput(lenIn, -1);
    }

    public void setNumberOfOutput(int lenOut) {
        setNumberOfInOutput(-1, lenOut);
    }

    public void setNumberOfInOutput(int lenIn, int lenOut) {

        if (lenIn > -1) {
            inputLength = lenIn;
            inDest = new int[lenIn][3];
            inputArrayList.clear();
            for (int i = 0; i < lenIn; i++) {
                this.addInput(new DataTypeIO("all"));
            }
        }
        if (lenOut > -1) {
            outputLength = lenOut;
            outputArrayList.clear();
            outDest = new int[lenOut][3];
            results = new Object[lenOut];
            for (int i = 0; i < lenOut; i++) {
                this.addOutput(new DataTypeIO("all"));

            }
        }
    }

    @Override
    public Object onExecute() {

        if (inputCount >= inputLength) {
            int len = ids.length;
            for (int k = 0; k < len; k++) {
                int id = ids[k];
                ComponentAbstraction com = componentArray[id];
                
                ComponentInfo componentInfo = componentDataManager.getComponentInfoHashMap().get(id);
                
                if(componentInfo.isFunctionInput){
                    System.out.println("here......");
                    if(execManagerThread==null){
                        execManagerThread = new ExecManagerThread(componentArray, componentDataManager);
                    }
                    execManagerThread.startProcess(id);
                    
                }
                 
            }
            inputCount = 0;
        }
        return results;
    }
  
    public Object onExecuteOrig() {

        if (inputCount >= inputLength) {
            int len = ids.length;
            for (int k = 0; k < len; k++) {
                int id = ids[k];
                ComponentAbstraction com = componentArray[id];
                if (com.enabled) {
                    Object r = com.onExecute();
                    for (int i = 0; i < outputLength; i++) {
                        int destId = outDest[i][0];
                        int thruId = outDest[i][1];
                        if (id == destId) {
                            this.sendData(thruId, r);
                            results[thruId] = r;
                            break;
                        }
                    }
                }
            }
            inputCount = 0;
        }
        return results;
    }
    public Object input(Object... args) {
        int len = args.length;
        for (int i = 0; i < len; i++) {
            handleMessage(i, args[i]);
        }

        return onExecute();
    }

    @Override
    public void handleMessage(int thru, Object obj) {

        if (thru >= inputLength) {
            System.out.println("Throughtput is out of index at clase " + this.getClass().getName() + " the size is " + inputLength + " the index is " + thru + " alias = " + this.getAlias());
            return;
        }

        int myId = getId();
        int destId = inDest[thru][0];
        int destThruId = inDest[thru][1];
        if (destId == getId()) {
            return;
        }
        componentArray[destId].handleMessage(destThruId, obj);
        inputCount++;

    }

    @Override
    public boolean start() {

        int len = ids.length;
        for (int k = 0; k < len; k++) {
            int id = ids[k];
            ComponentAbstraction com = componentArray[id];
            com.start();
        }

        return true;
    }

    @Override
    public void stop() {
        int len = ids.length;
        for (int k = 0; k < len; k++) {
            int id = ids[k];
            ComponentAbstraction com = componentArray[id];
            com.stop();
        }
    }

    @Override
    public void loadProperty(String key, Object value) {
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

}
