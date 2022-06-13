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

/**
 *
 * @author yada
 */
public class SonicCellDrawUtils {

    private static int xyColRowResultTmp[] = new int[2];

    protected static int[] xyToColRow(double x, double y) {
        int col = (int) (x / SonicCellProperties.WIDTH_OF_CELL);
        int row = (int) (y / SonicCellProperties.HEIGHT_OF_CELL);
        xyColRowResultTmp[0] = col;
        xyColRowResultTmp[1] = row;
        return xyColRowResultTmp;
    }
    private static double colRowXYResultTmp[] = new double[2];

    protected static double[] colRowToXY(double col, double row) {
        double x = col * SonicCellProperties.WIDTH_OF_CELL;
        double y = row * SonicCellProperties.HEIGHT_OF_CELL;
        colRowXYResultTmp[0] = x + SonicCellProperties.COMPONENT_WIDTH_SPACING / 2;
        colRowXYResultTmp[1] = y + SonicCellProperties.COMPONENT_HEIGHT_SPACING / 2;
        return colRowXYResultTmp;
    }
    private static double colRowXYTranslateTmp[] = new double[2];

    protected static double[] colRowXYTranslate(double x, double y) {
        int colRow[] = xyToColRow(x, y);
        double xy[] = colRowToXY(colRow[0], colRow[1]);
        return xy;
    }
}
