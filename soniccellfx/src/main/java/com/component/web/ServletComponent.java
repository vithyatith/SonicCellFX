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
package com.component.web;

import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.msg.PubSubSingleton;
import java.io.File;
import java.util.HashMap;
 
import jakarta.servlet.Servlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

public class ServletComponent extends ComponentAbstraction {

    private String keystore = "keystore.jks";
    private String password = "password";
    private int port = 8803;
    private int sslPort = 9000;
    private boolean enableSSL = true;
    private boolean processingBool = false;
    private boolean debug = true;

    private String baseFolder = "ws";

    private String mappingName = "endpoint";

//    private String dbIPAddr = "127.0.0.1";
//    private int dbPort = 3306;
//    private String dbName = "sonic_db_v45";
//    private String dbUser = "matcelladminapp";
//    private String dbPassword = "1nsnfCkrsSSE";
    private String contextName = "ws";

    private HashMap<String, Long> insertOnReceivedMsgHashMap = new HashMap<String, Long>();

    public ServletComponent() {
        setName("Servlet");

        // DB
//        this.setProperty("dbIPAddr", dbIPAddr);
//        this.setProperty("dbPort", dbPort);
//        this.setProperty("dbName", dbName);
//        this.setProperty("dbUser", dbUser);
//        this.setProperty("dbPassword", dbPassword);
        // Keystore
        this.setProperty("baseFolder", baseFolder);
        this.setProperty("keystore", keystore);
        this.setProperty("password", password);

        // Context anme
        this.setProperty("contextName", contextName);
        this.setProperty("mappingName", mappingName);

        // SSL
        this.setProperty("port", port);
        this.setProperty("sslPort", sslPort);
        this.setProperty("enableSSL", enableSSL);
        this.setProperty("debug", debug);

        this.addOutput(new DataTypeIO("string", "Response"));
        this.addOutput(new DataTypeIO("string", "Verbose"));
        this.addOutput(new DataTypeIO("string", "Error"));

    }

    @Override
    public Object onExecute() {

        if (processingBool) {
            processingBool = false;
            try {

                sendData(0, "SonickWS Started");
            } catch (Exception e) {
                sendData(0, "SonickWS Started");
                e.printStackTrace();
                PubSubSingleton.getIntance().send("Print", e.getMessage());
            }
        }

        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {

    }

    @Override
    public boolean start() {
        port = (int) Double.parseDouble(getProperty("port").toString());
        sslPort = (int) Double.parseDouble(getProperty("sslPort").toString());
        enableSSL = (boolean) Boolean.parseBoolean(getProperty("enableSSL").toString());
        debug = (boolean) Boolean.parseBoolean(getProperty("debug").toString());
        keystore = getProperty("keystore").toString();
        password = getProperty("password").toString();
        baseFolder = getProperty("baseFolder").toString();
        contextName = getProperty("contextName").toString();

        mappingName = getProperty("mappingName").toString();

//        dbIPAddr = getProperty("dbIPAddr").toString();
//        dbPort = (int) Double.parseDouble(getProperty("dbPort").toString());
//        dbName = getProperty("dbName").toString();
//        dbUser = getProperty("dbUser").toString();
//        dbPassword = getProperty("dbPassword").toString();
//  
        startServer();

        return true;
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public void stop() {
        stopServer();
    }

    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("port")) {
            port = (int) Double.parseDouble(getProperty("port").toString());
        } else if (key.equalsIgnoreCase("sslPort")) {
            sslPort = (int) Double.parseDouble(getProperty("sslPort").toString());
        } else if (key.equalsIgnoreCase("enableSSL")) {
            enableSSL = Boolean.parseBoolean(getProperty("enableSSL").toString());
        } else if (key.equalsIgnoreCase("keystore")) {
            keystore = getProperty("keystore").toString();
        } else if (key.equalsIgnoreCase("password")) {
            password = getProperty("password").toString();
        } else if (key.equalsIgnoreCase("baseFolder")) {
            baseFolder = getProperty("baseFolder").toString();
        } //        else if (key.equalsIgnoreCase("dbIPAddr")) {
        //            dbIPAddr = getProperty("dbIPAddr").toString();
        //        } else if (key.equalsIgnoreCase("dbPort")) {
        //            dbPort = (int) Double.parseDouble(getProperty("dbPort").toString());
        //        } else if (key.equalsIgnoreCase("dbName")) {
        //            dbName = getProperty("dbName").toString();
        //        } else if (key.equalsIgnoreCase("dbUser")) {
        //            dbUser = getProperty("dbUser").toString();
        //        } else if (key.equalsIgnoreCase("dbPassword")) {
        //            dbPassword = getProperty("dbPassword").toString();
        //        } 
        else if (key.equalsIgnoreCase("contextName")) {
            contextName = getProperty("contextName").toString();
        } else if (key.equalsIgnoreCase("debug")) {
            debug = Boolean.parseBoolean(getProperty("debug").toString());
        } else if (key.equalsIgnoreCase("mappingName")) {
            mappingName = getProperty("mappingName").toString();
        }

    }

