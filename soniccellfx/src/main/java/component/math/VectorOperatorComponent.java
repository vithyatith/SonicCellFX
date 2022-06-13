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

public class VectorOperatorComponent extends ComponentAbstraction {

    private float data_out[] = new float[1];
    private boolean readyBool = false;
    private float data_real1[] = new float[1];
    private float data_real2[] = new float[1];
    private String operator = "*";
    private boolean b1 = false;
    private boolean b2 = false;
    private OperatorEnum operatorEnum = OperatorEnum.MULITPLY;

    private enum OperatorEnum {
        ADD,
        SUBSTRACT,
        MULITPLY,
        DIVIDE
    }

    public VectorOperatorComponent() {
        setName("VectorOperator");
        setProperty("operator", operator);
        this.addInput(new DataTypeIO("float[]", "real"));
        this.addInput(new DataTypeIO("float[]", "real"));
        this.addOutput(new DataTypeIO("float[]", "float real"));
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public void handleMessage(int thru, Object obj) {

        if (thru == 0) {
            data_real1 = (float[]) obj;
            b1 = true;
        }

        if (thru == 1) {
            data_real2 = (float[]) obj;
            b2 = true;
        }

        if ((b1 == true) && (b2 == true)) {
            if (data_real1.length == data_real2.length) {
                if (data_out.length != data_real1.length) {
                    data_out = new float[data_real1.length];
                }
                readyBool = true;
            } else {
                printToConsole("Vector dimension doesn't match");
                readyBool = false;
            }

        } else {
            readyBool = false;
        }

    }

    private float v;

    @Override
    public Object onExecute() {
        if (readyBool) {
            readyBool = false;
            b1 = b2 = false;

            int len = data_out.length;
            if (operator.equalsIgnoreCase("*")) {
                for (int i = 0; i < len; i++) {
                    data_out[i] = data_real1[i] * data_real2[i];
                }

            } else if (operator.equalsIgnoreCase("/")) {
                for (int i = 0; i < len; i++) {
                    data_out[i] = data_real1[i] / data_real2[i];
                }
            } else if (operator.equalsIgnoreCase("+")) {
                for (int i = 0; i < len; i++) {
                    data_out[i] = data_real1[i] + data_real2[i];
                }
            } else if (operator.equalsIgnoreCase("-")) {
                for (int i = 0; i < len; i++) {
                    data_out[i] = data_real1[i] - data_real2[i];
                }
            }

            sendData(0,data_out);
        }

        return null;
    }

    @Override
    public void mouseDoubleClick() {

    }

    @Override
    public boolean start() {
        readyBool = false;
        b1 = b2 = false;
        operator = getProperty("operator").toString().trim();
        if (operator.equalsIgnoreCase("*")) {
            operatorEnum = OperatorEnum.MULITPLY;
        } else if (operator.equalsIgnoreCase("/")) {
            operatorEnum = OperatorEnum.DIVIDE;
        } else if (operator.equalsIgnoreCase("+")) {
            operatorEnum = OperatorEnum.ADD;
        } else if (operator.equalsIgnoreCase("-")) {
            operatorEnum = OperatorEnum.SUBSTRACT;
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
        if (key.equalsIgnoreCase("operator")) {
            operator = (String) value;
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
