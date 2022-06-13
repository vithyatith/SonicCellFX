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
package component.string;


import com.google.gson.Gson;
import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;

/**
 *
 * @author yada
 */
public class StringToTypeConverterComponent extends ComponentAbstraction {

//    private byte inputDataB[];
//    private short inputDataS[];
//    private int inputDataI[];
//    private float inputDataF[];
//    private double inputDataD[];
//    private long inputDataL[];

    private byte outputDataB[] = new byte[1];
    private short outputDataS[] = new short[1];
    private int outputDataI[] = new int[1];
    private float outputDataF[] = new float[1];
    private double outputDataD[] = new double[1];
    private long outputDataL[] = new long[1];
    private String stringData = "";

    private boolean readyBool = false;
 //   private String endian = "little";
    private String delimiter = ",";
    private String inputType = "float[]";
    private String outputType = "byte[]";
    private TypeEnum inputTypeEnum = TypeEnum.FLOAT;
    private TypeEnum outputTypeEnum = TypeEnum.BYTE;

    private Gson gson = new Gson();
    private String data = "";
    private enum TypeEnum {
        BYTE,
        SHORT,
        INTEGER,
        FLOAT,
        DOUBLE,
        LONG,
        STRING,
        BYTE_SINGLE,
        SHORT_SINGLE,
        INTEGER_SINGLE,
        FLOAT_SINGLE,
        DOUBLE_SINGLE,
        LONG_SINGLE
    }