    @Override
    public void mouseDoubleClick() {
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                FileChooser fileChooser = new FileChooser();
//                Stage stage = new Stage();
//                File selectedFile = fileChooser.showOpenDialog(stage);
//
//                if (selectedFile != null) {
//                    keystoreFile = selectedFile.getAbsoluteFile().toString();
//                    setProperty("filename", keystoreFile);
//                    PubSubSingleton.getIntance().send("UPDATE_PROPERTIES", getId());
//                }
//            }
//
//        });
    }

    @Override
    public int getPlatformSupport() {

        return 0;
    }

    @Override
    public void onPropertyChanged(String key, Object value) {
        loadProperty(key, value);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public String getHelp() {

        String doc = "";

        return doc;
    }

    private Tomcat tomcat = null;

    private Thread serverThread = null;

    public static void main(String args[]) {
        ServletComponent sw = new ServletComponent();
        sw.startServer();
    }

    private void startServer() {
        serverThread = new Thread() {
            public void run() {
                tomcat = new Tomcat();
                tomcat.setPort(port);

                // String mainBaseDir = "sonicTomcatDir";
                File file = new File(baseFolder);
                if (!file.exists()) {
                    if (!file.mkdir()) {
                        System.out.println("Fail to great folder " + baseFolder);
                    }
                }

                String keystoreFilename = baseFolder + File.separator + keystore;

                file = new File(keystoreFilename);
                if (!file.exists()) {

                }

                tomcat.setBaseDir(baseFolder);

                Service service = tomcat.getService();

                service.addConnector(getSslConnector());

                Context ctx = tomcat.addContext(contextName, new File(baseFolder).getAbsolutePath());

                GenericServlet genericServlet = new GenericServlet(new GenericServlet.CallBack() {
                    @Override
                    public void onReceived(String json) {

                        sendData(0, json);
                    }

                    @Override
                    public void onError(String error) {
                         sendData(2, error);
                    }

                    @Override
                    public void onVerbose(String verbose) {
                        sendData(1, verbose);
                    }

                    @Override
                    public boolean getDebug() {

                        return true;
                    }

                });

                Tomcat.addServlet(ctx, "GenericEmbedded", (Servlet) genericServlet);

               // ctx.addServletMapping("/" + mappingName, "GenericEmbedded");
                ctx.addServletMappingDecoded("/" + mappingName, "GenericEmbedded");

                try {
                    tomcat.start();
                    tomcat.getServer().await();
                } catch (LifecycleException ex) {
                    printToConsole(ex.getLocalizedMessage());
                }
            }
        };
        serverThread.setPriority(Thread.MAX_PRIORITY);
        serverThread.start();
    }

    private void stopServer() {

        if (serverThread != null) {

            if (tomcat != null) {
                try {
                    tomcat.stop();
                    tomcat.destroy();
                } catch (LifecycleException ex) {
                    printToConsole(ex.getLocalizedMessage());
                }
            }
            try {
                Thread th = serverThread;
                th.interrupt();

                th = null;
            } catch (Exception e) {

                System.out.println(e.getLocalizedMessage());
            }
            serverThread = null;
        }
    }

    // keytool -genkey -keyalg RSA -alias client -keystore keystore.jks -storepass password -validity 360 -keysize 2048
    private Connector getSslConnector() {
        Connector connector = new Connector();
        connector.setPort(sslPort);
        connector.setSecure(true);
        connector.setScheme("https");
        // connector.setAttribute("keyAlias", "client");
        connector.setProperty("keystorePass", password);
        connector.setProperty("keystoreType", "JKS");
        connector.setProperty("keystoreFile", keystore);
        connector.setProperty("clientAuth", "false");
        connector.setProperty("protocol", "HTTP/1.1");
        connector.setProperty("sslProtocol", "TLS");
        connector.setProperty("maxThreads", "200");
        connector.setProperty("protocol", "org.apache.coyote.http11.Http11AprProtocol");
        connector.setProperty("SSLEnabled", Boolean.toString(enableSSL));

        return connector;
    }
}
