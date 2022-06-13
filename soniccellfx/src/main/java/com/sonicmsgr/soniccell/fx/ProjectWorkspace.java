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

import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.ComponentInfo;
import com.sonicmsgr.soniccell.SonicCellSdk;
import com.sonicmsgr.soniccell.msg.MsgListener;
import com.sonicmsgr.soniccell.msg.PubSubSingleton;
import com.sonicmsgr.soniccell.msg.ResponseListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Stack;
import javafx.collections.ObservableList;

/**
 *
 * @author yada
 */
public class ProjectWorkspace {

    private SonicPane mainPane;
    //  protected SonicPane functionPane;
    // protected SonicPane currentPane;
    protected SonicPanePool sonicPanePool;
    private SonicCellSdk sonicCellSdk;
    private Stack<Integer> paneStack = new Stack<Integer>();

    public String currentProjectFile = "untitled.cell";
    public boolean fileIsModify = false;
    private ObservableList<PropertyTableData> tableData = null;

    private ComponentDBList componentDB = new ComponentDBList();

    private void createPubSub() {
        PubSubSingleton.getIntance().addReponseListener(new ResponseListener(PubSubTopic.GET_FUNCTION_PANE) {
            @Override
            public Object reply(Object argObj) {
                return sonicPanePool.getNextSonicPane();
            }
        });

        PubSubSingleton.getIntance().addReponseListener(new ResponseListener(PubSubTopic.GET_CURRENT_PANE) {
            @Override
            public Object reply(Object argObj) {
                return sonicPanePool.getCurrentSonicPane();
            }
        });

        PubSubSingleton.getIntance().addListener(new MsgListener(PubSubTopic.ADD_PANE_HISTORY) {
            @Override
            public void onMessage(Object msg) {
                addPaneHistory((Integer) msg);
            }

        });

        PubSubSingleton.getIntance().addReponseListener(new ResponseListener(PubSubTopic.GET_PANE_HISTORY_ID) {
            @Override
            public Object reply(Object argObj) {
                return getPaneHistory();
            }
        });

    }

    protected void addPaneHistory(int paneId) {
        paneStack.push(paneId);
        System.out.println("add to history paneId is " + paneId + "  paneStack = " + paneStack.toString());
    }

    public int getPaneHistory() {
        if (paneStack.isEmpty()) {
            return -1;
        } else {
            return paneStack.pop();
        }
    }

    public int getPaneHistoryPeek() {
        if (paneStack.isEmpty()) {
            return -1;
        } else {
            return paneStack.peek();
        }
    }

    public void clearPaneHistory() {
        paneStack.clear();

    }

    protected void printStack() {
        System.out.println("Stack = " + paneStack.toString());

    }

    public SonicPane findSonicPaneById(int id) {
        return sonicPanePool.getPoolById(id);
    }

    public ProjectWorkspace(String projectName, ComponentDBList componentDB) {
        this.componentDB = componentDB;
        currentProjectFile = projectName;
        newProject(currentProjectFile);
    }

    public ProjectWorkspace(ComponentDBList componentDB) {
        this.componentDB = componentDB;
        newProject("untitled.cell");
    }

    public SonicPane getCurrentSonicPane() {
        return sonicPanePool.getCurrentSonicPane();
    }

    public void setCurrentSonicPane(SonicPane pane) {
        pane.setTableData(tableData);
        sonicPanePool.setCurrentSonicPane(pane);
        sonicPanePool.setTableDataInPool(tableData);
    }

    public SonicPane getNewSonicPane() {

        SonicPane pane = sonicPanePool.getNextSonicPane();
        pane.setTableData(tableData);

        return pane;
    }

    public void setTableDataInPool(ObservableList<PropertyTableData> _tableData) {
        tableData = _tableData;
        /*
        mainPane.setTableData(tableData);
        sonicPanePool.setTableDataInPool(tableData);
         */
    }

    public SonicPane getRootSonicPane() {
        return mainPane;
    }


