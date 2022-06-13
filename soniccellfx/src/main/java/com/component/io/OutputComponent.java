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

package com.component.io;

import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.DataTypeIOList;

import java.net.*;
import java.io.*;
import java.nio.*;


/**
 *
 * @author anakin
 */
public class OutputComponent extends ComponentAbstraction {

    protected OutputGui gui;
    protected boolean showGuiBool = false;
    //0 is file, 1 is socket
    protected int inputMethod = 0;
    // 0 is TCP, 1 is UDP
    protected int protocolMethod = 0;
    //0 is server, 1 is client
    protected int socketMethod = 0;
    // 0 is binary, 1 is text
    protected int formatMethod = 0;
    // 0 is little, 1 is big
    protected int endianMethod = 0;
    protected int dataTypeId = 0;
    protected String dataType = "Byte";
    protected String bufferType = "Scalar";
    protected int bufferSize = 2048;
    protected String filename = "";
    protected int portNum = 2020;
    protected byte byte_scalar;
    protected byte byte_vec[];
    protected byte byte_mat[][];
    protected short short_scalar;
    protected short short_vec[];
    protected short short_mat[][];
    protected int int_scalar;
    protected int int_vec[];
    protected int int_mat[][];
    protected float float_scalar;
    protected float float_vec[];
    protected float float_mat[][];
    protected long long_scalar;
    protected long long_vec[];
    protected long long_mat[][];
    protected double double_scalar;
    protected double double_vec[];
    protected double double_mat[][];
    protected ByteBuffer byte_buf;// = ByteBuffer.wrap(byte_data);
    protected transient ServerSocket serverTCP = null;
    protected transient Socket socket;
    protected transient DatagramSocket clientUDP = null;
    protected transient DatagramPacket packet = null;
    protected int DATA_TYPE = 0;
    protected boolean fileSyncBool = true;
    protected boolean limitedFileBool = false;
    // Stream
    protected DataOutputStream dos = null;
    protected transient BufferedWriter syncBFOut = null;
    protected transient UnsynchronizedBufferedWriter unSyncBFOut = null;
    protected String encoding = System.getProperty("file.encoding", "ISO-8859-1");
    protected String datastr = "";
    protected String datastrvec[];
    protected StringBuffer strbuffer = new StringBuffer();
    protected StringBuilder strbuilder = new StringBuilder();
    protected boolean initBool = true;
    protected DDByteSwapper ddByteSwap = null;
    protected String strValue = "";
    protected int fileSize = 16;
    protected String delimiter = " ";
    protected long byte_size_track = 0;
    protected long byte_size_limit = 0;
    protected int fileNameInc = 0;
    protected OutputStream outStream = null;

    public OutputComponent() {
        setName("Output");
        init();
    }
    private Object data_tmp;
    private int data_tmp_len = 0;
    ;
    private boolean continueBool = true;
    private boolean stopPress = false;

    private void init() {

        DataTypeIOList dt = new DataTypeIOList();

        dt.addDataType(new DataTypeIO("byte"));
        dt.addDataType(new DataTypeIO("byte[]"));
        dt.addDataType(new DataTypeIO("byte[][]"));

        dt.addDataType(new DataTypeIO("short"));
        dt.addDataType(new DataTypeIO("short[]"));
        dt.addDataType(new DataTypeIO("short[][]"));

        dt.addDataType(new DataTypeIO("int"));
        dt.addDataType(new DataTypeIO("int[]"));
        dt.addDataType(new DataTypeIO("int[][]"));

        dt.addDataType(new DataTypeIO("float"));
        dt.addDataType(new DataTypeIO("float[]"));
        dt.addDataType(new DataTypeIO("float[][]"));

        dt.addDataType(new DataTypeIO("long"));
        dt.addDataType(new DataTypeIO("long[]"));
        dt.addDataType(new DataTypeIO("long[][]"));

        dt.addDataType(new DataTypeIO("double"));
        dt.addDataType(new DataTypeIO("double[]"));
        dt.addDataType(new DataTypeIO("double[][]"));

        dt.addDataType(new DataTypeIO("string"));
        dt.addDataType(new DataTypeIO("string[]"));
        dt.addDataType(new DataTypeIO("object"));
        this.addInput(dt);

        gui = new OutputGui(this);

        ddByteSwap = new DDByteSwapper();
    }


