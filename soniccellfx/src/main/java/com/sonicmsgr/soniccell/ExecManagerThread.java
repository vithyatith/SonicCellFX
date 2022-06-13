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
package com.sonicmsgr.soniccell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 *
 * @author Vithya Tith
 */
public class ExecManagerThread extends Thread {

    private int orderArray[] = null;
    private int orderSize = 0;
    private boolean exitBool = false;
    private boolean waitBool = true;
    private boolean pauseWaitBool = false;
    private ComponentAbstraction componentArray[] = null;
    private ComponentDataManager componentDataManager = null;

    public ExecManagerThread(ComponentAbstraction componentArray[], ComponentDataManager componentDataManager) {
        this.componentArray = componentArray;
        this.componentDataManager = componentDataManager;
    }

    public synchronized void run() {

        while (true) {
            if (waitBool) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                    System.out.println("Something wrong..stoping.." + e.getMessage());
                    this.stopProcess();

                    break;
                }
            }

            for (int i = 0; i < orderSize; i++) {
                if (exitBool) {
                    break;
                }
                if (pauseWaitBool) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                ComponentAbstraction componentAbstraction = componentArray[orderArray[i]];
                if (componentAbstraction.enabled) {
                    componentAbstraction.onExecute();
                }
            }
        }

    }

    private Thread threadPool[] = null;

    private void shutdownExecThreadPool() {

        if (threadPool != null) {

            for (int i = 0; i < threadPool.length; i++) {
                try {
                    Thread t = threadPool[i];
                    t.interrupt();
                    threadPool[i] = null;
                    t = null;
                } catch (Exception e) {

                }

                threadPool[i] = null;
            }

        }
        threadPool = null;

    }

    private int idTmp = 0;

    private boolean orginalBool = false;

    public void startProcess() {

        startProcess(-1);
    }

    public HashSet<Integer> listAllConnectionsFromNode(int id) {
        HashSet<Integer> rootHashSet = new HashSet<Integer>();
        try {
            LinkedHashSet hashSet = componentDataManager.listAllComponentConnectionById(id);
            Iterator<Integer> rootIT = hashSet.iterator();
            while (rootIT.hasNext()) {
                int srcId = rootIT.next();

                if (srcId == id) {
                    System.out.println("Avoid calling itself 1" + id);
                    continue;
                }
                rootHashSet.add(srcId);

                HashSet<Integer> childHashSet = listAllConnectionsFromNode(srcId);

                Iterator<Integer> childIT = childHashSet.iterator();
                while (childIT.hasNext()) {
                    srcId = childIT.next();

                    rootHashSet.add(srcId);
                }
            }
        } catch (StackOverflowError e) {
            System.out.println("id ===========" + id);
            e.printStackTrace();
        }

        return rootHashSet;
    }

    public void startProcess(int thisId) {
        boolean orderMode = componentDataManager.orderMode;
        // Get the id order

        System.out.println(" ExecManager....==============>");
        orderArray = componentDataManager.getIdExecOrderArray(thisId);

        orderSize = orderArray.length;

        ComponentInfo c = componentDataManager.getComponentInfoHashMap().get(thisId);

        if (c.isFunction) {

            int numOfInput = c.numOfInput;
            // Check if num input is less than 1, exit
            if(numOfInput<1){
                System.out.println("Zero...");
                return;
            }else{
                
                
                
                
            }
            
        }

        boolean runOneTimeBool = false;

        for (int i = 0; i < orderSize; i++) {
            idTmp = orderArray[i];

            ComponentAbstraction com = componentArray[idTmp];
            if (com == null) {
                continue;
            }

            if (com.enabled) {

                c = componentDataManager.getComponentInfoHashMap().get(idTmp);

                if (c.isFunction) {
                    int ids[] = componentDataManager.getIdExecOrderArrayForMethodId(idTmp);

                    FunctionComponent fc = (FunctionComponent) com;

                    fc.setExecutingComponentIds(runOneTimeBool, ids, componentArray, componentDataManager);
                }

                if (thisId < 0) {
                    componentArray[idTmp].start();
                } else {
                    if (com.getId() == thisId) {
                        // Only start what was click. Exit out
                        componentArray[idTmp].start();
                       // break;
                    }
                }
            }
        }
        if (thisId >= 0) {
            runOneTimeBool = true;
            orderMode = false;
            componentDataManager.orderMode = false;

            // Get all the connection from what was selected
            HashSet<Integer> hashSet = listAllConnectionsFromNode(thisId);
            Iterator<Integer> it = hashSet.iterator();
            while (it.hasNext()) {
                int srcId = it.next();
                for (int i = 0; i < orderSize; i++) {
                    idTmp = orderArray[i];

                    ComponentAbstraction com = componentArray[idTmp];
                    if (com != null) {
                        if (com.enabled) {
                            if (com.getId() == srcId) {
                                com.start();
                                break;
                            }
                        }
                    }

                }
            }

        }

        // Loop over and start() the individual 
//        for (int i = 0; i < orderSize; i++) {
//            idTmp = orderArray[i];
//
//            ComponentAbstraction com = componentArray[idTmp];
//            if (com == null) {
//                continue;
//            }
//
//            if (com.enabled) {
//
//                ComponentInfo c = componentDataManager.getComponentInfoHashMap().get(idTmp);
//
//                if (c.isFunction) {
//                    int ids[] = componentDataManager.getIdExecOrderArrayForMethodId(idTmp);
//
//                    FunctionComponent fc = (FunctionComponent) com;
//
//                    fc.setExecutingComponentIds(runOneTimeBool, ids, componentArray, componentDataManager);
//                }
//
//                if (thisId < 0) {
//                    componentArray[idTmp].start();
//                } else {
//                    if (com.getId() == thisId) {
//                        // Only start
//                        componentArray[idTmp].start();
//                    }
//                }
//            }
//        }
        ArrayList<ThroughputReferenceInfo> al = componentDataManager.throughputReferenceInfoArrayList;

        for (int i = 0; i < al.size(); i++) {
            ThroughputReferenceInfo th = al.get(i);

            FunctionComponent fc = (FunctionComponent) componentArray[th.srcId];
            // input
            if (th.ioType == 0) {
                fc.setInDest(th.srcThruId, th.destId, th.destThruId);
            } else { // output
                fc.setOutDest(th.srcThruId, th.destId, th.destThruId);
            }

        }

        pauseWaitBool = false;
        exitBool = false;
        waitBool = false;

        if (orderMode) {
            this.start();
            synchronized (this) {
                this.notify();
            }
        } else {

            componentDataManager.orderMode = orderMode;

            shutdownExecThreadPool();

            threadPool = new Thread[orderSize];

            for (int index = 0; index < orderSize; index++) {
                if (exitBool) {
                    break;
                }
                if (pauseWaitBool) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                idTmp = orderArray[index];

                final ComponentAbstraction componentAbstractionTmp = componentArray[idTmp];
                if (componentAbstractionTmp == null) {
                    continue;
                }
                if (thisId < 0) {
                    if (componentAbstractionTmp.enabled) {

                        if (componentDataManager.multiThreadMode) {
                            threadPool[index] = new Thread() {
                                @Override
                                public void run() {

                                    componentAbstractionTmp.onExecute();
                                }
                            };
                            threadPool[index].start();
                        } else {
                            componentAbstractionTmp.onExecute();
                        }

                    }
                } else {
                    if (componentAbstractionTmp.enabled) {

                        if (componentAbstractionTmp.getId() == thisId) {

                            if (componentDataManager.multiThreadMode) {
                                threadPool[index] = new Thread() {
                                    @Override
                                    public void run() {

                                        componentAbstractionTmp.onExecute();
                                    }
                                };
                                threadPool[index].start();
                            } else {
                                componentAbstractionTmp.onExecute();
                            }
                        }
                    }
                }

            }
        }
    }

    public void stopProcess(int thisId) {

        boolean orderMode = componentDataManager.orderMode;
        boolean runOneTimeBool = false;
        if (thisId >= 0) {
            orderMode = false;
            runOneTimeBool = true;
            componentDataManager.orderMode = false;

            HashSet<Integer> hashSet = listAllConnectionsFromNode(thisId);
            Iterator<Integer> it = hashSet.iterator();
            while (it.hasNext()) {
                int srcId = it.next();
                for (int i = 0; i < orderSize; i++) {
                    idTmp = orderArray[i];

                    ComponentAbstraction com = componentArray[idTmp];
                    if (com == null) {
                        continue;
                    }
                    if (com.enabled) {
                        if (com.getId() == srcId) {
                            com.stop();
                            break;
                        }
                    }

                }
            }
        }

        // Get the id order
        orderArray = componentDataManager.getIdExecOrderArray();
        orderSize = orderArray.length;

        for (int i = 0; i < orderSize; i++) {
            idTmp = orderArray[i];

            ComponentAbstraction com = componentArray[idTmp];
            if (com == null) {
                continue;
            }

            if (com.enabled) {

                ComponentInfo c = componentDataManager.getComponentInfoHashMap().get(idTmp);

                if (c.isFunction) {
                    int ids[] = componentDataManager.getIdExecOrderArrayForMethodId(idTmp);

                    FunctionComponent fc = (FunctionComponent) com;
                    fc.setExecutingComponentIds(runOneTimeBool, ids, componentArray, componentDataManager);
                }

                if (thisId < 0) {
                    componentArray[idTmp].stop();
                } else {
                    if (com.getId() == thisId) {
                        componentArray[idTmp].stop();
                    }
                }
            }
        }

        ArrayList<ThroughputReferenceInfo> al = componentDataManager.throughputReferenceInfoArrayList;

        if (al.size() > 0) {
            for (int i = 0; i < al.size(); i++) {
                ThroughputReferenceInfo th = al.get(i);

                FunctionComponent fc = (FunctionComponent) componentArray[th.srcId];
                // input
                if (th.ioType == 0) {
                    fc.setInDest(th.srcThruId, th.destId, th.destThruId);
                } else { // output
                    fc.setOutDest(th.srcThruId, th.destId, th.destThruId);
                }

            }
        } else {
            LinkedHashSet hashSet = componentDataManager.listAllComponentConnectionById(thisId);
            Iterator<Integer> it = hashSet.iterator();
            while (it.hasNext()) {
                int srcId = it.next();
                for (int i = 0; i < orderSize; i++) {
                    idTmp = orderArray[i];

                    ComponentAbstraction com = componentArray[idTmp];
                    if (com == null) {
                        continue;
                    }
                    if (com.enabled) {
                        if (com.getId() == srcId) {
                            com.stop();
                            break;
                        }
                    }

                }
            }

        }

        pauseWaitBool = false;
        exitBool = false;
        waitBool = false;

        if (orderMode) {
            this.start();
            synchronized (this) {
                this.notify();
            }
        } else {

            componentDataManager.orderMode = orderMode;

            shutdownExecThreadPool();

            threadPool = new Thread[orderSize];

            for (int index = 0; index < orderSize; index++) {
                if (exitBool) {
                    break;
                }
                if (pauseWaitBool) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                idTmp = orderArray[index];

                final ComponentAbstraction componentAbstractionTmp = componentArray[idTmp];
                if (thisId < 0) {
                    if (componentAbstractionTmp.enabled) {

//                        if (componentDataManager.multiThreadMode) {
//                            threadPool[index] = new Thread() {
//                                @Override
//                                public void run() {
//
//                                  //  componentAbstractionTmp.onExecute();
//                                }
//                            };
//                            threadPool[index].start();
//                        } else {
//                           // componentAbstractionTmp.onExecute();
//                        }
                        componentAbstractionTmp.stop();

                    }
                } else {

                    if ((componentAbstractionTmp != null) && (componentAbstractionTmp.enabled)) {

                        if (componentAbstractionTmp.getId() == thisId) {

//                            if (componentDataManager.multiThreadMode) {
//                                threadPool[index] = new Thread() {
//                                    @Override
//                                    public void run() {
//
//                                      //  componentAbstractionTmp.onExecute();
//                                    }
//                                };
//                                //threadPool[index].start();
//                            } else {
//                               // componentAbstractionTmp.onExecute();
//                            }
                            componentAbstractionTmp.stop();
                        }
                    }
                }

            }
        }
    }

