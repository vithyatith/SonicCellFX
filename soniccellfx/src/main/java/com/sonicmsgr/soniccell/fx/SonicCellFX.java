
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTreeView;
import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.ComponentInfo;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.msg.MsgListener;
import com.sonicmsgr.soniccell.msg.PubSubSingleton;
import com.sonicmsgr.util.FileUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class SonicCellFX extends Application {

    private Gson gson = null;

    private ComponentDBList componentDB = new ComponentDBList();

    private VBox topContainer = new VBox();  //Creates a container to hold all Menu Objects.
    private MenuBar menuBar = new MenuBar();

    // --- Menu File
    private Menu menuFile = new Menu("File");
    private Menu menuHelp = new Menu("Help");
    private Menu menuTheme = new Menu("Theme");
    private Menu menuConsole = new Menu("Console");

    private HBox toolBarHbox = new HBox();
    private ToolBar toolBar = new ToolBar();  //Creates our tool-bar to hold the buttons.
    private Stage primaryStage;

    private JFXTreeView<String> treeView = null;

    private ScrollPane scrollPane;

    private TableView<PropertyTableData> tableView = new TableView<PropertyTableData>();
    private final JFXTextArea textArea = new JFXTextArea();
    private final JFXListView<String> listView = new JFXListView<String>();
    private int listViewLineCount = 0;
    private int listViewLineMax = 10000;

    private ProjectWorkspace workspace;

    private BorderPane sonicPaneBar = new BorderPane();

    private JFXButton sonicPaneFunctionBackButton;// = new Button("Close");

    private int logPrintCount = 0;
    private int maxLogPrintCount = 500;

    final ToggleGroup buttonGroup = new ToggleGroup();
    private RadioButton syncButton = new RadioButton();
    private RadioButton unsyncButton = new RadioButton();

    private String version = "SonicCell Version 2.3";
    private Scene mainScene = null;

    public SonicCellFX() {

    }

    private void createPubSub() {

        PubSubSingleton.getIntance().addListener(new MsgListener(PubSubTopic.SET_PANE) {
            @Override
            public void onMessage(final Object msg) {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        SonicPane sonicPane = (SonicPane) msg;
                        if (sonicPane.functionIdPane == -1) {
                            sonicPaneBar.getChildren().remove(sonicPaneFunctionBackButton);
                        } else {
                            sonicPaneBar.setTop(sonicPaneFunctionBackButton);
                        }
                        workspace.setCurrentSonicPane(sonicPane);
                        scrollPane.setContent(sonicPane);
                    }
                });
            }
        });

        PubSubSingleton.getIntance().addListener(new MsgListener(PubSubTopic.UPDATE_PROPERTIES) {
            @Override
            public void onMessage(Object msg) {

                int id = (Integer) msg;

                tableData.clear();
                ComponentInfo info = workspace.getCurrentSonicPane().getSonicCellSdk().getComponentInfo(id);
                HashMap<String, Object> prop = info.propHash;
                tableData.add(new PropertyTableData("Name", info.name));
                if (info.isFunction) {
                    tableData.add(new PropertyTableData("# of Input", info.numOfInput + ""));
                    tableData.add(new PropertyTableData("# of Output", info.numOfOutput + ""));
                }

                Iterator it2 = prop.entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry pair2 = (Map.Entry) it2.next();
                    String name = pair2.getKey().toString();
                    if (pair2.getValue() == null) {
                    }
                    String value = pair2.getValue().toString();
                    tableData.add(new PropertyTableData(name, value));
                }

            }
        });

        // Reload
        PubSubSingleton.getIntance().addListener(new MsgListener(PubSubTopic.SET_FUNCTION_PANE) {
            @Override
            public void onMessage(final Object msg) {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Integer functionId = (Integer) msg;
                        // Create get a new SonicPane
                        SonicPane sonicPane = workspace.getNewSonicPane();
                        sonicPane.functionIdPane = functionId;
                        workspace.setCurrentSonicPane(sonicPane);
                        workspace.loadPane(workspace.getCurrentSonicPane());
                        if (workspace.getCurrentSonicPane().functionIdPane == -1) {
                            sonicPaneBar.getChildren().remove(sonicPaneFunctionBackButton);
                        } else {
                            sonicPaneBar.setTop(sonicPaneFunctionBackButton);
                        }

                        scrollPane.setContent(workspace.getCurrentSonicPane());
                    }
                });

            }
        });

        PubSubSingleton.getIntance().addListener(new MsgListener(PubSubTopic.REFRESH_COMPONENT_FX) {
            @Override
            public void onMessage(final Object msg) {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                        // Refresh the componentFx
                        workspace.getCurrentSonicPane().refreshComponentDraw();
                    }
                });

            }
        });

        PubSubSingleton.getIntance().addListener(new MsgListener(PubSubTopic.FILE_IS_MODIFY) {
            @Override
            public void onMessage(final Object msg) {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                        if (workspace.fileIsModify == false) {
                            primaryStage.setTitle(workspace.getProjectName() + "*");
                            workspace.fileIsModify = true;
                        }
                    }
                });

            }
        });

        PubSubSingleton.getIntance().addListener(new MsgListener(PubSubTopic.LOG) {
            @Override
            public void onMessage(final Object msg) {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        clearLogScreenIfReachMax();

                        //textArea.appendText(msg.toString() + "\n");
                        apppendTextListView(msg.toString());
                    }
                });

            }
        });

        PubSubSingleton.getIntance().addListener(new MsgListener(PubSubTopic.PRINT) {
            @Override
            public void onMessage(final Object msg) {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (msg != null) {
                            clearLogScreenIfReachMax();
                            //textArea.appendText(msg.toString() + "\n");
                            apppendTextListView(msg.toString());
                        }
                    }
                });

            }
        });

        PubSubSingleton.getIntance().addListener(new MsgListener(PubSubTopic.STOP_COMPONENT) {
            @Override
            public void onMessage(final Object msg) {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (msg != null) {
                            // Refresh the componentFx
                            int componentId = (int) msg;
                            workspace.getCurrentSonicPane().stopManualComponent(componentId);
                            System.out.println("Stoping manual component....");
                        }
                    }
                });

            }
        });
    }

    private void apppendTextListView(String msg) {

        listViewLineCount++;
        if (listViewLineCount > listViewLineMax) {
            clearListView();
        }

        listView.getItems().add(msg);
        if ((listViewLineCount % 100) == 0) {
            listView.scrollTo(listView.getItems().size() - 1);
        }
    }

    private void clearListView() {
        listViewLineCount = 0;
        listView.getItems().clear();
    }

    private void clearLogScreenIfReachMax() {
        logPrintCount++;
        if (logPrintCount > maxLogPrintCount) {
            textArea.clear();
        }
    }

    class ComboBoxCell extends ComboBoxTableCell<XYChart.Data, String> {

        @Override
        public void updateItem(String arg0, boolean empty) {
            super.updateItem(arg0, empty);

        }

        @Override
        public void commitEdit(String newValue) {
            super.commitEdit(newValue);

        }

    }

    class EditingComboBoxCell extends ComboBoxTableCell<XYChart.Data, String> {

        private TextField textField;

        private String originalString = "";

        public EditingComboBoxCell() {
            super();
        }

        public EditingComboBoxCell(ObservableList<String> s) {
            super(s);
        }

        @Override
        public void startEdit() {

            super.startEdit();

            if (textField == null) {
                createTextField();
            }
            originalString = String.valueOf(getItem());
            setGraphic(textField);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            textField.selectAll();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            originalString = String.valueOf(getItem());
            setText(String.valueOf(getItem()));
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            getStyleClass().clear();
            setStyle(null);

            //setStyle("-fx-font-size: 4");
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {

                // setStyle("-fx-background-color: #d1d1d1;");
                //#73d676
                // setStyle("-fx-background-color: white;");
                getStyleClass().clear();
                setStyle(null);

                PropertyTableData p2 = tableView.getItems().get(EditingComboBoxCell.this.getIndex());

                String name = p2.getName();
                int id = workspace.getCurrentSonicPane().getSelectedComponentId();

                ComponentAbstraction com = workspace.getCurrentSonicPane().getSonicCellSdk().getComponent(id);
                int numInput = com.getNumberOfInputSize();
                for (int i = 0; i < numInput; i++) {

                    //////////////
                    ArrayList<DataTypeIO> typeioAL = com.getInputDataType().get(i).typeArrayList;

                    String propName = typeioAL.get(0).getPropName();

                    if (propName.equalsIgnoreCase(name)) {
                        setStyle("-fx-background-color: #67db6a;");
                        break;
                    }
                }

                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setGraphic(textField);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                } else {
                    boolean successEdit = false;
                    if (textField != null) {
                        textField.setText(getString());

                        PropertyTableData p = tableView.getItems().get(EditingComboBoxCell.this.getIndex());
                        p.setValue(textField.getText());

                        textField.getStyleClass().removeIf(style -> style.equals("textfieldstyle"));

                        //System.out.println(p.getName() + " : " + textField.getText() + "  funcdion id = " + workspace.getCurrentSonicPane().functionIdPane);
                        textField = null;

                        successEdit = workspace.getCurrentSonicPane().updatedSelectedComponentProperty(p.getName(), p.getValue());

                    } else {
                        successEdit = true;
                    }

                    if (successEdit) {
                        setText(getString());
                    } else {
                        setText(originalString);
                    }
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                }
            }

        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.setOnKeyPressed(new EventHandler<KeyEvent>() {

                @Override
                public void handle(KeyEvent t) {
                    if (t.getCode() == KeyCode.ENTER) {
                        //commitEdit(Integer.parseInt(textField.getText()));
                        commitEdit((textField.getText()));
                    } else if (t.getCode() == KeyCode.ESCAPE) {
                        cancelEdit();
                    }
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }

    class EditingCell extends TableCell<XYChart.Data, String> {

        private TextField textField;

        private String originalString = "";

        public EditingCell() {
        }

        @Override
        public void startEdit() {

            super.startEdit();

            if (textField == null) {
                createTextField();
            }
            originalString = String.valueOf(getItem());
            setGraphic(textField);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            textField.selectAll();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            originalString = String.valueOf(getItem());
            setText(String.valueOf(getItem()));
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            getStyleClass().clear();
            setStyle(null);

            //setStyle("-fx-font-size: 4");
            if (empty) {
                setText(null);
                setGraphic(null);
                // this.setTextFill(Color.BLACK);
            } else {

                // setStyle("-fx-background-color: #d1d1d1;");
                //#73d676
                // setStyle("-fx-background-color: white;");
                getStyleClass().clear();
                setStyle(null);
                //this.setTextFill(Color.BLACK);

                PropertyTableData p2 = tableView.getItems().get(EditingCell.this.getIndex());

                String name = p2.getName();
                int id = workspace.getCurrentSonicPane().getSelectedComponentId();

                ComponentAbstraction com = workspace.getCurrentSonicPane().getSonicCellSdk().getComponent(id);
                if (com == null) {
                    //System.out.println("ComponentAbstraction is null name = "+name+" id = "+id);
                    return;
                }
                int numInput = com.getNumberOfInputSize();
                for (int i = 0; i < numInput; i++) {

                    //////////////
                    ArrayList<DataTypeIO> typeioAL = com.getInputDataType().get(i).typeArrayList;

                    String propName = typeioAL.get(0).getPropName();

                    if (propName.equalsIgnoreCase(name)) {
                        setStyle("-fx-background-color: #67db6a;");
                        break;
                    }
                }

                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setGraphic(textField);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                } else {
                    boolean successEdit = false;
                    if (textField != null) {
                        textField.setText(getString());

                        PropertyTableData p = tableView.getItems().get(EditingCell.this.getIndex());
                        p.setValue(textField.getText());

                        textField.getStyleClass().removeIf(style -> style.equals("textfieldstyle"));

                        //System.out.println(p.getName() + " : " + textField.getText() + "  funcdion id = " + workspace.getCurrentSonicPane().functionIdPane);
                        textField = null;

                        successEdit = workspace.getCurrentSonicPane().updatedSelectedComponentProperty(p.getName(), p.getValue());

                    } else {
                        successEdit = true;
                    }

                    if (successEdit) {
                        setText(getString());
                    } else {
                        setText(originalString);
                    }
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                }
            }

        }

        private void createTextField() {
            textField = new TextField(getString());
            // textField = new PasswordField();
            // textField.setText(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.setOnKeyPressed(new EventHandler<KeyEvent>() {

                @Override
                public void handle(KeyEvent t) {
                    if (t.getCode() == KeyCode.ENTER) {
                        //commitEdit(Integer.parseInt(textField.getText()));
                        commitEdit((textField.getText()));
                    } else if (t.getCode() == KeyCode.ESCAPE) {
                        cancelEdit();
                    }
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }

    private final ObservableList<PropertyTableData> tableData
            = FXCollections.observableArrayList();

    private boolean boolSwitch = false;
    private SettingData settingData = new SettingData();

    private String configHomeFile = "";

    private void createHomeDirectory() {
        configHomeFile = System.getProperty("user.home") + File.separator + ".soniccell";
        File f = new File(configHomeFile);
        if (!f.exists()) {
            boolean b = f.mkdir();
            if (b == false) {
                System.out.println("Cannot create " + configHomeFile);
            }
        }
    }

    private void loadSetting() {
        createHomeDirectory();
        String fname = configHomeFile + File.separator + "setting.json";
        File file = new File(fname);
        if (file.exists()) {
            String json = FileUtils.readTextFile(fname);
            settingData = gson.fromJson(json, SettingData.class);
        } else {
            saveSetting();
        }
    }

    private void saveSetting() {
        createHomeDirectory();
        String fname = configHomeFile + File.separator + "setting.json";
        String json = gson.toJson(settingData);
        FileUtils.writeTextToFile(fname, json);
    }

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {

        gson = new GsonBuilder().setPrettyPrinting().create();
        Parameters params = getParameters();
        List<String> list = params.getRaw();
        this.loadComponentListJson(params);

        workspace = new ProjectWorkspace(componentDB);
        workspace.setTableDataInPool(tableData);
        //  AquaFx.style();
        loadSetting();
        /////////

//        for (String each : list) {
//        }
        ///////
        //// adding the image the button
        InputStream input = getClass().getResourceAsStream("/asset/arrow_left.png");
//       URL u =getClass().getClassLoader().getResource("/asset/arrow_left.png");
//        InputStream input;
//        try {
//            input = u.openStream();
//            // FileInputStream input = new FileInputStream("asset" + File.separator + "arrow_left.png");

        //  Image image = new Image(input);
        Image image = new Image(input, 15, 15, false, false);
        ImageView imageLeft = new ImageView(image);

        sonicPaneFunctionBackButton = new JFXButton("", imageLeft);
        // sonicPaneFunctionBackButton = new JFXButton("", imageLeft);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            Logger.getLogger(SonicCellFX.class.getName()).log(Level.SEVERE, null, ex);
//        }
        ///////////
        Callback<TableColumn, TableCell> cellFactory
                = new Callback<TableColumn, TableCell>() {

            @Override
            public TableCell call(TableColumn p) {

                //boolSwitch = !boolSwitch;
                boolSwitch = true;
                if (boolSwitch) {

                    EditingCell editingCell = new EditingCell();
                    return editingCell;
                } else {

//                ObservableList<String> items = FXCollections.observableArrayList();
//                items.add("one");
//                items.add("two");
//
//                EditingComboBoxCell cell = new EditingComboBoxCell(items);
//                cell.setComboBoxEditable(true);
//                return cell;
                    ObservableList<String> items = FXCollections.observableArrayList();
                    items.add("one");
                    items.add("two");

                    ComboBoxTableCell cell = new ComboBoxTableCell(FXCollections.observableArrayList(items)) {
                        private TextField textField;

                        private String originalString = "";

                        @Override
                        public void startEdit() {

                            super.startEdit();
//
//                        if (textField == null) {
//                            createTextField();
//                        }
//                        originalString = String.valueOf(getItem());
//                        setGraphic(textField);
//                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
//                        textField.selectAll();
                        }
//                    @Override
//                    public void cancelEdit() {
//                        super.cancelEdit();
//                        originalString = String.valueOf(getItem());
//                        setText(String.valueOf(getItem()));
//                        setContentDisplay(ContentDisplay.TEXT_ONLY);
//                    }

                        @Override
                        public void updateItem(Object item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item != null) {
                                /**
                                 * Aqui acontece a atualização do objeto Command
                                 * de acordo com o esperado
                                 */
                                //  tabela.getItems().get(getIndex()).setProduct(products.get(item.toString()));
                            }
                        }

                        private void createTextField() {
                            textField = new TextField(getString());
                            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
                            textField.setOnKeyPressed(new EventHandler<KeyEvent>() {

                                @Override
                                public void handle(KeyEvent t) {
                                    if (t.getCode() == KeyCode.ENTER) {
                                        //commitEdit(Integer.parseInt(textField.getText()));
                                        commitEdit((textField.getText()));
                                    } else if (t.getCode() == KeyCode.ESCAPE) {
                                        cancelEdit();
                                    }
                                }
                            });
                        }

                        private String getString() {
                            return getItem() == null ? "" : getItem().toString();
                        }
                    };
                    cell.setAlignment(Pos.CENTER);
                    return cell;

                }

            }
        };

        //////////
        ////////
        this.primaryStage = primaryStage;
        MenuItem menuItemFileOpen = new MenuItem("Open");
        menuFile.getItems().add(menuItemFileOpen);

        MenuItem menuItemFileNew = new MenuItem("New");
        menuFile.getItems().add(menuItemFileNew);

        MenuItem menuItemFileSave = new MenuItem("Save");
        menuFile.getItems().add(menuItemFileSave);

        MenuItem menuItemFileSaveAs = new MenuItem("Save As");
        menuFile.getItems().add(menuItemFileSaveAs);

        MenuItem menuItemFileExit = new MenuItem("Exit");
        menuFile.getItems().add(menuItemFileExit);
        menuItemFileOpen.setOnAction(menuHandler);
        menuItemFileSave.setOnAction(menuHandler);
        menuItemFileSaveAs.setOnAction(menuHandler);
        menuItemFileNew.setOnAction(menuHandler);
        menuItemFileExit.setOnAction(menuHandler);

        // HELP
        MenuItem menuItemHelpAbout = new MenuItem("About");
        menuHelp.getItems().add(menuItemHelpAbout);

        MenuItem menuItemHelpWebsite = new MenuItem("Website");
        menuHelp.getItems().add(menuItemHelpWebsite);

        MenuItem menuItemHelpVideo = new MenuItem("Video");
        menuHelp.getItems().add(menuItemHelpVideo);

        menuItemHelpAbout.setOnAction(menuHandler);
        menuItemHelpWebsite.setOnAction(menuHandler);
        menuItemHelpVideo.setOnAction(menuHandler);

        MenuItem menuItemTheme = new MenuItem("CSS");
        menuTheme.getItems().add(menuItemTheme);
        menuItemTheme.setOnAction(menuHandler);

        //////////////
        MenuItem menuItemConsole = new MenuItem("Clear Console");
        menuConsole.getItems().add(menuItemConsole);
        menuItemConsole.setOnAction(menuHandler);
        /////////////

        //menuBar.getMenus().addAll(menuFile, menuEdit, menuView);
        menuBar.getMenus().addAll(menuFile, menuHelp, menuTheme, menuConsole);

        topContainer.getChildren().add(menuBar);

        toolBar.setStyle("-fx-background-color: transparent;");

        toolBar.setPrefHeight(1);
        toolBarHbox.getChildren().add(toolBar);
        toolBarHbox.setAlignment(Pos.CENTER);
        //  hbox.setPrefHeight(1);
        // hbox.setSpacing(0);
        // toolBar.setMaxHeight(10);

        topContainer.getChildren().add(toolBarHbox);

        BorderPane border = new BorderPane();

        //ImageView iv1 = new ImageView(new Image(new FileInputStream("asset" + File.separator + "start.png")));
        ImageView iv1 = new ImageView(new Image(getClass().getResourceAsStream("/asset/start.png")));
        iv1.setFitHeight(24);
        iv1.setFitWidth(24);
        iv1.setStyle("-fx-background-color: #7ED321;");

        ImageView iv2 = new ImageView(new Image(getClass().getResourceAsStream("/asset/stop.png")));
        // ImageView iv2 = new ImageView(new Image(new FileInputStream("asset" + File.separator + "stop.png")));
        iv2.setFitHeight(24);
        iv2.setFitWidth(24);

        final JFXButton startButton = new JFXButton("", iv1);
        final JFXButton stopButton = new JFXButton("", iv2);

        startButton.setPadding(new Insets(20, 20, 20, 20));
        stopButton.setPadding(new Insets(2, 2, 2, 2));

        //  Image img1 = new Image(new FileInputStream("asset" + File.separator + "start.png"));
        //  final Button startButton = new Button("", new ImageView(img1));
        // final Button stopButton = new Button("", new ImageView(new Image(new FileInputStream("asset" + File.separator + "stop.png"))));
        //stopButton.setMaxWidth(Double.MIN_VALUE);
        //   FileInputStream input = new FileInputStream("asset/arrow_left.png");
        //  Image image = new Image(input);
        //  Image image = new Image(input, 15, 15, false, false);
        // ImageView imageLeft = new ImageView(new Image(new FileInputStream("asset/arrow_left.png"), 15, 15, false, false));
        stopButton.setDisable(true);

        // startButton.setText("Start");
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (syncButton.isSelected()) {
                    workspace.getCurrentSonicPane().getSonicCellSdk().setOrderMode(true);
                } else {
                    workspace.getCurrentSonicPane().getSonicCellSdk().setOrderMode(false);
                }
                // textArea.clear();
                clearListView();
                logPrintCount = 0;
                startButton.setDisable(true);
                stopButton.setDisable(false);
                workspace.getCurrentSonicPane().getSonicCellSdk().start();
                workspace.getCurrentSonicPane().setDisable(true);

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();

                // textArea.appendText("Started at " + dateFormat.format(date) + "\n");
                clearListView();
                apppendTextListView("Started at " + dateFormat.format(date));
            }
        });

        //  stopButton.setText("Stop");
        stopButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                startButton.setDisable(false);
                stopButton.setDisable(true);

                workspace.getCurrentSonicPane().getSonicCellSdk().stops();
                workspace.getCurrentSonicPane().setDisable(false);

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();

                //textArea.appendText("Stopped at " + dateFormat.format(date) + "\n");
                apppendTextListView("Stopped at " + dateFormat.format(date));
                listView.scrollTo(listView.getItems().size() - 1);

            }
        });

        syncButton = new RadioButton();
        syncButton.setToggleGroup(buttonGroup);
        syncButton.setSelected(false);

        ImageView iv3 = new ImageView(new Image(getClass().getResourceAsStream("/asset/infinity.png")));
        // ImageView iv3 = new ImageView(new Image(new FileInputStream("asset" + File.separator + "infinity.png")));

        iv3.setFitHeight(24);
        iv3.setFitWidth(24);
        syncButton.setGraphic(iv3);

        unsyncButton = new RadioButton();
        unsyncButton.setToggleGroup(buttonGroup);
        unsyncButton.setSelected(true);

        ImageView iv4 = new ImageView(new Image(getClass().getResourceAsStream("/asset/one.png")));
        // ImageView iv4 = new ImageView(new Image(new FileInputStream("asset" + File.separator + "one.png")));
        iv4.setFitHeight(24);
        iv4.setFitWidth(24);
        unsyncButton.setGraphic(iv4);

        buttonGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov,
                    Toggle old_toggle, Toggle new_toggle) {
                if (buttonGroup.getSelectedToggle() != null) {

                    //  buttonGroup.getSelectedToggle()
                    if (syncButton.isSelected()) {
                        workspace.getCurrentSonicPane().getSonicCellSdk().setOrderMode(true);
                    } else {
                        workspace.getCurrentSonicPane().getSonicCellSdk().setOrderMode(false);
                    }
                }
            }
        });

        startButton.setPrefSize(20, 20);
        startButton.setMinSize(20, 20);
        startButton.setMaxSize(20, 20);

        stopButton.setPrefSize(20, 20);
        stopButton.setMinSize(20, 20);
        stopButton.setMaxSize(20, 20);
        startButton.setStyle("-fx-background-color: #7ED321;");
        stopButton.setStyle("-fx-background-color: #7ED321;");

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: black;");

        separator.setOrientation(Orientation.VERTICAL);
        // Disable
        if (false) {
            toolBar.getItems().addAll(startButton, stopButton, separator, syncButton, unsyncButton);
        }
        ///////
        // this.loadComponentListJson(params);
        /////////////////////////////////
        tableView.setEditable(true);

