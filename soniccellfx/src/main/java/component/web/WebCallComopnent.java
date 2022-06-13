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

import com.sonicmsgr.util.network.NetworkUtils;
import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.Log;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yada
 */
public class WebCallComopnent extends ComponentAbstraction {
    private String url = "http://localhost:8082/?root=COGQDTGFVFYL9BVTVSYUMTMZYDYBBETIAEKSVDOAJAWESBGXUEXQZYTLTZEXKTVMCDDWBETQTEPFUGDZM&address=FGYJVFOYITYKESMUMUFUQKWNVSNOHDWC9OX9QZGGIPIWVMAXWFEYRGRHOQ9PL9OHEITHUHKDOZPWBHQLM";
    public WebCallComopnent() {
        setName("WebCall");
        this.setProperty("url", url);
        this.addInput(new DataTypeIO("string","url","url"));
        this.addOutput(new DataTypeIO("string"));
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    
    @Override
    public Object onExecute() {
        try {
            String str = NetworkUtils.doHttpUrlConnectionAction(url);
            Log.v("VT","done  = "+str);
            sendData(0, str);
            return str;
        } catch (Exception ex) {
            Logger.getLogger(WebCallComopnent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    @Override
    public void handleMessage(int thru, Object obj) {
        url = (String) obj;
    }

    @Override
    public boolean start() {
        url = getProperty("url").toString();
        return true;
    }

    @Override
    public void stop() {

    }

    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("url")) {
            url = val.toString();
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
