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

import com.sonicmsgr.soniccell.util.ConnectionUtil;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Vithya Tith
 */
public final class ComponentDataManager {

    // System Properties
    public int numberOfRun = -1;
    protected boolean orderMode = false;
    protected boolean multiThreadMode = true;

    private int MAX_COMPONENT = 10;
    private int MAX_THRU_PUT_SIZE = 10;
    private int MAX_OUT_CONNECTION_SIZE = 10;

    protected int componentConnection[][][][] = new int[MAX_COMPONENT][MAX_THRU_PUT_SIZE][MAX_OUT_CONNECTION_SIZE][2];
    protected int componentThuOutSize[][] = new int[MAX_COMPONENT][MAX_THRU_PUT_SIZE];

    private final ConcurrentHashMap<Integer, ComponentInfo> componentInfoHashMap = new ConcurrentHashMap<Integer, ComponentInfo>();

    private final ConcurrentHashMap<Integer, ConnectionInfo> connectionInfoHashMap = new ConcurrentHashMap<Integer, ConnectionInfo>();

    protected ArrayList<ThroughputReferenceInfo> throughputReferenceInfoArrayList = new ArrayList<ThroughputReferenceInfo>();

    private ArrayList<Long> connectionLineHashMap = new ArrayList<Long>();

    public void clearAllData() {
        connectionLineHashMap.clear();
        throughputReferenceInfoArrayList.clear();
        connectionInfoHashMap.clear();
        componentInfoHashMap.clear();

        for (int i = 0; i < componentConnection.length; i++) {
            for (int j = 0; j < componentConnection[0].length; j++) {
                for (int k = 0; k < componentConnection[0][0].length; k++) {
                    componentConnection[i][j][k][0] = -1;
                    componentConnection[i][j][k][1] = -1;
                }
            }
        }

        for (int i = 0; i < componentConnection.length; i++) {
            for (int j = 0; j < componentConnection[0].length; j++) {
                componentThuOutSize[i][j] = 0;
            }
        }
    }

    public ComponentDataManager() {
        clearAllConnection();
    }

    protected void removeAllThroughputReference(int id) {

        Iterator<ThroughputReferenceInfo> iter = throughputReferenceInfoArrayList.iterator();
        while (iter.hasNext()) {
            ThroughputReferenceInfo th = iter.next();
            if (th.srcId == id) {
                iter.remove(); // Removes the 'current' item
            }
        }
    }

    protected boolean removeThroughputReference(ThroughputReferenceInfo ref) {
        for (int i = 0; i < throughputReferenceInfoArrayList.size(); i++) {

            ThroughputReferenceInfo th = throughputReferenceInfoArrayList.get(i);

            if ((ref.ioType == th.ioType)
                    && (ref.srcId == th.srcId)
                    && (ref.srcThruId == th.srcThruId)
                    && (ref.destThruId == th.destThruId)
                    && (ref.destId == th.destId)) {
                throughputReferenceInfoArrayList.remove(ref);
                return true;
            }
        }
        return false;
    }

    protected boolean addThroughputReference(ThroughputReferenceInfo ref) {

        for (int i = 0; i < throughputReferenceInfoArrayList.size(); i++) {

            ThroughputReferenceInfo th = throughputReferenceInfoArrayList.get(i);

            if ((ref.ioType == th.ioType)
                    && (ref.srcId == th.srcId)
                    && (ref.srcThruId == th.srcThruId)
                    && (ref.destThruId == th.destThruId)
                    && (ref.destId == th.destId)) {
                return false;
            }
        }
        throughputReferenceInfoArrayList.add(ref);

        return true;
    }

    protected void updateThroughputReference(int idOld, int idNew) {

        for (int i = 0; i < throughputReferenceInfoArrayList.size(); i++) {
            ThroughputReferenceInfo th = throughputReferenceInfoArrayList.get(i);
            int src = th.srcId;
            int des = th.destId;

            if (src == idOld) {
                th.srcId = idNew;
            } else if (des == idOld) {
                th.destId = idNew;
            }

        }
    }