//        tableView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
//            final TableHeaderRow header = (TableHeaderRow) tableView.lookup("TableHeaderRow");
//            header.reorderingProperty().addListener((o, oldVal, newVal) -> header.setReordering(false));
//        });
//
//        Platform.runLater(() -> {
//            for (Node header : tableView.lookupAll(".column-header")) {
//                header.addEventFilter(MouseEvent.MOUSE_DRAGGED, Event::consume);
//            }
//        });
//
//        TableHeaderRow header = (TableHeaderRow) tableView.lookup("TableHeaderRow");
//        header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
//            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                header.setReordering(false);
//            }
//        });
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn nameCol = new TableColumn("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<PropertyTableData, String>("Name"));
        // nameCol.setCellFactory(cellFactory);
        nameCol.setSortable(false);

        TableColumn valueCol = new TableColumn("Value");
        valueCol.setCellValueFactory(new PropertyValueFactory<PropertyTableData, String>("Value"));
        valueCol.setCellFactory(cellFactory);
        valueCol.setSortable(false);

        tableView.setItems(tableData);
        tableView.getColumns().addAll(nameCol, valueCol);

        // workspace.getCurrentSonicPane().setTableData(tableData);
        workspace.setTableDataInPool(tableData);
        ////////////

        Map<String, ArrayList<ComponentItem>> componentDBHM = componentDB.getHashMap();

        TreeItem<String> rootItem = new TreeItem<String>("Component");
        rootItem.setExpanded(true);

        Iterator it = componentDBHM.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String categoryName = (String) pair.getKey();
            TreeItem<String> branchItem = new TreeItem<String>(categoryName);
            ArrayList<ComponentItem> componentItemAL = (ArrayList<ComponentItem>) pair.getValue();

            int size = componentItemAL.size();
            for (int i = 0; i < size; i++) {
                TreeItem<String> leaftItem = new TreeItem<String>(componentItemAL.get(i).shortName);

                branchItem.getChildren().add(leaftItem);
            }
            branchItem.setExpanded(true);

            rootItem.getChildren().add(branchItem);
        }

        treeView = new JFXTreeView<String>(rootItem);
        /////////
        TreeItem<String> root = treeView.getRoot();
        //printChildren(root);

        /////////
        SplitPane splitPane = new SplitPane(treeView, tableView);

