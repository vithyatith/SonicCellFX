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

/**
 *
 * @author yada
 */
public class SonicCellProperties {

    public static float ZOOM_SCALE = 0.65f;
    public static int WIDTH_OF_CELL = (int) (140 * ZOOM_SCALE);  // 100
    public static int HEIGHT_OF_CELL = (int) (70 * ZOOM_SCALE);  // 50
    public static int COMPONENT_WIDTH_SPACING = (int) (60 * ZOOM_SCALE);
    public static int COMPONENT_HEIGHT_SPACING = (int) (20 * ZOOM_SCALE);
    public static int WIDTH_OF_COMPONENT = (int) ((WIDTH_OF_CELL - COMPONENT_WIDTH_SPACING));
    public static int RECTANGLE_HEIGHT_OF_COMPONENT = (int) ((HEIGHT_OF_CELL - COMPONENT_HEIGHT_SPACING));
    
    public static int CIRCLE_RADIUS_IN_OUT_PUT = (int) (7 * ZOOM_SCALE);
    
    public static int CIRCLE_OFFSET_POSITION = CIRCLE_RADIUS_IN_OUT_PUT;
    
    public static int TEXT_FONT_INSIDE_COMPONENT = (int) (12 * ZOOM_SCALE);
    public static int LINE_WIDTH_FOR_CONNECTION = (int) (5 * ZOOM_SCALE);
    public static int LINE_WIDTH_FOR_IO = (int) (4 * ZOOM_SCALE);
    
    public static int HIGHLIGHT_SELECT_WIDTH = (int) (5 * ZOOM_SCALE);

    public static final Color HIGHLIGHTED_COLOR = Color.YELLOW;
    public static final Color NOT_HIGHLIGHTED_COLOR = Color.BLACK;
    
    public static final Color HIGHLIGHTED_COLOR_IN_OUT_ENTER = Color.GREENYELLOW;
    public static final Color HIGHLIGHTED_COLOR_IN_OUT_EXIT = Color.BLACK;

    public static void recalulate_zoom() {
        WIDTH_OF_CELL = (int) (140 * ZOOM_SCALE);  // 100
        HEIGHT_OF_CELL = (int) (70 * ZOOM_SCALE);  // 50
        COMPONENT_WIDTH_SPACING = (int) (60 * ZOOM_SCALE);
        COMPONENT_HEIGHT_SPACING = (int) (20 * ZOOM_SCALE);
        WIDTH_OF_COMPONENT = (int) ((WIDTH_OF_CELL - COMPONENT_WIDTH_SPACING));
        RECTANGLE_HEIGHT_OF_COMPONENT = (int) ((HEIGHT_OF_CELL - COMPONENT_HEIGHT_SPACING));
        CIRCLE_RADIUS_IN_OUT_PUT = (int) (5 * ZOOM_SCALE);
        TEXT_FONT_INSIDE_COMPONENT = (int) (12 * ZOOM_SCALE);
        LINE_WIDTH_FOR_CONNECTION = (int) (3 * ZOOM_SCALE);
    }
}