    private void clearAllConnection() {
        for (int i = 0; i < componentConnection.length; i++) {
            for (int j = 0; j < componentConnection[0].length; j++) {
                for (int k = 0; k < componentConnection[0][0].length; k++) {
                    componentConnection[i][j][k][0] = -1;
                    componentConnection[i][j][k][1] = -1;
                }
            }
        }
    }

    public int getAvailableId() {

        if (componentInfoHashMap.size() == 0) {
            return 0;
        }

        for (int i = 0; i < componentInfoHashMap.size(); i++) {
            if (!componentInfoHashMap.containsKey((Integer) i)) {
                return i;
            }
        }

        resizeConnection(componentInfoHashMap.size() + 1, 1, 1);
        return componentInfoHashMap.size();
    }

    public String getAvailableAlias(String aliasName) {
        int index = 0;

        Iterator it = componentInfoHashMap.entrySet().iterator();

        for (Map.Entry<Integer, ComponentInfo> entry : componentInfoHashMap.entrySet()) {
            ComponentInfo c = entry.getValue();
            if (aliasName.equalsIgnoreCase(c.alias)) {
                String tryAliasName = aliasName + index;
                index++;
                return getAvailableAlias(tryAliasName);
            }
        }

        return aliasName;

    }

    public void addComponentInfo(int id, ComponentInfo componentInfo) {
        componentInfoHashMap.put(id, componentInfo);
        if (componentInfoHashMap.size() > MAX_COMPONENT) {
            MAX_COMPONENT = componentInfoHashMap.size() + 10;

        }
    }

    protected ConcurrentHashMap<Integer, ComponentInfo> getComponentInfoHashMap() {
        return componentInfoHashMap;
    }

    /**
     * Sorting the id base on row and col execution order.
     *
     * @return
     */
    
     protected int[] getIdExecOrderArray() {
         return getIdExecOrderArray(-1);
     }
    
    protected int[] getIdExecOrderArray(int rootId) {
        int tmps[] = new int[componentInfoHashMap.size()];
        int i = 0;
        
        int thisFunctionId = -1;
        
        if(rootId>=0){
           ComponentInfo info =  componentInfoHashMap.get(rootId);
           thisFunctionId = info.functionId;
        }
        
        
        for (Map.Entry<Integer, ComponentInfo> entry : componentInfoHashMap.entrySet()) {

            ComponentInfo c = entry.getValue();
            if (c.functionId == thisFunctionId) {
                tmps[i] = c.col * 100000000 + c.row * 10000 + c.id;
                i++;
            }
        }
        int ids[] = new int[i];
        for (int k = 0; k < ids.length; k++) {
            ids[k] = tmps[k];
        }

        Arrays.sort(ids);
        i = 0;
        for (i = 0; i < ids.length; i++) {
            int a = ids[i] / 100000000;
            int b = (ids[i] - a * 100000000) / 10000;
            int id = (ids[i] - a * 100000000 - b * 10000);
            ids[i] = id;
        }

        return ids;
    }
    private int idsTmp[] = new int[MAX_COMPONENT * 2];

    protected int[] getIdExecOrderArrayForMethodId(int methodId) {

        if (idsTmp.length < (MAX_COMPONENT * 2)) {
            idsTmp = new int[MAX_COMPONENT * 2];
        }

        // int ids[] = new int[componentHashMap.size()];
        int i = 0;
        for (Map.Entry<Integer, ComponentInfo> entry : componentInfoHashMap.entrySet()) {
            //int id = entry.getKey();
            ComponentInfo c = entry.getValue();
            if (c.functionId == methodId) {
                idsTmp[i] = c.col * 100000000 + c.row * 10000 + c.id;
                i++;
            }
        }

        int ids[] = new int[i];
        for (int k = 0; k < ids.length; k++) {
            ids[k] = idsTmp[k];
        }

        Arrays.sort(ids);
        i = 0;
        for (i = 0; i < ids.length; i++) {
            int a = ids[i] / 100000000;
            int b = (ids[i] - a * 100000000) / 10000;
            int id = (ids[i] - a * 100000000 - b * 10000);
            ids[i] = id;
        }

        return ids;
    }