//                tableView.widthProperty()
//                .addListener((observable, oldValue, newValue) -> yourReusableDisablingMethod(tableView));
        splitPane.setOrientation(Orientation.VERTICAL);

        treeView.setShowRoot(
                false);

        treeView.setCellFactory(
                new Callback<TreeView<String>, TreeCell<String>>() {

            @Override
            public TreeCell<String> call(TreeView<String> stringTreeView
            ) {

                /////////
//                final TreeTableRow<String> row = new TreeTableRow<String>() {
//                    @Override
//                    protected void updateItem(String item, boolean empty) {
//                        super.updateItem(item, empty);
//                        System.out.println("I am selected..." + item);
//                        if (empty) {
//                            this.setTooltip(null);
//                        } else {
//                            /* Tooltip & Context Menu */
//
//                        }
//                    }
//                };
                ////////
                final TreeCell<String> treeCell = new TreeCell<String>() {

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        //setStyle("-fx-text-fill: green; -fx-font-weight:bold;");

                        // setTooltip(new Tooltip("Live is short, make most of it..!"));
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                            return;
                        }
                        if (item != null) {
                            setText(item);
                        }
                    }

                };

                treeCell.setOnDragDetected(new EventHandler<MouseEvent>() {

                    public void handle(MouseEvent event) {

                        if (treeCell.getTreeItem() == null) {
                            return;
                        }

                        if (treeCell.getTreeItem().getChildren() == null) {
                            return;
                        }

                        int childrenSize = treeCell.getTreeItem().getChildren().size();
                        if (childrenSize > 0) {

                            event.consume();
                            return;
                        }

                        String category = treeCell.getTreeItem().getParent().getValue();
                        String shortName = treeCell.getItem();
                        String className = componentDB.getClassName(category, shortName);

                        if (className.equalsIgnoreCase("")) {
                            System.out.println("Its null");
                            event.consume();
                            return;
                        }

                        Dragboard db = treeCell.startDragAndDrop(TransferMode.ANY);
                        db.setDragView(treeCell.snapshot(null, null));
                        ClipboardContent cc = new ClipboardContent();
                        cc.putString(className);
                        db.setContent(cc);
                        event.consume();
                    }
                }
                );

                return treeCell;
            }
        }
        );
