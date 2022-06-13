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
package com.component.graph2D;

import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import javafx.application.Platform;

/**
 *
 * @author yada
 */
public class Graph2DComponent extends ComponentAbstraction {

    private boolean readyBool = false;
    private float data[] = new float[1024];

    private Graph2DFrame gui;

    public Graph2DComponent() {
        setName("LineGraph");
        //  this.setProperty("savePath", savePath);
        this.addInput(new DataTypeIO("float[]", "data"));
        this.setHasUISupport(true);
        gui = new Graph2DFrame();
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public Object onExecute() {
        if (readyBool) {
            readyBool = false;
            gui.getGraphPane().setData(data);
        }
        return null;
    }

    @Override
    public void mouseDoubleClick() {
        if (gui != null) {
            gui.setVisible(true);
        } else {
            gui = new Graph2DFrame();
        }
    }

    @Override
    public void handleMessage(int thruId, Object obj) {
        if (thruId == 0) {
            data = (float[]) obj;
            readyBool = true;
        }
    }

    @Override
    public boolean start() {

        if (gui == null) {
            gui = new Graph2DFrame();
        }

        return true;
    }

    @Override
    public void stop() {

    }

    @Override
    public void onPropertyChanged(String key, Object value) {

    }

    @Override
    public void loadProperty(String key, Object value) {

    }

    @Override
    public int getPlatformSupport() {

        return 0;
    }

    @Override
    public void onDestroy() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (gui != null) {
                     gui.hide();
                    gui.removeAll();
                   
                   // gui.dispose();
                    gui = null;
                }
            }
        });

    }

    @Override
    public String getHelp() {

        return "";
    }

}
