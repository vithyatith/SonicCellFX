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
import com.sonicmsgr.soniccell.FunctionComponent;
import com.sonicmsgr.soniccell.FunctionInputComponent;
import com.sonicmsgr.soniccell.FunctionOutputComponent;
import com.sonicmsgr.soniccell.Log;
import com.sonicmsgr.soniccell.SonicCellSdk;
import com.sonicmsgr.soniccell.ThroughputReferenceInfo;
import com.fx.PrintGuiFX;
import com.fx.TextConsoleFX;
import com.sonicmsgr.soniccell.msg.PubSubSingleton;
import com.sonicmsgr.soniccell.util.ConnectionUtil;
import component.basic.Print;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author yada
 */
public class SonicPane extends Pane {

    private ContextMenu popupRectListRoutine = new ContextMenu();
    private MenuItem itemEdit = new MenuItem("Edit");
    private MenuItem itemDel = new MenuItem("Delete");
    private MenuItem itemEnable = new MenuItem("Enable");
    private MenuItem itemDisable = new MenuItem("Disable");
    private MenuItem itemHelp = new MenuItem("Help");
    private MenuItem itemStart = new MenuItem("Start");
    private MenuItem itemStop = new MenuItem("Stop");
    private MenuItem itemForceStop = new MenuItem("ForceStop");

    private int selectedId = -1;

    private long selectedLineKey = -1;

    // Line info
    private ContextMenu popupLineInfo = new ContextMenu();
    private MenuItem itemDelLineInfo = new MenuItem("Delete Line");

    private ContextMenu popupSelectRect = new ContextMenu();
    private MenuItem itemCopyAllRect = new MenuItem("Copy");
    private MenuItem itemPasteAllRect = new MenuItem("Paste");
    private MenuItem itemCreateFunction = new MenuItem("Create Function");
    private MenuItem itemExecute = new MenuItem("Execute");
    private MenuItem itemDelAllRect = new MenuItem("Delete");

    private ContextMenu popupOutputThru = new ContextMenu();
    private ContextMenu popupInputThru = new ContextMenu();

    private ConcurrentHashMap<Integer, ComponentFX> componeFXHashMap = new ConcurrentHashMap<Integer, ComponentFX>();
    private ConcurrentHashMap<Long, Line> connectionLineHashMap = new ConcurrentHashMap<Long, Line>();
    private ArrayList<Integer> selectedRectHighlightedList = new ArrayList<Integer>();
    public int functionIdPane = -1;

    private Line previousSelectedLine = null;

    private int selectedCol = 0;
    private int selectedRow = 0;

    private Line tmpSelectedTmpLine = null;

    private boolean outputCircleIsPress = false;
    private boolean inputputCircleIsPress = false;

    private IOShape selectedOutputSonicCircle = null;
    private IOShape selectedInputSonicCircle = null;

    private boolean dragHighlightBool = false;
    private Rectangle rectangleSelectCompon = null;

    private double rectSelectXStart = 0;
    private double rectSelectYStart = 0;
    private int selectedColHightlgihtStart = 0;
    private int selectedRowHightlgihtStart = 0;

    private boolean copyBool = false;

    private ObservableList<PropertyTableData> tableData;
    private HashMap<Integer, Integer> copyIdMappingHM = new HashMap<Integer, Integer>();
    private ComponentDBList componentDB = new ComponentDBList();

    private void buildPopupOutputMenu() {

        popupOutputThru.setStyle("-fx-font-size: 12;");
        String shortNameBuff[] = {"Print", "PrintFX"};
        String classnameBuff[] = {Print.class.getCanonicalName(),
            PrintGuiFX.class.getCanonicalName()
        };
        int len = shortNameBuff.length;
        for (int i = 0; i < len; i++) {
            String classname = classnameBuff[i];
            MenuItem menuItem = new MenuItem(shortNameBuff[i]);
            menuItem.setOnAction(menuItemFreeHandler);
            menuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    //System.out.println(selectedId);
                    // ComponentFX componentFX = componeFXHashMap.get(selectedId);
                    int startCol = selectedCol + 1;
                    int startRow = selectedRow - 1;
                    int index = 0;
                    do {

                        if (index > 8) {
                            startCol++;
                            startRow = selectedRow - 1;
                            index = 0;
                        }

                        startRow++;
                        index++;

                    } while (sonicCellSdk.containColRow(functionIdPane, startCol, startRow));

                    int thisPrintComponentId = addComponentByClassName(classname, startCol, startRow);

                    int thisSelectedId = selectedOutputSonicCircle.srcId;
                    int thisThruId = selectedOutputSonicCircle.thruId;

                    addConnectComponentInOutput(thisSelectedId, thisThruId, thisPrintComponentId, 0);

                }
            });

            popupOutputThru.getItems().add(menuItem);
            if (i == 1) {
                SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
                separatorMenuItem.setStyle("-fx-background-color: white;");
                popupOutputThru.getItems().add(separatorMenuItem);

            }
        }
    }

    private void buildPopupInputMenu() {
        popupInputThru.setStyle("-fx-font-size: 12;");
        String shortNameBuff[] = {"TextConsole"};
        String classnameBuff[] = {TextConsoleFX.class.getCanonicalName()
        };
        int len = shortNameBuff.length;
        for (int i = 0; i < len; i++) {
            String classname = classnameBuff[i];
            MenuItem menuItem = new MenuItem(shortNameBuff[i]);
            menuItem.setOnAction(menuItemFreeHandler);
            menuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    //System.out.println(selectedId);
                    // ComponentFX componentFX = componeFXHashMap.get(selectedId);
//                    int startCol = selectedCol + 1;
//                    int startRow = selectedRow - 1;
//                    int index = 0;
//                    do {
//
//                        if (index > 8) {
//                            startCol++;
//                            startRow = selectedRow - 1;
//                            index = 0;
//                        }
//
//                        startRow++;
//                        index++;
//
//                    } while (sonicCellSdk.containColRow(functionIdPane, startCol, startRow));
//
//                    int thisPrintComponentId = addComponentByClassName(classname, startCol, startRow);
//
//                    int thisSelectedId = selectedOutputSonicCircle.srcId;
//                    int thisThruId = selectedOutputSonicCircle.thruId;
//
//                    addConnectComponentInOutput(thisSelectedId, thisThruId, thisPrintComponentId, 0);
                    ////////////
                    if (selectedInputSonicCircle.isInputOccupy) {
                        return;
                    }
                    //System.out.println(selectedId);
                    // ComponentFX componentFX = componeFXHashMap.get(selectedId);
                    int startCol = selectedCol - 1;
                    int startRow = selectedRow - 1;
                    int index = 0;
                    do {

                        if (index > 8) {
                            startCol--;
                            startRow = selectedRow - 1;
                            index = 0;
                        }

                        startRow++;
                        index++;

                    } while (sonicCellSdk.containColRow(functionIdPane, startCol, startRow));

                    int thisSelectedId = selectedInputSonicCircle.srcId;
                    int thisThruId = selectedInputSonicCircle.thruId;

                    boolean b = sonicCellSdk.checkIfIOConnectionTypeIsMatched("string", thisSelectedId, thisThruId);
                    if (b) {
                        //  String classname = TextConsoleFX.class.getCanonicalName();
                        int thisPrintComponentId = addComponentByClassName(classname, startCol, startRow);

                        addConnectComponentInOutput(thisPrintComponentId, 0, thisSelectedId, thisThruId);
                    }
                    //////////
                }
            });

            popupInputThru.getItems().add(menuItem);
            if (i == 1) {
                //  SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
                // separatorMenuItem.setStyle("-fx-background-color: white;");
                //  popupInputThru.getItems().add(separatorMenuItem);

            }
        }
    }

    // Make it selected or not
    public boolean updatedSelectedComponentProperty(String name, String value) {
        if (selectedId > -1) {

            if (name.equals("Id")) {
                return false;
            }
            if (name.equals("Name")) {
                sonicCellSdk.setName(selectedId, value);
                ComponentFX comFx = componeFXHashMap.get((Integer) selectedId);
                comFx.setText(value);
                return true;
            }
            if (name.equals("Alias")) {
                boolean b = sonicCellSdk.setAlias(selectedId, value);
                if (b == false) {
                    PubSubSingleton.getIntance().send(PubSubTopic.LOG, "'" + value + "'is not unique. Alias must have unique name");
                }
                return b;
            }

            if (name.equals("Name")) {
                sonicCellSdk.setName(selectedId, value);
                ComponentFX comFx = componeFXHashMap.get((Integer) selectedId);
                comFx.setText(value);
                return true;
            }

            if (name.equalsIgnoreCase("operator")) {
                sonicCellSdk.setName(selectedId, value);
                ComponentFX comFx = componeFXHashMap.get((Integer) selectedId);
                comFx.setText(value);
                sonicCellSdk.setProperty(selectedId, name, value);
                return true;
            }

            ComponentInfo info = sonicCellSdk.getComponentInfo(selectedId);

            // boolean isInOutputBeingEditBool = false;
            if (info.isFunction) {
                FunctionComponent comFunc = (FunctionComponent) sonicCellSdk.getComponent(selectedId);

                int inSize = comFunc.getNumberOfInputSize();
                int outSize = comFunc.getNumberOfInputSize();
                boolean sizeIsChangedBool = false;

                if (name.equals("# of Input")) {
                    inSize = Integer.parseInt(value);
                    sizeIsChangedBool = true;
                }

                if (name.equals("# of Output")) {
                    outSize = Integer.parseInt(value);
                    sizeIsChangedBool = true;
                }

                if (sizeIsChangedBool) {

                    try {
                        removeAllLineConnectionForId(comFunc.getId());
                        sonicCellSdk.removeAllFuncionIOComponentById(comFunc.getId());
                        sonicCellSdk.setNumOfInOutputToFunction(comFunc, inSize, outSize);
                        ComponentFX comFx = componeFXHashMap.get((Integer) selectedId);
                        comFx.redrawComponentFX();
                        comFx.addRectangleMouseDraggedEvent(rectangleMouseDragHandler);
                        comFx.addRectangleMousePressEvent(rectangleMousePressedHandler);
                        comFx.addCircleMousePressEvent(circleMousePressedHandler);
                        comFx.addCircleMouseEnterEvent(circleMouseEnterHandler);
                        comFx.addCircleMouseExitEvent(circleMouseExitHandler);

                        // Input
                        for (int i = 0; i < inSize; i++) {
                            FunctionInputComponent funcIn = new FunctionInputComponent();
                            funcIn.setName(i + "");
                            sonicCellSdk.addComponentToFunction(funcIn, 0, i, comFunc);
                            sonicCellSdk.addThroughputReference(0, comFunc, i, funcIn, i);
                            
                        }

                        // output
                        for (int i = 0; i < outSize; i++) {
                            FunctionOutputComponent funcOut = new FunctionOutputComponent();
                            funcOut.setName(i + "");
                            sonicCellSdk.addComponentToFunction(funcOut, 1, i, comFunc);
                            sonicCellSdk.addThroughputReference(1, comFunc, i, funcOut, i);
                        }

                    } catch (NumberFormatException ex) {
                        PubSubSingleton.getIntance().send(PubSubTopic.LOG, "'" + value + "' must be an integer");
                    }
                }
                // End number of function
            } else {

//                ComponentAbstraction comAbs = (ComponentAbstraction) sonicCellSdk.getComponent(selectedId);
//
//                if ((comAbs instanceof TypeCastConverterComponent)||(comAbs instanceof TypeArrayConverterComponent)) {
//
//                    if (name.equals("outputType")) {
//                        ComponentFX comFx = componeFXHashMap.get((Integer) selectedId);
//                        comFx.redrawComponentFX();
//                    }
//
//                } 
                if (name.equals("outputType")) {

                    sonicCellSdk.setOutputType(selectedId, 0, value);
                    ComponentFX comFx = componeFXHashMap.get((Integer) selectedId);
                    comFx.redrawComponentFX();
                } else if (name.equals("inputType")) {

                    sonicCellSdk.setInputType(selectedId, 0, value);
                    ComponentFX comFx = componeFXHashMap.get((Integer) selectedId);
                    comFx.redrawComponentFX();
                } else {

                    sonicCellSdk.setProperty(selectedId, name, value);
                    sonicCellSdk.getComponent((Integer) selectedId).onPropertyChanged(name, value);

                    ComponentFX comFx = componeFXHashMap.get((Integer) selectedId);
                    comFx.redrawComponentFX();

                }
            }

            return true;
        }
        return false;
    }

    public void refreshComponentDraw() {
        ComponentFX comFX = componeFXHashMap.get((Integer) selectedId);
        if (comFX != null) {
            comFX.redrawComponentFX();
        }
    }

    protected void setTableData(ObservableList<PropertyTableData> tableData) {
        this.tableData = tableData;
    }

    private SonicCellSdk sonicCellSdk;

    public void setSonicCellSdk(SonicCellSdk sonicCellSdk) {
        this.sonicCellSdk = sonicCellSdk;
    }

    public SonicPane(SonicCellSdk sdk, int funciontId, ComponentDBList componentDB) {
        super();
        this.componentDB = componentDB;
        this.functionIdPane = funciontId;

        // System.out.println("function id = "+funciontId);
        init(sdk);
    }

