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
import component.typeconverter.*;

import component.basic.*;
import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.Log;
import com.sonicmsgr.util.TypeBufferConverterUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author yada
 */
public class AppendToByteArrayComponent extends ComponentAbstraction {

    private byte outputDataB[] = new byte[1];
    private byte outputDataS[] = new byte[1];
    private byte outputDataI[] = new byte[1];
    private byte outputDataF[] = new byte[1];
    private byte outputDataD[] = new byte[1];
    private byte outputDataL[] = new byte[1];
    //private String stringData = "";

    private boolean readyBool = false;
    private String endian = "little";
    private boolean littleEndianBool = true;

    private Gson gson = new Gson();

    private byte tmpByte1[] = new byte[1];
    private byte tmpByte2[] = new byte[2];
    private byte tmpByte4[] = new byte[4];
    private byte tmpByte8[] = new byte[8];

    private byte tmpByte4_2[] = new byte[4];
    private byte tmpByte8_2[] = new byte[8];

    private int numberOfInputOccupy = 0;
    private int occupyCount = 0;
    //private ArrayList<byte[]> al = new ArrayList<byte[]>(4);
    private ConcurrentHashMap<Integer, byte[]> map = new ConcurrentHashMap<Integer, byte[]>();

    private int outputByteSize = 0;

    private byte outputBytes[] = new byte[1];
    private int inputSize = 4;
    private int inputSizeTrack[] = new int[inputSize];

    public AppendToByteArrayComponent() {
        setName("AppendBytes");
        this.setProperty("endian", endian);
        for (int i = 0; i < inputSize; i++) {
            this.addInput(new DataTypeIO("all"));
        }

        this.addOutput(new DataTypeIO("byte[]"));
        for (int i = 0; i < 4; i++) {
            map.put(i, new byte[1]);
        }

    }

