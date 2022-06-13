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

import com.google.gson.Gson;
import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yada
 */
public class JsonExtractComopnent extends ComponentAbstraction {

    private String key = "";
    private String json = "";
    private boolean hasData = false;
    private Gson gson = new Gson();

    public JsonExtractComopnent() {
        setName("jsonExtract");
        this.setProperty("key", key);
        this.addInput(new DataTypeIO("string", "json", "json"));
        this.addOutput(new DataTypeIO("string", "json", "json"));
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public Object onExecute() {
        try {

            String str = "";

            if (hasData) {
                Map map = gson.fromJson(json, Map.class);

                Object keyObject = map.get(key);
                str = gson.toJson(keyObject);
                sendData(0, str);
            }
            return str;
        } catch (Exception ex) {
            Logger.getLogger(JsonExtractComopnent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    @Override
    public void handleMessage(int thru, Object obj) {

        json = (String) obj;
        hasData = true;
    }

    @Override
    public boolean start() {
        key = getProperty("key").toString();
        json = "";
        hasData = false;
        return true;
    }

    @Override
    public void stop() {

    }

    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("key")) {
            key = val.toString();
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

    @Override
    public void onDestroy() {

    }

    @Override
    public String getHelp() {
        String doc = "";
        return doc;
    }
}