    protected int[] getIdExecOrderArrayForAllMethods() {

        int i = 0;
        for (Map.Entry<Integer, ComponentInfo> entry : componentInfoHashMap.entrySet()) {
            //int id = entry.getKey();
            ComponentInfo c = entry.getValue();
            if (c.isFunction) {
                idsTmp[i] = c.col * 100000000 + c.row * 10000 + c.id;
                i++;
            }
        }

        int ids[] = new int[i];
        for (int k = 0; k < ids.length; k++) {
            ids[k] = idsTmp[k];
        }

        Arrays.sort(ids);
        i = 0;
        for (i = 0; i < ids.length; i++) {
            int a = ids[i] / 100000000;
            int b = (ids[i] - a * 100000000) / 10000;
            int id = (ids[i] - a * 100000000 - b * 10000);
            ids[i] = id;
        }

        return ids;
    }

    public void removeComponentInfo(Integer id) {

        // Remove all the relationship
        removeAllComponentConnectionById(id);

        // Remove this relationship
        for (int i = 0; i < componentConnection[id].length; i++) {
            for (int j = 0; j < componentConnection[id][i].length; j++) {
                componentConnection[id][i][j][0] = -1;
                componentConnection[id][i][j][1] = -1;
            }
        }

        componentInfoHashMap.remove((Integer) id);

        // Remove outputsize 
        for (int i = 0; i < componentThuOutSize[id].length; i++) {
            componentThuOutSize[id][i] = 0;
        }

    }

    public void setComponentInfoXY(int id, int x, int y) {
        componentInfoHashMap.get(id).col = x;
        componentInfoHashMap.get(id).row = y;
    }

    protected int findIdByAlias(String alias) {

        for (Map.Entry<Integer, ComponentInfo> entry : componentInfoHashMap.entrySet()) {
            ComponentInfo c = entry.getValue();
            if (c.alias.equals(alias)) {
                return c.id;
            }
        }
        return -1;

    }

    protected boolean setAlias(int id, String alias) {

        for (Map.Entry<Integer, ComponentInfo> entry : componentInfoHashMap.entrySet()) {
            ComponentInfo c = entry.getValue();
            if (c.alias.equals(alias)) {

                System.out.println("Alias already exist. Can not set.");
                return false;
            }
        }

        ComponentInfo c = componentInfoHashMap.get(id);
        c.alias = alias;

        return true;

    }

    public boolean addComponentConnection(int srcID, int srcThruOutID, int destID, int destThruInID) {

        int code = destID * 1000000 + destThruInID;
        if (connectionInfoHashMap.containsKey(srcID)) {
            HashMap<Integer, ArrayList<Integer>> connectionHistory = connectionInfoHashMap.get(srcID).connectionHistory;

            if (connectionHistory.containsKey(srcThruOutID)) {
                ArrayList<Integer> al = connectionHistory.get(srcThruOutID);

                if (al.contains(code)) {
                    // alread contined
                } else {
                    al.add(code);
                }

            } else {
                ArrayList<Integer> al = new ArrayList<Integer>();
                al.add(code);
                connectionHistory.put(srcThruOutID, al);
            }

        } else {
            ConnectionInfo connectinfo = new ConnectionInfo();
            connectinfo.idSrc = srcID;

            ArrayList<Integer> al = new ArrayList<Integer>();
            al.add(code);
            connectinfo.connectionHistory.put(srcThruOutID, al);
            connectionInfoHashMap.put(srcID, connectinfo);
        }
        int emptyIndex = -1;

        resizeConnection(srcID, srcThruOutID, 1);
        int len = componentConnection[srcID][srcThruOutID].length;

        for (int i = 0; i < len; i++) {
            int id = componentConnection[srcID][srcThruOutID][i][0];
            int thur = componentConnection[srcID][srcThruOutID][i][1];
            // Already contained, no need to add
            if ((id == destID) && (thur == destThruInID)) {
                return false;
            }

            if (id < 0) {
                emptyIndex = i;
                break;
            }

        }

        resizeConnection(srcID, srcThruOutID, emptyIndex);

        // add
        componentThuOutSize[srcID][srcThruOutID] = componentThuOutSize[srcID][srcThruOutID] + 1;
        componentConnection[srcID][srcThruOutID][emptyIndex][0] = destID;
        componentConnection[srcID][srcThruOutID][emptyIndex][1] = destThruInID;

        long key = ConnectionUtil.keyEncode(srcID, srcID, destID, destThruInID);
        connectionLineHashMap.add((Long) key);
        return true;
    }

