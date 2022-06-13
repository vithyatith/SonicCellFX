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

import java.util.HashMap;

/**
 *
 * @author Administrator
 */
public final class MessageSender {

    private ComponentDataManager componentDataManager = null;
    private ComponentAbstraction componentArray[];
    protected HashMap<Integer, MessageListener> messageListenerHashMap = new HashMap<Integer, MessageListener>();

    public MessageSender(ComponentAbstraction _componentArray[], ComponentDataManager dms) {
        componentDataManager = dms;
        componentArray = _componentArray;
    }

    public int getNumerOfConnectionInputOccupy(int id) {
        return componentDataManager.getNumerOfConnectionInputOccupyById(id);
    }

    public void setComponentArray(ComponentAbstraction _componentArray[]) {
        componentArray = _componentArray;
    }
    
    public void senManualData(int destId, int thruId, Object value){
        ComponentAbstraction com = null;
        if(componentArray!=null){
            int len = componentArray.length;
            for(int i=0; i<len; i++){
                com = componentArray[i];
                if(com.getId()==destId){
                    com.handleMessage(thruId, value);
                    break;
                }
            }
        }
    }
    
    
    

    public void senData(int id, int thruID, Object value) {
        

        int comOutSize = componentDataManager.componentThuOutSize[id][thruID];
        int dataMatrix[][] = componentDataManager.componentConnection[id][thruID];

        for (int i = 0; i < comOutSize; i++) {
            int destID = dataMatrix[i][0];
            int destThruInID = dataMatrix[i][1];

            if (componentArray[destID].enabled) {
                componentArray[destID].handleMessage(destThruInID, value);
                if (componentDataManager.orderMode == false) {
                    componentArray[destID].onExecute();
                }
            }
        }

        if (messageListenerHashMap.size() > 0) {
            int key = id * 1000000 + thruID;
            MessageListener ml = messageListenerHashMap.get(key);
            if (ml != null) {
                ml.onReceived(id, thruID, value);
            }
        }

    }
}
