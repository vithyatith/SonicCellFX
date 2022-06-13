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
package com.sonicmsgr.soniccell.fx;

import com.sonicmsgr.soniccell.SonicCellSdk;
import javafx.collections.ObservableList;

/**
 *
 * @author yada
 */
public class SonicPanePool {

    private int MAX_POOL = 40;
    private SonicPane sonicPanePool[];
    private int currentIndex = 0;
    private SonicPane currentSonicPane;
    private ComponentDBList componentDB = new ComponentDBList();
    
    public void cleanUp(){
        currentIndex = 0;
        currentSonicPane = null;
    }

    public SonicPanePool(SonicCellSdk sdk,ComponentDBList componentDB) {
        this.componentDB = componentDB;
        init(MAX_POOL);
        resetPool(sdk);
    }

    public SonicPanePool(SonicCellSdk sdk, int size,ComponentDBList componentDB) {
        init(size);
        resetPool(sdk);
    }
    
    public SonicPane getPoolById(int id){
        for(int i=0; i<sonicPanePool.length; i++){
            int paneId = sonicPanePool[i].functionIdPane;
            if(paneId==id){
                return sonicPanePool[i];
            }
        }
        return null;
    }

    public SonicPanePool() {
        init(10);
    }

    public SonicPanePool(int size) {
        init(size);
    }

    private void init(int size) {
        MAX_POOL = size;
        sonicPanePool = new SonicPane[MAX_POOL];
    }

    protected void resetPool(SonicCellSdk sdk) {
        for (int i = 0; i < sonicPanePool.length; i++) {
            sonicPanePool[i] = new SonicPane(sdk, -1 * (i + 1),componentDB);
        }
        currentSonicPane = sonicPanePool[0];
    }

    public SonicPane getNextSonicPane() {
        if (currentIndex >= sonicPanePool.length) {
            currentIndex = 0;
        }
        currentSonicPane = sonicPanePool[currentIndex];
        currentSonicPane.clearAllData();
        currentIndex++;
        return currentSonicPane;
    }

    public void setTableDataInPool(ObservableList<PropertyTableData> tableData){
        for (int i = 0; i < sonicPanePool.length; i++) {
            sonicPanePool[i].setTableData(tableData);
        }
    }
    
    public SonicPane getCurrentSonicPane() {
        return currentSonicPane;
    }
    
    public void setCurrentSonicPane(SonicPane pane) {
        currentSonicPane = pane;
    }

}
