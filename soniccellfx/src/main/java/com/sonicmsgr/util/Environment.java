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

import java.io.File;

/**
 *
 * @author vithya
 */
public class Environment {

    public static final String MEDIA_MOUNTED = "mounted";
    private static String osName = null;
    private static boolean isAndroid = false;
    private static File rootAndroid = null;

    public static void setAndroidRooFolder(File rootFolder) {
        rootAndroid = rootFolder;
    }

    public static String getExternalStorageState() {

        return "mounted";
    }

    public static File getExternalStorageDirectory() {

        if (osName == null) {

            try {
                Class aClass = Environment.class.getClassLoader().loadClass("android.os.Build");
                isAndroid = true;
            } catch (ClassNotFoundException e) {
                isAndroid = false;
            }
            osName = "not null";
        }
        if (isAndroid) {
            return rootAndroid;

        } else {
            return new File(".");
        }
    }

}
