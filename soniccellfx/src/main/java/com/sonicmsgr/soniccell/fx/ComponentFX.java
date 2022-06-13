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

import com.sonicmsgr.soniccell.ComponentAbstraction;
import com.sonicmsgr.soniccell.DataTypeIO;
import com.sonicmsgr.soniccell.DataTypeIOList;
import com.sonicmsgr.soniccell.FunctionComponent;
import com.sonicmsgr.soniccell.FunctionInputComponent;
import com.sonicmsgr.soniccell.FunctionOutputComponent;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 * @author yada
 */
public class ComponentFX {

    private Rectangle rectangle;
    private Text textBox = new Text("");
    private Pane pane;
    private HashMap<Integer, IOShape> inputCircleHashMap = new HashMap<Integer, IOShape>();
    private HashMap<Integer, IOShape> outputCircleHashMap = new HashMap<Integer, IOShape>();
    private HashMap<Integer, Line> inputLineHashMap = new HashMap<Integer, Line>();
    private HashMap<Integer, Line> outputLineHashMap = new HashMap<Integer, Line>();
    private Rectangle uiRect = new Rectangle();

    private int currentCol = 0;
    private int currentRow = 0;

    private Color functionColor = Color.ORANGE;
    private Color uiColor = Color.WHITE;
    private Color platformIndependentColor = Color.GREEN;
    private Color platformPCColor = Color.BLUE;
    private Color platformAndroidColor = Color.RED;

    //protected int paneFunctionId = -1;
    protected int functionId = 0;

    public IOShape getCircleInput(int id) {
        return inputCircleHashMap.get(id);
    }

    public IOShape getCircleOutput(int id) {
        return outputCircleHashMap.get(id);
    }

    private ComponentAbstraction com;

    private EventHandler<MouseEvent> rectangleHandlerPress;
    private EventHandler<MouseEvent> rectangleHandlerDrag;

    private EventHandler<MouseEvent> circleHandlerPress;
    private EventHandler<MouseEvent> circleHandlerEnter;

    private EventHandler<MouseEvent> circleHandlerExit;

    private boolean isCorrectionFunctionIdBool = true;

    private boolean hashUISupport = false;

    public void setStartedColor() {

        functionColor = Color.BLACK;
        uiColor = Color.WHITE;
        platformIndependentColor = Color.BLACK;
        platformPCColor = Color.BLACK;
        platformAndroidColor = Color.BLACK;

        rectangle.setStroke(platformAndroidColor);
        rectangle.setFill(platformAndroidColor);

    }

    public void setStopColor() {

        functionColor = Color.ORANGE;
        uiColor = Color.WHITE;
        platformIndependentColor = Color.GREEN;
        platformPCColor = Color.BLUE;
        platformAndroidColor = Color.RED;

        rectangle.setStroke(platformAndroidColor);
        rectangle.setFill(platformAndroidColor);

    }

//    public void resetColor() {
//        functionColor = Color.ORANGE;
//        uiColor = Color.WHITE;
//        platformIndependentColor = Color.GREEN;
//        platformPCColor = Color.BLUE;
//        platformAndroidColor = Color.RED;
//
//        this.reDrawInit();
//    }
    public ComponentFX(Pane pane, ComponentAbstraction com, boolean isCorrectionFunctionIdBool) {
        this.com = com;
        this.isCorrectionFunctionIdBool = isCorrectionFunctionIdBool;
        hashUISupport = com.getHasUISupport();
        init(pane, com, com.getBodySize());

        ArrayList<DataTypeIOList> inputDataTypeAL = com.getInputDataType();

    }

    public int getId() {
        return com.getId();
    }

    protected void setEnable(boolean b) {
        com.setEnable(b);
        updateEnable();
    }

    protected boolean getEnable() {
        return com.getEnable();
    }

    protected void updateEnable() {

        if (com.getEnable()) {
            //rectangle.setStroke(Color.BLACK);

            Color color = platformIndependentColor;
            if (com.getPlatformSupport() == 1) {
                color = platformPCColor;
            } else if (com.getPlatformSupport() == 2) {
                color = platformAndroidColor;
            }
            rectangle.setStroke(Color.BLACK);
            rectangle.setFill(color);
        } else {
            rectangle.setStroke(Color.BLACK);
            rectangle.setFill(Color.GRAY);
        }
    }

    protected int getRectBodySize() {
        return com.getBodySize();
    }

    protected void refreshComponentInfo() {
        textBox.setText(com.getName());

    }