//    public void starOneComponenttProcess22(int componentId) {
//
//        // Get the id order
//        orderArray = componentDataManager.getIdExecOrderArray();
//        orderSize = orderArray.length;
//        boolean runOneTimeBool = false;
//        if(componentId>-1){
//            runOneTimeBool = true;
//        }
//
//        // Start the selected component
//        for (int i = 0; i < orderSize; i++) {
//            idTmp = orderArray[i];
//            ComponentAbstraction com = componentArray[idTmp];
//
//            if (com.enabled) {
//
//                if (componentId == com.getId()) {
//                    ComponentInfo c = componentDataManager.getComponentInfoHashMap().get(idTmp);
//
//                    if (c.isFunction) {
//                        int ids[] = componentDataManager.getIdExecOrderArrayForMethodId(idTmp);
//
//                        FunctionComponent fc = (FunctionComponent) com;
//                        fc.setExecutingComponentIds(runOneTimeBool,ids, componentArray, componentDataManager);
//                    }
//
//                    componentArray[idTmp].start();
//                }
//            }
//        }
//
//        ArrayList<ThroughputReferenceInfo> al = componentDataManager.throughputReferenceInfoArrayList;
//
//        for (int i = 0; i < al.size(); i++) {
//            ThroughputReferenceInfo th = al.get(i);
//
//            FunctionComponent fc = (FunctionComponent) componentArray[th.srcId];
//            // input
//            if (th.ioType == 0) {
//                fc.setInDest(th.srcThruId, th.destId, th.destThruId);
//            } else {
//                // output
//                fc.setOutDest(th.srcThruId, th.destId, th.destThruId);
//            }
//
//        }
//
//        pauseWaitBool = false;
//        exitBool = false;
//        waitBool = false;
//
//        if (componentDataManager.orderMode) {
//            this.start();
//            synchronized (this) {
//                this.notify();
//            }
//        } else {
//
//            shutdownExecThreadPool();
//
//            threadPool = new Thread[orderSize];
//
//            for (int index = 0; index < orderSize; index++) {
//                if (exitBool) {
//                    break;
//                }
//                if (pauseWaitBool) {
//                    try {
//                        this.wait();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                idTmp = orderArray[index];
//
//                final ComponentAbstraction componentAbstractionTmp = componentArray[idTmp];
//                if (componentAbstractionTmp.enabled) {
//
//                    if (componentDataManager.multiThreadMode) {
//                        threadPool[index] = new Thread() {
//                            @Override
//                            public void run() {
//                                componentAbstractionTmp.onExecute();
//                            }
//                        };
//                        threadPool[index].start();
//                    } else {
//
//                        // if()
//                        componentAbstractionTmp.onExecute();
//                    }
//
//                }
//            }
//        }
//    }
    public void stopProcess() {
        for (int i = 0; i < orderSize; i++) {
            if (componentArray[orderArray[i]].enabled) {
                componentArray[orderArray[i]].stop();
            }
        }

        pauseWaitBool = false;
        exitBool = true;
        waitBool = true;
        shutdownExecThreadPool();
    }

    public void pauseProcess() {
        pauseWaitBool = true;
        exitBool = false;
        waitBool = false;
    }

    public void resumeProcess() {
        pauseWaitBool = false;
        exitBool = false;
        waitBool = false;
        if (componentDataManager.orderMode) {
            synchronized (this) {
                this.notify();
            }
        }
    }
}
