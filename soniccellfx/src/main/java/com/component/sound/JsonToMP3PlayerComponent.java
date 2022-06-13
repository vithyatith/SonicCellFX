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
package com.component.sound;

import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.Log;
import com.sonicmsgr.soniccell.msg.PubSubSingleton;
import com.sonicmsgr.util.JsonUtil;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javazoom.jl.player.Player;

/**
 *
 * @author yada
 */
public class JsonToMP3PlayerComponent extends ComponentAbstraction {

    private String savePath = "path";
    private boolean processingBool = false;
    private String data = "";
    private Player playMP3 = null;

    private Mp3AudioFrame mp3AudioFrame = null;
    private String filename = "";
    private String fileNameOnly = "";

    private CallBack callBack = new CallBack() {
        @Override
        public boolean onPlay() {

            try {
                Thread th = new Thread() {

                    public void run() {
                        FileInputStream fis = null;
                        try {

                            File file = new File(filename);
                            if (!file.exists()) {
                                Log.v("VT", "Cannot play mp3. File doesn't exist");
                            }
                            if (filename.toLowerCase().indexOf(".mp3") < 0) {
                                Log.v("VT", "Can not playe. Not mp3 file");
                            }

                            fileNameOnly = file.getName();
                            mp3AudioFrame.setTitle("MP3: " + fileNameOnly);
                            fis = new FileInputStream(filename);
                            playMP3 = new Player(fis);
                            playMP3.play();
                            fis.close();
                        } catch (Exception ex) {

                        }

                    }
                };

                th.start();

                return true;
            } catch (Exception ex) {

            }
            return false;
        }

        @Override
        public boolean onStop() {

            if (playMP3 != null) {
                Thread th = new Thread() {
                    public void run() {
                        try {
                            System.out.println("close");
                            playMP3.close();

                        } catch (Exception ex) {

                        }
                    }
                };

                th.start();
                return true;
            }

            return false;
        }

    };
//
//    private void play() {
//        try {
//
//            FileInputStream fis = new FileInputStream("/Users/yada/sample.mp3");
//            Player playMP3 = new Player(fis);
//
//            playMP3.play();
//
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//    }

    public JsonToMP3PlayerComponent() {
        setName("Json2Mp3");
        this.setProperty("savePath", savePath);
        this.addInput(new DataTypeIO("string", "json"));
        this.addOutput(new DataTypeIO("boolean", "Success or Fail"));
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public Object onExecute() {

        if (processingBool) {
            processingBool = false;
            try {
                filename = JsonUtil.jsonToFile(data, savePath);
                sendData(0, true);
                if (filename != null) {
                    // imagePopupWindowShow(filename);
                    mp3AudioFrame.setVisible(true);
                    callBack.onPlay();

                }
            } catch (Exception e) {
                PubSubSingleton.getIntance().send("Print", e.getMessage());
            }
        }

        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {

        if (thru == 0) {
            data = (String) obj;
            processingBool = true;
        }
    }

    @Override
    public boolean start() {
        savePath = getProperty("savePath").toString();
        File file = new File(savePath);
        if (!file.exists()) {
            boolean b = file.mkdir();
            System.out.println("File creating for " + savePath + " is " + b);
        }
        processingBool = false;

        if (mp3AudioFrame != null) {
            mp3AudioFrame.setVisible(false);
        }

        mp3AudioFrame = new Mp3AudioFrame(callBack);
        mp3AudioFrame.setVisible(false);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        mp3AudioFrame.setLocation(dim.width / 2 - mp3AudioFrame.getSize().width / 2, dim.height / 2 - mp3AudioFrame.getSize().height / 2);

        return true;
    }

    @Override
    public void stop() {
        processingBool = false;
        if (callBack != null) {
            callBack.onStop();
        }
    }

    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("savePath")) {
            savePath = (String) val;
        }
    }

    @Override
    public void mouseDoubleClick() {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new Mp3AudioFrame();
//            }
//        });
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
    @Override
    public String getHelp() {

        String doc = "";
        
        return doc;
    }
    private void imagePopupWindowShow(String imagePath) {

        Platform.runLater(new Runnable() {

            @Override
            public void run() {

//                try {
//                    // get the sound file as a resource out of my jar file;
//                    // the sound file must be in the same directory as this class file.
//                    // the input stream portion of this recipe comes from a javaworld.com article.
//                    InputStream inputStream = getClass().getResourceAsStream("");
//                    AudioStream audioStream = new AudioStream(inputStream);
//                    AudioPlayer.player.start(audioStream);
//                } catch (Exception e) {
//                    // a special way i'm handling logging in this application
//                  //  if (debugFileWriter != null) {
//                        e.printStackTrace(debugFileWriter);
//                  //  }
//                }
            }
        });

    }

    public class Mp3AudioFrame extends javax.swing.JFrame {

        private CallBack callBack = null;

        public Mp3AudioFrame(CallBack callBack) {
            this.callBack = callBack;
            initComponents();
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
        }

        public void setPlay(boolean setToPlay) {
            if (setToPlay) {
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
            } else {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            }
        }

        private void initComponents() {

            startButton = new javax.swing.JButton();
            stopButton = new javax.swing.JButton();

            //  setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
            startButton.setText("Play");
            startButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (callBack != null) {
                        setPlay(true);
                        callBack.onPlay();

                    }
                }
            });

            stopButton.setText("Stop");
            stopButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if (callBack != null) {
                        setPlay(false);
                        callBack.onStop();

                    }
                }
            });

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addComponent(startButton)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(stopButton))
            );
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(startButton)
                                            .addComponent(stopButton))
                                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            pack();
        }

        private javax.swing.JButton startButton;
        private javax.swing.JButton stopButton;

    }

    interface CallBack {

        boolean onPlay();

        boolean onStop();
    }
}
