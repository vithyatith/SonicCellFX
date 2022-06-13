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
package component.network;

import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


import com.sonicmsgr.util.Log;

import java.io.*;

public class TCPServer implements Runnable {

    private ConcurrentHashMap<Long, ChatServerThread> clientMap = new ConcurrentHashMap<Long, ChatServerThread>();

    private ServerSocket server = null;
    private Thread thread = null;

    // private CallBack callBack;
    private TCPCallBack tcpCallBack;

    public static final int WAITING = 0;
    public static final int CONNECTED = 1;
    public static final int DISCONNECTED = 2;
    private int port = 7001;

    // private String id[] = new String[50];
    private ConcurrentHashMap<Long, DeviceData> deviceMap = new ConcurrentHashMap<Long, DeviceData>();

    private CallBack callBack = new CallBack() {

        @Override
        public void onReceivedMessage(long clientId, int len, byte[] msg) {

        }

        @Override
        public void onStatus(int state) {
        }

    };

    public TCPServer() {
    }

    public TCPServer(int port, TCPCallBack callBack) {
        setPortAndCallBack(port, callBack);
    }

    public void setPortAndCallBack(int port, TCPCallBack callBack) {
        this.tcpCallBack = callBack;
        this.port = port;
        List<ChatServerThread> list = Collections.synchronizedList(new ArrayList<ChatServerThread>());

    }

    public void startServer() {
        startServer(port);
    }

    public void startServer(int port) {
        try {
            server = new ServerSocket(port);
            start();
        } catch (IOException ioe) {
            Log.v("VT", "Can not bind to port " + port + ": " + ioe.getMessage());
        }
    }

    public void stopServer() {

        Iterator<Entry<Long, ChatServerThread>> it = clientMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Long, ChatServerThread> pair = (Entry<Long, ChatServerThread>) it.next();

            long id = pair.getKey();
            ChatServerThread clients = pair.getValue();

            clients.close();
            clients.interrupt();

        }