    public void newProject(String fileName) {
        if (mainPane != null) {

            mainPane.getSonicCellSdk().clearAllData();
            mainPane.clearAllData();
        }
        if (sonicPanePool != null) {
            sonicPanePool.cleanUp();
        }

        sonicCellSdk = new SonicCellSdk();
        sonicPanePool = new SonicPanePool(sonicCellSdk, componentDB);
        mainPane = new SonicPane(sonicCellSdk, -1, componentDB);

        setCurrentSonicPane(mainPane);

        Path currentRelativePath = Paths.get(".");
        String currentPathString = currentRelativePath.toAbsolutePath().toString();
        File file = new File(currentPathString + File.separator + "untitled.cell");
        currentProjectFile = file.getAbsolutePath();
        saveProjectFile(currentProjectFile);
        fileIsModify = false;

        createPubSub();
    }

    public String getProjectName() {
        return currentProjectFile;
    }

    public void openProject(String projectFile) {

        loadProjectFile(projectFile);
    }

    protected SonicCellSdk getSonicCellSdk() {
        return sonicCellSdk;
    }

    public void saveProjectFile(String filename) {
        sonicCellSdk.saveProjectFile(filename);
    }

    public void loadPane(SonicPane sonicPane) {
        setCurrentSonicPane(sonicPane);
        sonicPane.clearAllData();
        ArrayList<Integer> idArray = sonicCellSdk.getComponentIdArray();
        int len = idArray.size();

        // Load the component first
        for (int i = 0; i < len; i++) {
            int srcId = idArray.get(i);
            ComponentAbstraction com = sonicCellSdk.getComponent(srcId);
            ComponentInfo info = sonicCellSdk.getComponentInfo(srcId);
            com.setEnable(info.enabled);
            // Load function is the function Id
            if (sonicPane.functionIdPane == info.functionId) {
                sonicPane.addComponent(com, info.col, info.row);
            }
        }
        // Load the connection
        len = idArray.size();
        for (int i = 0; i < len; i++) {
            int srcId = idArray.get(i);
            ComponentAbstraction com = sonicCellSdk.getComponent(srcId);

            ComponentInfo info = sonicCellSdk.getComponentInfo((Integer) srcId);
            if (info.functionId != sonicPane.functionIdPane) {
                continue;
            }

            int srcComponentOutputSize = com.getNumberOfOutputSize();
            for (int srcThruId = 0; srcThruId < srcComponentOutputSize; srcThruId++) {
                int outSizeOfConnection = sonicCellSdk.getThruOutputConnectionSize(com.getId(), srcThruId);
                int connections[][] = sonicCellSdk.getComponentConnection(srcId, srcThruId);
                for (int k = 0; k < outSizeOfConnection; k++) {

                    int destID = connections[k][0];
                    int destThruInID = connections[k][1];
                    sonicPane.addConnectComponentInOutput(false, srcId, srcThruId, destID, destThruInID);
                }
            }
        }
    }

    public void loadProjectFile(String filename) {
        sonicCellSdk.loadProjectFromJsonFile(filename);
        mainPane.clearAllData();
        loadPane(mainPane);
    }

//    public void loadProjectFileOrig(String filename) {
//        sonicCellSdk.loadProjectFromJsonFile(filename);
//
//        mainPane.clearAllData();
//
//        ArrayList<Integer> idArray = sonicCellSdk.getComponentIdArray();
//        int len = idArray.size();
//
//        // Load the component first
//        for (int i = 0; i < len; i++) {
//            int srcId = idArray.get(i);
//            ComponentAbstraction com = sonicCellSdk.getComponent(srcId);
//            ComponentInfo info = sonicCellSdk.getComponentInfo(srcId);
//            mainPane.addComponent(com, info.col, info.row);
//        }
//        // Load the connection
//        len = idArray.size();
//        for (int i = 0; i < len; i++) {
//            int srcId = idArray.get(i);
//            ComponentAbstraction com = sonicCellSdk.getComponent(srcId);
//            int srcComponentOutputSize = com.getNumberOfOutputSize();
//            // System.out.println("srcComponentOutputSize = " + srcComponentOutputSize);
//            for (int srcThruId = 0; srcThruId < srcComponentOutputSize; srcThruId++) {
//                int outSizeOfConnection = sonicCellSdk.getThruOutputConnectionSize(com.getId(), srcThruId);
//                int connections[][] = sonicCellSdk.getComponentConnection(srcId, srcThruId);
//                for (int k = 0; k < outSizeOfConnection; k++) {
//
//                    int destID = connections[k][0];
//                    int destThruInID = connections[k][1];
//                    mainPane.addConnectComponentInOutput(false, srcId, srcThruId, destID, destThruInID);
//                }
//            }
//        }
//    }
}
