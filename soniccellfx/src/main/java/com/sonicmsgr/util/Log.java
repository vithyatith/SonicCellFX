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

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vithya
 */
public class Log {

    private static boolean debugBool = true;

    public static void v(String tag, String msg) {
        if (debugBool) {
            System.out.println(tag + ":" + msg);
        }
    }
	private FileHandler handler = null;

	private Logger logger = null;

	private String logFilename = "pfpGateway.log";
	private static Log instance = null;

	private Log() {

	}

	public static Log getInstance() {
		if (instance == null) {
			instance = new Log();
			instance.init();
		}
		return instance;
	}

	public static void setName(String name) {

		if (instance == null) {
			instance = new Log();
		}
		instance.logFilename = name;
		instance.init();
	}

	public static void log(String msg) {
		getInstance().logger.info(msg);
	}

	public static void logPrint(String msg) {
		getInstance().logger.info(msg);
		// print(msg);
	}

	public static void logPrint(String tag, String msg) {
		getInstance().logger.info(msg);
		print(tag, msg);
	}

//	private static boolean debugBool = true;

	public static void setPrintDebug(boolean b) {
		debugBool = b;
	}

	public static void print(String tag, String msg) {
		if (debugBool) {
			System.out.println(tag + " : " + msg);
		}
	}



	public static void print(String msg) {
		if (debugBool) {
			System.out.println(msg);
		}
	}

	public static void logError(String msg) {
		getInstance().logger.severe(msg);
	}

	public static void logError(Exception ex, String msg) {
		getInstance().logger.log(Level.SEVERE, msg, ex);
	}

	private void init() {
		boolean append = true;
		try {
                    try {
                        handler = new FileHandler(logFilename, append);
                    } catch (IOException ex) {
                        Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
                    }
			logger = Logger.getLogger("com.pfpcyber");
			logger.addHandler(handler);
		}catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // TODO Auto-generated catch block
        
	}
}
