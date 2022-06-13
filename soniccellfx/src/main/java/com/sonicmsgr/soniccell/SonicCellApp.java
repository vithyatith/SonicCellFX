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
package com.sonicmsgr.soniccell;

import component.basic.AddNumberComponent;

import component.basic.NumberFloatComponent;
import component.basic.Print;
import component.basic.RandomNumberServerComponent;
import component.basic.StringComponent;

/**
 *
 * @author yada
 */
public class SonicCellApp {

    public SonicCellApp() {

        //testCreateMethod();
        //  testCreate();
        //   testLoad2();
    }

    public void testLoad2() {
        SonicCellSdk sonicCellSdk = new SonicCellSdk();
        sonicCellSdk.loadProjectFromJsonFile("test.json");

        sonicCellSdk.addMessageOutputListener(0, 0, new MessageListener() {
            @Override
            public void onReceived(int srcId, int thurId, Object data) {
                System.out.println("event listener = id = " + srcId + " thurId=" + thurId + " data = " + data.toString());
            }
        });
        sonicCellSdk.start();

//        FunctionComponent fc = sonicCellSdk.getFunctionComponentByAlias("fun1");
//        Object ob[] = (Object[]) fc.input(12);
//
//        float val = (float) ob[0];
//        System.out.println(val);
        // sonicCellSdk.sendDirectDataById(0, 0, 24);
        //   sonicCellSdk.sendDirectDataByAlias("print2", 0, "Testing print2");
        //  sonicCellSdk.sendDirectDataById(2, 0, "Testing print2");
        sonicCellSdk.stops();

        //   sonicCellSdk.printInfo();
    }

    public void testLoad() {
        SonicCellSdk sonicCellSdk = new SonicCellSdk();
        sonicCellSdk.loadProjectFromJsonFile("test.txt");

        sonicCellSdk.addMessageOutputListener(0, 0, new MessageListener() {
            @Override
            public void onReceived(int srcId, int thurId, Object data) {
                System.out.println("event listener = id = " + srcId + " thurId=" + thurId + " data = " + data.toString());
            }
        });
        sonicCellSdk.start();

        FunctionComponent fc = sonicCellSdk.getFunctionComponentByAlias("fun1");
        Object ob[] = (Object[]) fc.input(12);

        float val = (float) ob[0];
        System.out.println(val);

        // sonicCellSdk.sendDirectDataById(0, 0, 24);
        //   sonicCellSdk.sendDirectDataByAlias("print2", 0, "Testing print2");
        //  sonicCellSdk.sendDirectDataById(2, 0, "Testing print2");
        sonicCellSdk.stops();

        //   sonicCellSdk.printInfo();
    }

    public void testLoadProject() {
        SonicCellSdk sonicCellSdk = new SonicCellSdk();
        sonicCellSdk.loadProjectFromJsonFile("/Users/yada/hello.cell");

//        sonicCellSdk.addMessageOutputListener(0, 0, new MessageListener() {
//            @Override
//            public void onReceived(int srcId, int thurId, Object data) {
//                System.out.println("event listener = id = " + srcId + " thurId=" + thurId + " data = " + data.toString());
//            }
//        });

         
       // sonicCellSdk.start(0);
       
       //
       String value = (String)sonicCellSdk.execComponent(6, 0, "hey2");  


      // System.out.println("my = "+value);
     
    }

