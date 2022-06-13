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
package com.fx;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author yada
 */
public class ListViewConsole {

    private Stage stage = new Stage();
    //  private TextArea textArea = new TextArea();
    private final ListView<String> listView = new ListView<String>();
    private int listViewLineCount = 0;
    private int listViewLineMax = 10000;

    private int lastCaretPosition = 0;

    public void clearListView() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                listViewLineCount = 0;
                listView.getItems().clear();
            }
        });
    }

    public void setTitle(String title) {
        stage.setTitle(title);
    }

    public void apppendTextListView(String msg) {

        if (msg == null) {
            return;
        }

        listViewLineCount++;
        if (listViewLineCount > listViewLineMax) {
            clearListView();
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // Update UI here.
                listView.getItems().add(msg);
            }
        });

        //listView.getItems().add(msg);
        if ((listViewLineCount % 100) == 0) {

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    // Update UI here.
                    listView.scrollTo(listView.getItems().size() - 1);
                }
            });

        }
    }

    public void create() {
        String style = "-fx-control-inner-background:darkgray; -fx-font-family: Consolas; -fx-highlight-fill: #00ff00; -fx-highlight-text-fill: white; -fx-text-fill: white; ";

        listView.setStyle(style);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setCellFactory(TextFieldListCell.forListView());
        listView.setEditable(true);

        VBox vbox = new VBox(listView);

        Scene scene = new Scene(vbox, 400, 100);
        stage.setScene(scene);
        //stage.show();
        stage.setAlwaysOnTop(true);

    }

    public void show() {
        stage.show();
    }

    public void hide() {
        stage.hide();
    }

}