    protected void redrawComponentFX() {
        redrawComponentFX(true);
    }

    protected void redrawComponentFX(boolean clearBool) {
        if (rectangle != null) {
            pane.getChildren().remove(rectangle);
        }

        if (hashUISupport) {
            pane.getChildren().remove(uiRect);
        }
        pane.getChildren().remove(textBox);

        for (int i = 0; i < inputCircleHashMap.size(); i++) {
            IOShape io = inputCircleHashMap.get((Integer) i);
            boolean b = pane.getChildren().remove(io);
            //System.out.println("in circle b = " + b);
        }
        if (clearBool) {
            inputCircleHashMap.clear();
        }

        for (int i = 0; i < outputCircleHashMap.size(); i++) {
            IOShape io = outputCircleHashMap.get((Integer) i);
            boolean b = pane.getChildren().remove(io);
            //System.out.println("in circle b = " + b);
        }
        if (clearBool) {
            outputCircleHashMap.clear();
        }

        for (int i = 0; i < inputLineHashMap.size(); i++) {
            Line io = inputLineHashMap.get((Integer) i);
            boolean b = pane.getChildren().remove(io);
            //System.out.println("in circle b = " + b);
        }
        if (clearBool) {
            inputLineHashMap.clear();
        }

        for (int i = 0; i < outputLineHashMap.size(); i++) {
            Line io = outputLineHashMap.get((Integer) i);
            boolean b = pane.getChildren().remove(io);
            //System.out.println("in circle b = " + b);
        }
        if (clearBool) {
            outputLineHashMap.clear();
        }

        init(pane, com, com.getBodySize());

        // Addd here..
//            private EventHandler<MouseEvent> ;
//    private EventHandler<MouseEvent> ;
//
//    private EventHandler<MouseEvent> ;
//    private EventHandler<MouseEvent> ;
//
//    private EventHandler<MouseEvent> circleHandlerExit;
//        
        if (rectangleHandlerDrag != null) {
            addRectangleMouseDraggedEvent(rectangleHandlerDrag);
        }
        if (rectangleHandlerPress != null) {
            addRectangleMousePressEvent(rectangleHandlerPress);
        }
        if (circleHandlerPress != null) {
            addCircleMousePressEvent(circleHandlerPress);
        }
        if (circleHandlerEnter != null) {
            addCircleMouseEnterEvent(circleHandlerEnter);
        }
        if (circleHandlerExit != null) {
            addCircleMouseExitEvent(circleHandlerExit);
        }

    }

    protected void reDrawInit() {
        if (rectangle != null) {
            pane.getChildren().remove(rectangle);
        }

        if (hashUISupport) {
            pane.getChildren().remove(uiRect);
        }

        pane.getChildren().remove(textBox);

        for (int i = 0; i < inputCircleHashMap.size(); i++) {
            IOShape io = inputCircleHashMap.get((Integer) i);
            boolean b = pane.getChildren().remove(io);
            //System.out.println("in circle b = " + b);
        }

        for (int i = 0; i < outputCircleHashMap.size(); i++) {
            IOShape io = outputCircleHashMap.get((Integer) i);
            boolean b = pane.getChildren().remove(io);
            //System.out.println("in circle b = " + b);
        }

        for (int i = 0; i < inputLineHashMap.size(); i++) {
            Line io = inputLineHashMap.get((Integer) i);
            boolean b = pane.getChildren().remove(io);
            //System.out.println("in circle b = " + b);
        }

        for (int i = 0; i < outputLineHashMap.size(); i++) {
            Line io = outputLineHashMap.get((Integer) i);
            boolean b = pane.getChildren().remove(io);
            //System.out.println("in circle b = " + b);
        }

        init(pane, com, com.getBodySize());

    }