    public void testCreateMethod() {

        // SDK
        SonicCellSdk sonicCellSdk = new SonicCellSdk();
        sonicCellSdk.setOrderMode(false);

        Print print = new Print();
        Print print2 = new Print();
        NumberFloatComponent float1 = new NumberFloatComponent();
        NumberFloatComponent float2 = new NumberFloatComponent();
        AddNumberComponent addComponent = new AddNumberComponent();
        FunctionComponent function = new FunctionComponent();

        sonicCellSdk.addComponent(print, 3, 1);
        //  sonicCellSdk.addComponent(print2, 3, 1);

        //  sonicCellSdk.addComponent(float1, 0, 1);
        //  sonicCellSdk.addComponent(float2, 0, 2);
        //  sonicCellSdk.addComponent(addComponent, 3, 1);
        sonicCellSdk.addFunctionCompnent(function, 0, 0);
        sonicCellSdk.setAlias(function, "fun1");

        //   sonicCellSdk.addComponentToFunction(function, 0, 0, function)
        sonicCellSdk.setNumOfInOutputToFunction(function, 1, 1);
        sonicCellSdk.addConnectComponentInOutput(function, 0, print, 0);

        // 2
        sonicCellSdk.addComponentToFunction(float1, 0, 0, function);
        sonicCellSdk.addComponentToFunction(float2, 0, 1, function);

        sonicCellSdk.addThroughputReference(0, function, 0, float1, 0);

        sonicCellSdk.addThroughputReference(1, function, 0, float1, 0);

        sonicCellSdk.start();
        //  sonicCellSdk.sendDirectDataByComponent(function, 0, 3);

        // sonicCellSdk.removeComponentByAlias("fun1");
        // Object value[] = (Object[])function.input(34);
        FunctionComponent fun = sonicCellSdk.getFunctionComponentByAlias("fun1");
        if (fun != null) {
            Object value[] = (Object[]) fun.input(34);
            float val = (Float) value[0];
            System.out.println("value = " + val);
        }

        //   sonicCellSdk.stops();
        // sonicCellSdk.printInfo();
        //stringComponent.handleMessage(0, "VT");
        // stringComponent.onExecute();
        sonicCellSdk.saveProjectFile("test.txt");
//        
//
//        int ids[] = dataManagerSingleton.getComponentIdExecOrder();
//        
//        for(int i=0; i<ids.length; i++){
//            System.out.println(ids[i]);
//        }
    }

    public void testCreateMethod2() {

        // SDK
        SonicCellSdk sonicCellSdk = new SonicCellSdk();
        sonicCellSdk.setOrderMode(false);

        // 0 
        Print print = new Print();

        // 1
        NumberFloatComponent float1 = new NumberFloatComponent();
        // 2
        NumberFloatComponent float2 = new NumberFloatComponent();

        // 3
        AddNumberComponent add = new AddNumberComponent();

        // 4
        FunctionComponent function = new FunctionComponent();
        function.setNumberOfInOutput(2, 1);

        // 0
        sonicCellSdk.addComponent(print, 3, 1);
        // 1
        sonicCellSdk.addFunctionCompnent(function, 10, 0);

        // 2
        sonicCellSdk.addComponentToFunction(float1, 0, 0, function);
        // 3
        sonicCellSdk.addComponentToFunction(float2, 0, 1, function);
        // 4
        sonicCellSdk.addComponentToFunction(add, 2, 2, function);

        //
        sonicCellSdk.addConnectComponentInOutput(function, 0, float1, 0);
        sonicCellSdk.addConnectComponentInOutput(function, 1, float2, 1);

        sonicCellSdk.addConnectComponentInOutput(float1, 0, add, 0);
        sonicCellSdk.addConnectComponentInOutput(float2, 0, add, 1);

        sonicCellSdk.addConnectComponentInOutput(add, 0, print, 0);

        // sonicCellSdk.addConnectComponentInOutput(function, 0, print, 0);
        //  sonicCellSdk.addComponent(randomNumberServerComponent, 0, 0);
        sonicCellSdk.setAlias(print, "print");

//        sonicCellSdk.setComponentXYPosition(stringComponent, 0, 1);
//        sonicCellSdk.setComponentXYPosition(print, 12, 1);
//        sonicCellSdk.setComponentXYPosition(print, 13, 1);
//        sonicCellSdk.setComponentXYPosition(randomNumberServerComponent, 0, 2);
//        
        // Connect from thruOut to thuIn
        //   sonicCellSdk.addConnectComponentInOutput(stringComponent, 0, print, 0);
        //   sonicCellSdk.addConnectComponentInOutput(stringComponent, 0, print2, 0);
        // sonicCellSdk.addConnectComponentInOutput(randomNumberServerComponent, 0, print, 0); 
        // sonicCellSdk.removeConnectComponentInOutput(stringComponent, 0, print, 0);
        // stringComponent.runAvailableTime();
        // sonicCellSdk.deleteComponent(print);
        sonicCellSdk.start();
        sonicCellSdk.sendDirectDataByComponent(function, 0, 2);
        
        //   sonicCellSdk.sendDirectDataByComponent(function, 1, 4);

        //   sonicCellSdk.stops();
        // sonicCellSdk.printInfo();
        //stringComponent.handleMessage(0, "VT");
        // stringComponent.onExecute();
        sonicCellSdk.saveProjectFile("test.txt");
//        
//
//        int ids[] = dataManagerSingleton.getComponentIdExecOrder();
//        
//        for(int i=0; i<ids.length; i++){
//            System.out.println(ids[i]);
//        }
    }

