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

import com.sonicmsgr.signalprocessing.SonicMsgrFFT;
import com.sonicmsgr.signalprocessing.Windowing;
import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.util.MathUtil;
import org.apache.commons.math3.util.MathUtils;

/**
 *
 * @author Administrator
 */
public class AverageComponent extends ComponentAbstraction {

    private float[] data_real = new float[1];
    private float[] data_real_tmp = new float[1];

    private int average = 1;
    private int trackAvg = 0;

    private boolean readyBool = false;
    
    private float avergeMatrix[][] = null;

    public AverageComponent() {
        setName("Avg");
        setProperty("average", average);
        this.addInput(new DataTypeIO("float[]", "Real float data "));
        this.addOutput(new DataTypeIO("float[]", "Average"));
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public void handleMessage(int thru, Object obj) {

        float refData[] = (float[]) obj;
        int len = refData.length;

        if (len != data_real.length) {
            data_real = new float[len];
            data_real_tmp = new float[len];
            trackAvg = 0;
        }

        if (average < 2) {
            for (int i = 0; i < len; i++) {
                data_real[i] = refData[i];
            }
            readyBool = true;
        } else {
            for (int i = 0; i < len; i++) {
                data_real_tmp[i] = refData[i];
                data_real[i] = data_real[i] + data_real_tmp[i];
            }
            trackAvg++;
            if (trackAvg >= average) {
                for (int i = 0; i < len; i++) {
                    data_real[i] = data_real[i] / average;
                }

                readyBool = true;
                trackAvg = 0;
            }

        }
    }

    @Override
    public Object onExecute() {
        if (readyBool) {
            readyBool = false;
            sendData(0, data_real);
        }
        return null;
    }

    @Override
    public void mouseDoubleClick() {

    }

    @Override
    public boolean start() {
        readyBool = false;
        average = (int) Double.parseDouble(getProperty("average").toString());
        trackAvg = 0;

        // Zero data
        for (int i = 0; i < data_real.length; i++) {
            data_real[i] = 0;
            data_real_tmp[i] = 0;
        }

        return true;
    }

    @Override
    public void stop() {

    }

    @Override
    public void onPropertyChanged(String key, Object value) {
        loadProperty(key, value);
    }

    @Override
    public void loadProperty(String key, Object value) {
        if (key.equalsIgnoreCase("average")) {
            average = (int) Double.parseDouble(getProperty("average").toString());
        }
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
