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
package com.sonicmsgr.net;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
public class HttpPostJson {

    private void sendDataHttp(HttpURLConnection con, String data) throws IOException {
        DataOutputStream wr = null;
        try {
            wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(data);
            wr.flush();
            wr.close();
        } catch (IOException exception) {
          //  throw exception;
        } finally {
            this.closeQuietly(wr);
        }
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

    public void sendPost(String apiURL, String json, int timeOutInMilisecond, ResponseCallBack callBack) {

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
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        conn.setRequestProperty("Accept", "application/json");
                        conn.setDoOutput(true);
                        conn.setConnectTimeout(timeOutInMilisecond);
                        
                        sendDataHttp(conn, json);
                        inputStream = conn.getInputStream();
                    } else {
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        conn.setRequestProperty("Accept", "application/json");
                        conn.setDoOutput(true);
                        conn.setConnectTimeout(timeOutInMilisecond);
                        sendDataHttp(conn, json);
                        inputStream = conn.getInputStream();
                    }

                    String responseData = readResponse(inputStream);

                    if (callBack != null) {
                        callBack.onReponse(responseData);
                    }

                } catch (Exception e) {

                    if (callBack != null) {
                        callBack.onError(e.getMessage());
                    }
                   // e.printStackTrace();
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

    public interface ResponseCallBack {

        void onReponse(String response);

        void onError(String error);
    }

    public static void main(String[] args) {

        HttpPostJson post = new HttpPostJson();
        String apiURL = "127.0.0.1:8895/SonicWS/sonicsecure";
        String json = "fasdfasdfasdfasdf";

        post.sendPost(apiURL, json, 500,new ResponseCallBack() {
            @Override
            public void onReponse(String response) {

                System.out.println("res: " + response);

            }

            @Override
            public void onError(String error) {
                System.out.println("err: " + error);
            }

        });
    }

}