//    private void initFromFunction(SonicCellSdk sdk, int funciontId, ComponentDBList componentDB) {
//
//        this.componentDB = componentDB;
//        this.functionIdPane = funciontId;
//
//        // System.out.println("function id = "+funciontId);
//        init(sdk,true);
//    }
    private final EventHandler<ActionEvent> menuItemFreeHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

            // if ((inputputCircleIsPress == false) && (inputputCircleIsPress == false)) {
            MenuItem mItem = (MenuItem) event.getSource();
            String text = mItem.getText();

            String categoryName = mItem.getParentMenu().getText();
            String className = componentDB.getClassName(categoryName, text);

            addComponentByClassNameUsingXY(className, selectedMenuX, selectedMenuY);
            //  }

        }

    };

    private int selectedMenuX = 0;
    private int selectedMenuY = 0;

    private ContextMenu contextComponentMenu = new ContextMenu();

    public void stopManualComponent(int componentId) {
        startStopSingleProcess(false);

        ComponentFX comFx = componeFXHashMap.get((Integer) componentId);
        comFx.setStopColor();
        getSonicCellSdk().stopsManual(componentId);
    }

//    private void addRightClickComponentListListener(Map<String, ArrayList<ComponentItem>> componentDBHM) {
//        //    Map<String, ArrayList<ComponentItem>> componentDBHM = componentDB.getHashMap();
//
//        // TreeItem<String> rootItem = new TreeItem<String>("Component");
//        // rootItem.setExpanded(true);
//        Iterator it = componentDBHM.entrySet().iterator();
//
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry) it.next();
//            String categoryName = (String) pair.getKey();
//            //  TreeItem<String> branchItem = new TreeItem<String>(categoryName);
//            ArrayList<ComponentItem> componentItemAL = (ArrayList<ComponentItem>) pair.getValue();
//
//            Menu categoryNameMenu = new Menu(categoryName);
//            int size = componentItemAL.size();
//            for (int i = 0; i < size; i++) {
//                MenuItem menuItem = new MenuItem(componentItemAL.get(i).shortName);
//
//                menuItem.setOnAction(menuItemFreeHandler);
//                categoryNameMenu.getItems().add(menuItem);
//            }
//            contextComponentMenu.getItems().add(categoryNameMenu);
//        }
//    }
    
    private void init(SonicCellSdk sdk) {
        init(sdk,false);
    }

    private void init(SonicCellSdk sdk, boolean fromFunction) {

        ///////
        contextComponentMenu.setStyle("-fx-font-size: 12;");
//        Menu parentMenu = new Menu("Parent");
//        MenuItem childMenuItem1 = new MenuItem("Child 1");
//        MenuItem childMenuItem2 = new MenuItem("Child 2");
//        childMenuItem1.setOnAction(menuItemFreeHandler);
//        childMenuItem2.setOnAction(menuItemFreeHandler);
//        parentMenu.getItems().add(childMenuItem1);
//        parentMenu.getItems().add(childMenuItem2);
//        contextMenu.getItems().add(parentMenu);

        Map<String, ArrayList<ComponentItem>> componentDBHM = componentDB.getHashMap();

        // TreeItem<String> rootItem = new TreeItem<String>("Component");
        // rootItem.setExpanded(true);
        Iterator it = componentDBHM.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String categoryName = (String) pair.getKey();
            //  TreeItem<String> branchItem = new TreeItem<String>(categoryName);
            ArrayList<ComponentItem> componentItemAL = (ArrayList<ComponentItem>) pair.getValue();

            Menu categoryNameMenu = new Menu(categoryName);
            int size = componentItemAL.size();
            for (int i = 0; i < size; i++) {
                MenuItem menuItem = new MenuItem(componentItemAL.get(i).shortName);

                menuItem.setOnAction(menuItemFreeHandler);
                categoryNameMenu.getItems().add(menuItem);
            }
            contextComponentMenu.getItems().add(categoryNameMenu);
        }
        if(fromFunction){
            return;
        }

        /////
        this.sonicCellSdk = sdk;

        this.setStyle("-fx-background-color: gray;");
        ///////////
        tmpSelectedTmpLine = new Line();
        tmpSelectedTmpLine.setStrokeWidth(SonicCellProperties.LINE_WIDTH_FOR_CONNECTION);
        tmpSelectedTmpLine.setStroke(Color.BLACK);
        this.getChildren().add(tmpSelectedTmpLine);

        rectangleSelectCompon = new Rectangle();
        rectangleSelectCompon.setStrokeWidth(SonicCellProperties.LINE_WIDTH_FOR_CONNECTION);
        rectangleSelectCompon.setStroke(Color.BLACK);
        rectangleSelectCompon.setFill(Color.TRANSPARENT);
        this.getChildren().add(rectangleSelectCompon);
        //////////

        itemStop.setDisable(true);
        itemEnable.setDisable(true);

        popupRectListRoutine.getItems().add(itemEdit);
        popupRectListRoutine.getItems().add(itemDel);
        popupRectListRoutine.getItems().add(itemEnable);
        popupRectListRoutine.getItems().add(itemDisable);
        popupRectListRoutine.getItems().add(itemHelp);
        popupRectListRoutine.getItems().add(itemStart);
        popupRectListRoutine.getItems().add(itemStop);
        popupRectListRoutine.getItems().add(itemForceStop);

        EventHandler<ActionEvent> itemActionListener = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                MenuItem item = (MenuItem) event.getSource();
                if (item.getText().equalsIgnoreCase("Delete")) {
                    if (selectedId > -1) {
                        removeComponent(selectedId);
                    }
                } else if (item.getText().equalsIgnoreCase("Enable")) {
                    if (selectedId > -1) {

                        updateSelectPopupMenu(true, true);
                    }
                } else if (item.getText().equalsIgnoreCase("Disable")) {
                    if (selectedId > -1) {
                        updateSelectPopupMenu(false, true);
                    }
                } else if (item.getText().equalsIgnoreCase("Help")) {
                    if (selectedId > -1) {

                        ComponentAbstraction com = sonicCellSdk.getComponent(selectedId);

                        Alert alert = new Alert(AlertType.NONE, com.getHelp(), ButtonType.OK);
                        alert.setTitle(com.getName() + "-Help");

                        alert.showAndWait();

                        if (alert.getResult() == ButtonType.OK) {
                        }

                    }
                } else if (item.getText().equalsIgnoreCase("Start")) {
                    if (selectedId > -1) {

                        stopManualComponent(selectedId);
                        startStopSingleProcess(true);

                        ComponentFX comFx = componeFXHashMap.get((Integer) selectedId);
                        comFx.setStartedColor();
                        //getSonicCellSdk().getComponentInfo(selectedId);
                        getSonicCellSdk().start(selectedId);

                        //setDisable(true);
//
//                        ComponentAbstraction com = sonicCellSdk.getComponent(selectedId);
//
//                        Alert alert = new Alert(AlertType.NONE, com.getHelp(), ButtonType.OK);
//                        alert.setTitle(com.getName() + "-Help");
//
//                        alert.showAndWait();
//
//                        if (alert.getResult() == ButtonType.OK) {
//                        }
                    }
                } else if (item.getText().equalsIgnoreCase("Stop")) {
                    if (selectedId > -1) {

                        stopManualComponent(selectedId);
                    }
                } else if (item.getText().equalsIgnoreCase("ForceStop")) {
                    if (selectedId > -1) {

                        stopManualComponent(selectedId);
                        startStopSingleProcess(false);
                        getSonicCellSdk().stopsManual(selectedId);
                    }
                }

            }

        };
        itemDel.setOnAction(itemActionListener);
        itemStart.setOnAction(itemActionListener);
        itemStop.setOnAction(itemActionListener);
        itemEnable.setOnAction(itemActionListener);
        itemDisable.setOnAction(itemActionListener);
        itemHelp.setOnAction(itemActionListener);
        itemForceStop.setOnAction(itemActionListener);
        this.buildPopupOutputMenu();
        this.buildPopupInputMenu();

