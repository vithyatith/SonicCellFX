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
package component.basic;

import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import java.util.HashMap;

/**
 *
 * @author yada
 */
public class MathOperatorCom extends ComponentAbstraction {

    private HashMap hashMap = new HashMap();
    private float result = 0f;
    private float f1;
    private float f2;
    private boolean b1 = false;
    private boolean b2 = false;
    private String operator = "add";
    private int operatorInt = 0;  //0 for add, 1 for -, 2 for multiply, 3 for divide

    public MathOperatorCom() {
        setName("add");
        this.setProperty("operator", operator);
        this.addInput(new DataTypeIO("float"));
        this.addInput(new DataTypeIO("float"));
        this.addOutput(new DataTypeIO("float"));
    }

    @Override
    public Object onExecute() {
        if ((b1 == true) && (b2 == true)) {
            if (operatorInt == 0) {
                result = f1 + f2;
            } else if (operatorInt == 1) {
                result = f1 - f2;
            } else if (operatorInt == 2) {
                result = f1 * f2;
            } else if (operatorInt == 3) {
                result = f1 / f2;
            }

            sendData(0, result);
            b1 = false;
            b2 = false;
            return result;
        }
        return null;
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public void handleMessage(int thru, Object obj) {

        if (thru == 0) {
            f1 = (Float) obj;
            b1 = true;
        }

        if (thru == 1) {
            f2 = (Float) obj;
            b2 = true;
        }

    }

    @Override
    public boolean start() {
        operator = getProperty("operator").toString();
        updateOperator();

        b1 = false;
        b2 = false;
        result = 0;
        return true;
    }

    @Override
    public void stop() {

    }

    private void updateOperator() {
        if (operator.equalsIgnoreCase("add")) {
            operatorInt = 0;
            operator = "add";
            this.setName("add");
        } else if (operator.equalsIgnoreCase("substract")) {
            operatorInt = 1;
            operator = "substract";
            this.setName("substract");
        } else if (operator.equalsIgnoreCase("multiply")) {
            operatorInt = 2;
            operator = "multiply";
            this.setName("multiply");
        } else if (operator.equalsIgnoreCase("divide")) {
            operatorInt = 3;
            operator = "divide";
            this.setName("divide");
        } else {
            operator = "add";
            this.setName("add");
            operatorInt = 0;
        }
    }

    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("operator")) {
            operator = (String) val;
            updateOperator();
        }
    }

    @Override
    public void mouseDoubleClick() {
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
}
