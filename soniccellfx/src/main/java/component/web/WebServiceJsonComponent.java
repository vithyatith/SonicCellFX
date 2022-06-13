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
package component.web;


import com.sonicmsgr.util.encryption.SonicCryptLibPool;
import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 *
 * @author yada
 */
public class WebServiceJsonComponent extends ComponentAbstraction {

    private String key = "1234";
    private String iv = "56789";
    private String apiURL = "https://prod.matcell.com/SonicWS/sonicsecure";
    private String json = "";
    private boolean encrypt = false;
    private SonicCryptLibPool sonicCryptLibPool = null;
    private boolean readyBool = false;

    public WebServiceJsonComponent() {
        setName("WebJson");
        this.setProperty("key", key);
        this.setProperty("iv", iv);
        this.setProperty("encrypt", (Boolean) encrypt);
        this.setProperty("apiURL", apiURL);
        this.setProperty("json", json);

        this.addInput(new DataTypeIO("string", "Json","json"));
        this.addOutput(new DataTypeIO("string", "Response"));
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public Object onExecute() {

        sendJsonToServer();
        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {
        json = (String) obj;
        readyBool = true;
    }

    @Override
    public boolean start() {

        key = getProperty("key").toString();
        iv = getProperty("iv").toString();
        json = getProperty("json").toString();
        if (!json.equalsIgnoreCase("")) {
            readyBool = true;
        }
        apiURL = getProperty("apiURL").toString();
        encrypt = Boolean.parseBoolean(getProperty("encrypt").toString());

        try {
            sonicCryptLibPool = new SonicCryptLibPool(key, iv);
        } catch (Exception e) {

        }

        return true;
    }

    @Override
    public void stop() {

    }

    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("key")) {
            key = getProperty("key").toString();
        } else if (key.equalsIgnoreCase("iv")) {
            iv = getProperty("iv").toString();
        } else if (key.equalsIgnoreCase("encrypt")) {
            encrypt = Boolean.parseBoolean(getProperty("encrypt").toString());
        } else if (key.equalsIgnoreCase("json")) {
            json = getProperty("json").toString();
        } else if (key.equalsIgnoreCase("url")) {
            apiURL = getProperty("apiURL").toString();
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

    }

    private void sendJsonToServer() {

        Thread th = new Thread() {
            @Override
            public void run() {
                try {

                    OutputStream outStream = null;
                    InputStream inStream = null;
                    String lowerCaseApiURL = apiURL.toLowerCase();
                    URL url = new URL(apiURL);

                    if (lowerCaseApiURL.indexOf("https:") > -1) {

                        SSLContext ctx = SSLContext.getInstance("TLS");
                        ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
                        SSLContext.setDefault(ctx);

                        // URLConnection conn = url.openConnection();
                        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                        conn.setHostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String arg0, SSLSession arg1) {
                                return true;
                            }
                        });

                        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        conn.setDoOutput(true);

                        outStream = conn.getOutputStream();
                        inStream = conn.getInputStream();
                    } else {
                        HttpURLConnection  conn = (HttpURLConnection)url.openConnection();
                        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        conn.setRequestProperty("Accept", "application/json");
                        conn.setDoOutput(true);
                        conn.setRequestMethod( "POST" );
 
        
            
                        
                        
                        Log.v("VT","Request method post");
                        outStream = conn.getOutputStream();
                        inStream = conn.getInputStream();
                    }

                    OutputStreamWriter wr = new OutputStreamWriter(outStream);

                    // Send to the servr encrypted
                    if (encrypt) {
                        String encryptSring = sonicCryptLibPool.getSonicCryptLibPool().encrypt(json);
                        sonicCryptLibPool.poolDecrement();
                        wr.write(encryptSring);

                    } else {
                        Log.v("VT","Sending result "+json);
                        wr.write(json);
                    }

                    wr.flush();
                    //  Here you read any answer from server.
                    BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(inStream));
                    String line = "";
                    String prevLine = "";
                    int i = 0;
                    while ((line = serverAnswer.readLine()) != null) {
                        line = prevLine + line;
                        // The response will have to decrypt
                        prevLine = line;

                    }
                    if (encrypt) {
                        try {
                            prevLine = sonicCryptLibPool.getSonicCryptLibPool().decrypt(prevLine);
                            sonicCryptLibPool.poolDecrement();
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                    }

                    sendData(0, prevLine);

                    wr.close();
                    serverAnswer.close();
                } catch (Exception e) {
                    Log.v("VT", "SonicDB from sendToJsonServer = " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        th.start();
    }

    private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
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