if(false){
        treeView.getSelectionModel()
                .selectedItemProperty().addListener(new ChangeListener() {

                    @Override
                    public void changed(ObservableValue observable, Object oldValue,
                            Object newValue
                    ) {

                        if (oldValue != null) {
                            TreeItem<String> selectedItem = (TreeItem<String>) newValue;

                            // System.out.println("New Selected Text : " + selectedItem.getValue());
                        }

                    }

                }
                );
}
        // drawGrid(mainPane);
        //  MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty()
                .bind(primaryStage.widthProperty());
        border.setTop(topContainer);

        border.setAlignment(toolBarHbox, Pos.CENTER);

        treeView.setPrefWidth(
                100);
        splitPane.setDividerPositions(
                0.65);
        border.setLeft(splitPane);

        scrollPane = new ScrollPane();

        treeView.setOnDragDone(new EventHandler<DragEvent>() {

            public void handle(DragEvent event) {
                /* the drag-and-drop gesture ended */

 /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
                    //   tree.setText("");
                }

                event.consume();
            }
        }
        );
        //////////
        //loadTestData();
        workspace.setCurrentSonicPane(workspace.getRootSonicPane());
        scrollPane.setContent(workspace.getCurrentSonicPane());

        //BorderPane SonicPaneBar = new BorderPane();
        //  sonicPaneBar.setTop(sonicPaneCloseButton);
        //  sonicPaneBar.getChildren().remove(sonicPaneCloseButton);
        //   sonicPaneBar.setTop(sonicPaneCloseButton);
        sonicPaneBar.setCenter(scrollPane);

        String style = "-fx-control-inner-background:darkgray; -fx-font-family: Consolas; -fx-highlight-fill: #00ff00; -fx-highlight-text-fill: white; -fx-text-fill: white; ";
        //textArea.setStyle(style);
        listView.setStyle(style);
        String style2 = "-fx-control-inner-background:darkgray; -fx-background-color: #0a0a0a ; -fx-text-fill: white ; ";
        treeView.setStyle(style2);
        tableView.setStyle(style);

        // listView.getSelectionModel().setCellSelectionEnabled(true);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setCellFactory(TextFieldListCell.forListView());
        listView.setEditable(true);

        //SplitPane splitPane2 = new SplitPane(sonicPaneBar, textArea);
        SplitPane splitPane2 = new SplitPane(sonicPaneBar, listView);

        splitPane2.setOrientation(Orientation.VERTICAL);
        splitPane2.setDividerPositions(0.7);

        border.setCenter(splitPane2);
        //border.setCenter(scrollPane);

        //  border.setBottom(textArea);
        //   border.setBottom(splitPane2);
        // setupScrolling();
        mainScene = new Scene(border, 024, 1024);
        //URL url = getClass().getResource("/asset/bootstrap3.css");
        //  Log.v("VT","sss = "+url.toString());

        //modena_dark.css
        //bootstrap3.css
        //File f = new File("asset" + File.separator + "modena_dark.css");
        File f = new File(settingData.cssBackground);
        mainScene.getStylesheets().clear();

        if (f.exists()) {
            mainScene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));
        } else {
            //   mainScene.getStylesheets().add(getClass().getClassLoader().getResource("/asset/dark1.css").toExternalForm());
            mainScene.getStylesheets().add(getClass().getResource("/asset/dark1.css").toExternalForm());

        }

        //  scene.getStylesheets().add(SonicCellFX.class.getResource("/asset/bootstrap3.css").toExternalForm());
        ///////
        // Dropping over surface
