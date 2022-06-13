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
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.text.*;
import java.util.*;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.*;


public class Graph2DPane extends JPanel implements MouseListener, MouseMotionListener, java.io.Serializable {

    private final float boxYRatio = .08f;
    private final float boxXRatio = .08f;
    private final float boxHRatio = .80f;
    private final float boxWRatio = .85f;

    private float data[];

    private transient BufferedImage bimage = null;
    private transient Graphics2D g2d = null;

    private Dimension di;

    private short w = 400;
    private short h = 400;

    private short boxX;
    private short boxY;
    private short boxWidth;
    private short boxHeight;

    private int bufferSize = 0;

    private float max = Float.NEGATIVE_INFINITY;
    private float min = Float.POSITIVE_INFINITY;

    private float fs = 1;
    private String xLabel = "Time";
    private String yLabel = "Amplitude";
    private boolean initial = true;

    private Line2D line = new Line2D.Float();
    private NumberFormat formatter = new DecimalFormat("0.00");
    private Line2D lineTick = new Line2D.Float();
    private transient Font font = new Font("Serif", Font.PLAIN, 10);

    private byte drawMethod = 0;
    private boolean isDragging = false;
    private boolean isZoom = false;
    private int indexZoom = 0;
    private int indexZoomPrev = 0;
    private int indexZoomTmp = 0;
    private int zoomSize = 0;

    private float ratio = 0;
    private float xbin = 0;
    private float xbinValue = 0f;
    private int length = 0;

    private ArrayList<ZoomSet> zoomArray = new ArrayList<ZoomSet>();

    private int zoomXStart = 0;
    private int zoomYStart = 0;
    private int zoomXEnd = 0;
    private int zoomYEnd = 0;
    private float zoomMaxValue = 0f;
    private float zoomMinValue = 0f;

    private boolean zoomBool = false;
    private UpdateGraph2DPane updatePane;

    private int ballX = 0;
    private int ballY = 0;
    private Ellipse2D trackRect;

    private float trackvalue = 0;

    public Graph2DPane() {

    }

