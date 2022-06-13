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

import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
public class HttpReqResAdvanceComponent extends ComponentAbstraction {

    private String apiURL = "https://prod.matcell.com/SonicWS/sonicsecure";
    private String json = "";
    private boolean readyBool1 = false;
    private boolean readyBool2 = false;
    private String method = "POST";
    //  private String contentType = "application/json; charset=UTF-8";
    private boolean apiBool = false;
    private boolean bypassMethod = false;
    private String authorization = "Bearer xxxx";
    private String headers = "";
    private String forms = "";
    private HashMap< String, String> paramHeader = new HashMap< String, String>();
    private HashMap< String, String> formHM = new HashMap< String, String>();

    private ResponseCallBack callBack = new ResponseCallBack() {
        public String tag = "";

        void onReponse(String resp) {

            sendData(0, resp);
        }

        void onError(String resp) {
            sendData(1, resp);
        }
    };

    public HttpReqResAdvanceComponent() {
        setName("HttpReqResAd");
        this.setProperty("method", method);
        this.setProperty("apiURL", apiURL);

        this.setProperty("headers", headers);
        this.setProperty("forms", forms);

        this.setProperty("Authorization", authorization);
        this.setProperty("json", json);

        this.addInput(new DataTypeIO("string", "Json", "json"));
        this.addInput(new DataTypeIO("string", "Authorization", "Authorization"));

        this.addOutput(new DataTypeIO("string", "Response"));
        this.addOutput(new DataTypeIO("string", "Message"));
    }

    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }

    @Override
    public Object onExecute() {

        if (!apiBool) {
            return null;
        }

        if ((readyBool1) && (readyBool2)) {
            readyBool1 = false;
            readyBool2 = false;
            //sendJsonToServer();
            sendToServer(apiURL, method, json, paramHeader, formHM, callBack);
        } else if (bypassMethod) {
            // sendJsonToServer();
            sendToServer(apiURL, method, json, paramHeader, formHM, callBack);
        }
        return null;
    }

    //////////
    private String getDataString(HashMap<String, String> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                if (first) {
                    first = false;
                } else {
                    result.append("&");
                }

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));

            } catch (UnsupportedEncodingException ex) {

            }
        }
        return result.toString();
    }

    public void sendToServer(String api, String method, String json, HashMap<String, String> paramHeader, HashMap<String, String> formHM, ResponseCallBack callBack) {

        Thread th = new Thread() {
            @Override
            public void run() {
                try {

                    String apiURL = api;
                    if (method.equalsIgnoreCase("GET")) {
                        if (formHM != null) {
                            String GET_FORMT = getDataString(formHM);
                            apiURL = apiURL + "?" + GET_FORMT;
                        }
                    }

                    String lowerCaseApiURL = apiURL.toLowerCase();
                    URL url = new URL(apiURL);
                    InputStream inputStream = null;

                    if (lowerCaseApiURL.indexOf("https:") > -1) {

                        SSLContext ctx = SSLContext.getInstance("TLS");
                        ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
                        SSLContext.setDefault(ctx);

                        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                        conn.setHostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String arg0, SSLSession arg1) {
                                return true;
                            }
                        });
                        conn.setRequestMethod(method.toUpperCase());

                        Set entrySet = paramHeader.entrySet();
                        Iterator it = entrySet.iterator();
                        while (it.hasNext()) {
                            Map.Entry me = (Map.Entry) it.next();

                            conn.setRequestProperty((String) me.getKey(), (String) me.getValue());
                        }

                        conn.setDoOutput(true);
                        if (method.equalsIgnoreCase("POST")) {
                            sendDataHttp(conn, json);
                        }
                        inputStream = conn.getInputStream();
                    } else {
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod(method.toUpperCase());

                        Set entrySet = paramHeader.entrySet();
                        Iterator it = entrySet.iterator();
                        while (it.hasNext()) {
                            Map.Entry me = (Map.Entry) it.next();

                            conn.setRequestProperty((String) me.getKey(), (String) me.getValue());
                        }

                        conn.setDoOutput(true);
                        if (method.equalsIgnoreCase("POST")) {
                            sendDataHttp(conn, json);
                        }
                        inputStream = conn.getInputStream();
                    }

                    String responseData = readResponse(inputStream);

                    if (callBack != null) {
                        callBack.onReponse(responseData);
                    }
                } catch (IOException e) {
                    if (callBack != null) {
                        callBack.onError("IOException " + e.getMessage());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (callBack != null) {
                        callBack.onError(e.getLocalizedMessage());
                    }
                }
            }
        };
        th.start();
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

    protected void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();

            }
        } catch (IOException ex) {

        }
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

    class ResponseCallBack {

        public String tag = "";

        void onReponse(String resp) {
        }

        void onError(String resp) {
        }
    }
    /////////

    @Override
    public void handleMessage(int thru, Object obj) {
        if (thru == 0) {
            json = (String) obj;
            readyBool1 = true;
        } else if (thru == 1) {
            authorization = (String) obj;
            readyBool2 = true;
        }
    }

    @Override
    public boolean start() {

        readyBool1 = false;
        readyBool2 = true;
        bypassMethod = false;

        method = getProperty("method").toString();
        headers = getProperty("headers").toString();
        forms = getProperty("forms").toString();
        authorization = getProperty("Authorization").toString();

        paramHeader.clear();
        formHM.clear();

        paramHeader.put("Authorization", authorization);
        String tmp[] = null;
        int len = 0;
        if (!headers.trim().equalsIgnoreCase("")) {
            tmp = headers.split(":");
            len = tmp.length;
            for (int i = 0; i < len; i++) {
                String s[] = tmp[i].split("=");
                if (s.length == 2) {
                    paramHeader.put(s[0], s[1]);
                }
            }
        }

        if (!forms.trim().equalsIgnoreCase("")) {
            tmp = forms.split(":");
            len = tmp.length;
            for (int i = 0; i < len; i++) {
                String s[] = tmp[i].split("=");
                if (s.length == 2) {
                    formHM.put(s[0], s[1]);
                }
            }
        }

        json = getProperty("json").toString();
        if (!json.equalsIgnoreCase("")) {
            readyBool1 = true;
        }
        apiURL = getProperty("apiURL").toString();

        if (!authorization.equalsIgnoreCase("")) {
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
        } else if (key.equalsIgnoreCase("apiURL")) {
            apiURL = getProperty("apiURL").toString();
        } else if (key.equalsIgnoreCase("Authorization")) {
            authorization = getProperty("Authorization").toString();
        } else if (key.equalsIgnoreCase("method")) {
            method = getProperty("method").toString();
        } else if (key.equalsIgnoreCase("headers")) {
            headers = getProperty("headers").toString();
        } else if (key.equalsIgnoreCase("forms")) {
            forms = getProperty("forms").toString();
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

//    private void sendJsonToServer() {
//
//        Thread th = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    String lowerCaseApiURL = apiURL.toLowerCase();
//                    URL url = new URL(apiURL);
//                    InputStream inputStream = null;
//
//                    if (lowerCaseApiURL.indexOf("https:") > -1) {
//
//                        SSLContext ctx = SSLContext.getInstance("TLS");
//                        ctx.init(new KeyManager[0], new TrustManager[]{new DefaultTrustManager()}, new SecureRandom());
//                        SSLContext.setDefault(ctx);
//
//                        // URLConnection conn = url.openConnection();
//                        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
//                        conn.setHostnameVerifier(new HostnameVerifier() {
//                            @Override
//                            public boolean verify(String arg0, SSLSession arg1) {
//                                return true;
//                            }
//                        });
//                        conn.setRequestMethod(method.toUpperCase());
//                        conn.setRequestProperty("Content-Type", contentType);
//                        conn.setRequestProperty("Accept", "application/json");
//                        if (!authKey.trim().equals("")) {
//                            conn.setRequestProperty("Authorization", authKey);
//                        }
//                        conn.setDoOutput(true);
//                        if (method.equalsIgnoreCase("POST")) {
//                            sendDataHttp(conn, json);
//                        }
//                        inputStream = conn.getInputStream();
//                    } else {
//                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                        conn.setRequestMethod(method.toUpperCase());
//                        conn.setRequestProperty("Content-Type", contentType);
//                        conn.setRequestProperty("Accept", "application/json");
//                        if (!authKey.trim().equals("")) {
//                            conn.setRequestProperty("Authorization", authKey);
//                        }
//                        conn.setDoOutput(true);
//                        if (method.equalsIgnoreCase("POST")) {
//                            sendDataHttp(conn, json);
//                        }
//                        inputStream = conn.getInputStream();
//                    }
//
//                    String responseData = readResponse(inputStream);
//
//                    sendData(0, responseData);
//
//                } catch (Exception e) {
//                    //Log.v("VT", "SonicDB from sendToJsonServer = " + e.getMessage());
//                    sendData(0, e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        };
//        th.start();
//    }
    @Override
    public void onDestroy() {

    }

    @Override
    public String getHelp() {

        String doc = "";

        return doc;
    }
}