    public void testCreate() {

        // SDK
        SonicCellSdk sonicCellSdk = new SonicCellSdk();
        sonicCellSdk.setOrderMode(false);

        RandomNumberServerComponent randomNumberServerComponent = new RandomNumberServerComponent();
        Print print = new Print();
        Print print2 = new Print();
        StringComponent stringComponent = new StringComponent();

        NumberFloatComponent float1 = new NumberFloatComponent();
        NumberFloatComponent float2 = new NumberFloatComponent();
        AddNumberComponent add = new AddNumberComponent();

        FunctionComponent function = new FunctionComponent();
        sonicCellSdk.setNumOfInOutputToFunction(function, 2, 1);
        // function.setNumberOfInOutput(2,1);

        // set property
        stringComponent.setProperty("value", "Dada Charlotte Vithya");

        // Add component
        sonicCellSdk.addComponent(stringComponent, 0, 1);
        sonicCellSdk.addComponent(print, 2, 1);
        sonicCellSdk.addComponent(print2, 3, 1);

        // Method
        sonicCellSdk.addFunctionCompnent(function, 10, 0);
        sonicCellSdk.addComponentToFunction(float1, 0, 0, function);
        sonicCellSdk.addComponentToFunction(float2, 0, 1, function);
        sonicCellSdk.addComponentToFunction(add, 2, 2, function);

        //
        sonicCellSdk.addConnectComponentInOutput(function, 0, float1, 0);
        sonicCellSdk.addConnectComponentInOutput(function, 1, float2, 1);

        sonicCellSdk.addConnectComponentInOutput(float1, 0, add, 0);
        sonicCellSdk.addConnectComponentInOutput(float2, 0, add, 1);

        sonicCellSdk.sendDirectDataByComponent(function, 0, 2);
        sonicCellSdk.sendDirectDataByComponent(function, 1, 4);

        //  sonicCellSdk.addComponent(randomNumberServerComponent, 0, 0);
        sonicCellSdk.setAlias(print, "print");
        sonicCellSdk.setAlias(print2, "print2");

        // Make it enable
        sonicCellSdk.setEnableComponent(randomNumberServerComponent, false);

//        sonicCellSdk.setComponentXYPosition(stringComponent, 0, 1);
//        sonicCellSdk.setComponentXYPosition(print, 12, 1);
//        sonicCellSdk.setComponentXYPosition(print, 13, 1);
//        sonicCellSdk.setComponentXYPosition(randomNumberServerComponent, 0, 2);
//        
        // Connect from thruOut to thuIn
        sonicCellSdk.addConnectComponentInOutput(stringComponent, 0, print, 0);
        sonicCellSdk.addConnectComponentInOutput(stringComponent, 0, print2, 0);
        // sonicCellSdk.addConnectComponentInOutput(randomNumberServerComponent, 0, print, 0); 
        // sonicCellSdk.removeConnectComponentInOutput(stringComponent, 0, print, 0);

        // stringComponent.runAvailableTime();
        // sonicCellSdk.deleteComponent(print);
        sonicCellSdk.start();
        //   sonicCellSdk.stops();
        // sonicCellSdk.printInfo();

        //stringComponent.handleMessage(0, "VT");
        // stringComponent.onExecute();
        sonicCellSdk.saveProjectFile("test.txt");
//        
//
//        int ids[] = dataManagerSingleton.getComponentIdExecOrder();
//        
//        for(int i=0; i<ids.length; i++){
//            System.out.println(ids[i]);
//        }
    }

    public static void main(String args[]) {
        SonicCellApp sonicCellApp = new SonicCellApp();
       sonicCellApp.testLoadProject();

//        if (args != null) {
//            for (int i = 0; i < args.length; i++) {
//                String v = args[i];
//                if (v.equalsIgnoreCase("-f")) {
//                    i++;
//
//                    if (i < args.length) {
//                        String filename = args[i];
//                        SonicCellSdk sonicCellSdk = new SonicCellSdk();
//                        sonicCellSdk.loadProjectFromJsonFile(filename);
//                        sonicCellSdk.start();
//                    }
//                }
//            }
//        }
    }

}
