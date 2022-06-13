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
package com.sonicmsgr.soniccell.fx;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


/**
 *
 * @author yada
 */
public class IOShape extends Circle{
    private Object objectTag;
    protected int srcId = -1;
    protected int thruId = -1;
    protected boolean isOutput = true;
    protected boolean isInputOccupy =false;
    protected String toolTipMsg = "";
    protected Color color = Color.BLACK;
    protected IOShape(double centerX, double centerY, double radius){
        super(centerX, centerY, radius);
    }
    protected void setTag(Object obj){
        objectTag = obj;
    }
    protected Object getTag(){
        return objectTag;
    }

}
