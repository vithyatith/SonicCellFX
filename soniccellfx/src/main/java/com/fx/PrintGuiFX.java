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
package com.fx;

import com.google.gson.Gson;
import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import javafx.application.Platform;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class PrintGuiFX extends ComponentAbstraction {

    private float dataScalarF;
    private long dataScalarl;
    private double dataScalarD;
    private float dataVectorF[];
    private short dataVectorS[];
    private double dataVectorD[];
    private int dataVectorI[];
    private float dataMatrixF[][];
    private double dataMatrixD[][];
    private byte dataScalarB;
    private byte dataVectorB[];
    private byte dataMatrixB[][];
    private boolean boolValue;

    private StringBuilder sb;
    private String datastr = "";
    private String datastrvec[];
    private String datamat[][];
    private ArrayList<String> arrayList;
    // private int DATA_TYPE = 0;

    private boolean dataInBoolReady = false;
    private String title = "PrintFX";

    private Gson gson = new Gson();
    private Object objValue = null;

    private ListViewConsole listViewConosle = null;
    
     private Queue<Object> fifoObject = new LinkedList<Object>();

  

    @Override
    public boolean start() {
        dataInBoolReady = false;
        title = getProperty("title").toString();
        if (listViewConosle != null) {
            listViewConosle.clearListView();
        }
        showUI();
        return true;
    }

    @Override
    public void stop() {

    }
//

    @Override
    public void mouseDoubleClick() {
                
        showUI();
    }

    @Override
    public void doneLoadingThisClassFromProjectFile() {

    }

    private enum TypeEnum {

        BYTE,
        BYTEVECTOR,
        BYTEMATRIX,
        FLOAT,
        DOUBLE,
        LONG,
        FLOATVECTOR,
        INTVECTOR,
        SHORTVECTOR,
        DOUBLEVECTOR,
        FLOATMATRIX,
        DOUBLEMATRIX,
        STRING,
        STRINGVECTOR,
        STRINGMATRIX,
        ARRAYLIST,
        VECTOR,
        BOOLEAN,
        OBJECT;
    }
    private TypeEnum typeEnum = TypeEnum.FLOAT;

    public PrintGuiFX() {
        setName("PrintFX");
        init();
        this.setHasUISupport(true);
    }

    private void init() {
        sb = new StringBuilder();
     //   this.setProperty("Console", Boolean.toString(consoleBool));
     this.setProperty("title",title);
        this.addInput(new DataTypeIO("all"));
    }

    @Override
    public synchronized Object onExecute() {

        if (dataInBoolReady) {
            dataInBoolReady = false;

            if(listViewConosle==null){
                return null;
            }

            if (typeEnum == TypeEnum.DOUBLE) {
                listViewConosle.apppendTextListView(dataScalarD + "");
            } else if (typeEnum == TypeEnum.LONG) {

                listViewConosle.apppendTextListView(dataScalarl + "");

            } else if (typeEnum == TypeEnum.BYTEVECTOR) {
                sb.delete(0, sb.length());
                String str = gson.toJson(dataVectorB);

                listViewConosle.apppendTextListView(str);

            } else if (typeEnum == TypeEnum.FLOATVECTOR) {
                sb.delete(0, sb.length());
                for (int i = 0; i < dataVectorF.length; i++) {
                    sb.append(dataVectorF[i] + " ");
                }

                listViewConosle.apppendTextListView(sb.toString());
            } else if (typeEnum == TypeEnum.SHORTVECTOR) {
                sb.delete(0, sb.length());
                for (int i = 0; i < dataVectorS.length; i++) {
                    sb.append(dataVectorS[i] + " ");
                }

                listViewConosle.apppendTextListView(sb.toString());
            } else if (typeEnum == TypeEnum.INTVECTOR) {
                sb.delete(0, sb.length());
                for (int i = 0; i < dataVectorI.length; i++) {
                    sb.append(dataVectorI[i] + " ");
                }

                listViewConosle.apppendTextListView(sb.toString());
            } else if (typeEnum == TypeEnum.DOUBLEVECTOR) {
                sb.delete(0, sb.length());
                for (int i = 0; i < dataVectorD.length; i++) {
                    sb.append(dataVectorD[i] + " ");
                }

                listViewConosle.apppendTextListView(sb.toString());
            } else if (typeEnum == TypeEnum.BYTEMATRIX) {

                int mLen = dataMatrixB.length;
                int nLen = dataMatrixB[0].length;

                for (int i = 0; i < mLen; i++) {
                    sb.delete(0, sb.length());
                    for (int j = 0; j < nLen; j++) {
                        sb.append(dataMatrixB[i][j] + " ");
                    }

                    listViewConosle.apppendTextListView(sb.toString());

                }

            } else if (typeEnum == TypeEnum.FLOATMATRIX) {

                int mLen = dataMatrixF.length;
                int nLen = dataMatrixF[0].length;

                for (int i = 0; i < mLen; i++) {
                    sb.delete(0, sb.length());
                    for (int j = 0; j < nLen; j++) {
                        sb.append(dataMatrixF[i][j] + " ");
                    }

                    listViewConosle.apppendTextListView(sb.toString());

                }
            } else if (typeEnum == TypeEnum.DOUBLEMATRIX) {

                int mLen = dataMatrixD.length;
                int nLen = dataMatrixD[0].length;

                for (int i = 0; i < mLen; i++) {
                    sb.delete(0, sb.length());
                    for (int j = 0; j < nLen; j++) {
                        sb.append(dataMatrixD[i][j] + " ");
                    }

                    listViewConosle.apppendTextListView(sb.toString());

                }

            } else if (typeEnum == TypeEnum.STRING) {
                sb.delete(0, sb.length());
                listViewConosle.apppendTextListView(datastr);

            } else if (typeEnum == TypeEnum.STRINGVECTOR) {
                for (int i = 0; i < datastrvec.length; i++) {

                    listViewConosle.apppendTextListView(datastrvec[i]);

                }

            } else if (typeEnum == TypeEnum.STRINGMATRIX) {
                int M = datamat.length;
                int N = datamat[0].length;
                for (int j = 0; j < M; j++) {
                    sb.delete(0, sb.length());
                    for (int i = 0; i < N; i++) {
                        sb.append(datamat[j][i] + " ");
                    }

                    listViewConosle.apppendTextListView(sb.toString());

                }

            } else if (typeEnum == TypeEnum.BOOLEAN) {

                listViewConosle.apppendTextListView(boolValue + "");

            } else if (typeEnum == TypeEnum.ARRAYLIST) {

                for (int i = 0; i < arrayList.size(); i++) {
                    System.out.println(arrayList.get(i));
                    listViewConosle.apppendTextListView(arrayList.get(i));
                }

            } else if (typeEnum == TypeEnum.OBJECT) {
                if (objValue != null) {
                    listViewConosle.apppendTextListView(objValue.toString());
                }
            }
        }
        return null;
    }

    @Override
    public void handleMessage(int thru, Object obj) {

        objValue = obj;
        if (obj == null) {
            return;
        }
        if(listViewConosle==null){
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {

            }
        }
        
        

        if (obj instanceof Float) {
            obj = (double) ((float) obj);
            typeEnum = TypeEnum.DOUBLE;
            dataScalarD = (Double) obj;
            dataInBoolReady = true;
        } else if (obj instanceof Integer) {
            obj = (double) ((int) obj);
            typeEnum = TypeEnum.DOUBLE;
            dataScalarD = (Double) obj;
            dataInBoolReady = true;
        } else if (obj instanceof Double) {
            obj = (double) ((double) obj);
            typeEnum = TypeEnum.DOUBLE;
            dataScalarD = (Double) obj;
            dataInBoolReady = true;
        } else if (obj instanceof Long) {
            obj = (long) ((long) obj);
            typeEnum = TypeEnum.LONG;
            dataScalarl = (Long) obj;
            dataInBoolReady = true;
        } else if (obj instanceof Short) {
            obj = (double) ((short) obj);
            typeEnum = TypeEnum.DOUBLE;
            dataScalarD = (Double) obj;
            dataInBoolReady = true;
        } else if (obj instanceof Byte) {
            obj = (double) ((byte) obj);
            typeEnum = TypeEnum.DOUBLE;
            dataScalarD = (Double) obj;
            dataInBoolReady = true;
        } else if (obj instanceof byte[]) {
            typeEnum = TypeEnum.BYTEVECTOR;
            dataVectorB = (byte[]) obj;
            dataInBoolReady = true;
        } else if (obj instanceof byte[][]) {
            typeEnum = TypeEnum.BYTEMATRIX;
            dataMatrixB = (byte[][]) obj;
            dataInBoolReady = true;
        } else if (obj instanceof float[]) {
            typeEnum = TypeEnum.FLOATVECTOR;
            dataVectorF = (float[]) obj;
            dataInBoolReady = true;
        } else if (obj instanceof short[]) {
            typeEnum = TypeEnum.SHORTVECTOR;
            dataVectorS = (short[]) obj;
            dataInBoolReady = true;
        } else if (obj instanceof int[]) {
            typeEnum = TypeEnum.INTVECTOR;
            dataVectorI = (int[]) obj;
            dataInBoolReady = true;
        } else if ((obj instanceof long[]) || (obj instanceof double[])) {
            typeEnum = TypeEnum.DOUBLEVECTOR;
            dataVectorD = (double[]) obj;
            dataInBoolReady = true;
        } else if ((obj instanceof float[][]) || (obj instanceof int[][]) || (obj instanceof short[][])) {
            typeEnum = TypeEnum.FLOATMATRIX;
            dataMatrixF = (float[][]) obj;
            dataInBoolReady = true;
        } else if ((obj instanceof long[][]) || (obj instanceof double[][])) {
            typeEnum = TypeEnum.DOUBLEMATRIX;
            dataMatrixD = (double[][]) obj;
            dataInBoolReady = true;
        } else if (obj instanceof String) {
            typeEnum = TypeEnum.STRING;
            datastr = obj.toString();
            dataInBoolReady = true;
        } else if (obj instanceof String[]) {
            typeEnum = TypeEnum.STRINGVECTOR;
            datastrvec = (String[]) obj;
            dataInBoolReady = true;
        } else if (obj instanceof String[][]) {
            typeEnum = TypeEnum.STRINGMATRIX;
            datamat = (String[][]) obj;
            dataInBoolReady = true;
        } else if (obj instanceof ArrayList) {

            typeEnum = TypeEnum.ARRAYLIST;
            arrayList = (ArrayList<String>) obj;
            dataInBoolReady = true;
        } else if (obj instanceof Boolean) {
            typeEnum = TypeEnum.BOOLEAN;
            boolValue = (Boolean) obj;
            dataInBoolReady = true;
        } else {
            typeEnum = TypeEnum.OBJECT;
            dataInBoolReady = true;
        }
    }

    @Override
    public void loadProperty(String key, Object value) {
        if (key.equalsIgnoreCase("title")) {
            title = getProperty("title").toString();
        }
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

        if(listViewConosle!=null){
            listViewConosle.clearListView();
            listViewConosle.hide();
            listViewConosle = null;
        }
        
    }

    @Override
    public String getHelp() {

        String doc = "";

        return doc;
    }

    public void showUI() {
        title = getProperty("title").toString();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
   //             new JFXPanel(); // this will prepare JavaFX toolkit and environment
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (listViewConosle == null) {
                                listViewConosle = new ListViewConsole();
                                listViewConosle.create();
                            }

                            listViewConosle.setTitle(title);
                            listViewConosle.show();
                            

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }
}