    public Graph2DPane(UpdateGraph2DPane _updatePane) {
        updatePane = _updatePane;
        trackRect = new Ellipse2D.Float(0, 0, 10, 10);

        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent componentEvent) {
                resize();
            }
        });

        recalculateDimension();
    }

    public void paintComponent(Graphics g) {
        //System.out.println("Zoom size = "+zoomArray.size());
        if (bimage == null) {

            if ((w == 0) || (h == 0)) {
                return;
            }

            //    System.out.println("W and h"+w+" "+h);
            bimage = (BufferedImage) createImage(w, h);
            g2d = bimage.createGraphics();
            g2d.setBackground(Color.WHITE);
        }
        if (bufferSize == 0) {
            g.drawImage(bimage, 0, 0, this);
            return;
        }

        if (initial) {
            for (int i = 0; i < bufferSize; i++) {
                min = Math.min(min, data[i]);
                max = Math.max(max, data[i]);
            }
            initial = false;
        }

        g2d.clearRect(0, 0, w, h);

        g2d.setColor(Color.BLUE);
        g2d.setFont(font);
        // Drawing the data according to the method

        if (drawMethod == 0) {
            // More pixels and data
            try {
                for (int i = 0; i < length - 1; i++) {

                    line.setLine(boxX + i * xbin, convertHeight(data[i + indexZoom]), boxX + (i + 1) * xbin, convertHeight(data[i + 1 + indexZoom]));
                    g2d.draw(line);

                }
            } catch (java.lang.ArrayIndexOutOfBoundsException e) {
               System.out.println("Graph2DPane148: error "+e.getMessage());
            }
            // Clear bottom
            g2d.clearRect(0, boxY + boxHeight, w, boxHeight);
            int skip = (int) (35 / xbin);

            //  System.out.println("skip = "+skip+" boxwidth = "+boxWidth+" zoomsize = "+zoomSize+"  length = "+length+"xbin = "+xbin);
            for (int i = 0; i < length; i++) {
                lineTick.setLine(boxX + i * xbin, boxY + boxHeight + 2, boxX + i * xbin, boxY + boxHeight + 7);
                g2d.drawString(formatter.format((i + indexZoom)), boxX + i * xbin - 12, boxY + boxHeight + 20);
                i = i + skip;
                g2d.draw(lineTick);
            }

        } else if (drawMethod == 1) {
            // More data than pixels
            if (true) {

                int skip = 45;
                drawAverageMethod();

                // Clear bottom
                g2d.clearRect(0, boxY + boxHeight, w, boxHeight);
                for (int i = 0; i < length; i++) {
                    lineTick.setLine(boxX + i * xbin, boxY + boxHeight + 2, boxX + i * xbin, boxY + boxHeight + 7);
                    g2d.drawString(formatter.format((i * ratio + indexZoom)), boxX + i * xbin - 12, boxY + boxHeight + 20);
                    i = i + skip;
                    g2d.draw(lineTick);
                }
            } else {
                // drawMaxMethod(L, xbin, ratio);
            }
        }

        if (zoomBool) {
            int mywidth = zoomXEnd - zoomXStart;
            int myheight = zoomYEnd - zoomYStart;
            g2d.drawRect(zoomXStart, zoomYStart, mywidth, myheight);
        }

        // Draw tracking ball
        g2d.setColor(Color.RED);

        float trackvalue_tmp = 0f;
        trackvalue_tmp = trackvalue;
        if (zoomArray.size() > 0) {
            if (trackvalue > zoomMaxValue) {
                trackvalue_tmp = zoomMaxValue;
            } else if (trackvalue < zoomMinValue) {
                trackvalue_tmp = zoomMinValue;
            }
        }

        trackRect.setFrame(ballX - 3, convertHeight(trackvalue_tmp), 6, 6);

        // g2d.draw(trackRect);
        g2d.fill(trackRect);

        // Clear top
        g2d.clearRect(0, 0, w, boxY);

        g2d.setColor(Color.BLACK);
        g2d.drawRect(boxX, boxY, boxWidth, boxHeight);

        g.drawImage(bimage, 0, 0, this);
    }

    private void resize() {
        recalculateMinMax();
        recalculateDimension();
        recalInit();

        // Resize forcing it to resize.
        if (drawMethod == 1) {
            max = Float.NEGATIVE_INFINITY;
            min = Float.POSITIVE_INFINITY;
            float sum = 0f;
            float sum_prev = 0f;
            for (int i = 0; i < length; i++) {
                int startIndex = indexZoom + (int) (ratio * i);
                int endIndex = indexZoom + (int) (ratio * (i + 1));
                int diff = endIndex - startIndex;
                for (int j = startIndex; j < endIndex; j++) {
                    sum = sum + data[j];
                }
                sum = sum / (float) diff;
                min = Math.min(min, sum);
                max = Math.max(max, sum);

                sum = 0;
            }
        }

        repaint();
    }

    private void recalculateDimension() {
        di = getSize();
        w = (short) di.getWidth();
        h = (short) di.getHeight();
        boxX = (short) (w * boxXRatio);
        boxY = (short) (h * boxYRatio);
        boxWidth = (short) (w * boxWRatio);
        boxHeight = (short) (h * boxHRatio);
        if (bimage != null) {
            if ((w < 0) || (h < 0)) {
                System.out.println("The width or height dimension is 0.  Cannot create buffer image. In code Graph2DPane:recalculateDimension.");
                return;
            }
            bimage = (BufferedImage) createImage(w, h);
            g2d = bimage.createGraphics();
            g2d.setBackground(Color.WHITE);
        }
    }

    private float convertHeight(float _y) {
        // return boxY+(float)boxHeight*(1-Math.abs(_y-min)/(float)Math.abs(max-min));

        //return boxY+(float)boxHeight*(1-(_y-min)/(float)(max-min));
        if (zoomArray.size() > 0) {
            /*
            if(_y>zoomMaxValue){
                _y = zoomMaxValue;
            }
            else if(_y<zoomMinValue){
                _y = zoomMinValue;
            }
             */
            return boxY + (float) boxHeight * (1 - (_y - zoomMinValue) / (float) (zoomMaxValue - zoomMinValue));
        } else {
            return boxY + (float) boxHeight * (1 - (_y - min) / (float) (max - min));
        }
    }

    private float convertHeightToValue(float _y) {
        float value = -((_y - boxY) / boxHeight - 1f) * (max - min) + min;
        return value;
    }

    private void recalInit() {
        if (isZoom == false) {
            zoomSize = bufferSize;
            indexZoom = 0;
        }

        if (boxWidth >= zoomSize) {
            length = zoomSize;
            xbin = (boxWidth) / (float) (length - 1);
            drawMethod = 0;
            ratio = xbin;
        } else {
            length = boxWidth;
            ratio = zoomSize / (float) length;;
            drawMethod = 1;
            xbin = 1;
        }
    }

    // orig
    public synchronized void setData(float _data[]) {
        data = _data;
        bufferSize = data.length;
        recalculateMinMax();
        recalInit();
        repaint();
    }

    public synchronized void setData(Object obj) {
        data = null;

        if (obj instanceof float[]) {
            data = (float[]) obj;
        }
        recalculateMinMax();
//        
//        else if(obj instanceof FloatVector){
//            data = ((FloatVector)obj).getDataRef();
//        } else if(obj instanceof ComplexVector){
//            data = ((ComplexVector)obj).getReal().getDataRef();
//        } else{
        //           return;
        //      }
        bufferSize = data.length;

        recalInit();

        if (fs == 0) {
            fs = bufferSize;
        }
        repaint();
    }

    private void recalculateMinMax() {
        max = Float.NEGATIVE_INFINITY;
        min = Float.POSITIVE_INFINITY;
        for (int i = 0; i < bufferSize; i++) {
            min = Math.min(min, data[i]);
            max = Math.max(max, data[i]);
        }
    }

    private void setMinMax(float _min, float _max) {
        max = _max;
        min = _min;
    }

    private void setXLabel(String _xlabelName) {
        xLabel = _xlabelName;
    }

    private void setYLabel(String _ylabelName) {
        yLabel = _ylabelName;
    }

    public void setSampleRate(float _fs) {
        fs = _fs;
    }

    private void drawAverageMethod() {
        try{
        float sum = 0f;
        float sum_prev = 0f;
        for (int i = 0; i < length; i++) {
            int startIndex = indexZoom + (int) (ratio * i);
            int endIndex = indexZoom + (int) (ratio * (i + 1));
            int diff = endIndex - startIndex;
            for (int j = startIndex; j < endIndex; j++) {
                sum = sum + data[j];
            }
            sum = sum / (float) diff;

            line.setLine(boxX + i * xbin, convertHeight(sum_prev), boxX + (i + 1) * xbin, convertHeight(sum));
            sum_prev = sum;
            g2d.draw(line);
            sum = 0;
        }
        }catch(java.lang.ArrayIndexOutOfBoundsException ex){
            System.out.println("Graph2D 385: error "+ex.getMessage());
        }
    }

    private void drawMaxMethod(int L, float xbin, float _ratio) {
        try{
        float maxV = Float.NEGATIVE_INFINITY;
        float maxV_prev = 0f;
        for (int i = 0; i < L; i++) {
            int startIndex = (int) Math.ceil(_ratio * i);
            int endIndex = (int) Math.ceil(_ratio * (i + 1));
            int diff = endIndex - startIndex;

            if (_ratio == 0) {
                for (int j = startIndex; j < endIndex; j++) {
                    maxV = (float) Math.max(maxV, data[j]);
                }
            } else {
                maxV = data[i];
            }

            line.setLine(boxX + i * xbin, convertHeight(maxV_prev), boxX + (i + 1) * xbin, convertHeight(maxV));
            maxV_prev = maxV;
            g2d.draw(line);
            maxV = Float.NEGATIVE_INFINITY;
        }
        }catch(java.lang.ArrayIndexOutOfBoundsException ex){
             System.out.println("Graph2D 412: error "+ex.getMessage());
        }
    }

    public void mouseClicked(MouseEvent mouseEvent) {
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    public void mouseExited(MouseEvent mouseEvent) {
    }

    public void mousePressed(MouseEvent mouseEvent) {
        isDragging = false;
        zoomBool = false;
        int mask = InputEvent.BUTTON1_MASK - 1;
        int mods = mouseEvent.getModifiers() & mask;
        zoomXStart = mouseEvent.getX();
        zoomYStart = mouseEvent.getY();
        if (mods != 0) {
            int L = zoomArray.size();
            if (L == 0) {
                //   repaint();
            } else if (L == 1) {
                isZoom = false;
                indexZoom = 0;
                zoomSize = bufferSize;
                indexZoomPrev = indexZoom;
                recalInit();
                //  indexZoom = 0;

                zoomArray.remove(0);
                repaint();
            } else {
                zoomArray.remove(L - 1);
                L = zoomArray.size();
                ZoomSet zoom = zoomArray.get(L - 1);
                indexZoom = zoom.getZoomIndex();
                zoomSize = zoom.getZoomSize();
                indexZoomPrev = indexZoom;
                recalInit();
                repaint();
            }
            return;
        }

        int x = mouseEvent.getX();
        int y = mouseEvent.getY();
        int i = x - boxX;
        //float value = 0;
        //value = (zoomSize/(float)boxWidth)/fs;

        if (isZoom == true) {
            // int indexTmp = (int)(i*(zoomSize/(float)boxWidth));
            int indexTmp = 0;
            if (drawMethod == 0) {
                indexTmp = (int) (i / xbin);
            } else {
                indexTmp = (int) (i * ratio);
            }
            indexZoomTmp = indexZoomPrev + indexTmp;
        } else {
            // indexZoomTmp = (int)(i*(zoomSize/(float)boxWidth));
            if (drawMethod == 0) {
                indexZoomTmp = (int) (i / xbin);
            } else {
                indexZoomTmp = (int) (i * ratio);
            }
        }

    }

    public void mouseReleased(MouseEvent mouseEvent) {
        int mask = InputEvent.BUTTON1_MASK - 1;
        int mods = mouseEvent.getModifiers() & mask;

        if (mods != 0) {
            return;
        }

        if (isDragging) {
            int x = mouseEvent.getX();
            int y = mouseEvent.getY();

            int indexEndZoom = 0;
            int i = x - boxX;

            if (isZoom == true) {
                //int indexTmp = (int)((i)*((zoomSize)/(float)boxWidth));
                int indexTmp = 0;
                if (drawMethod == 0) {
                    indexTmp = (int) Math.ceil((i) / xbin);
                } else {
                    indexTmp = (int) Math.ceil(i * ratio);
                }

                //indexEndZoom = indexZoom + indexTmp;
                indexEndZoom = indexZoomPrev + indexTmp;
                if (indexEndZoom >= bufferSize) {
                    indexEndZoom = bufferSize - 1;
                }

            } else {
                //indexEndZoom = (int)(i*(zoomSize/(float)boxWidth));
                if (drawMethod == 0) {
                    indexEndZoom = (int) Math.ceil(i / xbin);
                } else {
                    indexEndZoom = (int) Math.ceil(i * ratio);
                }

                if (indexEndZoom >= bufferSize) {
                    indexEndZoom = bufferSize - 1;
                }

            }
            indexZoomPrev = indexZoomTmp;
            indexZoom = indexZoomPrev;
            zoomSize = indexEndZoom - indexZoom + 1;
            isZoom = true;

            zoomArray.add(new ZoomSet(indexZoom, zoomSize));
            zoomBool = false;

            /////////////
            zoomMaxValue = convertHeightToValue(zoomYStart);
            zoomMinValue = convertHeightToValue(zoomYEnd);

            ///////////////
            recalInit();
            repaint();
        }
    }

    public void mouseDragged(MouseEvent me) {
        isDragging = true;
        zoomBool = true;
        zoomXEnd = me.getX();
        zoomYEnd = me.getY();
        repaint();
    }

    public void mouseMoved(MouseEvent me) {

        int x = me.getX();
        int y = me.getY();

        float sum = 0;
        if ((x >= boxX) && (x <= (boxWidth + boxX)) && (y >= boxY) && (y <= (boxHeight + boxY))) {
            ballX = x;
            ballY = y;

            int i = x - boxX;
            int indexTmp = 0;

            //   System.out.println("The value = "+convertHeightToValue(ballY));
            if (data == null) {
                return;
            }
            if (drawMethod == 0) {
                indexTmp = (int) (indexZoomPrev) + (int) (i / xbin);

                if ((indexTmp < 0) || indexTmp > (data.length - 1)) {
                    return;
                }
                sum = data[indexTmp];

            } else {
                indexTmp = (int) (indexZoomPrev) + (int) (i * ratio);

                int startIndex = indexZoom + (int) (ratio * i);
                int endIndex = indexZoom + (int) (ratio * (i + 1));
                if (endIndex == bufferSize) {
                    endIndex = endIndex - 1;
                }
                int diff = endIndex - startIndex;
                if (endIndex < data.length) {
                    for (int j = startIndex; j < endIndex; j++) {
                        sum = sum + data[j];
                    }
                    sum = sum / (float) diff;
                }
            }

            trackvalue = sum;

            updatePane.setXTextField(indexTmp, sum);
        }
    }

    protected void unZoom() {
        isZoom = false;
        zoomSize = bufferSize;
        zoomArray.clear();
        resize();
        recalInit();
        repaint();
    }

    class ZoomSet {

        private int zoomIndex;
        private int zoomSize;

        public ZoomSet(int _zoomIndex, int _zoomSize) {
            zoomIndex = _zoomIndex;
            zoomSize = _zoomSize;
        }

        public int getZoomIndex() {
            return zoomIndex;
        }

        public int getZoomSize() {
            return zoomSize;
        }

        public void setZoomIndex(int _zoomIndex) {
            zoomIndex = _zoomIndex;
        }

        public void setZoomSize(int _zoomSize) {
            zoomSize = _zoomSize;
        }
    }

}