    private void init(Pane pane, ComponentAbstraction com, int size) {
        this.pane = pane;
        this.com = com;
        textBox.setText(com.getName());
        textBox.setFont(Font.font("Verdana", FontWeight.BOLD, SonicCellProperties.TEXT_FONT_INSIDE_COMPONENT));
        textBox.setFill(Color.WHITE);

        rectangle = new Rectangle(SonicCellProperties.WIDTH_OF_COMPONENT, SonicCellProperties.RECTANGLE_HEIGHT_OF_COMPONENT * size + (SonicCellProperties.COMPONENT_HEIGHT_SPACING * (size - 1)));

        if (hashUISupport) {
            uiRect = new Rectangle(SonicCellProperties.WIDTH_OF_COMPONENT * .12, SonicCellProperties.WIDTH_OF_COMPONENT * .12);
            uiRect.setFill(uiColor);
        }

        if (com instanceof FunctionComponent) {
            rectangle.setArcWidth(0 * SonicCellProperties.ZOOM_SCALE);
            rectangle.setArcHeight(0 * SonicCellProperties.ZOOM_SCALE);
            rectangle.setStroke(functionColor);
            rectangle.setFill(functionColor);
        } else if (com instanceof FunctionInputComponent) {
            rectangle.setArcWidth(60 * SonicCellProperties.ZOOM_SCALE);
            rectangle.setArcHeight(60 * SonicCellProperties.ZOOM_SCALE);

            rectangle.setStroke(Color.GREY);
            rectangle.setFill(Color.GREY);
        } else if (com instanceof FunctionOutputComponent) {
            rectangle.setArcWidth(60 * SonicCellProperties.ZOOM_SCALE);
            rectangle.setArcHeight(60 * SonicCellProperties.ZOOM_SCALE);

            rectangle.setStroke(Color.GREY);
            rectangle.setFill(Color.GREY);
        } else {
            Color color = platformIndependentColor;
            if (com.getPlatformSupport() == 1) {
                color = platformPCColor;
            } else if (com.getPlatformSupport() == 2) {
                color = platformAndroidColor;
            }
            rectangle.setArcWidth(30 * SonicCellProperties.ZOOM_SCALE);
            rectangle.setArcHeight(30 * SonicCellProperties.ZOOM_SCALE);
            rectangle.setStroke(color);
            rectangle.setFill(color);
        }

        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(SonicCellProperties.HIGHLIGHT_SELECT_WIDTH);

        int numInput = com.getNumberOfInputSize();
        int numOutput = com.getNumberOfOutputSize();

        for (int i = 0; i < numInput; i++) {
            IOShape circle = new IOShape(0, 0, SonicCellProperties.CIRCLE_RADIUS_IN_OUT_PUT);

            //////////////
            ArrayList<DataTypeIO> typeioAL = com.getInputDataType().get(i).typeArrayList;

            StringBuilder sb = new StringBuilder();
            String typeName = null;
            String descriptions = "";
            for (int j = 0; j < typeioAL.size(); j++) {
                typeName = typeioAL.get(j).getTypeName();
                descriptions = typeioAL.get(j).getDescriptions();
                if (descriptions.equalsIgnoreCase("")) {
                    sb.append("-" + typeName);
                } else {
                    sb.append("-" + typeName + " : " + descriptions);
                }

                if (j < (typeioAL.size() - 1)) {
                    sb.append("\n");
                }
            }

            if (typeioAL.size() == 1) {
                circle.color = ColorMap.getIntance().getColor(typeName);
            }

            Tooltip t = new Tooltip(sb.toString());
            Tooltip.install(circle, t);
            circle.toolTipMsg = sb.toString();
            circle.setFill(circle.color);
            /////////////

            inputCircleHashMap.put(i, circle);
            addToPane(circle);
            circle.srcId = com.getId();
            circle.isOutput = false;
            circle.thruId = i;

            Line line = new Line(0, 0, 0, 0);
            line.setStrokeWidth(SonicCellProperties.LINE_WIDTH_FOR_IO);
            inputLineHashMap.put(i, line);

            String propName = typeioAL.get(0).getPropName();

            Object obj = com.getProperty(propName);

            if (obj != null) {
                String value = obj.toString().trim();

                if (!value.equalsIgnoreCase("")) {
                    circle.setVisible(false);
                    line.setVisible(false);
                }

            }

            addToPane(line);

        }

        for (int i = 0; i < numOutput; i++) {
            IOShape circle = new IOShape(0, 0, SonicCellProperties.CIRCLE_RADIUS_IN_OUT_PUT);
            //////////////
            ArrayList<DataTypeIO> typeioAL = com.getOutputDataType().get(i).typeArrayList;

            StringBuilder sb = new StringBuilder();
            String typeName = null;
            String descriptions = "";
            for (int j = 0; j < typeioAL.size(); j++) {
                typeName = typeioAL.get(j).getTypeName();

                descriptions = typeioAL.get(j).getDescriptions();
                if (descriptions.equalsIgnoreCase("")) {
                    sb.append("-" + typeName);
                } else {
                    sb.append("-" + typeName + " : " + descriptions);
                }
                if (j < (typeioAL.size() - 1)) {
                    sb.append("\n");
                }
            }
            Tooltip t = new Tooltip(sb.toString());
            Tooltip.install(circle, t);
            if (typeioAL.size() == 1) {
                circle.color = ColorMap.getIntance().getColor(typeName);
            }
            circle.setFill(circle.color);
            /////////////

            circle.toolTipMsg = sb.toString();
            outputCircleHashMap.put(i, circle);
            addToPane(circle);
            circle.srcId = com.getId();
            circle.isOutput = true;
            circle.thruId = i;

            Line line = new Line(0, 0, 0, 0);
            line.setStrokeWidth(SonicCellProperties.LINE_WIDTH_FOR_IO);
            outputLineHashMap.put(i, line);
            addToPane(line);

        }

        addToPane(rectangle);
        addToPane(textBox);
        if (hashUISupport) {
            addToPane(uiRect);
        }

        setColRow(currentCol, currentRow);

        updateEnable();
    }

