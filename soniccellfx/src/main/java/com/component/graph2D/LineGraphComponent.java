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
import com.sonicmsgr.soniccell.msg.PubSubSingleton;
import com.sonicmsgr.util.JsonUtil;
import java.io.File;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;
//import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author yada
 */
public class LineGraphComponent extends ComponentAbstraction {

    private String savePath = "path";
    private boolean processingBool = false;
    //  private String data = "";
    private float data[] = new float[1024];

    public LineGraphComponent() {
        setName("LineGraph");
        this.setProperty("savePath", savePath);
        this.addInput(new DataTypeIO("float[]", "data"));
        //  this.addOutput(new DataTypeIO("boolean", "Success or Fail"));
    }

    @Override
    public Object onExecute() {

        if (processingBool) {
            processingBool = false;
            plot(data);
//            try {
//                String filename = JsonUtil.jsonToFile(data, savePath);
//                sendData(0, true);
//                if (filename != null) {
//                    imagePopupWindowShow(filename);
//                }
//            } catch (Exception e) {
//                PubSubSingleton.getIntance().send("Print", e.getMessage());
//            }
        }

        return null;
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public void handleMessage(int thru, Object obj) {
        if (thru == 0) {
            data = (float[]) obj;
            processingBool = true;
        }
    }

    @Override
    public boolean start() {
        lineChart.setAnimated(false);

        plot(data);
        processingBool = false;
        return true;
    }

    @Override
    public void stop() {
        processingBool = false;
    }

    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("savePath")) {
            savePath = (String) val;
        }
    }

    private Stage stage = null;
    private XYChart.Series series = new XYChart.Series();
    private NumberAxis xAxis = new NumberAxis();
    private NumberAxis yAxis = new NumberAxis();

    private LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);

    public void setData(float f[]) {
        int len = f.length;
        int len2 = series.getData().size();
        if (len != len2) {
            series.getData().clear();
            for (int i = 0; i < len; i++) {
                series.getData().add(new XYChart.Data(i, f[i]));
            }
        } else {
            for (int i = 0; i < len; i++) {
                Data d = (Data) series.getData().get(i);
                d.setXValue(i);
                d.setYValue(f[i]);
            }
        }
    }

    private void plot(float f[]) {
        if (stage == null) {

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (stage == null) {
                        stage = new Stage();

                        stage.setTitle("XY Plot");
                        lineChart.setTitle("Stock Monitoring, 2010");
                        series.setName("My portfolio");
                        Scene scene = new Scene(lineChart, 800, 600);
                       // lineChart.getData().removeAll(series);
                        setData(f);
                        lineChart.getData().setAll(series);
                        stage.setScene(scene);

                    }

                }
            });
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                   // lineChart.getData().removeAll(series);
                    setData(f);
                    lineChart.getData().setAll(series);
                }
            });

        }

    }

    @Override
    public void mouseDoubleClick() {
        if (stage != null) {
            stage.show();
        } else {
            plot(data);
        }
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

    private void imagePopupWindowShow(String imagePath) {

    }
        @Override
    public String getHelp() {

        String doc = "";
        
        return doc;
    }
}
