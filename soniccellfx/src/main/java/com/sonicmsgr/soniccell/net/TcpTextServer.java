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
package com.sonicmsgr.soniccell.net;

import com.sonicmsgr.soniccell.Log;
import java.net.*;
import java.io.*;

public class TcpTextServer implements Runnable {

    private ChatServerThread clients[] = new ChatServerThread[50];
    private ServerSocket server = null;
    private Thread thread = null;
    private int clientCount = 0;
    private CallBack callBack;
    public static final int WAITING = 0;
    public static final int CONNECTED = 1;
    public static final int DISCONNECTED = 2;
    private int port = 8833;
    private boolean encodeUTF = true;

    private String id[] = new String[50];

    public TcpTextServer(int port, CallBack callBack) {
        this.callBack = callBack;
        this.port = port;
    }

    public void setUTFEncoding(boolean b) {
        encodeUTF = b;
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
        int len = clients.length;
        try {
            for (int i = 0; i < len; i++) {
                if (clients[i] != null) {
                    try {
                        clients[i].close();
                        clients[i].interrupt();
                    } catch (InterruptedIOException e) {
                        Log.v("VT", " e = " + e.getMessage());
                    }
                }

            }
        } catch (IOException e) {
            Log.v("VT", "Can not bind to port " + port + ": " + e.getMessage());
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
        while (thread != null) {
            try {
                callBack.onStatus(WAITING);
                Log.v("VT", "Waiting for a client ...");
                addThread(server.accept());
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

    private int findClient(int ID) {
        for (int i = 0; i < clientCount; i++) {
            if (clients[i].getID() == ID) {
                return i;
            }
        }
        return -1;
    }

    private synchronized void handle(int ID, String input) {
        input = input.trim();
        if (input.equals(".bye")) {
            clients[findClient(ID)].send(".bye");
            remove(ID);
        } else {
            for (int i = 0; i < clientCount; i++) {
                if (callBack != null) {
                    callBack.onReceivedMessage(input);
                }
            }
        }
    }

    public void sendMessageBackToClient(int index, String msg) {
        if (clients != null) {
            if (clients.length < index) {
                clients[index].send(msg);
            }
        }
    }

    public void sendMessageBackToAllClient(String msg) {
        if (clients != null) {
            for (int i = 0; i < clients.length; i++) {
                if (clients[i] != null) {
                    clients[i].send(msg);
                }
            }
        }
    }

    private synchronized void remove(int ID) {
        int pos = findClient(ID);
        if (pos >= 0) {
            ChatServerThread toTerminate = clients[pos];
            Log.v("VT", "Removing client thread " + ID + " at " + pos);
            if (pos < clientCount - 1) {
                for (int i = pos + 1; i < clientCount; i++) {
                    clients[i - 1] = clients[i];
                }
            }
            clientCount--;
            try {
                toTerminate.close();
            } catch (IOException ioe) {
                Log.v("VT", "Error closing thread: " + ioe);
            }
            //     toTerminate.stop();
        }
    }

    private void addThread(Socket socket) {
        if (clientCount < clients.length) {
            clients[clientCount] = new ChatServerThread(this, socket);
            try {
                clients[clientCount].setID(clientCount);
                clients[clientCount].open();
                clients[clientCount].start();
                clientCount++;
            } catch (IOException ioe) {
                Log.v("VT", "Error opening thread: " + ioe);
            }
        } else {
            Log.v("VT", "Client refused: maximum " + clients.length + " reached.");
        }
    }

    public static void main(String args[]) {

        TcpTextServer tcpTextServer = new TcpTextServer(1900, new CallBack() {
            @Override
            public void onReceivedMessage(String msg) {
                Log.v("VT", "data is received: msg");
                //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onStatus(int state) {
                //To change body of generated methods, choose Tools | Templates.
            }
        });

        tcpTextServer.setUTFEncoding(false);
        tcpTextServer.startServer();

//        WifiChatServer server = null;
//
//
//        args = new String[]{"7888"};
//
//        if (args.length != 1)
//            Log.v("VT","Usage: java ChatServer port");
//        else
//            server = new WifiChatServer(Integer.parseInt(args[0]));
        /* as before */
    }

    private class ChatServerThread extends Thread {

        private TcpTextServer server = null;
        private Socket socket = null;
        private int ID = -1;
        private DataInputStream streamIn = null;
        private DataOutputStream streamOut = null;

        public transient boolean runBool = true;
        public byte bytes[] = new byte[1024];

        public ChatServerThread(TcpTextServer _server, Socket _socket) {
            super();
            server = _server;
            socket = _socket;
            ID = socket.getPort();
        }

        public void send(String msg) {
            Thread th = new Thread() {
                @Override
                public void run() {
                    try {
                        Log.v("VT", "My ID is " + ID);

                        if (encodeUTF) {
                            streamOut.writeUTF(msg);
                        } else {
                            streamOut.write(msg.getBytes(), 0, msg.length());
                        }
                        streamOut.flush();

                    } catch (IOException ioe) {
                        Log.v("VT", ID + " ERROR sending: " + ioe.getMessage());
                        server.remove(ID);
                        stop();
                    }
                }
            };

            th.start();

        }

        protected void setID(int id) {
            ID = id;
        }

        public int getID() {
            return ID;
        }

        @Override
        public void run() {

            while (runBool) {
                try {

                    String data;
                    if (encodeUTF) {
                        data = streamIn.readUTF();
                    } else {
                        streamIn.read(bytes);
                        data = new String(bytes).trim();
                    }
                    server.handle(ID, data);
                    resetBytes(bytes);

                } catch (IOException ioe) {

                    Log.v("VT", ID + " ERROR reading: " + ioe.getMessage());
                    server.remove(ID);

                    try {
                        close();
                        if (callBack != null) {
                            callBack.onStatus(DISCONNECTED);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void open() throws IOException {
            streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        }

        public void close() throws IOException {
            runBool = false;
            if (socket != null) {
                socket.close();
            }
            if (streamIn != null) {
                streamIn.close();
            }
            if (streamOut != null) {
                streamOut.close();
            }
        }
    }

    public interface CallBack {

        void onReceivedMessage(String msg);

        void onStatus(int state);
    }

    private void resetBytes(byte data[]) {
        if (data != null) {
            int len = data.length;
            for (int i = 0; i < len; i++) {
                data[i] = 0;
            }
        }
    }
}
