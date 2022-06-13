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

import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import java.util.ArrayList;
import javafx.application.Platform;
//import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.swing.SwingUtilities;

/**
 *
 * @author yada
 */
public class TextConsoleFX extends ComponentAbstraction {
    
    private String title = "TxtConsole";

    public TextConsoleFX() {
        setName("TxtConsole");
        this.setProperty("title", title);
        this.addOutput(new DataTypeIO("string"));
        
        this.setHasUISupport(true);
    }

    @Override
    public Object onExecute() {

        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {

    }

    @Override
    public boolean start() {
        
        

        if (chatConsole != null) {
            chatConsole.reset();
        }

        return true;
    }

    @Override
    public void stop() {

    }

    @Override
    public void loadProperty(String key, Object value) {

    }

    @Override
    public void mouseDoubleClick() {

        showUI();
    }

    @Override
    public int getPlatformSupport() {

        return 1;
    }

    @Override
    public void onPropertyChanged(String key, Object value) {

    }

    private ChatConsole chatConsole = null;

    @Override
    public String getHelp() {

        String doc = "";

        return doc;
    }

    private class ChatConsole {

        private Stage stage = new Stage();
        private ConsoleTextArea consoleTextArea = new ConsoleTextArea();
        private CallBack callBack = null;

        public ChatConsole(CallBack cb) {
            this.callBack = cb;
        }

        public void reset() {

            consoleTextArea.reset();
            consoleTextArea.setText(">>");
            consoleTextArea.positionCaret(2);
        }

        public void create() {
            consoleTextArea = new ConsoleTextArea();
            consoleTextArea.setCallBack(callBack);
            consoleTextArea.setText(">>");
            consoleTextArea.positionCaret(2);
            consoleTextArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent keyEvent) {
                    if (keyEvent.getCode() == KeyCode.BACK_SPACE) {

                        int currentPos = consoleTextArea.getCaretPosition() - 3;

                        if (currentPos <= consoleTextArea.getLastPostion()) {
                            keyEvent.consume();
                        }

                        //  System.out.println(consoleTextArea.getLastPostion() + " : " + currentPos);
                    } else if (keyEvent.getCode() == KeyCode.UP) {

                        String s = consoleTextArea.getNextHistoryValue();

                        // console.replaceText(console.getLastPostion(), 0, s);
                        keyEvent.consume();

                    }
                }
            });

            VBox vbox = new VBox(consoleTextArea);

            Scene scene = new Scene(vbox, 200, 100);
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

    public static void main(String args[]) {
        TextConsoleFX gui = new TextConsoleFX();
        gui.showUI();
    }

    public void showUI() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
             //   new JFXPanel(); // this will prepare JavaFX toolkit and environment
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (chatConsole == null) {
                                chatConsole = new ChatConsole(new CallBack() {
                                    @Override
                                    public void onReceived(String data) {
                                        sendData(0, data);
                                    }
                                });
                                chatConsole.create();
                            }

                            chatConsole.show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
// ...
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        if (chatConsole != null) {
            chatConsole.hide();
        }
        chatConsole = null;
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    public class ConsoleTextArea extends TextArea {

        private int lastPosition = -1;
        private ArrayList<String> al = new ArrayList<String>();
        private int historyPos = 0;
        private boolean pasteBool = false;
        private CallBack callBack = null;

        public void setCallBack(CallBack callBack) {
            this.callBack = callBack;
        }

        public int getLastPostion() {
            return lastPosition;
        }
        public void reset(){
            lastPosition = -1;
            historyPos = 0;
            al.clear();
        }

        public String getNextHistoryValue() {
            if (al.size() < 1) {
                return "";
            }
            System.out.println("historyPos = " + historyPos);

            if (historyPos < 0) {
                historyPos = al.size() - 1;
            }

            String s = al.get(historyPos);;
            historyPos = historyPos - 1;
            return s;
        }

        @Override
        public void paste() {
            pasteBool = true;
            System.out.println("paste detected");
            super.paste();

        }

        @Override
        public void replaceText(int start, int end, String text) {
            String current = getText();
            // only insert if no new lines after insert position:
            if (!current.substring(start).contains("\n")) {

                super.replaceText(start, end, text);
            }
        }

        @Override
        public void replaceSelection(String text) {
            String current = getText();
            int selectionStart = getSelection().getStart();
            if (!current.substring(selectionStart).contains("\n")) {
                int range = lastPosition + 3;

                String s = current.substring(range).trim();
                int len = s.length();
                lastPosition = current.length();
                if (len > 0) {
                    al.add(s);

                    if (callBack != null) {
                        System.out.println(getCaretPosition() + ":" + s);
                        callBack.onReceived(s);
                    } else {
                        System.out.println("callback is null");
                    }
                    historyPos = al.size() - 1;
                }
                String str = null;

                if (pasteBool) {
                    str = text;
                    lastPosition = lastPosition - 3;
                } else {
                    str = text + ">>";
                }
                pasteBool = false;
                super.replaceSelection(str);

            }
        }

    }

    interface CallBack {

        void onReceived(String data);
    }
}
