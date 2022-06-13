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

import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.msg.PubSubSingleton;
import com.sonicmsgr.util.FileUtils;
import com.sonicmsgr.util.TypeBufferConverterUtil;
import component.typeconverter.TypeArrayConverterComponent;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author yada
 */
public class FileReaderBinaryComponent extends ComponentAbstraction {

    private String filename = "input.bin";
    private boolean processingBool = false;
    private int outputSize = 2048;
    private int bufferSize = 2048;
    private boolean paddLastBytes = false;
    private int nTime = -1;
    private int sleepInMilliSec = 20;
    private Thread thread = null;
    private boolean continueProcessingFile = true;
    private long count = 0;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private String outputType = "byte[]";
    private TypeEnum inputTypeEnum = TypeEnum.FLOAT;
    private TypeEnum outputTypeEnum = TypeEnum.BYTE;
    private boolean littleEndianBool = true;
    private String endian = "little";

    private byte outputDataB[] = new byte[1];
    private short outputDataS[] = new short[1];
    private int outputDataI[] = new int[1];
    private float outputDataF[] = new float[1];
    private double outputDataD[] = new double[1];
    private long outputDataL[] = new long[1];
    private String stringData = "";
    private boolean remainingBytesBool = true;

    private int trueSize = 0;

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

    public FileReaderBinaryComponent() {
        setName("ReadBinary");
        this.setProperty("filename", filename);

        this.setProperty("bufferSize", bufferSize);
        this.setProperty("outputSize", outputSize);
        this.setProperty("nTime", nTime);
        this.setProperty("sleepInMilliSec", sleepInMilliSec);
        this.setProperty("outputType", outputType);
        this.setProperty("endian", endian);
        this.setProperty("paddLastBytes", paddLastBytes);
        this.setProperty("remainingBytesBool", remainingBytesBool);
        

        this.addInput(new DataTypeIO("string", "Input file name", "filename"));
        this.addOutput(new DataTypeIO("byte[]", "byte array"));
        this.addOutput(new DataTypeIO("boolean", "Is successfule read?"));
        this.setHasUISupport(true);
    }