//        popupOutputThru.getItems().add(this.itemPrint);
//        popupOutputThru.getItems().add(this.itemPrintFX);
//        itemPrint.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//
//                //System.out.println(selectedId);
//                // ComponentFX componentFX = componeFXHashMap.get(selectedId);
//                int startCol = selectedCol + 1;
//                int startRow = selectedRow - 1;
//                int index = 0;
//                do {
//
//                    if (index > 8) {
//                        startCol++;
//                        startRow = selectedRow - 1;
//                        index = 0;
//                    }
//
//                    startRow++;
//                    index++;
//
//                } while (sonicCellSdk.containColRow(functionIdPane, startCol, startRow));
//
//                int thisPrintComponentId = addComponentByClassName("component.basic.Print", startCol, startRow);
//
//                int thisSelectedId = selectedOutputSonicCircle.srcId;
//                int thisThruId = selectedOutputSonicCircle.thruId;
//
//                addConnectComponentInOutput(thisSelectedId, thisThruId, thisPrintComponentId, 0);
//
//            }
//        });
//        itemPrintFX.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//
//                //System.out.println(selectedId);
//                // ComponentFX componentFX = componeFXHashMap.get(selectedId);
//                int startCol = selectedCol + 1;
//                int startRow = selectedRow - 1;
//                int index = 0;
//                do {
//
//                    if (index > 8) {
//                        startCol++;
//                        startRow = selectedRow - 1;
//                        index = 0;
//                    }
//
//                    startRow++;
//                    index++;
//
//                } while (sonicCellSdk.containColRow(functionIdPane, startCol, startRow));
//                String classname = PrintGuiFX.class.getCanonicalName();
//                int thisPrintComponentId = addComponentByClassName(classname, startCol, startRow);
//
//                int thisSelectedId = selectedOutputSonicCircle.srcId;
//                int thisThruId = selectedOutputSonicCircle.thruId;
//
//                addConnectComponentInOutput(thisSelectedId, thisThruId, thisPrintComponentId, 0);
//
//            }
//        });
        /////////
