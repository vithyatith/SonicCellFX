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
package component.message;

import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author yada
 */
public class MsgQueStartComponent extends ComponentAbstraction {

    private Queue<float[]> fifoMessageQue = new ConcurrentLinkedQueue<float[]>();
    private boolean jobRunning = false;
    private int poolSize = 50;
    ///  private int bufferSize = 1023;
    private boolean readyBool = true;
    private DataArrayPool dataArrayPool = null;

    public MsgQueStartComponent() {
        setName("MsgQueStart");
        setProperty("poolSize", poolSize);
        //  setProperty("bufferSize", bufferSize);
        this.addInput(new DataTypeIO("float[]", "Float array data"));
        this.addOutput(new DataTypeIO("all", "Data"));
        this.addOutput(new DataTypeIO("int", "Id"));
        this.addOutput(new DataTypeIO("string", "error"));

    }
    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }
    @Override
    public Object onExecute() {
        if (readyBool) {
            readyBool = false;
            processDataOnQue();

        }

        return null;
    }

    @Override
    public void mouseDoubleClick() {

    }

    @Override
    public void handleMessage(int thruId, Object obj) {

        float data[] = (float[]) obj;

        if (thruId == 0) {

            if (dataArrayPool.getBufferSize() != data.length) {
                dataArrayPool.resetPool();
                dataArrayPool.resizePool(poolSize, data.length);
            }

            try {
                float copyData[] = dataArrayPool.copyDataPool(data);
                addDataToMessageQue(copyData);
                readyBool = true;
            } catch (IndexOutOfBoundsException e) {

                sendData(2,"Error: "+e.getMessage());
            }
        } else {
            readyBool = true;
            jobRunning = false;
            dataArrayPool.poolDecrement();
        }

    }

    @Override
    public boolean start() {

        clearMessageQue();

        //  bufferSize = (int) Double.parseDouble(getProperty("bufferSize").toString());
        poolSize = (int) Double.parseDouble(getProperty("poolSize").toString());
        jobRunning = false;

        if (dataArrayPool == null) {
            dataArrayPool = new DataArrayPool();
        }
        dataArrayPool.resetPool();
        //    dataArrayPool.resizePool(poolSize, bufferSize);

        return true;
    }

    @Override
    public void stop() {
        clearMessageQue();
        if (dataArrayPool != null) {
            dataArrayPool.resetPool();
        }
    }

    @Override
    public void onPropertyChanged(String key, Object value) {
        if (key.equalsIgnoreCase("bufferSize")) {
            //   bufferSize = (int) Double.parseDouble(getProperty("bufferSize").toString());
        } else if (key.equalsIgnoreCase("poolSize")) {
            poolSize = (int) Double.parseDouble(getProperty("poolSize").toString());
        }
    }

    @Override
    public void loadProperty(String key, Object value) {
        onPropertyChanged(key, value);
    }

    @Override
    public int getPlatformSupport() {

        return 0;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public String getHelp() {

        return "";
    }

    public void addDataToMessageQue(float data[]) {
        int size = fifoMessageQue.size();
        fifoMessageQue.add(data);
        if ((size > 0) && (jobRunning == false)) {
            processDataOnQue();
        }
    }

    public boolean processDataOnQue() {
        int queSize = fifoMessageQue.size();

        if (queSize < 1) {
            return false;
        }

        if (jobRunning == false) {
            jobRunning = true;
            Object o = fifoMessageQue.poll();
            if (o instanceof float[]) {
                float data[] = (float[]) o;
                sendData(0, data);
                sendData(1, this.getId());

            }
        }
        return jobRunning;
    }

    public void clearMessageQue() {
        if (fifoMessageQue != null) {
            fifoMessageQue.clear();
        }
    }

    public void setJobRunningBool(boolean b) {
        jobRunning = b;
    }
}
