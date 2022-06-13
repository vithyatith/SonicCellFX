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
package component.file;

import com.google.gson.Gson;
import component.typeconverter.*;

import component.basic.*;
import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.Log;
import com.sonicmsgr.soniccell.msg.PubSubSingleton;
import com.sonicmsgr.util.TypeBufferConverterUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author yada
 */
public class WriteBinaryComponent extends ComponentAbstraction {

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
    private String filename = "out.bin";
    FileOutputStream fileOutputStream = null;
    private boolean append = false;

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

    private FileOutputStream FileOutputStream = null;

    public WriteBinaryComponent() {
        setName("WriteBinary");
        this.setProperty("filename", filename);
        this.setProperty("endian", endian);
        this.setProperty("append", append);
        // this.setProperty("outputType", outputType);
        this.addInput(new DataTypeIO("string", "Output file name", "filename"));
        this.addInput(new DataTypeIO("all"));
        this.setHasUISupport(true);
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    
    @Override
    public Object onExecute() {
        if (readyBool) {
            readyBool = false;

            if ((inputTypeEnum == TypeEnum.BYTE) && (outputTypeEnum == TypeEnum.BYTE)) {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.write(inputDataB);
                    } catch (IOException ex) {
                        printToConsole(ex.getLocalizedMessage());
                    }
                }

            } else if ((inputTypeEnum == TypeEnum.SHORT) && (outputTypeEnum == TypeEnum.BYTE)) {

                int inputSize = inputDataS.length * 2;
                int outputSize = outputDataB.length;
                if (inputSize != (outputSize)) {
                    outputDataB = new byte[inputSize];
                }
                TypeBufferConverterUtil.shortArrayToBytesArray(inputDataS, outputDataB, littleEndianBool);
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.write(outputDataB);
                    } catch (IOException ex) {
                        printToConsole(ex.getLocalizedMessage());
                    }
                }

            } else if ((inputTypeEnum == TypeEnum.INTEGER) && (outputTypeEnum == TypeEnum.BYTE)) {
                int inputSize = inputDataI.length * 4;
                int outputSize = outputDataB.length;
                if (inputSize != (outputSize)) {
                    outputDataB = new byte[inputSize];
                }
                TypeBufferConverterUtil.intArrayToBytes(inputDataI, outputDataB, littleEndianBool);
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.write(outputDataB);
                    } catch (IOException ex) {
                        printToConsole(ex.getLocalizedMessage());
                    }
                }
            } else if ((inputTypeEnum == TypeEnum.FLOAT) && (outputTypeEnum == TypeEnum.BYTE)) {
                int inputSize = inputDataF.length * 4;
                int outputSize = outputDataB.length;
                if (inputSize != (outputSize)) {
                    outputDataB = new byte[inputSize];
                }
                TypeBufferConverterUtil.floatArrayToBytes(inputDataF, outputDataB, littleEndianBool);
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.write(outputDataB);
                    } catch (IOException ex) {
                        printToConsole(ex.getLocalizedMessage());
                    }
                }
            } else if ((inputTypeEnum == TypeEnum.LONG) && (outputTypeEnum == TypeEnum.BYTE)) {
                int inputSize = inputDataL.length * 8;
                int outputSize = outputDataB.length;
                if (inputSize != (outputSize)) {
                    outputDataB = new byte[inputSize];
                }
                TypeBufferConverterUtil.longArrayToBytes(inputDataL, outputDataB, littleEndianBool);
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.write(outputDataB);
                    } catch (IOException ex) {
                        printToConsole(ex.getLocalizedMessage());
                    }
                }
            } else if ((inputTypeEnum == TypeEnum.DOUBLE) && (outputTypeEnum == TypeEnum.BYTE)) {
                int inputSize = inputDataD.length * 8;
                int outputSize = outputDataB.length;
                if (inputSize != (outputSize)) {
                    outputDataB = new byte[inputSize];
                }
                TypeBufferConverterUtil.doubleArrayToBytes(inputDataD, outputDataB, littleEndianBool);
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.write(outputDataB);
                    } catch (IOException ex) {
                        printToConsole(ex.getLocalizedMessage());
                    }
                }
            } else if ((inputTypeEnum == TypeEnum.STRING) && (outputTypeEnum == TypeEnum.BYTE)) {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.write(stringData.getBytes());
                    } catch (IOException ex) {
                        printToConsole(ex.getLocalizedMessage());
                    }
                }
            } else if ((inputTypeEnum == TypeEnum.BYTE_SINGLE) && (outputTypeEnum == TypeEnum.BYTE)) {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.write(byteValue);
                    } catch (IOException ex) {
                        printToConsole(ex.getLocalizedMessage());
                    }
                }
            } else if ((inputTypeEnum == TypeEnum.SHORT_SINGLE) && (outputTypeEnum == TypeEnum.BYTE)) {
                if (fileOutputStream != null) {
                    try {
                        TypeBufferConverterUtil.shortToBytes(shortValue, tmpByte2, littleEndianBool);
                        fileOutputStream.write(tmpByte2);

                        fileOutputStream.write(tmpByte2);
                    } catch (IOException ex) {
                        printToConsole(ex.getLocalizedMessage());
                    }
                }
            } else if ((inputTypeEnum == TypeEnum.INTEGER_SINGLE) && (outputTypeEnum == TypeEnum.BYTE)) {
                if (fileOutputStream != null) {
                    try {
                        TypeBufferConverterUtil.intToBytes(integerValue, tmpByte4, littleEndianBool);
                        fileOutputStream.write(tmpByte4);
                    } catch (IOException ex) {
                        printToConsole(ex.getLocalizedMessage());
                    }
                }
            } else if ((inputTypeEnum == TypeEnum.FLOAT_SINGLE) && (outputTypeEnum == TypeEnum.BYTE)) {
                if (fileOutputStream != null) {
                    try {
                        TypeBufferConverterUtil.floatToBytes(floatValue, tmpByte4, littleEndianBool);
                        fileOutputStream.write(tmpByte4);
                    } catch (IOException ex) {
                        printToConsole(ex.getLocalizedMessage());
                    }
                }
            }else if ((inputTypeEnum == TypeEnum.LONG_SINGLE) && (outputTypeEnum == TypeEnum.BYTE)) {
                if (fileOutputStream != null) {
                    try {
                        TypeBufferConverterUtil.longToBytes(longValue, tmpByte8, littleEndianBool);
                        fileOutputStream.write(tmpByte8);
                    } catch (IOException ex) {
                        printToConsole(ex.getLocalizedMessage());
                    }
                }
            }else if ((inputTypeEnum == TypeEnum.DOUBLE_SINGLE) && (outputTypeEnum == TypeEnum.BYTE)) {
                if (fileOutputStream != null) {
                    try {
                        TypeBufferConverterUtil.doubleToBytes(doubleValue, tmpByte8, littleEndianBool);
                        fileOutputStream.write(tmpByte8);
                    } catch (IOException ex) {
                        printToConsole(ex.getLocalizedMessage());
                    }
                }
            }
            
            else {
                printToConsole("Write binary Not found input is " + inputTypeEnum + "  output is " + outputTypeEnum);
            }

        }

        return null;
    }

    @Override
    public void mouseDoubleClick() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FileChooser fileChooser = new FileChooser();
                Stage stage = new Stage();
                File selectedFile = fileChooser.showOpenDialog(stage);

                if (selectedFile != null) {
                    filename = selectedFile.getAbsoluteFile().toString();
                    setProperty("filename", filename);
                    //PubSubSingleton.getIntance().send("UPDATE_PROPERTIES", getId());
                    updatePropertiesChanges();
                }
            }

        });
    }

    @Override
    public void handleMessage(int thru, Object obj) {

        if (thru == 1) {
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
            }else {
                readyBool = false;
            }
        } else if (thru == 0) {
            filename = (String) obj;
        }
    }

    @Override
    public boolean start() {

        endian = getProperty("endian").toString();
        filename = getProperty("filename").toString();
        append = Boolean.parseBoolean(getProperty("append").toString());
        if (endian.equalsIgnoreCase("little")) {
            littleEndianBool = true;
        } else {
            littleEndianBool = false;
        }
        updateOutputType();
        readyBool = false;
        try {
            fileOutputStream = new FileOutputStream(filename, append);
        } catch (FileNotFoundException ex) {
            printToConsole(ex.getLocalizedMessage());
        }

        return true;
    }

    @Override
    public void stop() {
        if (fileOutputStream != null) {
            try {
                fileOutputStream.close();
                fileOutputStream = null;
            } catch (IOException ex) {
                printToConsole(ex.getLocalizedMessage());
            }
        }

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
        if (key.equalsIgnoreCase("endian")) {
            endian = getProperty("endian").toString();
            if (endian.equalsIgnoreCase("little")) {
                littleEndianBool = true;
            } else {
                littleEndianBool = false;
            }
        } else if (key.equalsIgnoreCase("filename")) {
            filename = getProperty("filename").toString();
        } else if (key.equalsIgnoreCase("append")) {
            append = Boolean.parseBoolean(getProperty(key).toString());
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
