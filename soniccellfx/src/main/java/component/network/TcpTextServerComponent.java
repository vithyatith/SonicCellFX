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
package component.network;

import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.msg.PubSubSingleton;
import com.sonicmsgr.soniccell.net.TcpTextServer;

/**
 *
 * @author yada
 */
public class TcpTextServerComponent extends ComponentAbstraction {

    private int serverPort = 8888;
    private Thread mainThread;
    private TcpTextServer serverSocket;
    private boolean initStart = false;
    private String message = "";
    private boolean encodeUTF = true;

    public TcpTextServerComponent() {
        setName("TextServer");
        this.setProperty("serverPort", (Integer) serverPort);
        this.setProperty("message", message);
        this.setProperty("encodeUTF", encodeUTF);
        this.addInput(new DataTypeIO("string", "Message"));
        this.addOutput(new DataTypeIO("string", "received message from client"));
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public Object onExecute() {
        if (initStart) {
            initStart = false;
            startServer();
        }
        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {
        if(thru==0){
           // serverSocket.
           
        }
    }

    @Override
    public boolean start() {
        serverPort = (int) (Double.parseDouble(getProperty("serverPort").toString()));
        encodeUTF = Boolean.parseBoolean((getProperty("encodeUTF").toString()));
        initStart = true;
        return true;
    }

    private void shutdownServer() {

        if (serverSocket != null) {
            serverSocket.stopServer();
            serverSocket = null;
        }

        if (mainThread != null) {
            try {
                Thread t = mainThread;
                t.interrupt();
                mainThread = null;
                t = null;
            } catch (Exception e) {

            }

            mainThread = null;
        }

    }

    private void startServer() {
        shutdownServer();
        try {
            mainThread = new Thread() {
                @Override
                public void run() {
                    try {
                        serverSocket = new TcpTextServer(serverPort, new TcpTextServer.CallBack() {
                            @Override
                            public void onReceivedMessage(String msg) {
                                sendData(0, msg);
                            }

                            @Override
                            public void onStatus(int state) {

                            }
                        });

                        serverSocket.setUTFEncoding(encodeUTF);
                        serverSocket.startServer();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            mainThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stop() {
        initStart = false;

        shutdownServer();
    }

    @Override
    public void loadProperty(String key, Object val) {

        if (key.equalsIgnoreCase("serverPort")) {

            serverPort = (int) (Double.parseDouble(getProperty("serverPort").toString()));
        }
        if (key.equalsIgnoreCase("encodeUTF")) {
            encodeUTF = Boolean.parseBoolean((getProperty("encodeUTF").toString()));
        }
    }

    @Override
    public void mouseDoubleClick() {
    }

    @Override
    public int getPlatformSupport() {

        return 0;
    }

    @Override
    public void onPropertyChanged(String key, Object value) {
        if (key.equalsIgnoreCase("message")) {
            message = (String) value;
            System.out.println("Server outside send from propertry change = " + message);
            if (serverSocket != null) {
                System.out.println("Server inside send from propertry change = " + message);
                serverSocket.sendMessageBackToAllClient(message+"\n");
            }
        }

        if (key.equalsIgnoreCase("encodeUTF")) {
            encodeUTF = Boolean.parseBoolean((getProperty("encodeUTF").toString()));
            if(serverSocket!=null){
                 serverSocket.setUTFEncoding(encodeUTF);
            }
        }
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
