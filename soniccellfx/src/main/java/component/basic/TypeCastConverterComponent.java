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


import com.google.gson.Gson;
import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.msg.PubSubSingleton;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author anakin
 */
public class TypeCastConverterComponent extends ComponentAbstraction {

    private float dataScalarF;
    private long dataScalarl;
    private double dataScalarD;
    private float dataVectorF[];
    private double dataVectorD[];
    private float dataMatrixF[][];
    private double dataMatrixD[][];
    private byte dataScalarB;
    private byte dataVectorB[];
    private byte dataMatrixB[][];
    private boolean boolValue;

    private StringBuilder sb;
    private String datastr = "";
    private String datastrvec[];
    private String datamat[][];
    private ArrayList<String> arrayList;

    boolean dataInBoolReady = false;
    private String outputType = "all";
    private Gson gson = new Gson();
    private Object objValue = null;

    @Override
    public boolean start() {
        dataInBoolReady = false;
        outputType = getProperty("outputType").toString();
        return true;
    }

    @Override
    public void stop() {

    }

    @Override
    public void mouseDoubleClick() {
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    private enum TypeEnum {
        BYTE,
        BYTEVECTOR,
        BYTEMATRIX,
        FLOAT,
        DOUBLE,
        LONG,
        FLOATVECTOR,
        DOUBLEVECTOR,
        FLOATMATRIX,
        DOUBLEMATRIX,
        STRING,
        STRINGVECTOR,
        STRINGMATRIX,
        ARRAYLIST,
        VECTOR,
        BOOLEAN;
    }
    private TypeEnum typeEnum = TypeEnum.FLOAT;

    public TypeCastConverterComponent() {
        setName("Cast");
        init();
    }

    private void init() {
        sb = new StringBuilder();
        this.setProperty("outputType", outputType);
        this.addInput(new DataTypeIO("all"));
        this.addOutput(new DataTypeIO("all"));
    }

    @Override
    public synchronized Object onExecute() {
        if (dataInBoolReady) {
            dataInBoolReady = false;
            if(objValue!=null){
               sendData(0, objValue);
            }
        }
        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {

        if (obj == null) {
            return;
        }
        
        if(outputType.equalsIgnoreCase("float")){
            if(obj instanceof Float){
                objValue = (float)((float)obj);
            }else if(obj instanceof Integer){
                objValue = (float)((int)obj);
            }else if(obj instanceof Double){
                objValue = (float)((double)obj);
            }else if(obj instanceof Long){
                objValue = (float)((long)obj);
            }else if(obj instanceof Short){
                objValue = (float)((short)obj);
            }else if(obj instanceof Byte){
                objValue = (float)((byte)obj);
            }
            dataInBoolReady = true;
            
        }else if(outputType.equalsIgnoreCase("int")){      
            if(obj instanceof Float){
                objValue = (int)((float)obj);
            }else if(obj instanceof Integer){
                objValue = (int)((int)obj);
            }else if(obj instanceof Double){
                objValue = (int)((double)obj);
            }else if(obj instanceof Long){
                objValue = (int)((long)obj);
            }else if(obj instanceof Short){
                objValue = (int)((short)obj);
            }else if(obj instanceof Byte){
                objValue = (int)((byte)obj);
            }
            dataInBoolReady = true;
            
        }else if(outputType.equalsIgnoreCase("double")){
            if(obj instanceof Float){
                objValue = (double)((float)obj);
            }else if(obj instanceof Integer){
                objValue = (double)((int)obj);
            }else if(obj instanceof Double){
                objValue = (double)((double)obj);
            }else if(obj instanceof Long){
                objValue = (double)((long)obj);
            }else if(obj instanceof Short){
                objValue = (double)((short)obj);
            }else if(obj instanceof Byte){
                objValue = (double)((byte)obj);
            }
            dataInBoolReady = true;
        }else if(outputType.equalsIgnoreCase("short")){
            if(obj instanceof Float){
                objValue = (short)((float)obj);
            }else if(obj instanceof Integer){
                objValue = (short)((int)obj);
            }else if(obj instanceof Double){
                objValue = (short)((double)obj);
            }else if(obj instanceof Long){
                objValue = (short)((long)obj);
            }else if(obj instanceof Short){
                objValue = (short)((short)obj);
            }else if(obj instanceof Byte){
                objValue = (short)((byte)obj);
            }
            dataInBoolReady = true;
        }else if(outputType.equalsIgnoreCase("long")){
            if(obj instanceof Float){
                objValue = (long)((float)obj);
            }else if(obj instanceof Integer){
                objValue = (long)((int)obj);
            }else if(obj instanceof Double){
                objValue = (long)((double)obj);
            }else if(obj instanceof Long){
                objValue = (long)((long)obj);
            }else if(obj instanceof Short){
                objValue = (long)((short)obj);
            }else if(obj instanceof Byte){
                objValue = (long)((byte)obj);
            }
            dataInBoolReady = true;
        }else if(outputType.equalsIgnoreCase("byte")){
            if(obj instanceof Float){
                objValue = (byte)((float)obj);
            }else if(obj instanceof Integer){
                objValue = (byte)((int)obj);
            }else if(obj instanceof Double){
                objValue = (byte)((double)obj);
            }else if(obj instanceof Long){
                objValue = (byte)((long)obj);
            }else if(obj instanceof Short){
                objValue = (byte)((short)obj);
            }else if(obj instanceof Byte){
                objValue = (byte)((byte)obj);
            }
            dataInBoolReady = true;
        }
        else if(outputType.equalsIgnoreCase("float[]")){
            if ((obj instanceof float[])) {
                objValue = obj;
                dataInBoolReady = true;
            }else if (obj instanceof int[]) {
                dataInBoolReady = true;
                
            }
        } else if(outputType.equalsIgnoreCase("all")){
            if ((obj instanceof String)) {
                objValue = obj;
                dataInBoolReady = true;
            }
        }
    }

    @Override
    public void loadProperty(String key, Object value) {
        if (key.equalsIgnoreCase("outputType")) {
            outputType = getProperty("outputType").toString();
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
    @Override
    public String getHelp() {

        String doc = "";
        
        return doc;
    }
}