//        scene.setOnDragDropped(new EventHandler<DragEvent>() {
//            @Override
//            public void handle(DragEvent event) {
//                Dragboard db = event.getDragboard();
//                boolean success = false;
//                if (db.hasFiles()) {
//                    success = true;
//                    String filePath = null;
//                    for (File file:db.getFiles()) {
//                        filePath = file.getAbsolutePath();
//                        System.out.println(filePath);
//                    }
//                }
//                event.setDropCompleted(success);
//                event.consume();
//            }
//        });
        ///////
        // border.getChildrent().addAll(menuBar);
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);
        primaryStage.setScene(mainScene);

        //new JMetro(JMetro.Style.LIGHT).applyTheme(scene);
        //  primaryStage.minWidthProperty().bind(scene.heightProperty().multiply(2));
        //  primaryStage.minHeightProperty().bind(scene.widthProperty().divide(2));
        primaryStage.setTitle(workspace.getProjectName());

        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/asset/SonicCell.png")));
        // primaryStage.getIcons().add(new Image(new FileInputStream("asset" + File.separator + "SonicCell.png")));

        primaryStage.show();

        createPubSub();

        sonicPaneFunctionBackButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SonicPane thisSonicPane = null;

                int previousId = workspace.getPaneHistory();
                // System.out.println("previousId = " + previousId + "  paneFuncitionId = " + workspace.getCurrentSonicPane().functionIdPane);

                if (workspace.getCurrentSonicPane().functionIdPane == previousId) {

                    // Pop another id from the stack
                    previousId = workspace.getPaneHistoryPeek();

                }

                if (previousId > -1) {

                    thisSonicPane = workspace.findSonicPaneById(previousId);

                }
                if (thisSonicPane != null) {
                    workspace.setCurrentSonicPane(thisSonicPane);
                    workspace.loadPane(workspace.getCurrentSonicPane());
                    scrollPane.setContent(workspace.getCurrentSonicPane());
                    sonicPaneBar.setTop(sonicPaneFunctionBackButton);
                } else {
                    workspace.clearPaneHistory();
                    workspace.setCurrentSonicPane(workspace.getRootSonicPane());

                    scrollPane.setContent(workspace.getRootSonicPane());

                    if (workspace.getRootSonicPane().functionIdPane == -1) {
                        sonicPaneBar.getChildren().remove(sonicPaneFunctionBackButton);
                    } else {
                        sonicPaneBar.setTop(sonicPaneFunctionBackButton);
                    }
                }

            }

        });

    }

