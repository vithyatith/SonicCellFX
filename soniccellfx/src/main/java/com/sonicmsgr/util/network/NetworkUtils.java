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
package com.sonicmsgr.util.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

/**
 *
 * @author vithya
 */
public class NetworkUtils {

    private InputStream downloadUrl(final URL url) throws IOException {
        int NET_READ_TIMEOUT_MILLIS = 10000;
        int NET_CONNECT_TIMEOUT_MILLIS = 10000;
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(NET_READ_TIMEOUT_MILLIS /* milliseconds */);
        conn.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    public static String doHttpUrlConnectionAction(String desiredUrl)
            throws Exception {
        URL url = null;
        BufferedReader reader = null;
        StringBuilder stringBuilder;

        try {
            // create the HttpURLConnection
            url = new URL(desiredUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // just want to do an HTTP GET here
            connection.setRequestMethod("GET");

            // uncomment this if you want to write output to this url
            //connection.setDoOutput(true);
            // give it 15 seconds to respond
            connection.setReadTimeout(600 * 1000);
            connection.connect();

            // read the output from the server
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            stringBuilder = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            // close the reader; this can throw an exception too, so
            // wrap it in another try/catch block.
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    public static boolean checkHeartBeat(String ip, int sleepInMiliSec) {

        try {
            InetAddress address = InetAddress.getByName(ip);
            boolean reachable = address.isReachable(sleepInMiliSec);
            return reachable;
        } catch (Exception e) {

        }

        return false;
    }

    public static String[] extactURL(String url) {
        //https://nodes.devnet.iota.org:443
        if (url == null) {
            return null;
        }

        int len = url.length();
        if (len < 7) {
            return null;
        }
        char c = url.charAt(len - 1);
        if (c == '/') {
            url = url.substring(0, len - 1);
            len = len - 1;
        }

        String[] result = new String[3];
        String[] tmp = url.split(":");
        if (tmp.length != 3) {

            if (tmp[0].equalsIgnoreCase("https")) {
                result[2] = "443";
            } else {
                result[2] = "80";
            }
        } else {
            result[2] = tmp[2];
        }
        result[0] = tmp[0];
        result[1] = tmp[1].substring(2);

        return result;

    }
}
