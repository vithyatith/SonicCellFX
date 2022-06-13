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
package com.sonicmsgr.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 *
 * @author yada
 */
public class HardwareUtil {

    private static String hardwareId = "521913166";

    public static String getMac() {
        String macStr = null;
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface network = networkInterfaces.nextElement();
                byte[] mac = network.getHardwareAddress();
                if (mac == null) {
                    System.out.println("null mac");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        //sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));  
                        sb.append(String.format("%02X%s", mac[i], ""));
                    }
                    macStr = sb.toString();
                    break;
                }
            }
        } catch (SocketException e) {
        }
        return macStr;
    }

    public static void setHardwareId(String id) {
        hardwareId = id;
    }

    public static boolean containedHardwareId(String id) {

        try {

            Enumeration<NetworkInterface> netInterfaceList = NetworkInterface
                    .getNetworkInterfaces();
            
            StringBuilder sb = new StringBuilder();
            while (netInterfaceList.hasMoreElements()) {
                NetworkInterface inf = netInterfaceList.nextElement();
                byte[] mac = inf.getHardwareAddress();

                if (mac == null) {

                    String macStr = getMacByCommand();
                    macStr = macStr.replace(":", "");
                    if (macStr.equalsIgnoreCase(id)) {
                        return true;
                    }
                } else {
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], ""));
                    }

                    if (sb.toString().equalsIgnoreCase(id)) {
                        return true;
                    }
                    sb.delete(0, sb.length());
                }

            }

        } catch (SocketException e) {

            e.printStackTrace();

        }
        return false;
    }

    public static String getHardwareId() {

        if (isAndroidOS()) {
            return hardwareId;
        }

        String id = "";
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            if (network == null) {
                return getMac();
            }

            byte[] mac = network.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            if (mac == null) {

                String macStr = getMacByCommand();
                macStr = macStr.replace(":", "");

                return macStr;
            }
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], ""));
            }

            id = sb.toString();
            Log.v("VT", "Hardware mac id = " + id);

        } catch (UnknownHostException e) {

            e.printStackTrace();

        } catch (SocketException e) {

            e.printStackTrace();

        }
        return id;
    }

    public static String getOsName() {
        String os = "";
        os = System.getProperty("os.name");
        return os;
    }

    /**
     * Returns the MAC address of the computer.
     *
     * @return the MAC address
     */
    public static String getMacByCommand() {
        String address = "";
        String os = getOsName();
        if (os.startsWith("Windows")) {
            try {
                String command = "cmd.exe /c ipconfig /all";
                Process p = Runtime.getRuntime().exec(command);
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.indexOf("Physical Address") > 0) {
                        int index = line.indexOf(":");
                        index += 2;
                        address = line.substring(index);
                        break;
                    } else if (line.indexOf("Physical address") > 0) {
                        int index = line.indexOf(":");
                        index += 2;
                        address = line.substring(index);
                        break;
                    }
                }
                br.close();
                return address.trim();
            } catch (IOException e) {
            }
        } else if (os.startsWith("Linux")) {
            String command = "/bin/sh -c ifconfig -a";
            Process p;
            try {
                p = Runtime.getRuntime().exec(command);
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                    if (line.indexOf("HWaddr") > 0) {
                        int index = line.indexOf("HWaddr") + "HWaddr".length();
                        address = line.substring(index);
                        break;
                    }
                }
                br.close();
            } catch (IOException e) {
            }
        } else if (os.startsWith("Mac")) {
            String command = "/bin/sh -c ifconfig -a";
            Process p;
            try {
                p = Runtime.getRuntime().exec(command);
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {

                    if (line.indexOf("ether") > 0) {
                        String[] tmp = line.split("ether");
                        if (tmp.length == 2) {
                            address = tmp[1];
                            // address = address.replace(":", "");

                        } else {
                            address = "";
                        }

                        break;
                    }
                }
                br.close();
            } catch (IOException e) {
            }
        }
        address = address.trim();
        return address;
    }

    public static boolean isAndroidOS() {
        try {
            Class.forName("android.app.Activity");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
