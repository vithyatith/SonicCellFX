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
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by thing1 on 4/7/15.
 */
public class TcpClientSocket {

    private Socket socket = null;
    private DataOutputStream streamOut = null;
    private ChatClientThread client = null;

    private CallBack callBack;

    public static final int WAITING = 0;
    public static final int CONNECTED = 1;
    public static final int DISCONNECTED = 2;

    private int TIMEOUT_IN_MILLISECOND = 5000; //5 second

    private String TAG = "SOCKET-VT";

    // public static final String IOEXECEPTION_ERROR = "IOException";
    private boolean isConected = false;
    private boolean encodeUTF = true;

    public boolean getIsConnected() {
        return isConected;
    }

    public void setUTFEncoding(boolean b) {
        encodeUTF = b;
    }

    public void isSetConnected(boolean b) {
        isConected = b;
    }

    public TcpClientSocket(String serverName, int serverPort, CallBack callBack) {
        this.callBack = callBack;

        try {
            callBack.onStatus(WAITING);
            InetSocketAddress inetSocketAddress = new InetSocketAddress(serverName, serverPort);
            socket = new Socket();
            socket.connect(inetSocketAddress, TIMEOUT_IN_MILLISECOND);

            callBack.onStatus(CONNECTED);
            start();
        } catch (UnknownHostException uhe) {
            Log.v(TAG, "Host unknown: " + uhe.getMessage());
            callBack.onErrorMessage(uhe.getMessage());
        } catch (IOException ioe) {
            Log.v(TAG, "Unexpected exception: " + ioe.getMessage());
            if (callBack != null) {
                callBack.onErrorMessage(ioe.getMessage());
            }
        }

    }

    public boolean isSocketedConnect() {

        if (socket != null) {
            return socket.isConnected();
        }
        return false;
    }

    public Socket getSocket() {
        return socket;
    }

    private void handle(String msg) {
        if (msg == null) {
            return;
        }
        if (msg.equals(".exit")) {
            stopConnection();
        } else {
            if (callBack != null) {
                isSetConnected(true);
                callBack.onReceivedMessage(msg);

                try {
                    streamOut.flush();
                } catch (SocketException se) {
                    Log.v("VT", "from: " + se.getMessage());
                    isSetConnected(false);
                    callBack.onErrorMessage(se.getMessage());
                } catch (IOException e) {
                    callBack.onErrorMessage(e.getMessage());
                    e.printStackTrace();
                    isSetConnected(false);
                }
            }
        }
    }

    public void start() throws IOException {
        streamOut = new DataOutputStream(socket.getOutputStream());
        client = new ChatClientThread(this, socket);
    }

    public void shutDownEverything() {
        setRunThread(false);

        if (client != null) {
            stopConnection();

            try {
                client.interrupt();
            } catch (Exception e) {

            }

            Thread t = client;
            client = null;
            t = null;

        }
    }

    public void sendMessage(final String str) {

        //  new Thread(new Runnable() {
        //    public void run() {
        if (streamOut != null) {
            try {

                System.out.println("Sending data = " + str+" encodingUTF = "+encodeUTF);
                // streamOut.write(str.getBytes(), 0, str.length());
                if (encodeUTF) {
                    streamOut.writeUTF(str);
                } else {
                    streamOut.write(str.getBytes(), 0, str.length());
                   // streamOut.write(str.getBytes());
                }
                // Make sure its byte
                streamOut.flush();
            } catch (SocketException se) {
                Log.v("VT", "SocketException e " + se.getMessage());
                isSetConnected(false);
                System.out.println(se.getMessage());
                callBack.onErrorMessage(se.getMessage());
            } catch (IOException e) {
                Log.v(TAG, "Host unknown: " + e.getMessage());
                callBack.onErrorMessage(e.getMessage());
                e.printStackTrace();

            }
        }
        //    }
        //   }).start();
    }

    public void stopConnection() {
        if (callBack != null) {
            callBack.onStatus(DISCONNECTED);
        }

        try {
            if (streamOut != null) {
                streamOut.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ioe) {
            Log.v(TAG, "Error closing ...");
            callBack.onErrorMessage(ioe.getMessage());
        }
        if (client != null) {
            client.close();
        }
    }

    public void setRunThread(boolean b) {
        if (client != null) {
            client.setRunThread(b);
        }
    }

    class ChatClientThread extends Thread {

        private Socket socket = null;
        private TcpClientSocket client = null;
        private BufferedReader streamIn = null;
        private char cbufs[] = new char[1024];

        private transient boolean runThreadBool = true;

        public void setRunThread(boolean b) {
            runThreadBool = b;
        }

        public ChatClientThread(TcpClientSocket _client, Socket _socket) {
            client = _client;
            socket = _socket;

            open();
            start();
        }

        public void open() {
            try {
                streamIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                isSetConnected(true);
            } catch (IOException ioe) {
                //  android.util.Log.v(TAG, "Error getting input stream: " + ioe);
                isSetConnected(false);
                client.stopConnection();
            }
        }

        public void close() {
            try {
                if (streamIn != null) {
                    streamIn.close();
                }
            } catch (IOException ioe) {
                Log.v(TAG, "Error closing input stream: " + ioe);
            }
        }

        public void run() {

            if (callBack != null) {
                callBack.onConnected();
            }

            while (runThreadBool) {
                try {

                   
                    // String dataStr = streamIn.readLine();
                    int n = streamIn.read(cbufs);
                    
                    if(n<0){
                        try {
                            Thread.sleep(200);
                            continue;
                        } catch (InterruptedException ex) {
                            //Logger.getLogger(TcpClientSocket.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                     System.out.println("Inside client received from server = ");
                    System.out.println("n======= "+n);
                    String dataStr = new String(cbufs);
                    for(int i=0; i<cbufs.length; i++){
                        cbufs[i] = (char)0;
                    }
                    dataStr = dataStr.trim();

                    client.handle(dataStr);

                } catch (IOException ioe) {

                    // Connection Timeout
                    callBack.onErrorMessage(ioe.getMessage());
                    Log.v(TAG, "Listening error: " + ioe.getMessage());
                    client.stopConnection();

                    if (callBack != null) {
                        callBack.onConnectionClosed();
                    }
                    runThreadBool = false;
                    client.isSetConnected(false);
                    break;
                }
            }
        }
    }

    public interface CallBack {

        void onReceivedMessage(String msg);

        void onStatus(int state);

        void onErrorMessage(String error);

        void onConnected();

        void onConnectionClosed();
    }

}