        try {
            if (server != null) {
                server.close();
                server = null;
            }
            stopThread();
        } catch (IOException e) {
            Log.v("VT", " e = " + e.getMessage());
        }

    }

    public void run() {

        Log.v("VT", "Waiting for a client ...");

        while (thread != null) {
            try {
                callBack.onStatus(WAITING);
                addClient(server.accept());
                callBack.onStatus(CONNECTED);

            } catch (IOException ioe) {
                Log.v("VT", "Server accept error: " + ioe);
                stopThread();
            }
        }
    }

    private void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    private void stopThread() {

        try {
            if (thread != null) {
                Thread tmpThread = thread;
                tmpThread.interrupt();
                thread = null;
            }
        } catch (Exception e) {
            Log.v("VT", " et");
        }
    }

    private synchronized void handle(long clientId, int len, byte input[]) {

        if (callBack != null) {
            callBack.onReceivedMessage(clientId, len, input);
        }
    }

    public void sendMessageBackToClient(long key, byte msg[]) {
        ChatServerThread clients = clientMap.get(key);
        if (clients != null) {
            clients.send(msg);
        }
    }

    public void sendMessageBackToAllClient(byte msg[]) {
        Iterator<Entry<Long, ChatServerThread>> it = clientMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Long, ChatServerThread> pair = (Entry<Long, ChatServerThread>) it.next();
            long key = pair.getKey();
            sendMessageBackToClient(key, msg);
        }
    }

    private synchronized void remove(long id) {
        ChatServerThread client = clientMap.get(id);
        if (client != null) {
            client.close();
            clientMap.remove(id);
        }
    }

    private long generateRandomKey() {

        long generatedInteger = new Random().nextLong();
        return generatedInteger;
    }

    private void addClient(Socket socket) {
        ChatServerThread client = new ChatServerThread(this, socket);
        long key = 0;
        do {
            key = generateRandomKey();
        } while (clientMap.containsKey(key));

        clientMap.put(key, client);

        try {
            client.setID(key);
            client.open();
            client.start();
        } catch (IOException ioe) {
            Log.v("VT", "Error opening thread: " + ioe);
        }
    }

    private byte dataBytes[] = new byte[1];
    private ByteBuffer byteBuffer = ByteBuffer.allocate(dataBytes.length);

    public void sendDataCollectionRawDataBackToClient(long clientId, byte dataBytes[]) {
        if ((dataBytes != null)) {
            sendMessageBackToClient(clientId, dataBytes);
        }
    }


    public void runDataSimulator(int frequency, boolean littleEndian) {
        try {
            TCPCallBack tcpCallBack = new TCPCallBack() {

                byte data[] = new byte[1];
                byte ret[] = new byte[2];

                @Override
                public void onInit() {

                }

                @Override
                public void onDataCollection(long clientId, int sampleRate, int channel, int traceLength, int trigMode,
                        int trigSource, float trigLevel, float trigHyst, int trigPercentage, int gain) {

                    if (data.length != (traceLength * 2)) {
                        data = new byte[traceLength * 2];
                    }
                    float bin = 1 / (sampleRate * 1.0f);

                    float time = 0;
                    for (int i = 0; i < traceLength; i++) {
                        time = time + bin;
                        short x = 0;
                        if (frequency < 0) {
                            x = (short) (Math.random() * Short.MAX_VALUE);
                        } else {
                            x = (short) (Math.cos(2 * Math.PI * frequency * time) * Short.MAX_VALUE);
                        }

                        // Little Endian
                        if (littleEndian) {
                            ret[0] = (byte) (x & 0xFF);
                            ret[1] = (byte) (x >> 8 & 0xFF);
                        } else {
                            ret[0] = (byte) (x >> 8 & 0xFF);
                            ret[1] = (byte) (x & 0xFF);
                        }
                        data[i * 2] = ret[0];
                        data[i * 2 + 1] = ret[1];
                    }

                    sendDataCollectionRawDataBackToClient(clientId, data);
                }
            };

            setPortAndCallBack(7001, tcpCallBack);

            startServer();

        } catch (Exception e) {

        }
    }



    public static void main(String args[]) {

        TCPServer tcp = new TCPServer();

    }

 

    private class ChatServerThread extends Thread {

        private int SIZE = 8192;

        private TCPServer server = null;
        private Socket socket = null;
        private long ID = -1;
        private InputStream streamIn = null;
        private OutputStream streamOut = null;

        public transient boolean runBool = true;
        public byte bytes[] = new byte[SIZE];
        public byte outputBytes[] = new byte[SIZE];
        public byte leftOverBytes[] = new byte[1];

        public ChatServerThread(TCPServer _server, Socket _socket) {
            super();
            server = _server;
            socket = _socket;
            ID = socket.getPort();
            System.out.println("Connected to client " + socket.toString());
        }

        public synchronized void send(byte msg[]) {
            Thread th = new Thread() {
                @Override
                public void run() {
                    try {

                        int msgLen = msg.length;
                        int outLen = outputBytes.length;

                        if (msgLen <= outLen) {
                            streamOut.write(msg);
                            if (streamOut != null) {
                                streamOut.flush();
                            }
                        } else {

                            int n = msgLen / outLen;
                            int leftOver = msgLen - n * outLen;
                            if (leftOver != leftOverBytes.length) {
                                leftOverBytes = new byte[leftOver];
                                System.out.println("Newing leftover...");
                            }

                            for (int i = 0; i < n; i++) {
                                for (int j = 0; j < outLen; j++) {
                                    outputBytes[j] = msg[i * outLen + j];
                                }
                                streamOut.write(outputBytes);
                                if (streamOut != null) {
                                    streamOut.flush();
                                }
                            }

                            for (int i = 0; i < leftOver; i++) {
                                leftOverBytes[i] = msg[n * outLen + i];
                            }
                            streamOut.write(leftOverBytes);
                            if (streamOut != null) {
                                streamOut.flush();
                            }

                        }

                    } catch (IOException ioe) {
                        Log.v("VT", ID + " ERROR sending1: " + ioe.getMessage());
                        server.remove(ID);
                        stop();
                    } catch (NullPointerException ioe) {
                        Log.v("VT", ID + " ERROR sending2: " + ioe.getMessage());
                    }
                }
            };

            th.start();

        }

        protected void setID(long id) {
            ID = id;
        }

        public long getID() {
            return ID;
        }

        @Override
        public void run() {

            while (runBool) {
                try {

                    int nRead = streamIn.read(bytes);
                    if (nRead < 0) {
                        try {
                            Thread.sleep(200);
                            continue;
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            //e.printStackTrace();
                        }
                    }

                    server.handle(ID, nRead, bytes);
                    resetBytes(bytes);

                } catch (IOException ioe) {

                    Log.v("VT", ID + " ERROR reading: " + ioe.getMessage());
                    server.remove(ID);

                    close();
                    if (callBack != null) {
                        callBack.onStatus(DISCONNECTED);
                    }

                }
            }
        }

        public void open() throws IOException {
            streamIn = new BufferedInputStream(socket.getInputStream());
            streamOut = new BufferedOutputStream(socket.getOutputStream());
        }

        public void close() {
            runBool = false;
            if (socket != null) {

                try {
                    socket.close();
                } catch (SocketException e) {

                } catch (IOException e) {

                } catch (Exception e) {

                }
                socket = null;
            }
            if (streamIn != null) {
                try {
                    streamIn.close();
                } catch (SocketException e) {

                } catch (IOException e) {

                } catch (Exception e) {

                }
                streamIn = null;
            }
            if (streamOut != null) {

                try {
                    streamOut.close();
                } catch (SocketException e) {

                } catch (IOException e) {

                } catch (Exception e) {

                }
                streamOut = null;
            }
        }
    }

    private void resetBytes(byte data[]) {
        if (data != null) {
            int len = data.length;
            for (int i = 0; i < len; i++) {
                data[i] = 0;
            }
        }
    }

    public interface CallBack {

        void onReceivedMessage(long clientId, int len, byte msg[]);

        void onStatus(int state);
    }

    public interface TCPCallBack {

        void onInit();

        void onDataCollection(long clientId, int sampleRate, int channel, int traceLength, int trigMode, int trigSource,
                float trigLevel, float trigHyst, int trigPercentage, int gain);
    }

    public static class DeviceData {

        public long cliendId = 0;
        public int channel = 0;

        public int numberOfTraces = 0;
        public int traceLength = 0;

        public int sampleRate = 0;
        public int digitizer = 0;

        public int trigMode = 0;
        public int trigSource = 0;

        public float trigLevel = 0;
        public float trigHyst = 0;

        public int trigPercentage = 0;
        public int gain = 0;
    }
}
