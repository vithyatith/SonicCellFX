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

public class Graph2DFrame extends JFrame implements java.io.Serializable {
    private Graph2DPane graph=null;
    private UpdateGraph2DPane _updatePane = null;
    
    /** Creates a new instance of Graph2DFrame */
    public Graph2DFrame() {
        init();
    }
  
    private void init(){
        
        _updatePane = new UpdateGraph2DPane();
        graph = new Graph2DPane(_updatePane);
        
        add(graph);
        setSize(400,500);
    }
    
    public Graph2DPane getGraphPane(){
        return graph;
    }
    
    public static void main(String args[]){
        
        Graph2DFrame f = new Graph2DFrame();
        f.setVisible(true);
        
    }
    
}
