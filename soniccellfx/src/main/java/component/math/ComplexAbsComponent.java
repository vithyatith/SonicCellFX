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

//import com.sonicmsgr.soniccell.ComponentAbstraction;
//import com.sonicmsgr.soniccell.DataTypeIO;
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
public class ComplexAbsComponent extends ComponentAbstraction {

    private float data_complex[] = new float[1];
    private boolean readyBool = false;

    private float data_real_mag[] = new float[1];

    public ComplexAbsComponent() {
        setName("CmplxAbs");
        //   setProperty("size", Integer.toString(size));

        this.addInput(new DataTypeIO("float[]", "float complex"));
         this.addOutput(new DataTypeIO("float[]", "float real"));
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public void handleMessage(int thru, Object obj) {

        data_complex = (float[]) obj;
        int len = data_complex.length / 2;
        if (data_real_mag.length != len) {
            data_real_mag = new float[len];
        }
        readyBool = true;
        
    }

    private float v;

    @Override
    public Object onExecute() {
   
        if (readyBool) {
            readyBool = false;
            int len = data_real_mag.length;
            for (int i = 0; i < len; i++) {

                v = (float) Math.sqrt(data_complex[i * 2]
                        * data_complex[i * 2]
                        + (data_complex[i * 2 + 1] * data_complex[i * 2 + 1]))
                        / (len);

                data_real_mag[i] = v;
            }
            sendData(0,data_real_mag);
 
        }

        return null;
    }

    @Override
    public void mouseDoubleClick() {

    }

    @Override
    public boolean start() {
        readyBool = false;

        //  size = (int) Double.parseDouble(getProperty("size").toString());
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

    }

    @Override
    public String getHelp() {

        String doc = "";

        return doc;
    }
}
