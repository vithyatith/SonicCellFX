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
package com.component.graph2D;
import javax.swing.*;
/**
 *
 * @author anakin
 */
public class UpdateGraph2DPane extends JPanel implements java.io.Serializable{
    private JTextField valueUpdateTF;
    private JTextField valueUpdateTFY;
    
    public UpdateGraph2DPane() {
        valueUpdateTF = new JTextField();
        valueUpdateTF.setEditable(false);
        valueUpdateTF.setText("         Value         ");
        add(valueUpdateTF);
        valueUpdateTFY = new JTextField();
        valueUpdateTFY.setEditable(false);
        valueUpdateTFY.setText("         Value         ");
        add(valueUpdateTFY);        
    }
    
    public void setXTextField(int _index){
        valueUpdateTF.setText(""+_index+"");
    }
    public void setXTextField(int _indexX, float _indexY){
        valueUpdateTF.setText(""+_indexX+"");
        valueUpdateTFY.setText(""+_indexY+"");
    }    
    
}
