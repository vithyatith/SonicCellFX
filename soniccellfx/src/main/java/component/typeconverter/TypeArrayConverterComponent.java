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
package component.typeconverter;

import com.google.gson.Gson;
import component.basic.*;
import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.Log;
import com.sonicmsgr.util.TypeBufferConverterUtil;
import java.util.HashMap;

/**
 *
 * @author yada
 */
public class TypeArrayConverterComponent extends ComponentAbstraction {

    private byte inputDataB[];
    private short inputDataS[];
    private int inputDataI[];
    private float inputDataF[];
    private double inputDataD[];
    private long inputDataL[];

    private byte outputDataB[] = new byte[1];
    private short outputDataS[] = new short[1];
    private int outputDataI[] = new int[1];
    private float outputDataF[] = new float[1];
    private double outputDataD[] = new double[1];
    private long outputDataL[] = new long[1];
    private String stringData = "";

    //private byte outputData[] = new byte[1];
    private boolean readyBool = false;
    private String endian = "little";
    private boolean littleEndianBool = true;
    private String inputType = "float[]";
    private String outputType = "byte[]";
    private TypeEnum inputTypeEnum = TypeEnum.FLOAT;
    private TypeEnum outputTypeEnum = TypeEnum.BYTE;

    private Gson gson = new Gson();

    private byte byteValue = 0;
    private short shortValue = 0;
    private int integerValue = 0;
    private float floatValue = 0;
    private long longValue = 0;
    private double doubleValue = 0;

    byte tmpByte1[] = new byte[1];
    byte tmpByte2[] = new byte[2];
    byte tmpByte4[] = new byte[4];
    byte tmpByte8[] = new byte[8];

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
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    public TypeArrayConverterComponent() {
        setName("TypeConv");
        this.setProperty("endian", endian);
        this.setProperty("outputType", outputType);
        this.addInput(new DataTypeIO("all"));
        this.addOutput(new DataTypeIO("byte[]"));
    }