    public StringToTypeConverterComponent() {
        setName("StrConverter");
        this.setProperty("delimiter", delimiter);
        this.setProperty("data", data);
        this.setProperty("outputType", outputType);
        this.addInput(new DataTypeIO("string", "data", "data"));
        this.addOutput(new DataTypeIO("byte[]"));
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    
    @Override
    public Object onExecute() {
        if (readyBool) {
            readyBool = false;
            try {
                if (outputTypeEnum == TypeEnum.BYTE) {
                    String tmp[] = data.split(delimiter);
                    int len = tmp.length;
                    if (outputDataB.length != len) {
                        outputDataB = new byte[len];
                    }
                    for (int i = 0; i < len; i++) {
                        outputDataB[i] = Byte.parseByte(tmp[i]);
                    }
                    sendData(0, outputDataB);

                } else if (outputTypeEnum == TypeEnum.SHORT) {
                    String tmp[] = data.split(delimiter);
                    int len = tmp.length;
                    if (outputDataS.length != len) {
                        outputDataS = new short[len];
                    }
                    for (int i = 0; i < len; i++) {
                        outputDataS[i] = Short.parseShort(tmp[i]);
                    }
                    sendData(0, outputDataS);
                } else if (outputTypeEnum == TypeEnum.INTEGER) {
                    String tmp[] = data.split(delimiter);
                    int len = tmp.length;
                    if (outputDataI.length != len) {
                        outputDataI = new int[len];
                    }
                    for (int i = 0; i < len; i++) {
                        outputDataI[i] = Integer.parseInt(tmp[i]);
                    }
                    sendData(0, outputDataI);
                } else if (outputTypeEnum == TypeEnum.FLOAT) {
                    String tmp[] = data.split(delimiter);
                    int len = tmp.length;
                    if (outputDataF.length != len) {
                        outputDataF = new float[len];
                    }
                    for (int i = 0; i < len; i++) {
                        outputDataF[i] = Float.parseFloat(tmp[i]);
                    }
                    sendData(0, outputDataF);
                } else if (outputTypeEnum == TypeEnum.LONG) {
                    String tmp[] = data.split(delimiter);
                    int len = tmp.length;
                    if (outputDataL.length != len) {
                        outputDataL = new long[len];
                    }
                    for (int i = 0; i < len; i++) {
                        outputDataL[i] = Long.parseLong(tmp[i]);
                    }
                    sendData(0, outputDataL);
                } else if (outputTypeEnum == TypeEnum.DOUBLE) {
                    String tmp[] = data.split(delimiter);
                    int len = tmp.length;
                    if (outputDataD.length != len) {
                        outputDataD = new double[len];
                    }
                    for (int i = 0; i < len; i++) {
                        outputDataD[i] = Double.parseDouble(tmp[i]);
                    }
                    sendData(0, outputDataD);
                } else if (outputTypeEnum == TypeEnum.BYTE_SINGLE) {
                    data =  data.trim();
                    sendData(0,Byte.parseByte(data));
                } else if (outputTypeEnum == TypeEnum.SHORT_SINGLE) {
                    data =  data.trim();
                    sendData(0,Short.parseShort(data));
                } else if (outputTypeEnum == TypeEnum.INTEGER_SINGLE) {
                    data =  data.trim();
                    sendData(0,Integer.parseInt(data));
                } else if (outputTypeEnum == TypeEnum.FLOAT_SINGLE) {
                    data =  data.trim();
                    sendData(0,Float.parseFloat(data));
                } else if (outputTypeEnum == TypeEnum.LONG_SINGLE) {
                    data =  data.trim();
                    sendData(0,Long.parseLong(data));
                } else if (outputTypeEnum == TypeEnum.DOUBLE_SINGLE) {
                    data =  data.trim();
                    sendData(0,Double.parseDouble(data));
                }
            } catch (NumberFormatException e) {
                sendError(e.toString());
            } catch (Exception e) {
                sendError(e.toString());
            }
        }

        return null;
    }

    @Override
    public void mouseDoubleClick() {

    }

    @Override
    public void handleMessage(int thru, Object obj) {
        readyBool = true;
        data = (String) obj;
    }

    @Override
    public boolean start() {

        outputType = getProperty("outputType").toString();
        data = getProperty("data").toString();
        delimiter = getProperty("delimiter").toString();

        updateOutputType();

        if (data.trim().equalsIgnoreCase("")) {
            readyBool = false;
        } else {
            readyBool = true;
        }

        return true;
    }

    @Override
    public void stop() {

    }

    private void updateOutputType() {
        if (outputType.equalsIgnoreCase("byte[]")) {
            outputTypeEnum = TypeEnum.BYTE;
        } else if (outputType.equalsIgnoreCase("short[]")) {
            outputTypeEnum = TypeEnum.SHORT;
        } else if (outputType.equalsIgnoreCase("int[]")) {
            outputTypeEnum = TypeEnum.INTEGER;
        } else if (outputType.equalsIgnoreCase("float[]")) {
            outputTypeEnum = TypeEnum.FLOAT;
        } else if (outputType.equalsIgnoreCase("double[]")) {
            outputTypeEnum = TypeEnum.DOUBLE;
        } else if (outputType.equalsIgnoreCase("long[]")) {
            outputTypeEnum = TypeEnum.LONG;
        } else if (outputType.equalsIgnoreCase("byte")) {
            outputTypeEnum = TypeEnum.BYTE_SINGLE;
        } else if (outputType.equalsIgnoreCase("short")) {
            outputTypeEnum = TypeEnum.SHORT_SINGLE;
        } else if (outputType.equalsIgnoreCase("int")) {
            outputTypeEnum = TypeEnum.INTEGER_SINGLE;
        } else if (outputType.equalsIgnoreCase("float")) {
            outputTypeEnum = TypeEnum.FLOAT_SINGLE;
        } else if (outputType.equalsIgnoreCase("long")) {
            outputTypeEnum = TypeEnum.LONG_SINGLE;
        } else if (outputType.equalsIgnoreCase("double")) {
            outputTypeEnum = TypeEnum.DOUBLE_SINGLE;
        }
    }

    @Override
    public void onPropertyChanged(String key, Object value) {
        if (key.equalsIgnoreCase("outputType")) {
            outputType = getProperty("outputType").toString();
            updateOutputType();
        }  else if (key.equalsIgnoreCase("delimiter")) {
            delimiter = getProperty("delimiter").toString();

        } else if (key.equalsIgnoreCase("data")) {
            data = getProperty("data").toString();
        }
    }

    @Override
    public void loadProperty(String key, Object value) {
        onPropertyChanged(key, value);

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
