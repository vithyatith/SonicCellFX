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
package component.math;

import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.DataTypeIOList;
import java.util.*;

import org.apache.commons.math3.random.RandomData;
import org.apache.commons.math3.random.RandomDataImpl;

/**
 *
 * @author Administrator
 */
public class GaussianComponent extends ComponentAbstraction {

    private HashMap hashProp = new HashMap();
    private int size = 1024;
    private float mean = 1;
    private float std = 1;
    private float data[] = new float[1];
    private RandomData randomData;

    private int nTime = 1;
    private boolean startBool = true;

    public GaussianComponent() {
        setName("Gaussian");
        setProperty("size", Integer.toString(size));
        setProperty("mean", Float.toString(mean));
        setProperty("std", Float.toString(std));
        setProperty("nTime", nTime);

//        DataTypeIOList dt = new DataTypeIOList();
//        dt.addDataType(new DataTypeIO("double[]"));
//        dt.addDataType(new DataTypeIO("float[]"));
//        dt.addDataType(new DataTypeIO("short[]"));
//        this.addOutput(dt);
        this.addOutput(new DataTypeIO("float[]"));
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public void handleMessage(int thru, Object obj) {

    }

    @Override
    public Object onExecute() {
        try {
            if (nTime > 0) {
                for (int k = 0; k < nTime; k++) {
                    if (!startBool) {
                        break;
                    }
                    for (int i = 0; i < size; i++) {
                        data[i] = (float) randomData.nextGaussian(mean, std);
                    }
                    sendData(0, data);
                }
            } else {

                while (startBool) {
                    for (int i = 0; i < size; i++) {
                        data[i] = (float) randomData.nextGaussian(mean, std);
                    }
                    sendData(0, data);
                }

            }

        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void mouseDoubleClick() {

    }

    @Override
    public boolean start() {

        size = (int) Double.parseDouble(getProperty("size").toString());
        nTime = (int) Double.parseDouble(getProperty("nTime").toString());
        mean = (float) Double.parseDouble(getProperty("mean").toString());
        std = (float) Double.parseDouble(getProperty("std").toString());

        if (data.length != size) {
            data = new float[size];
        }
        randomData = new RandomDataImpl();

        if (nTime < 1) {
            // nTime = 1;
        }
        startBool = true;

        return true;

    }

    @Override
    public void stop() {
        startBool = false;
    }

    @Override
    public void onPropertyChanged(String key, Object value) {

        if (key.equalsIgnoreCase("size")) {
            size = (int) Double.parseDouble(getProperty("size").toString());

            if (data.length != size) {
                data = new float[size];
            }
        } else if (key.equalsIgnoreCase("mean")) {
            mean = (float) Double.parseDouble(getProperty("mean").toString());
        } else if (key.equalsIgnoreCase("std")) {
            std = (float) Double.parseDouble(getProperty("std").toString());
        } else if (key.equalsIgnoreCase("nTime")) {
            nTime = (int) Double.parseDouble(getProperty("nTime").toString());
        }

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

    }

    @Override
    public String getHelp() {

        String doc = "";

        return doc;
    }
}