    private void clearSize() {
        for (int i = 0; i < inputSize; i++) {
            inputSizeTrack[i] = 0;
        }
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public Object onExecute() {
        if (readyBool) {
            readyBool = false;
            occupyCount = 0;
            int sum = 0;
            for (int i = 0; i < inputSizeTrack.length; i++) {
                sum = sum + inputSizeTrack[i];
            }
            if (outputBytes.length != sum) {
                outputBytes = new byte[sum];
            }

            int m = 0;

            for (int i = 0; i < inputSizeTrack.length; i++) {
                int len = inputSizeTrack[i];
                byte b[] = map.get(i);
                for (int j = 0; j < len; j++) {
                    outputBytes[m] = b[j];
                    m++;
                }
                inputSizeTrack[i] = 0; // reset back
            }

            sendData(0, outputBytes);
        }

        return null;
    }

    @Override
    public void mouseDoubleClick() {

    }

    @Override
    public void handleMessage(int thru, Object obj) {

        if (obj instanceof byte[]) {
            byte[] inputDataB = (byte[]) obj;
            int size = map.get(thru).length;
            if (size != inputDataB.length) {
                map.put(thru, new byte[inputDataB.length]);
            }
            size = map.get(thru).length;

            System.arraycopy(inputDataB, 0, map.get(thru), 0, size);

            inputSizeTrack[thru] = inputDataB.length;
        } else if (obj instanceof short[]) {
            short[] inputDataS = (short[]) obj;
            int inputSize = inputDataS.length * 2;
            int outputSize = outputDataS.length;
            if (inputSize != outputSize) {
                outputDataS = new byte[inputSize];
            }
            TypeBufferConverterUtil.shortArrayToBytesArray(inputDataS, outputDataS, littleEndianBool);

            int size = map.get(thru).length;
            if (size != outputDataS.length) {
                map.put(thru, new byte[outputDataS.length]);
            }
            size = map.get(thru).length;
            System.arraycopy(outputDataS, 0, map.get(thru), 0, size);
            inputSizeTrack[thru] = outputDataS.length;
        } else if (obj instanceof int[]) {
            int[] inputDataI = (int[]) obj;

            int inputSize = inputDataI.length * 4;
            int outputSize = outputDataI.length;
            if (inputSize != outputSize) {
                outputDataI = new byte[inputSize];
            }
            TypeBufferConverterUtil.intArrayToBytes(inputDataI, outputDataI, littleEndianBool);

            int size = map.get(thru).length;
            if (size != outputDataI.length) {
                map.put(thru, new byte[outputDataI.length]);
            }
            size = map.get(thru).length;
            System.arraycopy(outputDataI, 0, map.get(thru), 0, size);

            inputSizeTrack[thru] = outputDataI.length;
        } else if (obj instanceof float[]) {
            float[] inputDataF = (float[]) obj;

            int inputSize = inputDataF.length * 4;
            int outputSize = outputDataF.length;
            if (inputSize != outputSize) {
                outputDataF = new byte[inputSize];
            }

            TypeBufferConverterUtil.floatArrayToBytes(inputDataF, outputDataF, littleEndianBool);
            int size = map.get(thru).length;
            if (size != outputDataF.length) {
                map.put(thru, new byte[outputDataF.length]);
            }
            size = map.get(thru).length;
            System.arraycopy(outputDataF, 0, map.get(thru), 0, size);

            inputSizeTrack[thru] = outputDataF.length;

        } else if (obj instanceof long[]) {
            long[] inputDataL = (long[]) obj;
            int inputSize = inputDataL.length * 8;
            int outputSize = outputDataL.length;
            if (inputSize != outputSize) {
                outputDataL = new byte[inputSize];
            }
            TypeBufferConverterUtil.longArrayToBytes(inputDataL, outputDataL, littleEndianBool);
            int size = map.get(thru).length;
            if (size != outputDataL.length) {
                map.put(thru, new byte[outputDataL.length]);
            }
            size = map.get(thru).length;
            System.arraycopy(outputDataL, 0, map.get(thru), 0, size);

            inputSizeTrack[thru] = outputDataL.length;

        } else if (obj instanceof double[]) {
            double[] inputDataD = (double[]) obj;
            int inputSize = inputDataD.length * 8;
            int outputSize = outputDataD.length;
            if (inputSize != outputSize) {
                outputDataD = new byte[inputSize];
            }
            TypeBufferConverterUtil.doubleArrayToBytes(inputDataD, outputDataD, littleEndianBool);
            int size = map.get(thru).length;
            if (size != outputDataD.length) {
                map.put(thru, new byte[outputDataD.length]);
            }
            size = map.get(thru).length;
            System.arraycopy(outputDataD, 0, map.get(thru), 0, size);

            inputSizeTrack[thru] = outputDataD.length;
        } else if (obj instanceof String) {
            String stringData = (String) obj;
            byte b[] = stringData.getBytes();
            int inputSize = b.length;
            int size = map.get(thru).length;
            if (size != b.length) {
                map.put(thru, new byte[inputSize]);
            }
            size = map.get(thru).length;
            System.arraycopy(b, 0, map.get(thru), 0, size);
            inputSizeTrack[thru] = b.length;

        } else if (obj instanceof Byte) {
            byte byteValue = (Byte) obj;
            tmpByte1[0] = byteValue;

            int size = map.get(thru).length;
            if (size != tmpByte1.length) {
                map.put(thru, new byte[tmpByte1.length]);
            }
            size = map.get(thru).length;

            System.arraycopy(tmpByte1, 0, map.get(thru), 0, size);

            inputSizeTrack[thru] = tmpByte1.length;
        } else if (obj instanceof Short) {
            short shortValue = (Short) obj;
            TypeBufferConverterUtil.shortToBytes(shortValue, tmpByte2, littleEndianBool);

            int size = map.get(thru).length;
            if (size != tmpByte2.length) {
                map.put(thru, new byte[tmpByte2.length]);
            }
            size = map.get(thru).length;

            System.arraycopy(tmpByte2, 0, map.get(thru), 0, size);
            inputSizeTrack[thru] = tmpByte2.length;

        } else if (obj instanceof Integer) {
            int integerValue = (Integer) obj;
            TypeBufferConverterUtil.intToBytes(integerValue, tmpByte4, littleEndianBool);

            int size = map.get(thru).length;
            if (size != tmpByte4.length) {
                map.put(thru, new byte[tmpByte4.length]);
            }
            size = map.get(thru).length;
            System.arraycopy(tmpByte4, 0, map.get(thru), 0, size);

            inputSizeTrack[thru] = tmpByte4.length;
        } else if (obj instanceof Float) {
            float floatValue = (Float) obj;
            TypeBufferConverterUtil.floatToBytes(floatValue, tmpByte4_2, littleEndianBool);

            int size = map.get(thru).length;
            if (size != tmpByte4_2.length) {
                map.put(thru, new byte[tmpByte4_2.length]);
            }
            size = map.get(thru).length;
            System.arraycopy(tmpByte4_2, 0, map.get(thru), 0, size);

            inputSizeTrack[thru] = tmpByte4_2.length;
        } else if (obj instanceof Long) {
            long longValue = (Long) obj;
            TypeBufferConverterUtil.longToBytes(longValue, tmpByte8, littleEndianBool);

            int size = map.get(thru).length;
            if (size != tmpByte8.length) {
                map.put(thru, new byte[tmpByte8.length]);
            }
            size = map.get(thru).length;
            System.arraycopy(tmpByte8, 0, map.get(thru), 0, size);

            inputSizeTrack[thru] = tmpByte8.length;
        } else if (obj instanceof Double) {
            double doubleValue = (Double) obj;
            TypeBufferConverterUtil.doubleToBytes(doubleValue, tmpByte8_2, littleEndianBool);

            int size = map.get(thru).length;
            if (size != tmpByte8_2.length) {
                map.put(thru, new byte[tmpByte8_2.length]);
            }
            size = map.get(thru).length;
            System.arraycopy(tmpByte8_2, 0, map.get(thru), 0, size);
            inputSizeTrack[thru] = tmpByte8_2.length;
        }
        occupyCount++;
        if (occupyCount == numberOfInputOccupy) {
            readyBool = true;
        } else {
            readyBool = false;
        }

    }

    @Override
    public boolean start() {
        endian = getProperty("endian").toString();

        if (endian.equalsIgnoreCase("little")) {
            littleEndianBool = true;
        } else {
            littleEndianBool = false;
        }
        readyBool = false;

        numberOfInputOccupy = this.getNumberOfInputOccupy();
        occupyCount = 0;
        for (int i = 0; i < inputSizeTrack.length; i++) {
            inputSizeTrack[i] = 0;
        }
        return true;
    }

    @Override
    public void stop() {

    }

    @Override
    public void onPropertyChanged(String key, Object value) {
        if (key.equalsIgnoreCase("endian")) {
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
