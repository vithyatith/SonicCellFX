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
import com.sonicmsgr.soniccell.Log;
import com.sonicmsgr.soniccell.msg.PubSubSingleton;
import com.sonicmsgr.soniccell.net.TcpClientSocket;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author yada
 */
public class TcpClientComponent extends ComponentAbstraction {

    private String ipAddress = "127.0.0.1";
    private int portNumber = 8888;
    private String message = "";
    private Thread mainThread;
    private TcpClientSocket clientSocket;
    private boolean initStart = false;
    private boolean startBool = false;
    private boolean encodeUTF = true;

    private Queue<String> messageQue = new LinkedList<String>();

    public TcpClientComponent() {
        setName("TextClient");
        this.setProperty("ipAddress", ipAddress);
        this.setProperty("portNumber", (Integer) portNumber);
        this.setProperty("message", message);
        this.setProperty("encodeUTF", encodeUTF);
        this.addInput(new DataTypeIO("string", "send message to server","message"));
        this.addOutput(new DataTypeIO("string", "response back from server"));
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public Object onExecute() {
        if (initStart) {
            initStart = false;
            startConnection(ipAddress, portNumber);
            if (clientSocket != null) {
                clientSocket.sendMessage(message+"\r\n");
            }
        }

        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {
        if (clientSocket != null) {
            message = (String) obj;
            //System.out.println("sending to server = " + message);
            clientSocket.sendMessage(message+"\r\n");
        } else {
            messageQue.add((String) obj);
            // System.out.println("clientSocket is null " + Math.random());
        }
    }

    @Override
    public boolean start() {
        startBool = true;
        initStart = true;
        ipAddress = getProperty("ipAddress").toString();
        portNumber = (int) (Double.parseDouble(getProperty("portNumber").toString()));
        encodeUTF = Boolean.parseBoolean((getProperty("encodeUTF").toString()));

        message = (String) getProperty("message");

        return true;
    }

    private void shutDownSocket() {

        if (clientSocket != null) {
            clientSocket.shutDownEverything();
            clientSocket = null;
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

    private void startConnection(final String ipAddress, final int portNumber) {
        shutDownSocket();
        try {
            mainThread = new Thread() {
                @Override
                public void run() {
                    while (startBool) {
                        try {

                            clientSocket = new TcpClientSocket(ipAddress, portNumber, new TcpClientSocket.CallBack() {
                                @Override
                                public void onReceivedMessage(String msg) {
                                    System.out.println("Client received message = " + msg);
                                    sendData(0, msg);
                                }

                                @Override
                                public void onStatus(int state) {

                                }

                                @Override
                                public void onErrorMessage(String error) {

                                    PubSubSingleton.getIntance().send("Print", error);
                                    System.out.println("error = " + error);
                                }

                                @Override
                                public void onConnected() {

                                    sendData(0, "Connected");
                                }

                                @Override
                                public void onConnectionClosed() {
                                    sendData(0, "Connection colsed");
                                }
                            });

                            if (clientSocket.isSocketedConnect()) {

                                clientSocket.setUTFEncoding(encodeUTF);
                                
                                
          
                                System.out.println("Success connected.............. send message que + setting encoding..."+encodeUTF);
                                sendMessageQue();
                                break;
                            } else {
                                System.out.println("Socket not connectd. sleeping first.");
                                Thread.sleep(2000);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            mainThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendMessageQue() {
        if (clientSocket != null) {
            String msg = "";
            while ((msg = messageQue.poll()) != null) {
                clientSocket.sendMessage((String) msg+"\r\n");
            }
        }

    }

    @Override
    public void stop() {
        startBool = false;
        shutDownSocket();
    }

    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("ipAddress")) {
            ipAddress = (String) val;
        }

        if (key.equalsIgnoreCase("portNumber")) {
            portNumber = (int) (Double.parseDouble(getProperty("portNumber").toString()));
        }

        if (key.equalsIgnoreCase("encodeUTF")) {
             Log.v("VT","encodeUTF = "+encodeUTF);
            encodeUTF = Boolean.parseBoolean((getProperty("encodeUTF").toString()));
            
            if(clientSocket!=null){
                clientSocket.setUTFEncoding(encodeUTF);
            }
            
            
            Log.v("VT","after encodeUTF = "+encodeUTF);
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
            System.out.println("outside send from propertry change = " + message);
            if (clientSocket != null) {
                System.out.println("inside send from propertry change = " + message);
                clientSocket.sendMessage(((String) message)+"\r\n");
                
            }
        }else if (key.equalsIgnoreCase("encodeUTF")) {
            encodeUTF = Boolean.parseBoolean((getProperty("encodeUTF").toString()));
            if(clientSocket!=null){
                clientSocket.setUTFEncoding(encodeUTF);
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