    private void resizeConnection(int srcID, int srcThruOutID, int index) {

        int srcIDLen = componentConnection.length;
        int srcThruOutIDLen = componentConnection[0].length;
        int indexLen = componentConnection[0][0].length;

        int srcIDLenOrg = srcIDLen;
        int srcThruOutIDLenOrg = srcThruOutIDLen;
        int indexLenOrg = indexLen;

        boolean toResizeBool = false;

        if (srcID >= srcIDLen) {
            srcIDLen = srcID + 8;
            toResizeBool = true;
            MAX_COMPONENT = srcIDLen;

        }

        if (srcThruOutID >= srcThruOutIDLen) {
            srcThruOutIDLen = srcThruOutID + 4;
            toResizeBool = true;
        }

        if (index >= indexLen) {
            indexLen = index + 8;
            toResizeBool = true;
        }

        if (toResizeBool) {
            int tmp[][][][] = new int[srcIDLen][srcThruOutIDLen][indexLen][2];

            int tmpOutputSize[][] = new int[srcIDLen][srcThruOutIDLen];

            for (int i = 0; i < srcIDLen; i++) {
                for (int j = 0; j < srcThruOutIDLen; j++) {

                    tmpOutputSize[i][j] = 0;
                    for (int k = 0; k < indexLen; k++) {

                        for (int l = 0; l < 2; l++) {
                            tmp[i][j][k][l] = -1;
                        }
                    }
                }
            }

            for (int i = 0; i < srcIDLenOrg; i++) {
                for (int j = 0; j < srcThruOutIDLenOrg; j++) {

                    tmpOutputSize[i][j] = componentThuOutSize[i][j];
                    for (int k = 0; k < indexLenOrg; k++) {

                        for (int l = 0; l < 2; l++) {
                            tmp[i][j][k][l] = componentConnection[i][j][k][l];
                        }
                    }
                }
            }
            componentThuOutSize = tmpOutputSize;
            componentConnection = tmp;
        }

    }

    public int getThruOutputConnectionSize(int srcID, int srcThruOutID) {
        return componentThuOutSize[srcID][srcThruOutID];
    }

    public int[][] getComponentConnection(int srcID, int srcThruOutID) {
        return componentConnection[srcID][srcThruOutID];
    }

    private int tmp[][] = new int[100][2];

    public void removeAllComponentConnectionById(int destID) {

        int lenSrc = componentConnection.length;
        int lenSrcThru = componentConnection[0].length;
        int lenIndex = componentConnection[0][0].length;

        for (int srcID = 0; srcID < lenSrc; srcID++) {
            for (int srcThruOutID = 0; srcThruOutID < lenSrcThru; srcThruOutID++) {
                //  int foundConnecton = 0;
                int newSize = 0;

                for (int k = 0; k < lenIndex; k++) {
                    int thisDestId = componentConnection[srcID][srcThruOutID][k][0];
                    int thisDestThru = componentConnection[srcID][srcThruOutID][k][1];

                    if (destID == thisDestId) {
                        componentConnection[srcID][srcThruOutID][k][0] = -1;
                        componentConnection[srcID][srcThruOutID][k][1] = -1;
                        thisDestId = -1;
                        thisDestThru = -1;
                    }
                    if (thisDestId > -1) {
                        tmp[newSize][0] = thisDestId;
                        tmp[newSize][1] = thisDestThru;
                        newSize++;
                    }
                    // reset all equal to zeros for clarity
                    componentConnection[srcID][srcThruOutID][k][0] = -1;
                    componentConnection[srcID][srcThruOutID][k][1] = -1;

                }

                // Copy over new size;
                for (int i = 0; i < newSize; i++) {
                    componentConnection[srcID][srcThruOutID][i][0] = tmp[i][0];
                    componentConnection[srcID][srcThruOutID][i][1] = tmp[i][1];
                }
                componentThuOutSize[srcID][srcThruOutID] = newSize;

            }
        }

    }