    protected int getCol() {
        return currentCol;
    }

    protected int getRow() {
        return currentRow;
    }

    public void setColRow(int col, int row) {
        currentCol = col;
        currentRow = row;
        double tmpXY[] = SonicCellDrawUtils.colRowToXY(col, row);
        double xy[] = SonicCellDrawUtils.colRowXYTranslate(tmpXY[0], tmpXY[1]);
        setXY(xy[0], xy[1]);
    }

    private void setXY(double x, double y) {

        rectangle.setX(x);
        rectangle.setY(y);

        if (hashUISupport) {
            uiRect.setX(x + rectangle.getWidth() / 2 - uiRect.getWidth() / 2);
            uiRect.setY(y + 2);
        }
        double stringWidth = textBox.getLayoutBounds().getWidth();
        double stringHeight = textBox.getLayoutBounds().getHeight();

//                textBox.setX(x+SonicCellProperties.WIDTH_OF_COMPONENT/2-stringWidth/2);
//        textBox.setY(y+SonicCellProperties.HEIGHT_OF_CELL/2-stringHeight/2);
//        
        textBox.setX(x + SonicCellProperties.WIDTH_OF_COMPONENT / 2 - stringWidth / 2);
        textBox.setY(y + rectangle.getHeight() / 2 + stringHeight / 4);

        //     textBox.setX(rectangle.getWidth()/2+rectangle.getX()+SonicCellProperties.WIDTH_OF_COMPONENT/2-stringWidth/2);
        //     textBox.setY(rectangle.getHeight()/2+rectangle.getY()+SonicCellProperties.HEIGHT_OF_CELL/2-stringHeight/2);
//        textBox.setX(rectangle.getWidth()/2+rectangle.getX());
//        textBox.setY();
        int inputSize = inputCircleHashMap.size();

        // Input
        for (int i = 0; i < inputSize; i++) {
            IOShape circle = inputCircleHashMap.get(i);
            //inputCircleHashMap.put(i, circle);
            circle.setCenterX(x - SonicCellProperties.COMPONENT_WIDTH_SPACING / 4);
            circle.setCenterY(y + (i + 1) * (rectangle.getHeight() / (inputSize + 1)));

            Line line = inputLineHashMap.get(i);
            line.setStartX(x);
            line.setStartY(y + (i + 1) * (rectangle.getHeight() / (inputSize + 1)));
            line.setEndX(x - SonicCellProperties.COMPONENT_WIDTH_SPACING / 4 + circle.getRadius());
            line.setEndY(y + (i + 1) * (rectangle.getHeight() / (inputSize + 1)));

        }

        int outputSize = outputCircleHashMap.size();

        // Output
        for (int i = 0; i < outputSize; i++) {
            IOShape circle = outputCircleHashMap.get(i);
            //outputCircleHashMap.put(i, circle);
            circle.setCenterX(x + SonicCellProperties.WIDTH_OF_COMPONENT + SonicCellProperties.COMPONENT_WIDTH_SPACING / 4);

            circle.setCenterY(y + (i + 1) * (rectangle.getHeight() / (outputSize + 1)));

            Line line = outputLineHashMap.get(i);
            line.setStartX(x + SonicCellProperties.WIDTH_OF_COMPONENT - circle.getRadius());
            line.setStartY(y + (i + 1) * (rectangle.getHeight() / (outputSize + 1)));
            line.setEndX(x + SonicCellProperties.WIDTH_OF_COMPONENT + SonicCellProperties.COMPONENT_WIDTH_SPACING / 4 - circle.getRadius());
            line.setEndY(y + (i + 1) * (rectangle.getHeight() / (outputSize + 1)));

        }
    }

