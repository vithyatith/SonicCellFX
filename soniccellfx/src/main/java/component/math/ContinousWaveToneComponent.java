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

import com.sonicmsgr.signalprocessing.XCrossUtil;
import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.Log;

/**
 *
 * @author Administrator
 */
public class ContinousWaveToneComponent extends ComponentAbstraction {

    private byte outputDataB[] = new byte[1];
    private short outputDataS[] = new short[1];
    private int outputDataI[] = new int[1];
    private float outputDataF[] = new float[1];
    private double outputDataD[] = new double[1];
    private long outputDataL[] = new long[1];

    private int sampleRate = 44100;

    private float nSecond = 1;
    private float amplitude = 1;
    private int frequency = 10000;
   // private boolean readyBool = false;
    private String endian = "little";
    private boolean littleEndianBool = true;
    private String outputType = "float[]";
    private TypeEnum outputTypeEnum = TypeEnum.BYTE;

    private enum TypeEnum {
        BYTE,
        SHORT,
        INTEGER,
        FLOAT,
        DOUBLE,
        LONG,
    }

    public ContinousWaveToneComponent() {
        setName("CW");
        setProperty("sampleRate", Integer.toString(sampleRate));
        setProperty("nSecond", Float.toString(nSecond));
        setProperty("amplitude", Float.toString(amplitude));
        setProperty("frequency", Float.toString(frequency));
        this.setProperty("endian", endian);
        this.setProperty("outputType", outputType);
        this.addOutput(new DataTypeIO("float[]"));
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public Object onExecute() {

            if ((outputTypeEnum == TypeEnum.FLOAT)) {
                XCrossUtil.cw(amplitude, outputDataF, sampleRate, frequency);
                sendData(0, outputDataF);
            } else if ((outputTypeEnum == TypeEnum.SHORT)) {
                XCrossUtil.cw_shorts(amplitude, outputDataS, sampleRate, frequency);
                sendData(0, outputDataS);
            } else if ((outputTypeEnum == TypeEnum.BYTE)) {
                XCrossUtil.cw_bytes(amplitude, outputDataB, sampleRate, frequency);
                sendData(0, outputDataB);
            } else {

                Log.v("VT", "Not found");
            }

 

        return null;
    }

    @Override
    public void mouseDoubleClick() {

    }

    @Override
    public void handleMessage(int thru, Object obj) {
    }

    @Override
    public boolean start() {

        outputType = getProperty("outputType").toString();

        endian = getProperty("endian").toString();
        sampleRate = (int) Double.parseDouble(getProperty("sampleRate").toString());
        nSecond = (float) Double.parseDouble(getProperty("nSecond").toString());
        amplitude = (float) Double.parseDouble(getProperty("amplitude").toString());
        frequency = (int) Double.parseDouble(getProperty("frequency").toString());

        if (endian.equalsIgnoreCase("little")) {
            littleEndianBool = true;
        } else {
            littleEndianBool = false;
        }

        updateOutputType();
        int N = (int) (sampleRate * nSecond);

        if (outputTypeEnum == TypeEnum.FLOAT) {
            if (N != outputDataF.length) {
                outputDataF = new float[N];
            }
        } else if (outputTypeEnum == TypeEnum.SHORT) {
            if (N != outputDataS.length) {
                outputDataS = new short[N];
            }
        } else if (outputTypeEnum == TypeEnum.DOUBLE) {
            if (N != outputDataD.length) {
                outputDataD = new double[N];
            }
        } else if (outputTypeEnum == TypeEnum.LONG) {
            if (N != outputDataL.length) {
                outputDataL = new long[N];
            }
        } else if (outputTypeEnum == TypeEnum.BYTE) {
            if (N != outputDataB.length) {
                outputDataB = new byte[N * 2];
            }
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
        }
    }

    @Override
    public void onPropertyChanged(String key, Object value) {
        Log.v("VT", "Contiueous key is " + key + "  value is " + value);
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
        } else if (key.equalsIgnoreCase("sampleRate")) {
            sampleRate = (int) Double.parseDouble(getProperty("sampleRate").toString());
        } else if (key.equalsIgnoreCase("nSecond")) {
            nSecond = (float) Double.parseDouble(getProperty("nSecond").toString());
        } else if (key.equalsIgnoreCase("amplitude")) {
            amplitude = (float) Double.parseDouble(getProperty("amplitude").toString());
        } else if (key.equalsIgnoreCase("frequency")) {
            frequency = (int) Double.parseDouble(getProperty("frequency").toString());
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