    public void connectTCP() throws Exception {

        if (stopPress) {
            stop();
            return;
        }

        if (socketMethod == 0) {  //Server

            if (serverTCP == null) {
                serverTCP = new ServerSocket(portNum);
            }

            if (serverTCP.isClosed()) {
                System.out.println("Returning.............." + Math.random());
                return;
            } else {
                System.out.println("TCP Server Waiting for cnnection............");
                socket = serverTCP.accept();
            }
        } else {  // Client
            //   System.out.println("Client trying to connect with server on "+ipAddress+":"+portNum);
//            consoleMsg.appendErrorMsgConsole("Client trying to connect with server on " + ipAddress + ":" + portNum);
            Thread.sleep(2000);

            if (socket == null) {
                socket = new Socket(ipAddress, portNum);
                dos = null;
            } else {

                if (socket.isConnected() && !socket.isClosed()) {
                    System.out.println("Not connected........");
                    socket = null;
                    socket = new Socket(ipAddress, portNum);
                    dos = null;
                }

            }
        }

        outStream = socket.getOutputStream();
        if (dos == null) {
            dos = new DataOutputStream(outStream);
        }

    }

    public void connectUDP() throws Exception {

        if (clientUDP != null) {
            clientUDP.close();
        }
        if (packet != null) {
            packet = null;
        }

        clientUDP = new DatagramSocket();
        if (byte_vec == null) {
            byte_vec = new byte[1024];
        }
        packet = new DatagramPacket(byte_vec, byte_vec.length);

        packet.setAddress(InetAddress.getByName(ipAddress));
        packet.setPort(portNum);
    }


