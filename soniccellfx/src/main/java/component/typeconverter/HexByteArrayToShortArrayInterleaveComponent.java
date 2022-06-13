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
package component.typeconverter;

import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.msg.PubSubSingleton;
import com.sonicmsgr.util.HexUtils;

/**
 *
 * @author yada
 */
public class HexByteArrayToShortArrayInterleaveComponent extends ComponentAbstraction {

    private boolean processingBool = false;
    private byte byteArray[] = new byte[1];
    private short shortArray[] = new short[1];
    private int index = 1;
    private int maxIndex = 1;

    private HexUtils hexUtil = new HexUtils();

    public HexByteArrayToShortArrayInterleaveComponent() {
        setName("Bytes2ShortI");
        this.setProperty("index", index);
        this.setProperty("maxIndex", maxIndex);
        this.addInput(new DataTypeIO("byte[]", "byte[]"));
        this.addOutput(new DataTypeIO("short[]", "short[]"));
    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public Object onExecute() {
        if (processingBool) {
            processingBool = false;
            try {

                hexUtil.hex16BitToShortArrayInterLeave(byteArray, shortArray, index, maxIndex);
                this.sendData(0, shortArray);
            } catch (Exception e) {
                PubSubSingleton.getIntance().send("Print", e.getMessage());
            }
        }

        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {
        byteArray = (byte[]) obj;
        int len = byteArray.length / 2;
        if (len != shortArray.length) {
            shortArray = new short[len];
        }
        processingBool = true;
    }

    @Override
    public boolean start() {
        processingBool = false;
        index = (int) Double.parseDouble(getProperty("index").toString());
        maxIndex = (int) Double.parseDouble(getProperty("maxIndex").toString());
        return true;
    }

    @Override
    public void stop() {
        processingBool = false;

    }

    // Load from a file
    @Override
    public void loadProperty(String key, Object val) {
        if (key.equalsIgnoreCase("index")) {
            index = (int) Double.parseDouble(val.toString());
        } else if (key.equalsIgnoreCase("maxIndex")) {
            maxIndex = (int) Double.parseDouble(val.toString());
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