    public void deleteAndCleanup() {
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
                if (hashUISupport) {
                    pane.getChildren().remove(uiRect);
                }

                pane.getChildren().remove(rectangle);

                rectangle.removeEventHandler(MouseEvent.MOUSE_PRESSED, rectangleHandlerPress);
                rectangle.removeEventHandler(MouseEvent.MOUSE_DRAGGED, rectangleHandlerDrag);

                pane.getChildren().remove(textBox);

                int inputSize = inputCircleHashMap.size();

                // Input
                for (int i = 0; i < inputSize; i++) {
                    IOShape circle = inputCircleHashMap.get(i);
                    circle.removeEventHandler(MouseEvent.MOUSE_ENTERED, circleHandlerEnter);
                    circle.removeEventHandler(MouseEvent.MOUSE_PRESSED, circleHandlerPress);
                    circle.removeEventHandler(MouseEvent.MOUSE_EXITED, circleHandlerExit);

                    Line line = inputLineHashMap.get(i);
                    pane.getChildren().remove(circle);
                    pane.getChildren().remove(line);

                }
                inputCircleHashMap.clear();
                int outputSize = outputCircleHashMap.size();

                // Output
                for (int i = 0; i < outputSize; i++) {
                    IOShape circle = outputCircleHashMap.get(i);
                    circle.removeEventHandler(MouseEvent.MOUSE_ENTERED, circleHandlerEnter);
                    circle.removeEventHandler(MouseEvent.MOUSE_PRESSED, circleHandlerPress);
                    circle.removeEventHandler(MouseEvent.MOUSE_EXITED, circleHandlerExit);
                    Line line = outputLineHashMap.get(i);
                    pane.getChildren().remove(circle);
                    pane.getChildren().remove(line);
                }
                outputCircleHashMap.clear();
//            }
//        });

    }

    public Shape getRectangleShape() {

        return rectangle;
    }

    public Shape getTextShape() {
        return rectangle;
    }

    public void setColor(Color color) {
        rectangle.setStroke(color);
        rectangle.setFill(color);
    }

    public void setText(String text) {
        this.textBox.setText(text);
        double stringWidth = textBox.getLayoutBounds().getWidth();
        double x = rectangle.getX();
        textBox.setX(x + SonicCellProperties.WIDTH_OF_COMPONENT / 2 - stringWidth / 2);
    }

    protected void addRectangleMousePressEvent(EventHandler<MouseEvent> handler) {
        rectangleHandlerPress = handler;
        rectangle.setOnMousePressed(handler);
        textBox.setOnMousePressed(handler);
    }

    protected void addRectangleMouseDraggedEvent(EventHandler<MouseEvent> handler) {
        rectangleHandlerDrag = handler;
        rectangle.setOnMouseDragged(handler);
        textBox.setOnMouseDragged(handler);
    }

    // circle
    protected void addCircleMousePressEvent(EventHandler<MouseEvent> handler) {

        circleHandlerPress = handler;
        int size = inputCircleHashMap.size();
        // Input
        for (int i = 0; i < size; i++) {
            inputCircleHashMap.get(i).setOnMousePressed(handler);
        }

        size = outputCircleHashMap.size();
        // Input
        for (int i = 0; i < size; i++) {
            outputCircleHashMap.get(i).setOnMousePressed(handler);
        }

    }

    private void addToPane(Node e) {
        pane.getChildren().add(e);
    }

    private void removeAllNode(Node e) {

        pane.getChildren().add(e);
    }

    protected void addCircleMouseEnterEvent(EventHandler<MouseEvent> handler) {

        circleHandlerEnter = handler;
        int size = inputCircleHashMap.size();
        // Input
        for (int i = 0; i < size; i++) {
            inputCircleHashMap.get(i).setOnMouseEntered(handler);
        }

        size = outputCircleHashMap.size();
        // Input
        for (int i = 0; i < size; i++) {
            outputCircleHashMap.get(i).setOnMouseEntered(handler);
        }
    }

    protected void addCircleMouseExitEvent(EventHandler<MouseEvent> handler) {

        circleHandlerExit = handler;
        int size = inputCircleHashMap.size();
        // Input
        for (int i = 0; i < size; i++) {
            inputCircleHashMap.get(i).setOnMouseExited(handler);
        }

        size = outputCircleHashMap.size();
        // Input
        for (int i = 0; i < size; i++) {
            outputCircleHashMap.get(i).setOnMouseExited(handler);
        }
    }

}
