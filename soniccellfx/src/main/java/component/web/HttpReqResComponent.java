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
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class HttpReqResComponent extends ComponentAbstraction {

    private String apiURL = "https://prod.matcell.com/SonicWS/sonicsecure";
    private String json = "";
    private boolean readyBool1 = false;
    private boolean readyBool2 = false;
    private String authKey = "";
    private String method = "POST";
    private String contentType = "application/json; charset=UTF-8";
    private boolean apiBool = false;
    private boolean bypassMethod = false;

    public HttpReqResComponent() {
        setName("HttpReqRes");
        this.setProperty("method", method);
        this.setProperty("contentType", contentType);

        this.setProperty("authKey", authKey);
        this.setProperty("apiURL", apiURL);
        this.setProperty("json", json);

        this.addInput(new DataTypeIO("string", "Json", "json"));
        this.addInput(new DataTypeIO("string", "authKey", "authKey"));
        this.addOutput(new DataTypeIO("string", "Response"));
        this.addOutput(new DataTypeIO("string", "Message"));
    }

    @Override
    public Object onExecute() {

        if (!apiBool) {
            return null;
        }

        if ((readyBool1) && (readyBool2)) {
            readyBool1 = false;
            readyBool2 = false;
            sendJsonToServer();
        }else if(bypassMethod){
            sendJsonToServer();
        }
        return null;
    }

    private void sendDataHttp(HttpURLConnection con, String data) throws IOException {
        DataOutputStream wr = null;
        try {
            wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(data);
            wr.flush();
            wr.close();
        } catch (IOException exception) {
            throw exception;
        } finally {
            this.closeQuietly(wr);
        }
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    private String readResponse(InputStream is) throws IOException {
        BufferedReader in = null;
        String inputLine;
        StringBuilder body;
        try {
            in = new BufferedReader(new InputStreamReader(is));

            body = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                body.append(inputLine);
            }
            in.close();

            return body.toString();
        } catch (IOException ioe) {
            throw ioe;
        } finally {
            this.closeQuietly(in);
        }
    }

    protected void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ex) {

        }
    }

    @Override
    public void handleMessage(int thru, Object obj) {
        if (thru == 0) {
            json = (String) obj;
            readyBool1 = true;
        } else if (thru == 1) {
            authKey = (String) obj;
            readyBool2 = true;
        }
    }

    @Override
    public boolean start() {

        readyBool1 = false;
        readyBool2 = true;
        bypassMethod = false;

        method = getProperty("method").toString();
        contentType = getProperty("contentType").toString();

        json = getProperty("json").toString();
        if (!json.equalsIgnoreCase("")) {
            readyBool1 = true;
        }
        apiURL = getProperty("apiURL").toString();

        authKey = getProperty("authKey").toString();
        if (!authKey.equalsIgnoreCase("")) {
            readyBool2 = true;
        }

        if (apiURL.trim().equalsIgnoreCase("")) {
            apiBool = false;
        } else {
            apiBool = true;
        }

        if (method.trim().equalsIgnoreCase("GET") || method.trim().equalsIgnoreCase("PUT")) {
            bypassMethod = true;
        }

        return true;
    }

    @Override
    public void stop() {

    }

    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("json")) {
            json = getProperty("json").toString();
        } else if (key.equalsIgnoreCase("url")) {
            apiURL = getProperty("apiURL").toString();
        } else if (key.equalsIgnoreCase("authKey")) {
            authKey = getProperty("authKey").toString();
        } else if (key.equalsIgnoreCase("method")) {
            method = getProperty("method").toString();
        } else if (key.equalsIgnoreCase("contentType")) {
            contentType = getProperty("contentType").toString();
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
                    String lowerCaseApiURL = apiURL.toLowerCase();
                    URL url = new URL(apiURL);
                    InputStream inputStream = null;

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
                        conn.setRequestMethod(method.toUpperCase());
                        conn.setRequestProperty("Content-Type", contentType);
                        conn.setRequestProperty("Accept", "application/json");
                        if (!authKey.trim().equals("")) {
                            conn.setRequestProperty("Authorization", authKey);
                        }
                        conn.setDoOutput(true);
                        if (method.equalsIgnoreCase("POST")) {
                            sendDataHttp(conn, json);
                        }
                        inputStream = conn.getInputStream();
                    } else {
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod(method.toUpperCase());
                        conn.setRequestProperty("Content-Type", contentType);
                        conn.setRequestProperty("Accept", "application/json");
                        if (!authKey.trim().equals("")) {
                            conn.setRequestProperty("Authorization", authKey);
                        }
                        conn.setDoOutput(true);
                        if (method.equalsIgnoreCase("POST")) {
                            sendDataHttp(conn, json);
                        }
                        inputStream = conn.getInputStream();
                    }

                    String responseData = readResponse(inputStream);

                    sendData(0, responseData);

                } catch (Exception e) {
                    //Log.v("VT", "SonicDB from sendToJsonServer = " + e.getMessage());
                    sendData(0, e.getMessage());
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
