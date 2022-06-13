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
import java.util.List;

/**
 *
 * @author yada
 */
public class ListArrayComponent extends ComponentAbstraction {

    private HashMap hashMap = new HashMap();
    private float result = 0f;
    private float f1;
    private float f2;
    private boolean b1 = false;
    private boolean b2 = false;
    private String type = "string";
    private int operatorInt = 0;  //0 for add, 1 for -, 2 for multiply, 3 for divide

    public ListArrayComponent() {
        setName("List");
        this.setProperty("type", type);
        this.addInput(new DataTypeIO("List"));
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

        if(obj instanceof List<?>){
            
        }
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
        type = getProperty("type").toString();
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
        if (type.equalsIgnoreCase("add")) {
            operatorInt = 0;
            type = "add";
            this.setName("add");
        } else if (type.equalsIgnoreCase("substract")) {
            operatorInt = 1;
            type = "substract";
            this.setName("substract");
        } else if (type.equalsIgnoreCase("multiply")) {
            operatorInt = 2;
            type = "multiply";
            this.setName("multiply");
        } else if (type.equalsIgnoreCase("divide")) {
            operatorInt = 3;
            type = "divide";
            this.setName("divide");
        } else {
            type = "add";
            this.setName("add");
            operatorInt = 0;
        }
    }

    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("operator")) {
            type = (String) val;
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