//    @SuppressWarnings("restriction")
//    private void yourReusableDisablingMethod(TableView tableView) {
//        TableHeaderRow header = (TableHeaderRow) tableView.lookup("TableHeaderRow");
//        header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
//            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                header.setReordering(false);
//            }
//        });
//    }
    public void setToolBarBackgroundColor(String color) {
        toolBarHbox.setStyle("-fx-background-color: " + color + ";");
    }

    private void printChildren(TreeItem<String> root) {
        System.out.println("Current Parent :" + root.getValue());
        for (TreeItem<String> child : root.getChildren()) {
            if (child.getChildren().isEmpty()) {

                Tooltip t = new Tooltip("A Square......");

                Tooltip.install(child.getGraphic(), t);
            } else {
                printChildren(child);
            }
        }
    }
    private final EventHandler<ActionEvent> menuHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            MenuItem mItem = (MenuItem) event.getSource();
            String side = mItem.getText();
            if ("Open".equalsIgnoreCase(side)) {
                FileChooser fileChooser = new FileChooser();

                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("SonicCell files (*.cell)", "*.cell");
                FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);
                fileChooser.getExtensionFilters().add(extFilter2);
                File selectedFile = fileChooser.showOpenDialog(null);

                if (selectedFile != null) {
                    // currentProjectFile = selectedFile.getAbsolutePath();
                    workspace.loadProjectFile(selectedFile.getAbsolutePath());
                    workspace.currentProjectFile = selectedFile.getAbsolutePath();
                    primaryStage.setTitle(selectedFile.getName());
                    scrollPane.setContent(workspace.getCurrentSonicPane());

                    boolean orderMode = workspace.getCurrentSonicPane().getSonicCellSdk().getOrderMode();
                    syncButton.setSelected(orderMode);

                } else {
                    System.out.println("File selection cancelled.");
                }
            }
            if ("New".equalsIgnoreCase(side)) {

                workspace.setTableDataInPool(tableData);
                workspace.newProject("untitled.cell");

                scrollPane.setContent(workspace.getCurrentSonicPane());
                //  workspace.getCurrentSonicPane().setTableData(tableData);

//                mainPane.getSonicCellSdk().clearAllData();
//                mainPane.clearAllData();
//                Path currentRelativePath = Paths.get(".");
//                String currentPathString = currentRelativePath.toAbsolutePath().toString();
//
//                File file = new File(currentPathString + File.separator + "Untitled.cell");
//                currentProjectFile = file.getAbsolutePath();
//                mainPane.saveProjectFile(currentProjectFile);
//                primaryStage.setTitle(file.getName());
//                fileIsModify = false;
            } else if ("Save".equalsIgnoreCase(side)) {
                File file = new File(workspace.getProjectName());
                if (!file.exists()) {
                    Path currentRelativePath = Paths.get(".");
                    String currentPathString = currentRelativePath.toAbsolutePath().toString();
                    workspace.currentProjectFile = currentPathString + File.pathSeparator + "Untitled.cell";

                }

                workspace.currentProjectFile = file.getAbsolutePath();
                workspace.saveProjectFile(workspace.currentProjectFile);
                primaryStage.setTitle(file.getName());
                workspace.fileIsModify = false;

            } else if ("Save As".equalsIgnoreCase(side)) {
                FileChooser fileChooser = new FileChooser();

                //Set extension filter
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("SonicCell files (*.cell)", "*.cell");
                FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);
                fileChooser.getExtensionFilters().add(extFilter2);

                //fileChooser.setInitialFileName(workspace.getProjectName());
                File file = fileChooser.showSaveDialog(primaryStage);

                if (file != null) {
                    workspace.currentProjectFile = file.getAbsolutePath();
                    //System.out.println("save as = " + workspace.currentProjectFile);
                    workspace.saveProjectFile(workspace.currentProjectFile);
                    primaryStage.setTitle(file.getName());
                    workspace.fileIsModify = false;
                }

            } else if ("Exit".equalsIgnoreCase(side)) {
                System.exit(0);
            } else if ("About".equalsIgnoreCase(side)) {
                Alert alert = new Alert(Alert.AlertType.NONE, version, ButtonType.OK);
                alert.setTitle("About");

                alert.showAndWait();

            } else if ("Video".equalsIgnoreCase(side)) {

            } else if ("Website".equalsIgnoreCase(side)) {

            } else if ("CSS".equalsIgnoreCase(side)) {
                FileChooser fileChooser = new FileChooser();

                FileChooser.ExtensionFilter extentionFilter = new FileChooser.ExtensionFilter("CSS files (*.css)", "*.css");
                fileChooser.getExtensionFilters().add(extentionFilter);
                File dir = new File("asset" + File.separator);
                fileChooser.setInitialDirectory(dir);
                Stage stage = new Stage();
                File selectedFile = fileChooser.showOpenDialog(stage);
                settingData.cssBackground = selectedFile.getAbsolutePath();
                saveSetting();
                File f = new File(settingData.cssBackground);
                mainScene.getStylesheets().clear();
                mainScene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));

            } else if ("Clear Console".equalsIgnoreCase(side)) {
                clearListView();
            }

        }

    };

    private EventHandler<ActionEvent> changeTabPlacement() {
        return new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                MenuItem mItem = (MenuItem) event.getSource();
                String side = mItem.getText();
                if ("left".equalsIgnoreCase(side)) {
                    System.out.println("left");
                } else if ("right".equalsIgnoreCase(side)) {
                    System.out.println("right");
                } else if ("top".equalsIgnoreCase(side)) {
                    System.out.println("top");
                } else if ("bottom".equalsIgnoreCase(side)) {
                    System.out.println("bottom");
                }
            }
        };
    }

    public static void main(String[] args) {

        try {


            launch(args);
        } catch (Exception ex) {
            ex.printStackTrace();

            PubSubSingleton.getIntance().send(PubSubTopic.LOG, ex.getLocalizedMessage());
        }
    }

    private void loadTestData() {

//        mainPane.addComponentByClassName("component.basic.NumberFloatComponent", 0, 1);
//        mainPane.addComponentByClassName("component.basic.NumberFloatComponent", 0, 3);
//        mainPane.addComponentByClassName("component.basic.AddNumberComponent", 1, 2);
//        mainPane.addComponentByClassName("component.basic.Print", 2, 2);
//
//        mainPane.addComponentByClassName("component.basic.TestComponent", 3, 3);
//        mainPane.addComponentByClassName("component.basic.Test4Component", 4, 4);
//        mainPane.addComponentByClassName("component.basic.Test10Component", 5, 5);
//        mainPane.addComponentByClassName("component.basic.TestDoubleComponent", 6, 6);
//        mainPane.addComponentByClassName("component.basic.TestObjectComponent", 5, 2);
//        mainPane.addConnectComponentInOutput(0, 0, 2, 0);
//        mainPane.addConnectComponentInOutput(1, 0, 2, 1);
//        mainPane.addConnectComponentInOutput(2, 0, 3, 0);
        //    mainPane.saveProjectFile("test_no_connect.json");
        //   mainPane.loadProjectFile("test.json");
    }

    private Timeline scrolltimeline = new Timeline();
    private double scrollVelocity = 0;

    boolean dropped;

    //Higher speed value = slower scroll.
    int speed = 200;

    private void setupScrolling() {
        scrolltimeline.setCycleCount(Timeline.INDEFINITE);
        scrolltimeline.getKeyFrames().add(new KeyFrame(Duration.millis(20),
                new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                dragScroll();
            }
        }));

        scrollPane.setOnDragExited(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {

                if (event.getY() > 0) {
                    scrollVelocity = 1.0 / speed;
                } else {
                    scrollVelocity = -1.0 / speed;
                }
                if (!dropped) {
                    scrolltimeline.play();
                }

            }
        });

        scrollPane.setOnDragEntered(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                scrolltimeline.stop();
                dropped = false;
            }
        });
        scrollPane.setOnDragDone(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                System.out.print("test");
                scrolltimeline.stop();
            }
        });

        scrollPane.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                ((VBox) scrollPane.getContent()).getChildren().add(new Label(db.getString()));
                scrolltimeline.stop();
                event.setDropCompleted(true);
                dropped = true;

            }
        });

        scrollPane.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
        });

        scrollPane.setOnScroll(new EventHandler<ScrollEvent>() {
            public void handle(ScrollEvent event) {
                scrolltimeline.stop();
            }
        });

        scrollPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                System.out.println(scrolltimeline.getStatus());

            }
        });

    }

    private void dragScroll() {
        ScrollBar sb = getVerticalScrollbar();
        if (sb != null) {
            double newValue = sb.getValue() + scrollVelocity;
            newValue = Math.min(newValue, 1.0);
            newValue = Math.max(newValue, 0.0);
            sb.setValue(newValue);
        }
    }

    private ScrollBar getVerticalScrollbar() {
        ScrollBar result = null;
        for (Node n : scrollPane.lookupAll(".scroll-bar")) {
            if (n instanceof ScrollBar) {
                ScrollBar bar = (ScrollBar) n;
                if (bar.getOrientation().equals(Orientation.VERTICAL)) {
                    result = bar;
                }
            }
        }
        return result;
    }

    //        Parameters params = getParameters();
//        List<String> list = params.getRaw();
//        for (String each : list) {
//        }
    private String toolBarBackgroundColor = "#7ED321";

    public void loadComponentListJson(Parameters params) {

        List<String> list = params.getRaw();
        //   componentDB = (ComponentDBList) gson.fromJson(ComponentListJson.json, ComponentDBList.class);

        if (list.size() == 0) {
            componentDB = (ComponentDBList) gson.fromJson(ComponentListJson.getDefaultJson(), ComponentDBList.class
            );
        } else {
            String json = list.get(0);
            componentDB
                    = (ComponentDBList) gson.fromJson(json, ComponentDBList.class
                    );
            if (list.size() == 2) {
                String color = list.get(1);
                toolBarBackgroundColor = color;
                this.setToolBarBackgroundColor(color);
            }

        }
    }

}
