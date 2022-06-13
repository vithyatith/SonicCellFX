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
package com.sonicmsgr.util;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yada
 */
public class JsonUtil {
    
    private static final Gson gson = new Gson();
    
    public static String bytesArrayToJson(byte[] data){
        return gson.toJson(data);
    }
    
    public static byte[] jsonToBytesArray(String json){
        return gson.fromJson(json, byte[].class);
    }
    
    public static String fileToJson(String fileName){
        File file = new File(fileName);
        byte[] b = FileUtils.readBinaryFile(fileName);
        fileName = file.getName();
        HashMap map = new HashMap();
        map.put("filename", fileName);
        map.put("data", b);
        return gson.toJson(map);
    }
    public static String jsonToFile(String json, String savedPath){

        try {
            HashMap hm = gson.fromJson(json, HashMap.class);
            String fullPath = savedPath+File.separator+hm.get("filename");
            ArrayList al = (ArrayList)hm.get("data");
            byte[] data = new byte[al.size()];
            for(int i=0; i<al.size(); i++){
                data[i] = (byte)Float.parseFloat(al.get(i).toString());
            }
            
            
            FileUtils.writeBinaryFile(data, fullPath);
            return fullPath;
        } catch (IOException ex) {
           
        }
        return null;
    }
    
//    public static void main(String args[]){
//        String s = fileToJson("/Users/yada/tmp2/image.jpg");
//        jsonToFile(s,"/Users/yada");  
//    }
}