    @Override
    public Object onExecute() {
        if (readyBool) {
            readyBool = false;

            if ((inputTypeEnum == TypeEnum.BYTE) && (outputTypeEnum == TypeEnum.SHORT)) {

                int inputSize = inputDataB.length;
                int outputSize = outputDataS.length;
                if (inputSize != (outputSize*2)) {
                    outputDataS = new short[inputSize/2];
                }
                
                TypeBufferConverterUtil.bytesToShortsArray(inputDataB, outputDataS, littleEndianBool);
                sendData(0, outputDataS);
            } else if ((inputTypeEnum == TypeEnum.BYTE) && (outputTypeEnum == TypeEnum.INTEGER)) {
               int inputSize = inputDataB.length;
                int outputSize = outputDataI.length;
                if (inputSize != (outputSize*4)) {
                    outputDataI = new int[inputSize/4];
                }
                
                TypeBufferConverterUtil.bytesToIntegerArray(inputDataB, outputDataI, littleEndianBool);
                sendData(0, outputDataI);
            } else if ((inputTypeEnum == TypeEnum.BYTE) && (outputTypeEnum == TypeEnum.FLOAT)) {
               int inputSize = inputDataB.length;
                int outputSize = outputDataF.length;
                if (inputSize != (outputSize*4)) {
                    outputDataF = new float[inputSize/4];
                }
               
                TypeBufferConverterUtil.bytesToFloatArray(inputDataB, outputDataF, littleEndianBool);
                sendData(0, outputDataF);
            } else if ((inputTypeEnum == TypeEnum.BYTE) && (outputTypeEnum == TypeEnum.LONG)) {
               int inputSize = inputDataB.length;
                int outputSize = outputDataL.length;
                if (inputSize != (outputSize*8)) {
                    outputDataL = new long[inputSize/8];
                }
                
                TypeBufferConverterUtil.bytesToLongArray(inputDataB, outputDataL, littleEndianBool);
                sendData(0, outputDataL);
            } else if ((inputTypeEnum == TypeEnum.BYTE) && (outputTypeEnum == TypeEnum.DOUBLE)) {
               int inputSize = inputDataB.length;
                int outputSize = outputDataD.length;
                if (inputSize != (outputSize*8)) {
                    outputDataD = new double[inputSize/8];
                }
                
                TypeBufferConverterUtil.bytesToDoubleArray(inputDataB, outputDataD, littleEndianBool);
                sendData(0, outputDataD);
            } 
            
            else if ((inputTypeEnum == TypeEnum.FLOAT) && (outputTypeEnum == TypeEnum.BYTE)) {

                int inputSize = inputDataF.length;
                int outputSize = outputDataB.length / 4;
                if (inputSize != outputSize) {
                    outputDataB = new byte[inputSize * 4];
                }

                TypeBufferConverterUtil.floatArrayToBytes(inputDataF, outputDataB, littleEndianBool);
                sendData(0, outputDataB);
            } else if ((inputTypeEnum == TypeEnum.FLOAT) && (outputTypeEnum == TypeEnum.SHORT)) {

                int inputSize = inputDataF.length;
                int outputSize = outputDataS.length;
                if (inputSize != outputSize) {
                    outputDataS = new short[inputSize];
                }
                for (int i = 0; i < inputSize; i++) {
                    outputDataS[i] = (short) inputDataF[i];
                }
                sendData(0, outputDataS);
            } else if ((inputTypeEnum == TypeEnum.SHORT) && (outputTypeEnum == TypeEnum.BYTE)) {

                int inputSize = inputDataS.length;
                int outputSize = outputDataB.length / 2;
                if (inputSize != outputSize) {
                    outputDataB = new byte[inputSize * 2];
                }
                TypeBufferConverterUtil.shortArrayToBytesArray(inputDataS, outputDataB, littleEndianBool);
                sendData(0, outputDataB);
            } else if ((inputTypeEnum == TypeEnum.BYTE) && (outputTypeEnum == TypeEnum.STRING)) {
                String s = gson.toJson(inputDataB);
                sendData(0, s);
            } else if ((inputTypeEnum == TypeEnum.STRING) && (outputTypeEnum == TypeEnum.BYTE)) {
                if (!stringData.trim().equalsIgnoreCase("")) {
                    sendData(0, stringData.getBytes());
                }
            } else if ((inputTypeEnum == TypeEnum.SHORT_SINGLE) && (outputTypeEnum == TypeEnum.BYTE)) {
                TypeBufferConverterUtil.shortToBytes(shortValue, tmpByte2, littleEndianBool);
                sendData(0, tmpByte2);
            } else if ((inputTypeEnum == TypeEnum.INTEGER_SINGLE) && (outputTypeEnum == TypeEnum.BYTE)) {
                TypeBufferConverterUtil.intToBytes(integerValue, tmpByte4, littleEndianBool);
                sendData(0, tmpByte4);
            } else if ((inputTypeEnum == TypeEnum.FLOAT_SINGLE) && (outputTypeEnum == TypeEnum.BYTE)) {
                TypeBufferConverterUtil.floatToBytes(floatValue, tmpByte4, littleEndianBool);
                sendData(0, tmpByte4);
            } else if ((inputTypeEnum == TypeEnum.LONG_SINGLE) && (outputTypeEnum == TypeEnum.BYTE)) {
                TypeBufferConverterUtil.longToBytes(longValue, tmpByte8, littleEndianBool);
                sendData(0, tmpByte8);
            } else if ((inputTypeEnum == TypeEnum.BYTE_SINGLE) && (outputTypeEnum == TypeEnum.BYTE)) {
                tmpByte1[0] = byteValue;
                sendData(0, tmpByte1);
            } else if ((inputTypeEnum == TypeEnum.STRING) && (outputTypeEnum == TypeEnum.BYTE_SINGLE)) {
                sendData(0, Byte.parseByte(stringData));
            } else if ((inputTypeEnum == TypeEnum.STRING) && (outputTypeEnum == TypeEnum.SHORT_SINGLE)) {
                sendData(0, Short.parseShort(stringData));
            } else if ((inputTypeEnum == TypeEnum.STRING) && (outputTypeEnum == TypeEnum.INTEGER_SINGLE)) {
                sendData(0, Integer.parseInt(stringData));
            } else if ((inputTypeEnum == TypeEnum.STRING) && (outputTypeEnum == TypeEnum.FLOAT_SINGLE)) {
                sendData(0, Float.parseFloat(stringData));
            } else if ((inputTypeEnum == TypeEnum.STRING) && (outputTypeEnum == TypeEnum.LONG_SINGLE)) {
                sendData(0, Long.parseLong(stringData));
            } else if ((inputTypeEnum == TypeEnum.STRING) && (outputTypeEnum == TypeEnum.DOUBLE_SINGLE)) {
                sendData(0, Double.parseDouble(stringData));
            } 
            
            
            else {
                Log.v("VT", "Not found input is " + inputTypeEnum + "  output is " + outputTypeEnum);
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
        if (obj instanceof float[]) {
            inputTypeEnum = TypeEnum.FLOAT;
            inputDataF = (float[]) obj;
        } else if (obj instanceof short[]) {
            inputTypeEnum = TypeEnum.SHORT;
            inputDataS = (short[]) obj;
        } else if (obj instanceof byte[]) {
            inputTypeEnum = TypeEnum.BYTE;
            inputDataB = (byte[]) obj;
        
        } else if (obj instanceof double[]) {
            inputTypeEnum = TypeEnum.DOUBLE;
            inputDataD = (double[]) obj;
        } else if (obj instanceof long[]) {
            inputTypeEnum = TypeEnum.LONG;
            inputDataL = (long[]) obj;
        } else if (obj instanceof int[]) {
            inputTypeEnum = TypeEnum.INTEGER;
            inputDataI = (int[]) obj;
        } else if (obj instanceof String) {
            inputTypeEnum = TypeEnum.STRING;
            stringData = (String) obj;
        } else if (obj instanceof Byte) {
            inputTypeEnum = TypeEnum.BYTE_SINGLE;
            byteValue = (Byte) obj;
        } else if (obj instanceof Short) {
            inputTypeEnum = TypeEnum.SHORT_SINGLE;
            shortValue = (Short) obj;
        } else if (obj instanceof Integer) {
            inputTypeEnum = TypeEnum.INTEGER_SINGLE;
            integerValue = (Integer) obj;
        } else if (obj instanceof Float) {
            inputTypeEnum = TypeEnum.FLOAT_SINGLE;
            floatValue = (Float) obj;
        } else if (obj instanceof Long) {
            inputTypeEnum = TypeEnum.LONG_SINGLE;
            longValue = (Long) obj;
        } else if (obj instanceof Double) {
            inputTypeEnum = TypeEnum.DOUBLE_SINGLE;
            doubleValue = (Double) obj;
        } else {
            readyBool = false;
        }

    }

    @Override
    public boolean start() {

        outputType = getProperty("outputType").toString();
        endian = getProperty("endian").toString();
        if (endian.equalsIgnoreCase("little")) {
            littleEndianBool = true;
        } else {
            littleEndianBool = false;
        }
        updateOutputType();
        readyBool = false;

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
        } else if (outputType.equalsIgnoreCase("string")) {
            outputTypeEnum = TypeEnum.STRING;
        }else if (outputType.equalsIgnoreCase("short")) {
            outputTypeEnum = TypeEnum.SHORT_SINGLE;
        }else if (outputType.equalsIgnoreCase("int")) {
            outputTypeEnum = TypeEnum.INTEGER_SINGLE;
        }else if (outputType.equalsIgnoreCase("float")) {
            outputTypeEnum = TypeEnum.FLOAT_SINGLE;
        }else if (outputType.equalsIgnoreCase("long")) {
            outputTypeEnum = TypeEnum.LONG_SINGLE;
        }else if (outputType.equalsIgnoreCase("double")) {
            outputTypeEnum = TypeEnum.DOUBLE_SINGLE;
        }else if (outputType.equalsIgnoreCase("byte")) {
            outputTypeEnum = TypeEnum.BYTE_SINGLE;
        }
    }

    @Override
    public void onPropertyChanged(String key, Object value) {
        if (key.equalsIgnoreCase("outputType")) {
            outputType = getProperty("outputType").toString();
            updateOutputType();
        } else if (key.equalsIgnoreCase("endian")) {
            endian = getProperty("endian").toString();
            if (endian.equalsIgnoreCase("little")) {
                littleEndianBool = true;
            } else {
                littleEndianBool = false;
            }
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