    public LinkedHashSet listAllComponentConnectionById(int destID) {

        int lenSrc = componentConnection.length;
        int lenSrcThru = componentConnection[0].length;
        int lenIndex = componentConnection[0][0].length;
        LinkedHashSet<Integer> hashSet = new LinkedHashSet<Integer>();

        for (int srcID = 0; srcID < lenSrc; srcID++) {
            for (int srcThruOutID = 0; srcThruOutID < lenSrcThru; srcThruOutID++) {
                //  int foundConnecton = 0;
                int newSize = 0;

                for (int k = 0; k < lenIndex; k++) {
                    int thisDestId = componentConnection[srcID][srcThruOutID][k][0];
                    int thisDestThru = componentConnection[srcID][srcThruOutID][k][1];

                    if(srcID==destID){
                        if(thisDestId>-1){
                            hashSet.add(thisDestId);
                        }
                    }
                    
                }

            }
        }

        return hashSet;
    }

    public int getNumerOfConnectionInputOccupyById(int id) {
        int lenSrc = componentConnection.length;
        int lenSrcThru = componentConnection[0].length;
        int lenIndex = componentConnection[0][0].length;
        int count = 0;
        for (int srcID = 0; srcID < lenSrc; srcID++) {
            for (int srcThruOutID = 0; srcThruOutID < lenSrcThru; srcThruOutID++) {
                //  int foundConnecton = 0;
                int newSize = 0;

                for (int k = 0; k < lenIndex; k++) {
                    int thisDestId = componentConnection[srcID][srcThruOutID][k][0];
                    int thisDestThru = componentConnection[srcID][srcThruOutID][k][1];

                    if (id == thisDestId) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    public boolean removeComponentConnection(int srcID, int srcThruOutID, int destID, int destThruInID) {

        int code = destID * 1000000 + destThruInID;
        if (connectionInfoHashMap.containsKey(srcID)) {
            HashMap<Integer, ArrayList<Integer>> connectionHistory = connectionInfoHashMap.get(srcID).connectionHistory;

            if (connectionHistory.containsKey(srcThruOutID)) {
                ArrayList<Integer> al = connectionHistory.get(srcThruOutID);

                int size = al.size();
                for (int i = 0; i < size; i++) {

                    int val = al.get(i);
                    if (val == code) {

                        al.remove(i);
                        break;
                    }

                    // if(al.contains(code)){
                    //  al.remove(code);
                    // }
                }

            }
        }

        int len = componentConnection[srcID][srcThruOutID].length;

        int newSize = 0;
        for (int i = 0; i < len; i++) {
            int id = componentConnection[srcID][srcThruOutID][i][0];
            int thur = componentConnection[srcID][srcThruOutID][i][1];
            // Already contained, no need to add
            if ((id == destID) && (thur == destThruInID)) {
                componentConnection[srcID][srcThruOutID][i][0] = -1;
                componentConnection[srcID][srcThruOutID][i][1] = -1;
                id = -1;
                thur = -1;
            }
            if (id > -1) {
                tmp[newSize][0] = id;
                tmp[newSize][1] = thur;
                newSize++;
            }
        }

        for (int i = 0; i < newSize; i++) {
            componentConnection[srcID][srcThruOutID][i][0] = tmp[i][0];
            componentConnection[srcID][srcThruOutID][i][1] = tmp[i][1];
        }
        componentThuOutSize[srcID][srcThruOutID] = newSize;

        return true;
    }

    protected void printInfo() {
        for (Map.Entry<Integer, ComponentInfo> entry : componentInfoHashMap.entrySet()) {
            //int id = entry.getKey();
            ComponentInfo c = entry.getValue();
            System.out.println("id = " + c.id + " name = " + c.name + " alias = " + c.alias + " className = " + c.className + " x = " + c.col + " y = " + c.row);
        }
    }

}