    private void processByte(byte v[], int nRow, int nCol) {
        int len = v.length;
        if (dataType.equalsIgnoreCase("Byte")) {
            byte_vec = v;
            ddByteSwap.setByteVector(v);
            try {
                if (formatMethod == 1) {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(byte_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Short")) {

            if (initBool) {
                short_vec = new short[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {

                short_vec[i] = (short) v[i];

            }
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(short_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(short_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Int")) {
            if (initBool) {
                int_vec = new int[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {

                int_vec[i] = (int) v[i];

            }
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(int_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(int_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Float")) {
            if (initBool) {
                float_vec = new float[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {

                float_vec[i] = (float) v[i];

            }
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(float_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(float_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Long")) {
            if (initBool) {
                long_vec = new long[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {

                long_vec[i] = (long) v[i];

            }
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(long_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(long_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Double")) {
            if (initBool) {
                double_vec = new double[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {
                double_vec[i] = (double) v[i];
            }
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(double_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(double_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        }

    }

    private void processShort(short v[], int nRow, int nCol) {
        int len = v.length;
        if (dataType.equalsIgnoreCase("Byte")) {
            if (initBool) {
                byte_vec = new byte[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {
                byte_vec[i] = (byte) v[i];
            }
            try {
                ddByteSwap.setByteVector(byte_vec);
                if (formatMethod == 1) {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(byte_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Short")) {

            short_vec = v;
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(short_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(short_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Int")) {
            if (initBool) {
                int_vec = new int[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {

                int_vec[i] = (int) v[i];

            }
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(int_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(int_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Float")) {
            if (initBool) {
                float_vec = new float[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {

                float_vec[i] = (float) v[i];

            }
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(float_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(float_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Long")) {
            if (initBool) {
                long_vec = new long[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {

                long_vec[i] = (long) v[i];

            }
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(long_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(long_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Double")) {
            if (initBool) {
                double_vec = new double[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {
                double_vec[i] = (double) v[i];
            }
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(double_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(double_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        }

    }

    private void processInt(int v[], int nRow, int nCol) {
        int len = v.length;
        if (dataType.equalsIgnoreCase("Byte")) {
            if (initBool) {
                byte_vec = new byte[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {
                byte_vec[i] = (byte) v[i];
            }
            ddByteSwap.setByteVector(byte_vec);
            try {
                if (formatMethod == 1) {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(byte_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Short")) {

            if (initBool) {
                short_vec = new short[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {

                short_vec[i] = (short) v[i];

            }
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(short_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(short_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Int")) {

            int_vec = v;
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(int_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(int_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Float")) {
            if (initBool) {
                float_vec = new float[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {

                float_vec[i] = (float) v[i];

            }
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(float_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(float_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Long")) {
            if (initBool) {
                long_vec = new long[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {

                long_vec[i] = (long) v[i];

            }
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(long_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(long_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Double")) {
            if (initBool) {
                double_vec = new double[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {
                double_vec[i] = (double) v[i];
            }
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(double_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(double_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        }

    }

    private void processFloat(float v[], int nRow, int nCol) {
        int len = v.length;
        if (dataType.equalsIgnoreCase("Byte")) {
            if (initBool) {
                byte_vec = new byte[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {
                byte_vec[i] = (byte) v[i];
            }

            ddByteSwap.setByteVector(byte_vec);
            try {
                if (formatMethod == 1) {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(byte_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }
                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Short")) {

            if (initBool) {
                short_vec = new short[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {

                short_vec[i] = (short) v[i];

            }
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(short_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(short_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Int")) {
            if (initBool) {
                int_vec = new int[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {

                int_vec[i] = (int) v[i];

            }
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(int_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(int_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Float")) {

            float_vec = v;
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(float_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(float_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }

        } else if (dataType.equalsIgnoreCase("Long")) {
            if (initBool) {
                long_vec = new long[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {

                long_vec[i] = (long) v[i];

            }
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(long_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(long_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }
                }
            } catch (NullPointerException e) {
            }

        } else if (dataType.equalsIgnoreCase("Double")) {
            if (initBool) {
                double_vec = new double[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {
                double_vec[i] = (double) v[i];
            }
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(double_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(double_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        }

    }

    private void processLong(long v[], int nRow, int nCol) {
        int len = v.length;
        if (dataType.equalsIgnoreCase("Byte")) {
            if (initBool) {
                byte_vec = new byte[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {
                byte_vec[i] = (byte) v[i];
            }
            ddByteSwap.setByteVector(byte_vec);
            //////////////////////////
            //        DATA_TYPE = DataType.BYTE_MATRIX;
            try {
                if (formatMethod == 1) {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(byte_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Short")) {

            if (initBool) {
                short_vec = new short[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {

                short_vec[i] = (short) v[i];

            }

            //       DATA_TYPE = DataType.SHORT_MATRIX;
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(short_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(short_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Int")) {
            if (initBool) {
                int_vec = new int[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {

                int_vec[i] = (int) v[i];

            }

            //           DATA_TYPE = DataType.INT_MATRIX;
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(int_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(int_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Float")) {
            if (initBool) {
                float_vec = new float[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {

                float_vec[i] = (float) v[i];

            }

            //          DATA_TYPE = DataType.INT_MATRIX;
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(float_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(float_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Long")) {

            long_vec = v;
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(long_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(long_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Double")) {
            if (initBool) {
                double_vec = new double[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {
                double_vec[i] = (double) v[i];
            }

            //          DATA_TYPE = DataType.DOUBLE_MATRIX;
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(double_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(double_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        }

    }

    private void processDouble(double v[], int nRow, int nCol) {
        int len = v.length;
        if (dataType.equalsIgnoreCase("Byte")) {
            if (initBool) {
                byte_vec = new byte[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {
                byte_vec[i] = (byte) v[i];
            }
            ddByteSwap.setByteVector(byte_vec);
            //////////////////////////
            //          DATA_TYPE = DataType.BYTE_MATRIX;
            try {
                if (formatMethod == 1) {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(byte_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Short")) {

            if (initBool) {
                short_vec = new short[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {

                short_vec[i] = (short) v[i];

            }

            //          DATA_TYPE = DataType.SHORT_MATRIX;
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(short_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(short_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Int")) {
            if (initBool) {
                int_vec = new int[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {

                int_vec[i] = (int) v[i];

            }

            //          DATA_TYPE = DataType.INT_MATRIX;
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(int_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(int_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Float")) {
            if (initBool) {
                float_vec = new float[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {

                float_vec[i] = (float) v[i];

            }

            //          DATA_TYPE = DataType.INT_MATRIX;
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(float_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(float_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Long")) {
            if (initBool) {
                long_vec = new long[nRow * nCol];
                initBool = false;
            }

            for (int i = 0; i < len; i++) {

                long_vec[i] = (long) v[i];

            }

            //          DATA_TYPE = DataType.INT_MATRIX;
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(long_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(long_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        } else if (dataType.equalsIgnoreCase("Double")) {

            double_vec = v;
            try {
                if (formatMethod == 0) { // binary
                    ddByteSwap.swap(double_vec);
                } else {               // text
                    strbuffer.delete(0, strbuffer.length());
                    for (int i = 0; i < nRow; i++) {
                        for (int j = 0; j < nCol; j++) {
                            strbuffer.append(double_vec[i * nCol + j]).append(' ');
                        }
                        strbuffer.append('\n');
                    }

                }
            } catch (NullPointerException e) {
            }
        }

    }

    public synchronized void handleMessage(int thru, Object obj) {
        if (thru == 0) {

            int offset = 0;
            int len = 0;
            int m = 0;
            int n = 0;

            if (obj instanceof Byte) {

                Byte f = (Byte) obj;
                byte_scalar = f.byteValue();
                ddByteSwap.byteToVec(byte_scalar);
            } else if (obj instanceof byte[]) {

                byte_vec = (byte[]) obj;
                processByte(byte_vec, 1, byte_vec.length);
            } else if (obj instanceof byte[][]) {

                byte_mat = (byte[][]) obj;
                int nrow = byte_mat.length;
                int ncol = byte_mat[0].length;

                if (initBool) {
                    byte_vec = new byte[nrow * ncol];
                    initBool = false;
                }
                for (int i = 0; i < nrow; i++) {
                    for (int j = 0; j < ncol; j++) {
                        byte_vec[i] = byte_mat[i][j];
                    }
                }
                processByte(byte_vec, nrow, ncol);
            } else if (obj instanceof Short) {

                Short f = (Short) obj;
                short_scalar = f.shortValue();
                ddByteSwap.swap(short_scalar);

            } else if (obj instanceof short[]) {

                short_vec = (short[]) obj;

                processShort(short_vec, 1, short_vec.length);

            } else if (obj instanceof short[][]) {

                short_mat = (short[][]) obj;
                int nrow = short_mat.length;
                int ncol = short_mat[0].length;

                if (initBool) {
                    short_vec = new short[nrow * ncol];
                    initBool = false;
                }
                for (int i = 0; i < nrow; i++) {
                    for (int j = 0; j < ncol; j++) {
                        short_vec[i] = short_mat[i][j];
                    }
                }

                processShort(short_vec, nrow, ncol);
            } else if (obj instanceof Integer) {

                Integer f = (Integer) obj;
                int_scalar = f.intValue();
                ddByteSwap.swap(int_scalar);
            } else if (obj instanceof int[]) {

                int_vec = (int[]) obj;
                processInt(int_vec, 1, int_vec.length);
            } else if (obj instanceof int[][]) {

                int_mat = (int[][]) obj;
                int nrow = int_mat.length;
                int ncol = int_mat[0].length;

                if (initBool) {
                    int_vec = new int[nrow * ncol];
                    initBool = false;
                }
                for (int i = 0; i < nrow; i++) {
                    for (int j = 0; j < ncol; j++) {
                        int_vec[i] = int_mat[i][j];
                    }
                }

                processInt(int_vec, nrow, ncol);
            } else if (obj instanceof Float) {

                Float f = (Float) obj;
                float_scalar = f.floatValue();
                ddByteSwap.swap(float_scalar);
            } else if (obj instanceof float[]) {

                float_vec = (float[]) obj;

                processFloat(float_vec, 1, float_vec.length);

            } else if (obj instanceof float[][]) {

                float_mat = (float[][]) obj;
                int nrow = float_mat.length;
                int ncol = float_mat[0].length;

                if (initBool) {
                    float_vec = new float[nrow * ncol];
                    initBool = false;
                }
                for (int i = 0; i < nrow; i++) {
                    for (int j = 0; j < ncol; j++) {
                        float_vec[i] = float_mat[i][j];
                    }
                }

                processShort(short_vec, nrow, ncol);
            } else if (obj instanceof Long) {

                Long f = (Long) obj;
                long_scalar = f.longValue();
                ddByteSwap.swap(long_scalar);
            } else if (obj instanceof long[]) {

                long_vec = (long[]) obj;
                processLong(long_vec, 1, long_vec.length);
            } else if (obj instanceof long[][]) {

                long_mat = (long[][]) obj;

                int nrow = long_mat.length;
                int ncol = long_mat[0].length;

                if (initBool) {
                    long_vec = new long[nrow * ncol];
                    initBool = false;
                }
                for (int i = 0; i < nrow; i++) {
                    for (int j = 0; j < ncol; j++) {
                        long_vec[i] = long_mat[i][j];
                    }
                }

                processLong(long_vec, nrow, ncol);

            } else if (obj instanceof Double) {

                Double f = (Double) obj;
                double_scalar = f.doubleValue();
                ddByteSwap.swap(double_scalar);
            } else if (obj instanceof double[]) {

                double_vec = (double[]) obj;
                processDouble(double_vec, 1, double_vec.length);
            } else if (obj instanceof double[][]) {

                double_mat = (double[][]) obj;

                int nrow = double_mat.length;
                int ncol = double_mat[0].length;

                if (initBool) {
                    double_vec = new double[nrow * ncol];
                    initBool = false;
                }
                for (int i = 0; i < nrow; i++) {
                    for (int j = 0; j < ncol; j++) {
                        double_vec[i] = double_mat[i][j];
                    }
                }

                processDouble(double_vec, nrow, ncol);

            } else if (obj instanceof String) {
                strValue = obj.toString();

            }

            if (obj instanceof String) {
                byte_vec = obj.toString().getBytes();
            } else {
                byte_vec = ddByteSwap.get();
            }

            if (limitedFileBool) {
                if (byte_size_track >= byte_size_limit) {
                    initFileStream(filename);
                    byte_size_track = 0;
                }
                if (formatMethod == 0) { // binary

                    byte_size_track = byte_size_track + byte_vec.length;
                } else {               // text
                    byte_size_track = byte_size_track + strbuffer.length();
                }
            }

            //  turnRunOn();
        }

    }

    private void initFileStream(String fname) {

        if (syncBFOut != null) {
            try {

                syncBFOut.close();
                syncBFOut = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (unSyncBFOut != null) {
            try {

                unSyncBFOut.close();
                unSyncBFOut = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (dos != null) {
            try {

                dos.close();
                dos = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        fileNameInc++;
        fname = fname + fileNameInc;

        if (formatMethod == 0) { // binary
            try {
                dos = new DataOutputStream(new FileOutputStream(new File(fname)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else { //text

            if (fileSyncBool) {
                try {
                    syncBFOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fname), encoding));
                } catch (UnsupportedEncodingException ue) {
                    try {
                        syncBFOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fname)));
                    } catch (FileNotFoundException fne) {
                        fne.printStackTrace();
                    }
                } catch (FileNotFoundException fe) {
                    fe.printStackTrace();
                }
            } else {
                try {
                    unSyncBFOut = new UnsynchronizedBufferedWriter(new OutputStreamWriter(new FileOutputStream(fname), encoding));
                } catch (UnsupportedEncodingException ue) {
                    try {
                        unSyncBFOut = new UnsynchronizedBufferedWriter(new OutputStreamWriter(new FileOutputStream(fname)));
                    } catch (FileNotFoundException fne) {
                        fne.printStackTrace();
                    }
                } catch (FileNotFoundException fe) {
                    fe.printStackTrace();
                }
            }

        }
    }

    /**
     * When the user double-click on the flow graph this method is executed.
     * This method displays the gui window the sensor.
     *
     */
    public void setComponentGui(boolean _set) {
        showGuiBool = _set;
        gui.setVisible(_set);
    }

    public boolean getComponentGui() {
        return showGuiBool;
    }

    // If in headless mode this function will not be call
    public void updateGui() {
        gui.setInputMethod(inputMethod);
        gui.setProtocolMethod(protocolMethod);
        gui.setSocketMethod(socketMethod);
        gui.setFormatMethod(formatMethod);
        gui.setEndianMethod(endianMethod);
        gui.setDataType(dataType);
        gui.setBufferType(bufferType);
        gui.setBufferSize(bufferSize);
        gui.setFilename(filename);
        gui.setPort(portNum);
        ////////////////////

        gui.setSemiCheck(semiCheckBool);
        gui.setSpaceCheck(spaceCheckBool);
        gui.setCommaCheck(commaCheckBool);
        gui.setTabCheck(tabCheckBool);
        gui.setOtherCheck(otherCheckBool);
        gui.setIP(ipAddress);
        gui.setOtheValue(otherValue);
        gui.setNCol(nCol);

        gui.setFileSyncBool(fileSyncBool);
        gui.setLimitedFileBool(limitedFileBool);
        gui.setFileSize(fileSize);
        //////////////////
        gui.updateValue();
    }


    /////////////////////////////////////////
    protected String ipAddress = "127.0.0.1";
    protected String otherValue = "";
    protected int nCol = 0;
    protected boolean commaCheckBool = false;
    protected boolean spaceCheckBool = false;
    protected boolean semiCheckBool = false;
    protected boolean tabCheckBool = false;
    protected boolean otherCheckBool = false;
    // Setter

    public void setOtherCheck(boolean _b) {
        otherCheckBool = _b;
        if (otherCheckBool) {
            delimiter = otherValue;
        }
    }

    public void setFileSize(int _b) {
        fileSize = _b;
    }

    public void setLimitedFileBool(boolean _b) {
        limitedFileBool = _b;
    }

    public void setFileSyncBool(boolean _b) {
        fileSyncBool = _b;
    }

    public void setCommaCheck(boolean _b) {
        commaCheckBool = _b;
        if (commaCheckBool) {
            delimiter = ",";
        }
    }

    public void setSpaceCheck(boolean _b) {
        spaceCheckBool = _b;
        if (spaceCheckBool) {
            delimiter = " ";
        }
    }

    public void setSemiCheck(boolean _b) {
        semiCheckBool = _b;
        if (semiCheckBool) {
            delimiter = ";";
        }
    }

    public void setTabCheck(boolean _b) {
        tabCheckBool = _b;
        if (tabCheckBool) {
            delimiter = "\t";
        }
    }

//    public void setIP(String _s) {
//        ipAddress = _s;
//    }
//
//    public void setOtheValue(String _s) {
//        otherValue = _s;
//    }
//
//    public void setNCol(int _i) {
//        nCol = _i;
//    }
//
//    public void setInputMethod(int _i) {
//        inputMethod = _i;
//    }
//
//    public void setProtocolMethod(int _i) {
//        protocolMethod = _i;
//    }
//
//    public void setSocketMethod(int _i) {
//        socketMethod = _i;
//    }
//
//    public void setFormatMethod(int _i) {
//        formatMethod = _i;
//    }
//
//    public void setEndianMethod(int _i) {
//        endianMethod = _i;
//    }
//
//    public void setDataType(String _i) {
//        dataType = _i;
//    }
//
//    public void setBufferType(String _i) {
//        bufferType = _i;
//    }
//
//    public void setBufferSize(int _i) {
//        bufferSize = _i;
//    }
//
//    public void setFilename(String _i) {
//        filename = _i;
//    }
//
//    public void setPort(int _i) {
//        portNum = _i;
//    }
//
//    // Getter
//    public boolean getOtherCheck() {
//        return otherCheckBool;
//    }
//
//    public int getFileSize() {
//        return fileSize;
//    }
//
//    public boolean getLimitedFileBool() {
//        return limitedFileBool;
//    }
//
//    public boolean getFileSyncBool() {
//        return fileSyncBool;
//    }
//
//    public boolean getCommaCheck() {
//        return commaCheckBool;
//    }
//
//    public boolean getSpaceCheck() {
//        return spaceCheckBool;
//    }
//
//    public boolean getSemiCheck() {
//        return semiCheckBool;
//    }
//
//    public boolean getTabCheck() {
//        return tabCheckBool;
//    }
//
//    public String getIP() {
//        return ipAddress;
//    }
//
//    public String getOtheValue() {
//        return otherValue;
//    }
//
//    public int getNCol() {
//        return nCol;
//    }
//
//    public int getInputMethod() {
//        return inputMethod;
//    }
//
//    public int getProtocolMethod() {
//        return protocolMethod;
//    }
//
//    public int getSocketMethod() {
//        return socketMethod;
//    }
//
//    public int getFormatMethod() {
//        return formatMethod;
//    }
//
//    public int getEndianMethod() {
//        return endianMethod;
//    }
//
//    public String getDataType() {
//        return dataType;
//    }
//
//    public String getBufferType() {
//        return bufferType;
//    }
//
//    public long getBufferSize() {
//        return bufferSize;
//    }
//
//    public String getFilename() {
//        return filename;
//    }
//
//    public int getPort() {
//        return portNum;
//    }

    @Override
    public Object onExecute() {
        if (stopPress) {
            stop();
            return null;
        }
        try {

            int n = 0;
            if (inputMethod == 0) { // file

                if (formatMethod == 0) {  // binary
                    dos.write(byte_vec);
                } else {               // text
                    if (fileSyncBool) {  //sync buffered wrter
                        syncBFOut.write(strbuffer.toString());

                    } else {              // unsync buffered writer
                        unSyncBFOut.write(strbuffer.toString());
                    }
                    strbuffer.delete(0, strbuffer.length());
                }
            } else {  // socket

                if (protocolMethod == 0) {  //TCP take care of server & client
                    if (dos == null) {
                        connectTCP();
                    }
                    if (formatMethod == 0) {  // binary
                        dos.write(byte_vec);
                    } else {               // text
                        dos.writeUTF(strbuffer.toString());
                    }
                } else { //UDP Client only
                    if (formatMethod == 0) {  // binary
                        packet.setData(byte_vec, 0, byte_vec.length);
                    } else {               // text
                        String tmpStr = strbuffer.toString();
                        byte tmp_byte_vec[] = tmpStr.getBytes();
                        packet.setData(tmp_byte_vec, 0, tmp_byte_vec.length);
                    }
                    //      System.out.println("Sending...."+Math.random());
                    clientUDP.send(packet);
                }

            }
        } catch (SocketException se) {
            try {
                if (protocolMethod == 0) {
                    connectTCP();
                } else {
                    connectUDP();
                }
            } catch (ConnectException e) {
                //  consoleMsg.appendErrorMsgConsole("Client trying to connect with server on " + ipAddress + ":" + portNum);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SocketTimeoutException ste) {
            System.out.println("Socket time out " + ste.getMessage());
        } catch (NullPointerException ne) {
            System.out.println("-------------->Null exception");
            stop();
        } catch (Exception e) {
            System.out.println("Exception ddd");
            e.printStackTrace();
        }

        return null;
    }

    private boolean visibleBool = false;
    @Override
    public void mouseDoubleClick() {

        visibleBool = !visibleBool;
        this.setComponentGui(visibleBool);
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public boolean start() {
        stopPress = false;

        ddByteSwap.reset();
        initBool = true;
        byte_size_track = 0;
        if (formatMethod == 0) {//Binary
            dataTypeId = 0;
            int byteSize = 1;
            if (dataType.equalsIgnoreCase("Short")) {
                byteSize = 2;
                dataTypeId = 1;
                short_vec = new short[bufferSize];
            } else if (dataType.equalsIgnoreCase("Int")) {
                byteSize = 4;
                dataTypeId = 2;
                int_vec = new int[bufferSize];
            } else if (dataType.equalsIgnoreCase("Float")) {
                byteSize = 4;
                dataTypeId = 3;
                float_vec = new float[bufferSize];
            } else if (dataType.equalsIgnoreCase("Long")) {
                byteSize = 8;
                dataTypeId = 4;
                long_vec = new long[bufferSize];
            } else if (dataType.equalsIgnoreCase("Double")) {
                byteSize = 8;
                dataTypeId = 5;
                double_vec = new double[bufferSize];
            }
            if (!dataType.equalsIgnoreCase("String")) {
                byte_vec = new byte[bufferSize * byteSize];
                byte_buf = ByteBuffer.wrap(byte_vec);
            }
        } else {  //Text
        }

        if (inputMethod == 0) { // File

            if (formatMethod == 0) { // binary
                try {
                    dos = new DataOutputStream(new FileOutputStream(new File(filename)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    //    controlApp.getControlApp().stopProcess();
                    //  e.printStackTrace();
                    // controlApp.stopRunProcess();
                    //consoleMsg.appendErrorMsgConsole("File " + filename + " not found for output component");
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else { //text

                if (fileSyncBool) {
                    try {
                        syncBFOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), encoding));
                    } catch (UnsupportedEncodingException ue) {
                        try {
                            syncBFOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename)));
                        } catch (FileNotFoundException fne) {
                            fne.printStackTrace();
                        }
                    } catch (FileNotFoundException fe) {

                        //    controlApp.getControlApp().stopProcess();
                        //  e.printStackTrace();
                        //controlApp.stopRunProcess();
                        //consoleMsg.appendErrorMsgConsole("File " + filename + " not found for output component");
                        return false;

                    }
                } else {
                    try {
                        unSyncBFOut = new UnsynchronizedBufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), encoding));
                    } catch (UnsupportedEncodingException ue) {
                        try {
                            unSyncBFOut = new UnsynchronizedBufferedWriter(new OutputStreamWriter(new FileOutputStream(filename)));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            //    controlApp.getControlApp().stopProcess();
                            //  e.printStackTrace();
//                            controlApp.stopRunProcess();
                            //  consoleMsg.appendErrorMsgConsole("File " + filename + " not found for output component");
                            return false;
                        }
                    } catch (FileNotFoundException fe) {
                        fe.printStackTrace();
                    }
                }

            }

        } else {  // Socket
            try {
                if (protocolMethod == 0) {  //tcp
                    if (socketMethod == 0) {  //Server
                        if (serverTCP == null) {
                            serverTCP = new ServerSocket(portNum);
                            //   serverTCP.setSoTimeout(2000);
                        }
                    }
                    // connectTCP();
                } else { // udp
                    connectUDP();
                }
            } catch (ConnectException ce) {
                //  System.out.println("Client trying to connect with server on "+ipAddress+":"+portNum);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (endianMethod == 1) {
            ddByteSwap.setLittleEndianBool(false);
        }

        if (limitedFileBool) {
            byte_size_limit = 1024 * 1024 * fileSize;
        }

        return true;
    }
    @Override
    public String getHelp() {

        String doc = "";
        
        return doc;
    }
    @Override
    public void stop() {

        stopPress = true;
        ////////////////
        short_vec = null;
        int_vec = null;
        float_vec = null;
        long_vec = null;
        double_vec = null;
        byte_vec = null;

        if (syncBFOut != null) {
            try {

                syncBFOut.close();
                syncBFOut = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (unSyncBFOut != null) {
            try {

                unSyncBFOut.close();
                unSyncBFOut = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            if (outStream != null) {
                outStream.flush();
                outStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (serverTCP != null) {
            try {

                serverTCP.close();
                serverTCP = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (clientUDP != null) {
            try {
                clientUDP.close();
                clientUDP = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (socket != null) {
            try {

                socket.close();
                socket = null;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        socket = null;

        if (dos != null) {
            try {

                dos.close();
                dos = null;
            } catch (IOException e) {
                e.printStackTrace();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onPropertyChanged(String key, Object value) {
 
    }

    @Override
    public void loadProperty(String key, Object value) {

    }

    @Override
    public int getPlatformSupport() {

        return 0;
    }

    @Override
    public void onDestroy() {
        gui.setVisible(false);
        gui.dispose();
    }
    


}