//        popupInputThru.getItems().add(this.itemTextConsole);
//        itemTextConsole.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//
//                if (selectedInputSonicCircle.isInputOccupy) {
//                    return;
//                }
//                //System.out.println(selectedId);
//                // ComponentFX componentFX = componeFXHashMap.get(selectedId);
//                int startCol = selectedCol - 1;
//                int startRow = selectedRow - 1;
//                int index = 0;
//                do {
//
//                    if (index > 8) {
//                        startCol--;
//                        startRow = selectedRow - 1;
//                        index = 0;
//                    }
//
//                    startRow++;
//                    index++;
//
//                } while (sonicCellSdk.containColRow(functionIdPane, startCol, startRow));
//
//                int thisSelectedId = selectedInputSonicCircle.srcId;
//                int thisThruId = selectedInputSonicCircle.thruId;
//
//                boolean b = sonicCellSdk.checkIfIOConnectionTypeIsMatched("string", thisSelectedId, thisThruId);
//                if (b) {
//                    String classname = TextConsoleFX.class.getCanonicalName();
//                    int thisPrintComponentId = addComponentByClassName(classname, startCol, startRow);
//
//                    addConnectComponentInOutput(thisPrintComponentId, 0, thisSelectedId, thisThruId);
//                }
//
//            }
//        });
        ///////
        try {
            popupLineInfo.getItems().add(itemDelLineInfo);
        } catch (java.lang.UnsupportedOperationException ex) {

            System.out.println("SonicPane737: error "+ex.getMessage());
        }

        itemDelLineInfo.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                if (selectedLineKey > -1) {
                    int keys[] = ConnectionUtil.keyDecode(selectedLineKey);
                    int srcId = keys[0];
                    int srcThruId = keys[1];
                    int destId = keys[2];
                    int destThruId = keys[3];

                    // Delete the relationship on the SDK
                    boolean b = sonicCellSdk.removeConnectComponentInOutputById(srcId, srcThruId, destId, destThruId);

                    // delete
                    if (b) {
                        componeFXHashMap.get(destId).getCircleInput(destThruId).isInputOccupy = false;
                    }

                    Line lin = connectionLineHashMap.get(selectedLineKey);

                    SonicPane.this.getChildren().remove(lin);
                    connectionLineHashMap.remove(selectedLineKey);

                    selectedLineKey = -1;
                }

            }
        });

        popupSelectRect.getItems().add(itemCopyAllRect);
        itemCopyAllRect.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                if (selectedRectHighlightedList.size() > 0) {
                    for (int i = 0; i < selectedRectHighlightedList.size(); i++) {
                        int id = selectedRectHighlightedList.get(i);

                    }
                    copyBool = true;
                    itemPasteAllRect.setDisable(false);
                    itemCopyAllRect.setDisable(true);
                    itemDelAllRect.setDisable(false);

                    changeSelectedRectToUnselected();
                }
            }
        });
        itemPasteAllRect.setDisable(true);
        popupSelectRect.getItems().add(itemPasteAllRect);
        itemPasteAllRect.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                if (selectedRectHighlightedList.size() > 0) {

                    int minCol = Integer.MAX_VALUE;

                    int minRow = Integer.MAX_VALUE;

                    // Find the min
                    for (int i = 0; i < selectedRectHighlightedList.size(); i++) {
                        Integer id = selectedRectHighlightedList.get(i);
                        ComponentInfo info = sonicCellSdk.getComponentInfo(id);
                        if (info.col < minCol) {
                            minCol = info.col;
                        }

                        if (info.row < minRow) {
                            minRow = info.row;
                        }

                    }

                    // Check to see component placement is available
                    for (int i = 0; i < selectedRectHighlightedList.size(); i++) {
                        Integer id = selectedRectHighlightedList.get(i);
                        ComponentInfo info = sonicCellSdk.getComponentInfo(id);

                        int newCol = selectedCol + info.col - minCol;
                        int newRow = info.row + selectedRow - minRow;
                        if (sonicCellSdk.containColRow(functionIdPane, newCol, newRow)) {
                            Log.v("SonicCell", "Can not copy component, cell overlap.");
                            PubSubSingleton.getIntance().send("Print", "Can not copy component, cell overlap.");
                            return;
                        }
                    }

                    // Create the copy component
                    for (int i = 0; i < selectedRectHighlightedList.size(); i++) {

                        // Get the orginal
                        Integer id = selectedRectHighlightedList.get(i);
                        ComponentInfo info = sonicCellSdk.getComponentInfo(id);

                        // Create the copy
                        int newId = addComponentByClassName(info.className, selectedCol + info.col - minCol, info.row + selectedRow - minRow);
                        ComponentInfo infoNew = sonicCellSdk.getComponentInfo(newId);
                        sonicCellSdk.cloneComponentInfo(info, infoNew);

                        // Update the ComponentFX
                        ComponentFX comFx = componeFXHashMap.get((Integer) newId);
                        comFx.refreshComponentInfo();

                        copyIdMappingHM.put((Integer) id, (Integer) newId);
                    }

                    // Copy the thru-put
                    ///////////////////
//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
                    Iterator it = connectionLineHashMap.entrySet().iterator();

                    int size = connectionLineHashMap.size();

                    Iterator<Long> its2 = connectionLineHashMap.keySet().iterator();
                    while (its2.hasNext()) {
                        long key = its2.next();
                        int keys[] = ConnectionUtil.keyDecode(key);
                        int srcId = keys[0];
                        int srcThruId = keys[1];
                        int destId = keys[2];
                        int destThruId = keys[3];

                        if (selectedRectHighlightedList.contains((Integer) srcId)) {
                            if (selectedRectHighlightedList.contains((Integer) destId)) {
                                Integer newSrcId = copyIdMappingHM.get((Integer) srcId);
                                Integer newDesId = copyIdMappingHM.get((Integer) destId);
                                addConnectComponentInOutput(newSrcId, srcThruId, newDesId, destThruId);
                            }
                        }

                    }

                    copyBool = false;
                    selectedRectHighlightedList.clear();

                    itemPasteAllRect.setDisable(true);
                    itemCopyAllRect.setDisable(false);
                    itemDelAllRect.setDisable(false);

                }
            }
        });

        popupSelectRect.getItems().add(itemCreateFunction);
        popupSelectRect.getItems().add(itemExecute);

        itemExecute.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (selectedRectHighlightedList.size() > 0) {
                    startStopSingleProcess(true);
                    int minCol = Integer.MAX_VALUE;

                    int minRow = Integer.MAX_VALUE;

                    int maxCol = Integer.MIN_VALUE;

                    // Find the col and min row
                    for (int i = 0; i < selectedRectHighlightedList.size(); i++) {
                        Integer id = selectedRectHighlightedList.get(i);
                        ComponentInfo info = sonicCellSdk.getComponentInfo(id);

                        if (info.col < minCol) {
                            minCol = info.col;
                        }

                        if (info.col > maxCol) {
                            maxCol = info.col;
                        }

                        if (info.row < minRow) {
                            minRow = info.row;
                        }

                        //  ComponentFX comFx = componeFXHashMap.get((Integer) id);
                        //  comFx.setStartedColor();
                        getSonicCellSdk().getComponentInfo(id);
                        getSonicCellSdk().start(id);

                    }
                }
            }

        });

        EventHandler<ActionEvent> executeAndCreateFunctionEvent = new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                //  SonicPane sonicPane = new SonicPane(sonicCellSdk);
                //  PubSubSingleton.getIntance().send(PubSubTopic.SET_PANE
                FunctionComponent func = sonicCellSdk.createFunctionComponent(selectedCol, selectedRow);
                int thisFunctionId = func.getId();

                SonicPane sonicPane = (SonicPane) PubSubSingleton.getIntance().sendReply(PubSubTopic.GET_FUNCTION_PANE, null);
               //  SonicPane sonicPane = new SonicPane(sonicCellSdk, -1,componentDB);
               //  sonicPane.addRightClickComponentListListener(componentDB.getHashMap());
               
               // sonicPane.initFromFunction(sonicCellSdk, thisFunctionId, componentDB);

                sonicPane.clearAllData();
                 
                sonicPane.functionIdPane = thisFunctionId;
                PubSubSingleton.getIntance().send(PubSubTopic.ADD_PANE_HISTORY, thisFunctionId);

                sonicPane.setTableData(tableData);
                int numOfInput = 0;
                int numOfOutput = 0;

                //////////////////////////
                if (selectedRectHighlightedList.size() > 0) {

                    int minCol = Integer.MAX_VALUE;

                    int minRow = Integer.MAX_VALUE;

                    int maxCol = Integer.MIN_VALUE;

                    // Find the col and min row
                    for (int i = 0; i < selectedRectHighlightedList.size(); i++) {
                        Integer id = selectedRectHighlightedList.get(i);
                        ComponentInfo info = sonicCellSdk.getComponentInfo(id);
                        if (info.col < minCol) {
                            minCol = info.col;
                        }

                        if (info.col > maxCol) {
                            maxCol = info.col;
                        }

                        if (info.row < minRow) {
                            minRow = info.row;
                        }

                    }

                    // Get the total number of input and output
                    // Create and copy component
                    for (int i = 0; i < selectedRectHighlightedList.size(); i++) {
                        // Get the orginal
                        Integer id = selectedRectHighlightedList.get(i);
                        ComponentInfo infoOrig = sonicCellSdk.getComponentInfo(id);
                        ComponentAbstraction newComponent = sonicCellSdk.getComponent(id);

                        if (newComponent instanceof FunctionInputComponent) {
                            numOfInput++;
                        } else if (newComponent instanceof FunctionOutputComponent) {
                            numOfOutput++;
                        }

//                        if (infoOrig.col == minCol) {
//                            numOfInput = numOfInput + newComponent.getNumberOfInputSize();
//                        } else if (infoOrig.col == maxCol) {
//                            numOfOutput = numOfOutput + newComponent.getNumberOfOutputSize();
//                        }
                    }

                    // Set the number of Input
                    //sonicCellSdk.setNumOfInOutputToFunction(func.getId(), numOfInput, numOfOutput);
                    sonicCellSdk.setNumOfInOutputToFunction(func.getId(), numOfInput, numOfOutput);
                    int inputIndex = 0;
                    int outputIndex = 0;

                    // Create and copy component
                    for (int i = 0; i < selectedRectHighlightedList.size(); i++) {

                        // Get the orginal
                        Integer id = selectedRectHighlightedList.get(i);

                        ComponentInfo infoOrig = sonicCellSdk.getComponentInfo(id);
                        // Create the copy
                        int newId = sonicPane.addComponentByClassName(infoOrig.className, selectedCol + infoOrig.col - minCol, infoOrig.row + selectedRow - minRow);
                        ComponentInfo infoNew = sonicCellSdk.getComponentInfo(newId);
                        ComponentAbstraction newComponent = sonicCellSdk.getComponent(newId);
                        infoNew.functionId = thisFunctionId;
                        sonicCellSdk.cloneComponentInfo(infoOrig, infoNew);

                        // Update the ComponentFX
                        ComponentFX comFx = sonicPane.componeFXHashMap.get((Integer) newId);
                        comFx.refreshComponentInfo();

                        // VT TMP
                        // Don't add the reference
                        if (false) {
                            if (infoOrig.col == minCol) {
                                int len = newComponent.getNumberOfInputSize();
                                for (int k = 0; k < len; k++) {
                                    sonicCellSdk.addThroughputReference(ThroughputReferenceInfo.IO_TYPE_INPUT, func, inputIndex, newComponent, k);
                                    inputIndex++;
                                }
                            } else if (infoOrig.col == maxCol) {
                                int len = newComponent.getNumberOfOutputSize();
                                for (int k = 0; k < len; k++) {
                                    sonicCellSdk.addThroughputReference(ThroughputReferenceInfo.IO_TYPE_OUTPUT, func, outputIndex, newComponent, k);
                                    outputIndex++;
                                }
                            }
                        }

                        //////////////
                        // sonicCellSdk.addThroughputReference(0, func, 0, float1, 0);
                        /////////////
                        copyIdMappingHM.put((Integer) id, (Integer) newId);

                    }

                    // Create the reference
                    // Copy the thru-put
                    ///////////////////
//           
                    Iterator it = connectionLineHashMap.entrySet().iterator();

                    int size = connectionLineHashMap.size();

                    Iterator<Long> its2 = connectionLineHashMap.keySet().iterator();
                    while (its2.hasNext()) {
                        long key = its2.next();
                        int keys[] = ConnectionUtil.keyDecode(key);
                        int srcId = keys[0];
                        int srcThruId = keys[1];
                        int destId = keys[2];
                        int destThruId = keys[3];

                        if (selectedRectHighlightedList.contains((Integer) srcId)) {
                            if (selectedRectHighlightedList.contains((Integer) destId)) {
                                Integer newSrcId = copyIdMappingHM.get((Integer) srcId);
                                Integer newDesId = copyIdMappingHM.get((Integer) destId);
                                sonicPane.addConnectComponentInOutput(newSrcId, srcThruId, newDesId, destThruId);
                            }
                        }

                    }

                    ///// delete the component ///
                    for (int i = 0; i < selectedRectHighlightedList.size(); i++) {
                        int id = selectedRectHighlightedList.get(i);
                        removeComponent(id);
                    }
                    selectedRectHighlightedList.clear();

                    ///////////////////
                    copyBool = false;
                    selectedRectHighlightedList.clear();

                    itemPasteAllRect.setDisable(true);
                    itemCopyAllRect.setDisable(false);
                    itemDelAllRect.setDisable(false);

                }

                addComponent(func, selectedCol, selectedRow);

                Iterator it = copyIdMappingHM.entrySet().iterator();

                for (Map.Entry<Integer, Integer> entry : copyIdMappingHM.entrySet()) {

                    int srcIdOld = entry.getKey();
                    int desIdNew = entry.getValue();
                    sonicCellSdk.updateThroughputReference(srcIdOld, desIdNew);
                }

                //////////////////////////
                PubSubSingleton.getIntance().send(PubSubTopic.SET_PANE, sonicPane);

                /*
                final Stage dialog = new Stage();
                dialog.initModality(Modality.APPLICATION_MODAL);
               // FunctionComponent func = sonicCellSdk.createFunctionComponent(23, 3);
               // int id = func.getId();
                ScrollPane scrollPane = new ScrollPane();
                SonicPane sonicPane = new SonicPane(sonicCellSdk,id);
                scrollPane.setContent(sonicPane);
                Scene dialogScene = new Scene(scrollPane, 512, 512);
                dialog.setScene(dialogScene);
                dialog.show();
                 */
            }
        };

        itemCreateFunction.setOnAction(executeAndCreateFunctionEvent);

        popupSelectRect.getItems().add(itemDelAllRect);
        itemDelAllRect.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                if (selectedRectHighlightedList.size() > 0) {

                    for (int i = 0; i < selectedRectHighlightedList.size(); i++) {
                        int id = selectedRectHighlightedList.get(i);

                        ComponentInfo info = sonicCellSdk.getComponentInfo(id);
                        if (info.isFunction) {
                            ArrayList<Integer> al = sonicCellSdk.getAllComponentFunctionList(info.id);

                            // remove all reference
                            //     sonicCellSdk.removeAllFuncionIOComponentById(info.id);
                            for (int k = 0; k < al.size(); k++) {

                                removeComponent(al.get(k));
                            }

                        }
                        removeComponent(id);

                    }

                    selectedRectHighlightedList.clear();
                }
                copyBool = false;
            }
        });

        addEventFilter(MouseEvent.MOUSE_PRESSED,
                new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent mouseEvent) {

                // This is the first to be pressed
                double x = mouseEvent.getX();
                double y = mouseEvent.getY();
                dragHighlightBool = false;

                updateColRow(x, y);

                if (selectedId > -1) {
                    ComponentFX componentFX = componeFXHashMap.get(selectedId);

                    if (componentFX == null) {
                        System.out.println("1 return for now componentFX is null selected_id = " + selectedId);
                        selectedId = -1;

                        return;
                    }
                    //componentFX.getRectangleShape().setStroke(Color.BLACK);
                    componentFX.updateEnable();

                    if ((mouseEvent.getButton().equals(MouseButton.PRIMARY)) && (mouseEvent.getClickCount() == 2)) {

                        ComponentAbstraction component = sonicCellSdk.getComponent(selectedId);
                        if (component instanceof FunctionComponent) {
                            PubSubSingleton.getIntance().send(PubSubTopic.ADD_PANE_HISTORY, component.getId());
                            PubSubSingleton.getIntance().send(PubSubTopic.SET_FUNCTION_PANE, component.getId());
                        }
                        component.mouseDoubleClick();

                    }

                    selectedId = -1;
                    tableData.clear();
                }

                resetLineToNotSeclted();

                // Popup hide all
                popupOutputThru.hide();
                popupInputThru.hide();
                contextComponentMenu.hide();

                if (outputCircleIsPress) {

                    // System.out.println("outputcircle is pressed...");
                    int size = componeFXHashMap.size();

                    boolean containedBool = false;

                    Iterator it = componeFXHashMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        int key = (Integer) pair.getKey();
                        ComponentFX com = (ComponentFX) pair.getValue();

                        double xx = ((Rectangle) com.getRectangleShape()).getX();
                        double yy = ((Rectangle) com.getRectangleShape()).getY();
                        int rectBodySize = com.getRectBodySize() - 1;

                        int colRow[] = SonicCellDrawUtils.xyToColRow(xx, yy);

                        //if ((colRow[0] == selectedCol) && (colRow[1] == selectedRow)) {
                        if ((colRow[0] == selectedCol) && ((selectedRow >= colRow[1]) && (selectedRow <= (colRow[1] + rectBodySize)))) {
                            containedBool = true;

                            break;
                        }

                    }

                    if (containedBool == false) {
                        resetLineSelectConnectionOccupy();
                    }
                } else {

                    if (selectedId == -1) {

                        selectedColHightlgihtStart = selectedCol;
                        selectedRowHightlgihtStart = selectedRow;

                        rectSelectXStart = x;
                        rectSelectYStart = y;

                        rectangleSelectCompon.setX(rectSelectXStart);
                        rectangleSelectCompon.setY(rectSelectYStart);
                        rectangleSelectCompon.setWidth(0);
                        rectangleSelectCompon.setHeight(0);
                        rectangleSelectCompon.setVisible(true);
                        dragHighlightBool = true;
                    }
                }

                if (mouseEvent.getButton() == MouseButton.SECONDARY) {

                    if (selectedRectHighlightedList.size() > 0) {

                        if (selectedId > -1) {

                            updateSelectPopupMenu(componeFXHashMap.get(selectedId).getEnable(), false);
                        }

                        popupSelectRect.show(SonicPane.this, mouseEvent.getScreenX(), mouseEvent.getScreenY());

                    } else {
                        selectedMenuX = (int) mouseEvent.getX();
                        selectedMenuY = (int) mouseEvent.getY();

                        contextComponentMenu.show(SonicPane.this, mouseEvent.getScreenX(), mouseEvent.getScreenY());

                    }

                } else {

                    if (copyBool) {

                        changeSelectedRectToUnselected();
                        dragHighlightBool = true;
                        popupSelectRect.hide();

                    } else {

                        if (selectedRectHighlightedList.size() > 0) {

                            changeSelectedRectToUnselected();
                            dragHighlightBool = false;
                            popupSelectRect.hide();
                        }
                    }

                }

            }
        });

        ////////////
        addEventFilter(
                MouseEvent.MOUSE_DRAGGED,
                new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent mouseEvent) {

                if (selectedId == -1) {
                    if (dragHighlightBool) {
                        double x = mouseEvent.getX();
                        double y = mouseEvent.getY();
                        double width = x - rectSelectXStart;
                        double height = y - rectSelectYStart;
                        rectangleSelectCompon.setWidth(width);
                        rectangleSelectCompon.setHeight(height);

                        if (copyBool) {
                            copyBool = false;

                            itemPasteAllRect.setDisable(true);
                            itemCopyAllRect.setDisable(false);
                            itemDelAllRect.setDisable(false);
                            changeSelectedRectToUnselected();

                        }
                    }
                }

            }
        });

        addEventFilter(
                MouseEvent.MOUSE_RELEASED,
                new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent mouseEvent) {

                if (dragHighlightBool) {
                    // selectedRectHighlightedList.clear();
                    rectangleSelectCompon.setVisible(false);
                    rectangleSelectCompon.setWidth(0);
                    rectangleSelectCompon.setHeight(0);
                    dragHighlightBool = false;

                    double x = mouseEvent.getX();
                    double y = mouseEvent.getY();

                    //////////////////////
                    // Checkt to make sure x and y are not overlapping
                    Iterator it = componeFXHashMap.entrySet().iterator();

                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        int id = (Integer) pair.getKey();
                        ComponentFX componentFX = (ComponentFX) pair.getValue();

                        Rectangle rect = (Rectangle) componentFX.getRectangleShape();

                        if ((rect.getX() > rectSelectXStart) && (rect.getX() < x)) {
                            if ((rect.getY() > rectSelectYStart) && (rect.getY() < y)) {
                                componentFX.getRectangleShape().setStroke(SonicCellProperties.HIGHLIGHTED_COLOR);
                                selectedRectHighlightedList.add((Integer) id);
                            }
                        }

                    }

                    ///////////////////
                }

            }
        });
        ///////////

        addEventFilter(
                MouseEvent.MOUSE_MOVED,
                new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent mouseEvent) {

                if (outputCircleIsPress) {
                    tmpSelectedTmpLine.setStartX(selectedOutputSonicCircle.getCenterX());
                    tmpSelectedTmpLine.setStartY(selectedOutputSonicCircle.getCenterY());
                    tmpSelectedTmpLine.setEndX(mouseEvent.getX());
                    tmpSelectedTmpLine.setEndY(mouseEvent.getY());
                }
            }
        });

        //////////
        setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {

                event.acceptTransferModes(TransferMode.ANY);
                event.consume();
            }
        });

        setOnDragEntered(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (event.getGestureSource() != SonicPane.this
                        && db.hasString()) {
                }

                event.consume();
            }
        });

        setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /* data dropped */
                //System.out.println("target onDragDropped " + event.getX() + " : " + event.getY());
                /* if there is a string data on dragboard, read it and use it */
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    String className = db.getString();
                    addComponentByClassNameUsingXY(className, (int) event.getX(), (int) event.getY());
                    success = true;
                }
                /* let the source know whether the string was successfully 
                 * transferred and used */
                event.setDropCompleted(success);
                event.consume();
            }
        });
        drawGrid(this);

    }

    private void removeAllLineConnectionForId(int id) {

        for (Map.Entry<Long, Line> entry : connectionLineHashMap.entrySet()) {
            long key = entry.getKey();
            int keys[] = ConnectionUtil.keyDecode(key);
            int srcId = keys[0];
            int srcThruId = keys[1];
            int destId = keys[2];
            int destThruId = keys[3];

            if ((srcId == id) || (destId == id)) {

                // Delete the relationship on the SDK
                boolean b = sonicCellSdk.removeConnectComponentInOutputById(srcId, srcThruId, destId, destThruId);

                // delete
                if (b) {
                    componeFXHashMap.get(destId).getCircleInput(destThruId).isInputOccupy = false;
                }

                Line lin = connectionLineHashMap.get(key);

                SonicPane.this.getChildren().remove(lin);
                connectionLineHashMap.remove(key);
            }
        }

    }

    private void updateSelectPopupMenu(boolean enable, boolean update) {
        if (selectedId > -1) {
            if (update) {
                ComponentFX componentFX = componeFXHashMap.get(selectedId);
                componentFX.setEnable(enable);
                sonicCellSdk.setEnableComponent(selectedId, enable);

                //  sonicCellSdk.getComponentInfo(selectedId).enabled = enable;
                // sonicCellSdk.getComponent(selectedId).setEnable(enable);
            }

            if (enable) {
                itemEnable.setDisable(true);
                itemDisable.setDisable(false);
                popupRectListRoutine.getItems().get(5).setDisable(false);  //start
                popupRectListRoutine.getItems().get(6).setDisable(true);  //stop

            } else {
                itemEnable.setDisable(false);
                itemDisable.setDisable(true);
                popupRectListRoutine.getItems().get(5).setDisable(true);  //start
                popupRectListRoutine.getItems().get(6).setDisable(true);  //stop
            }
        }
    }

    private void startStopSingleProcess(boolean startBool) {
        if (selectedId > -1) {

            Thread th = new Thread() {

                public void run() {
                    if (startBool) {

                        popupRectListRoutine.getItems().get(0).setDisable(false);  // edit
                        //   popupRectListRoutine.getItems().get(1).setDisable(true);  //del
                        popupRectListRoutine.getItems().get(2).setDisable(true);  //enable
                        popupRectListRoutine.getItems().get(3).setDisable(true);  //disable
                        popupRectListRoutine.getItems().get(4).setDisable(false);  //help
                        popupRectListRoutine.getItems().get(5).setDisable(true);  //start
                        popupRectListRoutine.getItems().get(6).setDisable(false);  //stop

//        popupRect.getItems().add(itemDel);
//        popupRect.getItems().add(itemEnable);
//        popupRect.getItems().add(itemDisable);
//        popupRect.getItems().add(itemHelp);
//        popupRect.getItems().add(itemStart);
//        popupRect.getItems().add(itemStop);
//                
//
//                itemEdit.setVisible(false);
//                itemDel.setVisible(false);
//                itemEnable.setVisible(false);
//                itemDisable.setVisible(false);
//                itemHelp.setVisible(false);
//                itemStart.setVisible(false);
//                itemStop.setVisible(false);
                        //  itemEnable.setDisable(true);
                        //  itemDisable.setDisable(false);
                    } else {
                        popupRectListRoutine.getItems().get(0).setDisable(false);  // edit
                        //  popupRectListRoutine.getItems().get(1).setDisable(false);  //del
                        popupRectListRoutine.getItems().get(2).setDisable(false);//enable
                        popupRectListRoutine.getItems().get(3).setDisable(true);  //disable
                        popupRectListRoutine.getItems().get(4).setDisable(false);  //help
                        popupRectListRoutine.getItems().get(5).setDisable(false);  //start
                        popupRectListRoutine.getItems().get(6).setDisable(true);  //stop

                        //  itemEnable.setDisable(false);
                        //   itemDisable.setDisable(true);
                    }
                }

            };

            th.start();

        }
    }

    private void drawGrid(Pane pane) {

        Line line1 = new Line();
        line1.setStartX(0);
        line1.setStartY(0);
        line1.setEndX(2048 * 4);
        line1.setEndY(2048 * 4);
        pane.getChildren().add(line1);
        line1.setStroke(Color.TRANSPARENT);

        /*
        for (int i = 0; i < 300; i++) {

            Line line1 = new Line();
            line1.setStartX(i * SonicCellProperties.WIDTH_OF_CELL);
            line1.setStartY(0);
            line1.setEndX(i * SonicCellProperties.WIDTH_OF_CELL);
            line1.setEndY(1024 * 20);
            pane.getChildren().add(line1);
            line1.setStroke(Color.TRANSPARENT);

            if (true) {
                Line line2 = new Line();
                line2.setStartX(0);
                line2.setStartY(i * SonicCellProperties.HEIGHT_OF_CELL);
                line2.setEndX(1024 * 8);
                line2.setEndY(i * SonicCellProperties.HEIGHT_OF_CELL);
                // line1.setStroke(Color.);
                pane.getChildren().add(line2);
                line2.setStroke(Color.TRANSPARENT);
            }
        }
         */
    }

    private void updateColRow(double x, double y) {
        int colRow[] = SonicCellDrawUtils.xyToColRow(x, y);

        selectedCol = colRow[0];
        selectedRow = colRow[1];
    }

    public int addComponentByClassName(String className, int col, int row) {
        int id = sonicCellSdk.addComponentByClassName(className);
        ComponentAbstraction componentAbstraction = sonicCellSdk.getComponent(id);
        addComponent(componentAbstraction, col, row);
        return id;
    }

    public int addComponentByClassNameUsingXY(String className, int x, int y) {
        int colRow[] = SonicCellDrawUtils.xyToColRow(x, y);
        return addComponentByClassName(className, colRow[0], colRow[1]);
    }

    protected void addComponent(ComponentAbstraction componentAbstraction, int col, int row) {
        int id = componentAbstraction.getId();

        ComponentInfo info = sonicCellSdk.getComponentInfo((Integer) id);
        info.functionId = this.functionIdPane;
        boolean thisFunctionid = false;
        if (info.functionId > -1) {
            thisFunctionid = true;
        }
        ComponentFX componentFX = new ComponentFX(this, componentAbstraction, thisFunctionid);
        componentFX.addRectangleMouseDraggedEvent(rectangleMouseDragHandler);
        componentFX.addRectangleMousePressEvent(rectangleMousePressedHandler);
        componentFX.addCircleMousePressEvent(circleMousePressedHandler);
        componentFX.addCircleMouseEnterEvent(circleMouseEnterHandler);
        componentFX.addCircleMouseExitEvent(circleMouseExitHandler);
        componentFX.setColRow(col, row);

        sonicCellSdk.setComponentColRow(componentAbstraction, col, row);
        componeFXHashMap.put(id, componentFX);

    }

    public SonicCellSdk getSonicCellSdk() {
        return sonicCellSdk;
    }

    public void removeComponent(int id) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (id < 0) {
                    return;
                }

                // Cleanup ComponentFX
                ComponentFX com = componeFXHashMap.get(id);
                if (com == null) {

                    ComponentInfo info = sonicCellSdk.getComponentInfo(id);
                    if (info.isFunction) {
                        sonicCellSdk.removeAllThroughputReference(info.id);
                    }

                    // VT test maybe it doesn't cause other errors
                    //System.out.println("Testing maybe its safe to delete anyway");
                    sonicCellSdk.removeComponentById(id);
                    return;
                }
                com.deleteAndCleanup();

                ComponentInfo info = sonicCellSdk.getComponentInfo(id);
                if (info.isFunction) {
                    sonicCellSdk.removeAllThroughputReference(info.id);
                }

                // Remove the component in the SDK
                boolean bool = sonicCellSdk.removeComponentById(id);

                Iterator it = connectionLineHashMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    long key = (Long) pair.getKey();
                    Line line = (Line) pair.getValue();

                    int keys[] = ConnectionUtil.keyDecode(key);
                    int srcId = keys[0];
                    int srcThruId = keys[1];
                    int destId = keys[2];
                    int destThruId = keys[3];

                    if ((id == srcId) || (id == destId)) {
                        boolean b = getChildren().remove(line);
                        it.remove(); // avoids a ConcurrentModificationException

                        line.removeEventHandler(MouseEvent.MOUSE_PRESSED, lineMousePressedHandler);
                        line.removeEventHandler(MouseEvent.MOUSE_ENTERED, lineMouseEnterHandler);
                        line.removeEventHandler(MouseEvent.MOUSE_EXITED, lineMouseExitHandler);

                        // Don't need this
                        // sonicCellSdk.removeConnectComponentInOutputById(srcId, srcThruId, destId, destThruId);
                    }

                    // Set the occupy to false
                    if (id == srcId) {
                        // if (componeFXHashMap.get(destId) != null) {
                        if (componeFXHashMap.get(destId).getCircleInput(destThruId) != null) {
                            componeFXHashMap.get(destId).getCircleInput(destThruId).isInputOccupy = false;
                        }
                    }
                    // }
                }

                componeFXHashMap.remove(id);

                selectedId = -1;
                tableData.clear();

            }
        });

    }

    protected boolean addConnectComponentInOutput(int srcId, int srcOutputId, int destId, int destInputId) {

        return addConnectComponentInOutput(true, srcId, srcOutputId, destId, destInputId);
    }

    protected boolean addConnectComponentInOutput(boolean addToSDKBool, int srcId, int srcOutputId, int destId, int destInputId) {

        if (componeFXHashMap.get((Integer) destId) == null) {
            System.out.println(" addConnectComponentInOutput is null");
            return false;
        }

        if (componeFXHashMap.get((Integer) destId).getCircleInput((Integer) destInputId) == null) {
            System.out.println(" addConnectComponentInOutput is null2");
        }

        if (componeFXHashMap.get((Integer) destId).getCircleInput((Integer) destInputId).isInputOccupy) {
            return false;
        }

        componeFXHashMap.get(destId).getCircleInput(destInputId).isInputOccupy = true;

        if (addToSDKBool) {
            boolean b = sonicCellSdk.addConnectComponentInOutput(srcId, srcOutputId, destId, destInputId);
            if (b == false) {
                return false;
            }
        }
        long key = ConnectionUtil.keyEncode(srcId, srcOutputId, destId, destInputId);
        Line line = new Line();
        line.setOnMousePressed(lineMousePressedHandler);
        line.setOnMouseEntered(lineMouseEnterHandler);
        line.setOnMouseExited(lineMouseExitHandler);
        line.setStrokeWidth(SonicCellProperties.LINE_WIDTH_FOR_CONNECTION);
        line.setStroke(Color.BLACK);

        connectionLineHashMap.put(key, line);
        getChildren().add(line);
        refereshConnections(key);
        return true;
    }

    private void refereshConnections(long key) {
        Line line = connectionLineHashMap.get(key);

        int keys[] = ConnectionUtil.keyDecode(key);
        int srcId = keys[0];
        int srcThruId = keys[1];
        int destId = keys[2];
        int destThruId = keys[3];

        if ((componeFXHashMap.containsKey(srcId) == false) || (componeFXHashMap.containsKey(destId) == false)) {
            return;
        }

        ComponentFX srcComponent = componeFXHashMap.get(srcId);

        IOShape srcThurOutId = srcComponent.getCircleOutput(srcThruId);

        ComponentFX destComponent = componeFXHashMap.get(destId);
        IOShape destThurOutId = destComponent.getCircleInput(destThruId);

        line.setStartX(srcThurOutId.getCenterX() + SonicCellProperties.CIRCLE_RADIUS_IN_OUT_PUT - 1);
        line.setStartY(srcThurOutId.getCenterY());

        line.setEndX(destThurOutId.getCenterX() - SonicCellProperties.CIRCLE_RADIUS_IN_OUT_PUT + 1);
        line.setEndY(destThurOutId.getCenterY());

    }

    private void refereshAllConnections() {

        int size = connectionLineHashMap.size();

        for (Map.Entry<Long, Line> entry : connectionLineHashMap.entrySet()) {

            long key = entry.getKey();

            refereshConnections(key);

        }

    }

    private void changeSelectedRectToUnselected() {

        if (selectedRectHighlightedList.size() > 0) {
            for (int i = 0; i < selectedRectHighlightedList.size(); i++) {
                int id = selectedRectHighlightedList.get(i);
                componeFXHashMap.get((Integer) id).getRectangleShape().setStroke(SonicCellProperties.NOT_HIGHLIGHTED_COLOR);
            }
            if (copyBool == false) {
                selectedRectHighlightedList.clear();
            }
        }
    }

    private void refereshSelectedConnections() {

        for (Map.Entry<Long, Line> entry : connectionLineHashMap.entrySet()) {
            long key = entry.getKey();
            Line line = entry.getValue();

            int keys[] = ConnectionUtil.keyDecode(key);

            int srcId = keys[0];
            int srcThruId = keys[1];
            int destId = keys[2];
            int destThruId = keys[3];

            if ((selectedId == srcId) || (selectedId == destId)) {
                refereshConnections(key);
            }
        }
    }

    private void resetLineSelectConnectionOccupy() {
        tmpSelectedTmpLine.setVisible(false);
        outputCircleIsPress = false;
        inputputCircleIsPress = false;
    }

    private void resetLineToNotSeclted() {
        if (previousSelectedLine != null) {
            previousSelectedLine.setStroke(SonicCellProperties.NOT_HIGHLIGHTED_COLOR);
            previousSelectedLine = null;
            selectedLineKey = -1;
        }
    }

    /////////////////////////////////////////////////////////////
    /////////////////////////// EVENT //////////////////////////
    private EventHandler<MouseEvent> circleMousePressedHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {

            contextComponentMenu.hide();
            double x = t.getX();
            double y = t.getY();
            // System.out.println("circleMousePressedHandler updateColRow");
            // updateColRow(x, y);

            IOShape circle = (IOShape) t.getSource();
            MouseButton button = t.getButton();

            if (circle.isOutput) {
                if (outputCircleIsPress) {
                    resetLineSelectConnectionOccupy();
                    return;
                }

                //////////
                // MouseButton button = t.getButton();
                if (button == MouseButton.PRIMARY) {
                    selectedOutputSonicCircle = circle;
                    outputCircleIsPress = true;
                    tmpSelectedTmpLine.setVisible(true);
                    tmpSelectedTmpLine.setStartX(selectedOutputSonicCircle.getCenterX());
                    tmpSelectedTmpLine.setStartY(selectedOutputSonicCircle.getCenterY());
                    tmpSelectedTmpLine.setEndX(selectedOutputSonicCircle.getCenterX());
                    tmpSelectedTmpLine.setEndY(selectedOutputSonicCircle.getCenterY());

                } else if (button == MouseButton.SECONDARY) {

                    popupOutputThru.show(SonicPane.this, t.getScreenX(), t.getScreenY());
                    selectedOutputSonicCircle = circle;

                } else if (button == MouseButton.MIDDLE) {

                }
                /////////

//                selectedOutputSonicCircle = circle;
//                outputCircleIsPress = true;
//                tmpSelectedTmpLine.setVisible(true);
//                tmpSelectedTmpLine.setStartX(selectedOutputSonicCircle.getCenterX());
//                tmpSelectedTmpLine.setStartY(selectedOutputSonicCircle.getCenterY());
//                tmpSelectedTmpLine.setEndX(selectedOutputSonicCircle.getCenterX());
//                tmpSelectedTmpLine.setEndY(selectedOutputSonicCircle.getCenterY());
            } else {

                if (outputCircleIsPress) {
                    selectedInputSonicCircle = circle;

                    if (selectedInputSonicCircle.isInputOccupy) {
                        return;
                    }

                    addConnectComponentInOutput(selectedOutputSonicCircle.srcId, selectedOutputSonicCircle.thruId, selectedInputSonicCircle.srcId, selectedInputSonicCircle.thruId);
                    inputputCircleIsPress = false;
                } else {

                    if (button == MouseButton.PRIMARY) {

                    } else if (button == MouseButton.SECONDARY) {
                        selectedInputSonicCircle = circle;
                        inputputCircleIsPress = true;

                        int thisSelectedId = selectedInputSonicCircle.srcId;
                        int thisThruId = selectedInputSonicCircle.thruId;
                        if (!selectedInputSonicCircle.isInputOccupy) {
                            boolean b = sonicCellSdk.checkIfIOConnectionTypeIsMatched("string", thisSelectedId, thisThruId);
                            if (b) {
                                //   itemTextConsole.setDisable(false);
                                popupInputThru.getItems().get(0).setDisable(false);

                            } else {
                                //   itemTextConsole.setDisable(true);
                                popupInputThru.getItems().get(0).setDisable(true);
                            }

                        } else {
                            // itemTextConsole.setDisable(true);
                            popupInputThru.getItems().get(0).setDisable(true);
                        }

                        popupInputThru.show(SonicPane.this, t.getScreenX(), t.getScreenY());
                    }

                }

                tmpSelectedTmpLine.setVisible(false);
                outputCircleIsPress = false;
            }

        }
    };

    private EventHandler<MouseEvent> circleMouseEnterHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            IOShape circle = (IOShape) t.getSource();
            circle.setFill(SonicCellProperties.HIGHLIGHTED_COLOR_IN_OUT_ENTER);

        }
    };

    private EventHandler<MouseEvent> circleMouseExitHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            IOShape circle = (IOShape) t.getSource();
            // circle.setFill(SonicCellProperties.HIGHLIGHTED_COLOR_IN_OUT_EXIT);
            circle.setFill(circle.color);

        }
    };
    private EventHandler<MouseEvent> lineMouseEnterHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            Line line = (Line) t.getSource();
            line.setStroke(SonicCellProperties.HIGHLIGHTED_COLOR);
        }
    };

    private EventHandler<MouseEvent> lineMouseExitHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            Line line = (Line) t.getSource();
            line.setStroke(SonicCellProperties.HIGHLIGHTED_COLOR_IN_OUT_EXIT);
        }
    };

    private EventHandler<MouseEvent> lineMousePressedHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {
            resetLineToNotSeclted();

            Line line = (Line) t.getSource();

            /////////
            for (Map.Entry<Long, Line> entry : connectionLineHashMap.entrySet()) {
                long key = entry.getKey();
                Line line2 = entry.getValue();
                if (line2 == line) {
                    selectedLineKey = key;
                    break;
                }

            }
            ////////

            line.setStroke(SonicCellProperties.HIGHLIGHTED_COLOR);

            if (t.getButton() == MouseButton.SECONDARY) {
                contextComponentMenu.hide();
                popupLineInfo.show(line, t.getScreenX(), t.getScreenY());

            }

            previousSelectedLine = line;
        }
    };

    public int getSelectedComponentId() {

//        ComponentInfo info = sonicCellSdk.getComponentInfo(selectedId);
//        HashMap<String, Object> prop = info.
//        
//        
        return selectedId;
    }

    private EventHandler<MouseEvent> rectangleMousePressedHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {

            contextComponentMenu.hide();
            double x = t.getX();
            double y = t.getY();
            //  System.out.println("rectangleMousePressedHandler updateColRow");
            //  updateColRow(x, y);

            // Checkt to make sure x and y are not overlapping
            Iterator it = componeFXHashMap.entrySet().iterator();

            if (selectedId > -1) {
                ComponentFX componentFX = componeFXHashMap.get(selectedId);

                if (componentFX == null) {
                    System.out.println("2 return for now componentFX is null selected_id = " + selectedId);
                    return;
                }

                //componentFX.getRectangleShape().setStroke(Color.BLACK);
                componentFX.updateEnable();
                sonicCellSdk.setComponentColRowById(selectedId, selectedCol, selectedRow);
            }

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                int id = (Integer) pair.getKey();
                ComponentFX componentFX = (ComponentFX) pair.getValue();

                if (componentFX.getRectangleShape().contains(x, y)) {
                    selectedId = id;

                    tableData.clear();

                    ComponentInfo info = sonicCellSdk.getComponentInfo(id);
                    HashMap<String, Object> prop = info.propHash;

                    tableData.add(new PropertyTableData("Id", info.id + ""));  // Don't show
                    tableData.add(new PropertyTableData("Name", info.name));
                    //  tableData.add(new PropertyTableData("Alias", info.alias));  // Don't show
                    if (info.isFunction) {
                        tableData.add(new PropertyTableData("# of Input", info.numOfInput + ""));
                        tableData.add(new PropertyTableData("# of Output", info.numOfOutput + ""));
                    }

                    Iterator it2 = prop.entrySet().iterator();
                    while (it2.hasNext()) {
                        Map.Entry pair2 = (Map.Entry) it2.next();
                        String name = pair2.getKey().toString();
                        if (pair2.getValue() == null) {
                            System.out.println("Get null.....");
                        }
                        String value = pair2.getValue().toString();
                        //System.out.println(name + " = " + value);
                        tableData.add(new PropertyTableData(name, value));

                    }

                    if (t.getButton() == MouseButton.SECONDARY) {

                        // System.out.println("44444");
                        // VT TEMP disable 12/10/
                        //  updateSelectPopupMenu(componentFX.getEnable(), false);
                        if (selectedRectHighlightedList.size() > 0) {
                            popupSelectRect.show(SonicPane.this, t.getScreenX(), t.getScreenY());
                            break;
                        } else {

                            updateSelectPopupMenu(componentFX.getEnable(), true);

                            popupRectListRoutine.show(componentFX.getRectangleShape(), t.getScreenX(), t.getScreenY());
                        }
                    }
                    refereshSelectedConnections();
                    componentFX.getRectangleShape().setStroke(SonicCellProperties.HIGHLIGHTED_COLOR);

                    // Tell to update file;
                    PubSubSingleton.getIntance().send(PubSubTopic.FILE_IS_MODIFY, null);
                    break;
                }

                if (componentFX.getTextShape().contains(x, y)) {
                    selectedId = id;
                    if (t.getButton() == MouseButton.SECONDARY) {
                        updateSelectPopupMenu(componentFX.getEnable(), true);
                        popupRectListRoutine.show(componentFX.getTextShape(), t.getScreenX(), t.getScreenY());
                    }
                    refereshSelectedConnections();
                    break;
                }

            }

        }
    };

    private EventHandler<MouseEvent> rectangleMouseDragHandler
            = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent t) {

            double x = t.getX();
            double y = t.getY();

            double xy[] = SonicCellDrawUtils.colRowXYTranslate(x, y);

            if ((xy[0] < 0) || (xy[1] < 0)) {
                return;
            }

            if (selectedId > -1) {
                /////////
                int colRow[] = SonicCellDrawUtils.xyToColRow(x, y);
                int thisCol = colRow[0];
                int thisRow = colRow[1];
                if (sonicCellSdk.containColRow(functionIdPane, thisCol, thisRow)) {
                    return;
                }

                ////////
                // System.out.println("rectangleMouseDragHandler updateColRow");
                updateColRow(x, y);

                // componeFXHashMap.get(selectedId).setXY(xy[0], xy[1]);
                componeFXHashMap.get(selectedId).setColRow(selectedCol, selectedRow);
                sonicCellSdk.setComponentColRowById(selectedId, selectedCol, selectedRow);
                refereshSelectedConnections();
            }
        }
    };

    /////////////// all Data
    protected void clearAllData() {

        Iterator it = componeFXHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            int key = (Integer) pair.getKey();
            ComponentFX com = (ComponentFX) pair.getValue();
            com.deleteAndCleanup();
            componeFXHashMap.remove((com));
        }

        it = connectionLineHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Long key = (Long) pair.getKey();
            Line line = (Line) pair.getValue();

            this.getChildren().remove(line);
        }

        selectedRectHighlightedList.clear();
        connectionLineHashMap.clear();
        componeFXHashMap.clear();

    }

}