    private void updateOutputType() {
        if (outputType.equalsIgnoreCase("byte[]")) {
            outputTypeEnum = TypeEnum.BYTE;
            trueSize = outputSize;
        } else if (outputType.equalsIgnoreCase("short[]")) {
            outputTypeEnum = TypeEnum.SHORT;
            trueSize = outputSize * 2;
        } else if (outputType.equalsIgnoreCase("int[]")) {
            outputTypeEnum = TypeEnum.INTEGER;
            trueSize = outputSize * 4;
        } else if (outputType.equalsIgnoreCase("float[]")) {
            outputTypeEnum = TypeEnum.FLOAT;
            trueSize = outputSize * 4;
        } else if (outputType.equalsIgnoreCase("double[]")) {
            outputTypeEnum = TypeEnum.DOUBLE;
            trueSize = outputSize * 8;
        } else if (outputType.equalsIgnoreCase("long[]")) {
            outputTypeEnum = TypeEnum.LONG;
            trueSize = outputSize * 8;
        } else if (outputType.equalsIgnoreCase("string")) {
            outputTypeEnum = TypeEnum.STRING;
            trueSize = outputSize;
        } else {
            processingBool = false;
            printToConsole("outputType " + outputType + " is not supported. Only byte[],short[],int[],float[],long[],double[] and string are supported.");
        }
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    
    @Override
    public Object onExecute() {

        if (processingBool) {
            processingBool = false;
            try {
                sendData(1, true);

                thread = new Thread() {
                    @Override
                    public void run() {
                        
     
                        FileUtils.readByteBinaryFile(filename, bufferSize,trueSize, nTime, sleepInMilliSec, paddLastBytes, remainingBytesBool,new FileUtils.CallBack() {
                            @Override
                            public void onReceived(Object data) {
                                byte inputDataB[] = (byte[]) data;

                                //////
                                if ((outputTypeEnum == TypeEnum.BYTE)) {
                                    sendData(0, inputDataB);
                                } else if ((outputTypeEnum == TypeEnum.SHORT)) {

                                    int inputSize = inputDataB.length;
                                    int outputSize = outputDataS.length;
                                    if (inputSize != (outputSize * 2)) {
                                        outputDataS = new short[inputSize / 2];
                                    }

                                    TypeBufferConverterUtil.bytesToShortsArray(inputDataB, outputDataS, littleEndianBool);
                                    sendData(0, outputDataS);
                                } else if ((outputTypeEnum == TypeEnum.INTEGER)) {
                                    int inputSize = inputDataB.length;
                                    int outputSize = outputDataI.length;
                                    if (inputSize != (outputSize * 4)) {
                                        outputDataI = new int[inputSize / 4];
                                    }

                                    TypeBufferConverterUtil.bytesToIntegerArray(inputDataB, outputDataI, littleEndianBool);
                                    sendData(0, outputDataI);
                                } else if ((outputTypeEnum == TypeEnum.FLOAT)) {
                                    int inputSize = inputDataB.length;
                                    int outputSize = outputDataF.length;
                                    if (inputSize != (outputSize * 4)) {
                                        outputDataF = new float[inputSize / 4];
                                    }

                                    TypeBufferConverterUtil.bytesToFloatArray(inputDataB, outputDataF, littleEndianBool);
                                    sendData(0, outputDataF);
                                } else if ((outputTypeEnum == TypeEnum.LONG)) {
                                    int inputSize = inputDataB.length;
                                    int outputSize = outputDataL.length;
                                    if (inputSize != (outputSize * 8)) {
                                        outputDataL = new long[inputSize / 8];
                                    }

                                    TypeBufferConverterUtil.bytesToLongArray(inputDataB, outputDataL, littleEndianBool);
                                    sendData(0, outputDataL);
                                } else if ((outputTypeEnum == TypeEnum.DOUBLE)) {
                                    int inputSize = inputDataB.length;
                                    int outputSize = outputDataD.length;
                                    if (inputSize != (outputSize * 8)) {
                                        outputDataD = new double[inputSize / 8];
                                    }

                                    TypeBufferConverterUtil.bytesToDoubleArray(inputDataB, outputDataD, littleEndianBool);
                                    sendData(0, outputDataD);
                                }

                                count++;
                            }

                            @Override
                            public synchronized boolean continueProcessing() {
                                return continueProcessingFile;
                            }
                        });
                    }
                };

                thread.setPriority(Thread.MIN_PRIORITY);
                thread.start();
            } catch (Exception e) {
                sendData(1, false);
                e.printStackTrace();
                PubSubSingleton.getIntance().send("Print", e.getMessage());
            }
        }

        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {

        if (thru == 0) {
            filename = (String) obj;
            if (filename.trim().equalsIgnoreCase("")) {
                return;
            }
            File file = new File(filename);
            if (file.exists()) {
                processingBool = true;
            } else {
                processingBool = false;
                sendData(1, false);
    
                printToConsole(filename + " doesn't exist");
            }

        }
    }

    @Override
    public boolean start() {

        if (thread != null) {
            try {
                Thread th = thread;
                th.interrupt();
                thread = null;
                th = null;

            } catch (Exception e) {

            }
        }

        count = 0;
        continueProcessingFile = true;
        filename = getProperty("filename").toString();

        outputSize = (int) Double.parseDouble(getProperty("outputSize").toString());
        bufferSize = (int) Double.parseDouble(getProperty("bufferSize").toString());
        paddLastBytes = Boolean.parseBoolean(getProperty("paddLastBytes").toString());
        remainingBytesBool = Boolean.parseBoolean(getProperty("remainingBytesBool").toString());
        
        nTime = (int) Double.parseDouble(getProperty("nTime").toString());
        sleepInMilliSec = (int) Double.parseDouble(getProperty("sleepInMilliSec").toString());
        outputType = getProperty("outputType").toString();
        endian = getProperty("endian").toString();

        if (endian.equalsIgnoreCase("little")) {
            littleEndianBool = true;
        } else {
            littleEndianBool = false;
        }

        if (sleepInMilliSec < 0) {
            sleepInMilliSec = 0;
        }

        File file = new File(filename);
        if (file.exists()) {
            processingBool = true;
        } else {
          //  sendData(1, false);
          //  printToConsole(filename + " doesn't exist");
        }
        updateOutputType();
        return true;
    }

    @Override
    public void stop() {
        processingBool = false;
        continueProcessingFile = false;

        //  System.out.println("Stop continueProcessingFile = "+continueProcessingFile);
        if (thread != null) {
            try {
                Thread th = thread;
                th.interrupt();
                thread = null;
                th = null;

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("filename")) {
            filename = (String) val;
        } else if (key.equalsIgnoreCase("outputSize")) {
            outputSize = (int) Double.parseDouble(val.toString());
        } else if (key.equalsIgnoreCase("nTime")) {
            nTime = (int) Double.parseDouble(val.toString());
        } else if (key.equalsIgnoreCase("sleepInMilliSec")) {
            sleepInMilliSec = (int) Double.parseDouble(val.toString());
        } else if (key.equalsIgnoreCase("outputType")) {
            outputType = (String) val;
            updateOutputType();
        } else if (key.equalsIgnoreCase("endian")) {
            endian = (String) val;
            if (endian.equalsIgnoreCase("little")) {
                littleEndianBool = true;
            } else {
                littleEndianBool = false;
            }
        } else if (key.equalsIgnoreCase("bufferSize")) {
            bufferSize = (int) Double.parseDouble(val.toString());
        } else if (key.equalsIgnoreCase("paddLastBytes")) {
            paddLastBytes = Boolean.parseBoolean(val.toString());
        }else if (key.equalsIgnoreCase("remainingBytesBool")) {
            remainingBytesBool = Boolean.parseBoolean(val.toString());
        }


        
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
                    PubSubSingleton.getIntance().send("UPDATE_PROPERTIES", getId());
                }
            }

        });
    }

    @Override
    public int getPlatformSupport() {

        return 0;
    }

    @Override
    public void onPropertyChanged(String key, Object value) {
        loadProperty(key,value);
//        if (key.equalsIgnoreCase("outputType")) {
//            outputType = getProperty("outputType").toString();
//            updateOutputType();
//        } else if (key.equalsIgnoreCase("endian")) {
//            endian = getProperty("endian").toString();
//            if (endian.equalsIgnoreCase("little")) {
//                littleEndianBool = true;
//            } else {
//                littleEndianBool = false;
//            }
//        }

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
